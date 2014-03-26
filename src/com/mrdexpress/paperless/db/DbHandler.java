package com.mrdexpress.paperless.db;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.mrdexpress.paperless.workflow.ObservableJSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mrdexpress.paperless.datatype.ComLogObject;
import com.mrdexpress.paperless.datatype.DeliveryHandoverDataObject;
import com.mrdexpress.paperless.datatype.DialogDataObject;
import com.mrdexpress.paperless.datatype.UserItem;
import com.mrdexpress.paperless.datatype.UserItem.UserType;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.CallQueueObject;

public class DbHandler extends SQLiteOpenHelper
{

	static final String TAG = "DbHandler";
	private static DbHandler dbHandler;

	// DB Info
	public static final int DB_VERSION = 1;
	public static final String DB_NAME = "Paperless.db";

	// Tables
	public static final String TABLE_DRIVERS = "Drivers";
	public static final String TABLE_MANAGERS = "Managers";
	public static final String TABLE_CALLQUEUE = "CallQueue"; // Queues API calls if net is down
	public static final String TABLE_CONTACTS = "Contacts";
	public static final String TABLE_COMLOG = "ComLog";

	// ------------ Fields - Drivers ---------
	public static final String C_DRIVER_ID = "_id"; // Primary key
	public static final String C_DRIVER_NAME = "dvr_name";
	public static final String C_DRIVER_PIN = "dvr_pin";

	// ------------ Fields - Managers ---------
	public static final String C_MANAGER_ID = "_id"; // Primary key
	public static final String C_MANAGER_NAME = "man_name";

	// ------------ Fields - Bags -------------
	public static final String C_BAG_ID = "_id"; // Consignment number
													// (PK)
	// ------------ Fields - Contacts -------------
	public static final String C_CONTACTS_ID = "_id"; // Primary key
	public static final String C_CONTACTS_NAME = "contact_name";
	public static final String C_CONTACTS_NUMBER = "contact_number";
	public static final String C_CONTACTS_BAG_ID = "contact_bagid"; // Foreign key. linking contact

	// ------------ Fields - ComLog -------------
	public static final String C_COMLOG_ID = "_id";
	public static final String C_COMLOG_TIMESTAMP = "comlog_timestamp";
	public static final String C_COMLOG_USER = "comlog_user";
	public static final String C_COMLOG_NOTE = "comlog_note";
	public static final String C_COMLOG_BAGID = "comlog_bag_id";

	// ------------ Fields - Call Queue -------------
	public static final String C_CALLQUEUE_ID = "_id";
	public static final String C_CALLQUEUE_URL = "callqueue_url";
	public static final String C_CALLQUEUE_JSON = "callqueue_json";

	SharedPreferences prefs;
	boolean training_run;

	private Context context;
	
	static final String COMM_LOG_TIMESTAMP_FORMAT = "dd:MM:yyyy HH:mm";
	private SimpleDateFormat comm_log_date_formatter = new SimpleDateFormat(COMM_LOG_TIMESTAMP_FORMAT);
	

	public DbHandler(Context context)
	{
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
		this.context = context;
		prefs = context.getSharedPreferences(VariableManager.PREF, Context.MODE_PRIVATE);
	}

	// Return singleton instance of DbHandler
	public static DbHandler getInstance(Context context)
	{
		if (dbHandler == null)
		{
			dbHandler = new DbHandler(context.getApplicationContext());
		}
		return dbHandler;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// TODO Auto-generated method stub
		try
		{
			db.beginTransaction();

			final String CREATE_TABLE_DRIVERS = "CREATE TABLE " + TABLE_DRIVERS + "(" + C_DRIVER_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT," + C_DRIVER_NAME + " TEXT,"
					+ C_DRIVER_PIN + " TEXT)";
			createTable(db, TABLE_DRIVERS, CREATE_TABLE_DRIVERS);

			final String CREATE_TABLE_MANAGERS = "CREATE TABLE " + TABLE_MANAGERS + "("
					+ C_MANAGER_ID + " INTEGER PRIMARY KEY," + " TEXT," + C_MANAGER_NAME + " TEXT)";
			createTable(db, TABLE_MANAGERS, CREATE_TABLE_MANAGERS);

			final String CREATE_TABLE_CONTACTS = "CREATE TABLE " + TABLE_CONTACTS + "("
					+ C_CONTACTS_ID + " INTEGER PRIMARY KEY," + C_CONTACTS_NAME + " TEXT,"
					+ C_CONTACTS_BAG_ID + " TEXT," + C_CONTACTS_NUMBER + " TEXT)";
			createTable(db, TABLE_CONTACTS, CREATE_TABLE_CONTACTS);

			final String CREATE_TABLE_CALLQUEUE = "CREATE TABLE " + TABLE_CALLQUEUE + "("
					+ C_CALLQUEUE_ID + " INTEGER PRIMARY KEY," + C_CALLQUEUE_JSON + " TEXT,"
					+ C_CALLQUEUE_URL + " TEXT)";
			createTable(db, TABLE_CALLQUEUE, CREATE_TABLE_CALLQUEUE);

			final String CREATE_TABLE_COMLOG = "CREATE TABLE " + TABLE_COMLOG + "(" + C_COMLOG_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT," + C_COMLOG_TIMESTAMP + " TEXT,"
					+ C_COMLOG_NOTE + " TEXT," + C_COMLOG_BAGID + " TEXT," + C_COMLOG_USER
					+ " TEXT)";
			createTable(db, TABLE_COMLOG, CREATE_TABLE_COMLOG);

			db.setTransactionSuccessful();
		}
		catch (SQLiteException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, DbHandler.class.getName() + " - Error creating table: " + sw.toString());
		}
		finally
		{
			db.endTransaction();
		}

	}

	/**
	 * Create new table from raw SQL query.
	 * 
	 * @param table
	 * @param raw_query
	 */
	private void createTable(SQLiteDatabase db, String table, String raw_query)
	{
		db.execSQL(raw_query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRIVERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MANAGERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMLOG);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALLQUEUE);
		onCreate(db);
	}

	/**
	 * Add a new driver to the database.
	 * 
	 * Params: Driver object. Name of table to insert into
	 * 
	 * Returns boolean of whether the database transaction was successful
	 */
	public boolean addDriver(Driver driver)
	{
		ContentValues values = new ContentValues();

		values.put(C_DRIVER_ID, driver.getId());
		values.put(C_DRIVER_NAME, driver.getName());
		values.put(C_DRIVER_PIN, driver.getPin());

		String where_clause = C_DRIVER_ID + " LIKE '"
				+ driver.getId() + "'";
		
		String countQuery = "SELECT * FROM " + TABLE_DRIVERS + " WHERE " + where_clause;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		if (cursor.getCount() > 0)
		{
			cursor.close();
			int affected_rows = db.update(TABLE_DRIVERS, values, where_clause, null);
			if (affected_rows > 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			
			return addRow(TABLE_DRIVERS, values);
		}

	}

	/**
	 * Add a manager to the DB.
	 * 
	 */
	public boolean addManager(String id, String name)
	{
		ContentValues values = new ContentValues();

		values.put(C_MANAGER_ID, id);
		values.put(C_MANAGER_NAME, name);

		String where_clause = C_MANAGER_ID + " LIKE '"
				+ id + "'";
		
		String countQuery = "SELECT * FROM " + TABLE_MANAGERS + " WHERE " + where_clause;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		
		if (cursor.getCount() > 0)
		{
			cursor.close();
			int affected_rows = db.update(TABLE_MANAGERS, values, where_clause, null);
			if (affected_rows > 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			
			return addRow(TABLE_MANAGERS, values);
		}
		
	}

	public boolean addContact(String name, String number, String bagid)
	{
		ContentValues values = new ContentValues();

		values.put(C_CONTACTS_NAME, name);
		values.put(C_CONTACTS_NUMBER, number);
		values.put(C_CONTACTS_BAG_ID, bagid);

		return addRow(TABLE_CONTACTS, values);
	}

	/**
	 * Add entry into ComLog table
	 * 
	 */
	public boolean addComLog(JSONArray comlog, String bagid)
	{
		ContentValues values = new ContentValues();
		boolean result = true;

		for (int i = 0; i < comlog.length(); i++)
		{
			try
			{
				values.put(C_COMLOG_TIMESTAMP, comlog.getJSONObject(i).getString("timestamp"));
				values.put(C_COMLOG_NOTE, comlog.getJSONObject(i).getString("note"));
				values.put(C_COMLOG_USER, comlog.getJSONObject(i).getString("user"));
				values.put(C_COMLOG_BAGID, bagid);

				addRow(TABLE_COMLOG, values);
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = false;
			}
		}
		return result;
	}

	/**
	 * Add commlog record to DB
	 * 
	 * @param timestamp
	 * @param note
	 * @param user
	 * @param bagid
	 * @return
	 */
	public boolean addComLog(java.util.Date timestamp, String note, String user, String bagid)
	{
		ContentValues values = new ContentValues();
		boolean result = true;

		try
		{
			values.put(C_COMLOG_TIMESTAMP, comm_log_date_formatter.format(timestamp));
			values.put(C_COMLOG_NOTE, note);
			values.put(C_COMLOG_USER, user);
			values.put(C_COMLOG_BAGID, bagid);

			addRow(TABLE_COMLOG, values);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	/**
	 * Inserts a row into the database.
	 * 
	 * @param table
	 * @param values
	 * @return
	 */
	public boolean addRow(String table, ContentValues values)
	{
		SQLiteDatabase db = null;
		try
		{
			db = this.getWritableDatabase(); // Open db
			// Write to db and return success status
			return db.insertOrThrow(table, null, values) >= 0;
		}
		catch (SQLiteException e)
		{ // TODO Auto-generated catch
			// block
			try
			{
				return db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE) >= 0;
			}
			catch (Exception err)
			{
				StringWriter sw = new StringWriter();
				err.printStackTrace(new PrintWriter(sw));
				Log.e(TAG, sw.toString());
				return false; // Db transaction failed
			}
		}
		catch (IllegalStateException err)
		{
			StringWriter sw = new StringWriter();
			err.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			return false;
		}
		finally
		{

			if (db != null)
			{
				if (db.isOpen()) // check if db is already open
				{
					db.close(); // close db
				}
			}
		}
	}

	/**
	 * Returns database filename
	 * 
	 * @return
	 */
	public static String getDbName()
	{
		return DB_NAME;
	}

	/**
	 * Converts boolean datatype to int, because SQLite doesn't have boolean
	 * primitives.
	 * 
	 * @param bool
	 * @return
	 */
	public int convertBoolToInt(boolean bool)
	{
		if (bool)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}

	/**
	 * Converts int datatype to boolean, because SQLite doesn't have boolean
	 * primitives.
	 * 
	 * @param bool
	 * @return
	 */
	public boolean convertIntToBool(int integer)
	{
		if (integer == 1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Return contact details linked to the specified bag ID.
	 * 
	 * @param bag_id
	 * @return
	 */
	public ArrayList<DialogDataObject> getContacts(String bag_id)
	{
			// Log.d(TAG, "zeus adding real contacts");
			SQLiteDatabase db = null;
			ArrayList<DialogDataObject> contacts = new ArrayList<DialogDataObject>();
			try
			{

				db = this.getReadableDatabase(); // Open db

				// ArrayList<Bag> bags = null;
				String sql = "SELECT * FROM " + TABLE_CONTACTS + " WHERE " + C_CONTACTS_BAG_ID
						+ " LIKE '" + bag_id + "'";
				Cursor cursor = db.rawQuery(sql, null);

				if (cursor != null && cursor.moveToFirst())
				{
					// bags = new ArrayList<Bag>();

					if (!cursor.isAfterLast())
					{
						DialogDataObject contact = new DialogDataObject();

						Log.d("getContacts",
								"getColumnIndex.Name: " + cursor.getColumnIndex(C_CONTACTS_NAME));
						Log.d("getContacts", "getColumnIndex.Contact: " + cursor);

						contact.setMainText(cursor.getString(cursor.getColumnIndex(C_CONTACTS_NAME)));

						contact.setSubText(cursor.getString(cursor
								.getColumnIndex(C_CONTACTS_NUMBER)));

						contacts.add(contact);
						cursor.moveToNext();
					}
				}
			}
			catch (SQLiteException e)
			{ // TODO Auto-generated catch
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				Log.e(TAG, sw.toString());
			}
			catch (IllegalStateException e)
			{
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				Log.e(TAG, sw.toString());
			}
			finally
			{

				if (db != null)
				{
					if (db.isOpen()) // check if db is already open
					{
						db.close(); // close db
					}
				}
			}
			return contacts;
	}

	public ArrayList<ComLogObject> getComLog(String bag_id)
	{
		SQLiteDatabase db = null;
		ArrayList<ComLogObject> comlogs = new ArrayList<ComLogObject>();
		try
		{

			db = this.getReadableDatabase(); // Open db

			// ArrayList<Bag> bags = null;
			String sql = "SELECT * FROM " + TABLE_COMLOG + " WHERE " + C_COMLOG_BAGID + " LIKE '"
					+ bag_id + "'";
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst())
			{
				// bags = new ArrayList<Bag>();

				while (!cursor.isAfterLast())
				{
					ComLogObject comlog = new ComLogObject(cursor.getString(cursor
							.getColumnIndex(C_COMLOG_TIMESTAMP)), cursor.getString(cursor
							.getColumnIndex(C_COMLOG_USER)), cursor.getString(cursor
							.getColumnIndex(C_COMLOG_NOTE)));

					/*					Log.d("getContacts",
												"getColumnIndex.Name: " + cursor.getColumnIndex(C_CONTACTS_NAME));
										Log.d("getContacts", "getColumnIndex.Contact: " + cursor);*/

					comlogs.add(comlog);
					cursor.moveToNext();
				}
			}
		}
		catch (SQLiteException e)
		{ // TODO Auto-generated catch
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
		}
		catch (IllegalStateException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
		}
		finally
		{

			if (db != null)
			{
				if (db.isOpen()) // check if db is already open
				{
					db.close(); // close db
				}
			}
		}
		return comlogs;
	}

	/**
	 * Pushes attempted API call to the queue in case of network unavailability.
	 * 
	 * @param url
	 *            URL of API call
	 * @return
	 */
	public boolean pushCall(String url, JSONObject json)
	{
		ContentValues values = new ContentValues();

		values.put(C_CALLQUEUE_URL, url);
		values.put(C_CALLQUEUE_JSON, json.toString());
		boolean success = addRow(TABLE_CALLQUEUE, values);
		Log.d(TAG, "Pushing call to queue: " + success + "\n" + url);
		return success;
	}

	/**
	 * Return first row of queue, then delete it.
	 * 
	 * @return URL at top of queue.
	 */
	public CallQueueObject popCallQueue()
	{
		SQLiteDatabase db = null;
		CallQueueObject call = null;
		try
		{

			db = this.getReadableDatabase(); // Open db

			// ArrayList<Bag> bags = null;
			String sql = "SELECT * FROM " + TABLE_CALLQUEUE;
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst())
			{
				// bags = new ArrayList<Bag>();

				if (!cursor.isAfterLast())
				{
					call = new CallQueueObject(cursor.getString(cursor
							.getColumnIndex(C_CALLQUEUE_URL)), cursor.getString(cursor
							.getColumnIndex(C_CALLQUEUE_JSON)));

					// Delete topmost row
					db.execSQL("DELETE FROM " + TABLE_CALLQUEUE + " WHERE " + C_CALLQUEUE_ID
							+ " IN (SELECT " + C_CALLQUEUE_ID + " FROM " + TABLE_CALLQUEUE
							+ " ORDER BY " + C_CALLQUEUE_ID + " LIMIT 1)");
					// bags.add(bag);
					// cursor.moveToNext();
				}
			}
		}
		catch (SQLiteException e)
		{ // TODO Auto-generated catch
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
		}
		catch (IllegalStateException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
		}
		finally
		{

			if (db != null)
			{
				if (db.isOpen()) // check if db is already open
				{
					db.close(); // close db
				}
			}
		}
		return call;
	}


	/**
	 * Return list of people to be SMSed. Hardcoded for now.
	 * 
	 * @return
	 */
	public ArrayList<DialogDataObject> getSMSContactPeople()
	{
		ArrayList<DialogDataObject> msgs = new ArrayList<DialogDataObject>();

		msgs.add(new DialogDataObject("Branch", ""));
		msgs.add(new DialogDataObject("Call Centre", ""));
		msgs.add(new DialogDataObject("Chief Operating Officer", ""));

		return msgs;
	}

	/**
	 * Return list of predefined message to be SMSed. Hardcoded for now.
	 * 
	 * @return
	 */
	public ArrayList<DialogDataObject> getSMSMessages()
	{
		ArrayList<DialogDataObject> msgs = new ArrayList<DialogDataObject>();

		msgs.add(new DialogDataObject("HIJACK", "HIJACK"));
		msgs.add(new DialogDataObject("Accident", "Accident"));
		msgs.add(new DialogDataObject("IED", "IED"));
		msgs.add(new DialogDataObject("RPG", "RPG"));
		msgs.add(new DialogDataObject("Ambush", "Ambush"));

		return msgs;
	}

	/**
	 * Returns true if the driver has set a PIN.
	 * 
	 * @return Barcode at specified row
	 */
	public boolean isDriverPinSet(String driver_id)
	{
		String countQuery = "SELECT  * FROM " + TABLE_DRIVERS + " WHERE " + C_DRIVER_ID + " LIKE '"
				+ driver_id + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		String driver_pin = "";
		if (cursor != null && cursor.moveToFirst())
		{
			driver_pin = cursor.getString(cursor.getColumnIndex(C_DRIVER_PIN));
			Log.d(TAG, "PIN: " + driver_pin);
		}
		// Log.d(TAG, "zorro cursor bagid " + barcode);
		cursor.close();
		boolean isDriverPinSet = true;
		if (driver_pin.equals("null"))
		{
			Log.d(TAG, "inside: " + driver_pin);
			isDriverPinSet = false;
		}

		return isDriverPinSet;
	}

	/**
	 * Return the name of a driver.
	 * 
	 * @return Barcode at specified row
	 */
	public String getDriverName(String driver_id)
	{
		String countQuery = "SELECT  * FROM " + TABLE_DRIVERS + " WHERE " + C_DRIVER_ID + " LIKE '"
				+ driver_id + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		String driver_name = "";
		if (cursor != null && cursor.moveToFirst())
		{
			driver_name = cursor.getString(cursor.getColumnIndex(C_DRIVER_NAME));
		}
		// Log.d(TAG, "zorro cursor bagid " + barcode);
		cursor.close();
		return driver_name;
	}

	/**
	 * Returns list of drivers
	 * 
	 * @return
	 */
	/*public ArrayList<UserItem> getDrivers()
	{
		SQLiteDatabase db = null;
		try
		{
			db = this.getReadableDatabase(); // Open db

			ArrayList<UserItem> drivers = null;
			String sql = "SELECT * FROM " + TABLE_DRIVERS;
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst())
			{
				drivers = new ArrayList<UserItem>();

				while (!cursor.isAfterLast())
				{
					String driver_id = cursor.getString(cursor.getColumnIndex(C_DRIVER_ID));
					String driver_name = cursor.getString(cursor.getColumnIndex(C_DRIVER_NAME));

                    net.minidev.json.JSONObject jso = new net.minidev.json.JSONObject();
                    jso.put("id", driver_id);
                    jso.put("firstname", driver_name);
                    jso.put("surname", "");
                    jso.put("role","{DRIVER}");

                    UserItem person_item = new UserItem( new ObservableJSONObject( jso) );

					drivers.add(person_item);

					cursor.moveToNext();
				}
			}

			return drivers;
		}
		catch (SQLiteException e)
		{ // TODO Auto-generated catch
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			return null;
		}
		catch (IllegalStateException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			return null;
		}
		finally
		{

			if (db != null)
			{
				if (db.isOpen()) // check if db is already open
				{
					db.close(); // close db
				}
			}
		}
	}    */

	/**
	 * Returns list of drivers
	 * 
	 * @return
	 */
	public ArrayList<UserItem> getManagers()
	{
		SQLiteDatabase db = null;
		try
		{
			db = this.getReadableDatabase(); // Open db

			ArrayList<UserItem> managers = null;
			String sql = "SELECT * FROM " + TABLE_MANAGERS;
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst())
			{
				managers = new ArrayList<UserItem>();

				while (!cursor.isAfterLast())
				{
					String manager_id = cursor.getString(cursor.getColumnIndex(C_MANAGER_ID));
					String manager_name = cursor.getString(cursor.getColumnIndex(C_MANAGER_NAME));

                    net.minidev.json.JSONObject jso = new net.minidev.json.JSONObject();
                    jso.put("id", manager_id);
                    jso.put("firstname", manager_name);
                    jso.put("surname", "");
                    jso.put("role","{MANAGER}");

					UserItem person_item = new UserItem( new ObservableJSONObject( jso) );

					managers.add(person_item);

					cursor.moveToNext();
				}
			}

			return managers;
		}
		catch (SQLiteException e)
		{ // TODO Auto-generated catch
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			return null;
		}
		catch (IllegalStateException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			return null;
		}
		finally
		{

			if (db != null)
			{
				if (db.isOpen()) // check if db is already open
				{
					db.close(); // close db
				}
			}
		}
	}
}
