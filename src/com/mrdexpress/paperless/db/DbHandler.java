package com.mrdexpress.paperless.db;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

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
	public static final String TABLE_BAGS = "Bag"; // Consignments going to various delivery points
	public static final String TABLE_BAGS_TRAINING = "BagTrainingRun"; // Training run
	public static final String TABLE_WAYBILLS = "Waybill";
	public static final String TABLE_WAYBILLS_TRAINING = "WaybillTrainingRun";
	public static final String TABLE_CALLQUEUE = "CallQueue"; // Queues API calls if net is down
	public static final String TABLE_CONTACTS = "Contacts";
	public static final String TABLE_DELAYS = "Delays";
	public static final String TABLE_DELAYS_DURATIONS = "DelaysDurations";
	public static final String TABLE_COMLOG = "ComLog";
	public static final String TABLE_FAILED_HANDOVER_REASONS = "FailedHandoverReasons";
	public static final String TABLE_FAILED_HANDOVER_REASONS_TRAINING = "FailedHandoverReasonsTraining";
	public static final String TABLE_PARTIAL_DELIVERY_REASONS = "PartialDeliveryReasons";

	// ------------ Fields - Drivers ---------
	public static final String C_DRIVER_ID = "_id"; // Primary key
	public static final String C_DRIVER_NAME = "dvr_name";
	public static final String C_DRIVER_PIN = "dvr_pin";

	// ------------ Fields - Managers ---------
	public static final String C_MANAGER_ID = "_id"; // Primary key
	public static final String C_MANAGER_NAME = "man_name";

	// ------------ Fields - Delays ---------
	public static final String C_DELAYS_ID = "_id"; // Primary key
	public static final String C_DELAYS_REASON_ID = "delay_reason_id";
	public static final String C_DELAYS_REASON = "delay_reason";
	public static final String C_DELAYS_DURATION = "delay_duration";
	public static final String C_DELAYS_DURATION_ID = "delay_duration_id";

	// ------------ Fields - Bags -------------
	public static final String C_BAG_ID = "_id"; // Consignment number
													// (PK)

	// ------------ Fields - Contacts -------------
	public static final String C_CONTACTS_ID = "_id"; // Primary key
	public static final String C_CONTACTS_NAME = "contact_name";
	public static final String C_CONTACTS_NUMBER = "contact_number";
	public static final String C_CONTACTS_BAG_ID = "contact_bagid"; // Foreign key. linking contact
																	// to bag

	/*
	 * Has bag been scanned? used to move consignments to bottom of list as they
	 * are scanned.
	 */
	public static final String C_BAG_SCANNED = "bag_scanned";

	public static final String C_BAG_DEST_HUBNAME = "bag_hubname";
	public static final String C_BAG_DEST_HUBCODE = "bag_hubcode";
	public static final String C_BAG_DEST_ADDRESS = "bag_destaddress";
	public static final String C_BAG_DEST_SUBURB = "bag_suburb";
	public static final String C_BAG_DEST_TOWN = "bag_town";
	public static final String C_BAG_DEST_CONTACT = "bag_contact";
	public static final String C_BAG_DEST_LAT = "bag_lat";
	public static final String C_BAG_DEST_LONG = "bag_long";
	public static final String C_BAG_BARCODE = "bag_barcode";
	public static final String C_BAG_STOPID = "bag_stopid";

	/*
	 * ID of driver this bag belongs to
	 */
	public static final String C_BAG_DRIVER_ID = "bag_driver_id";

	/*
	 * Is bag assigned?
	 */
	public static final String C_BAG_ASSIGNED = "bag_assigned";

	/*
	 * Completed, TO DO, or unsuccessful
	 */
	public static final String C_BAG_STATUS = "bag_status";

	public static final String C_BAG_STATUS_REASON = "bag_status_reason";
	/*
	 * Date that a status update was sent
	 */
	public static final String C_BAG_SUBMISSION_DATE = "bag_submission_date";

	/*
	 * Creation date/time
	 */
	public static final String C_BAG_CREATION_TIME = "bag_creation_time";

	public static final String C_BAG_NUM_ITEMS = "bag_number_items";

	// --------- Fields - Waybills ----------

	public static final String C_WAYBILL_ID = "_id"; // ID PK
	public static final String C_WAYBILL_BAG_ID = "bag_id"; // FK
	public static final String C_WAYBILL_BARCODE = "waybill_barcode"; // barcode
	public static final String C_WAYBILL_SCANNED = "waybill_scanned"; // ID PK

	// Waybill dimensions (volumetrics)
	public static final String C_WAYBILL_DIMEN = "waybill_dimen";

	// Mass X gravitational force
	public static final String C_WAYBILL_WEIGHT = "waybill_weight";

	// Parcel's destination
	public static final String C_WAYBILL_DEST_ADDRESS = "waybill_destination_address";
	public static final String C_WAYBILL_DEST_SUBURB = "waybill_destination_suburb";
	public static final String C_WAYBILL_DEST_TOWN = "waybill_destination_town";
	public static final String C_WAYBILL_DEST_LAT = "waybill_destination_lat";
	public static final String C_WAYBILL_DEST_LONG = "waybill_destination_long";

	// Customer
	public static final String C_WAYBILL_CUSTOMER_NAME = "waybill_customer_name";
	public static final String C_WAYBILL_CUSTOMER_ID = "waybill_customer_id";
	public static final String C_WAYBILL_CUSTOMER_CONTACT1 = "waybill_customer_contact1";
	public static final String C_WAYBILL_CUSTOMER_CONTACT2 = "waybill_customer_contact2";
	public static final String C_WAYBILL_CUSTOMER_EMAIL = "waybill_customer_email";

	// Recipient's tel number
	public static final String C_WAYBILL_TEL = "waybill_tel";

	// number of items in waybill. to be passed to ScanSimpleCursorAdapter
	public static final String C_WAYBILL_PARCELCOUNT = "waybill_parcelcount";

	// X of whatever
	public static final String C_wAYBILL_PARCEL_SEQUENCE = "waybill_parcel_seq";

	// ------------ Fields - Failed handover reasons -------------
	public static final String C_FAILED_HANDOVER_REASONS_ID = "failed_handover_reasons_id";
	public static final String C_FAILED_HANDOVER_REASONS_NAME = "failed_handover_reasons_name";

	// ------------ Fields - ComLog -------------
	public static final String C_COMLOG_ID = "_id";
	public static final String C_COMLOG_TIMESTAMP = "comlog_timestamp";
	public static final String C_COMLOG_USER = "comlog_user";
	public static final String C_COMLOG_NOTE = "comlog_note";
	public static final String C_COMLOG_BAGID = "comlog_bag_id";

	// ------------ Fields - Partial delivery reasons -------------
	public static final String C_PARTIAL_DELIVERY_REASONS_ID = "partial_delvery_reasons_id";
	public static final String C_PARTIAL_DELIVERY_REASONS_NAME = "partial_delivery_reasons_name";

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
			// Log.d(TAG, CREATE_TABLE_DRIVERS);

			final String CREATE_TABLE_MANAGERS = "CREATE TABLE " + TABLE_MANAGERS + "("
					+ C_MANAGER_ID + " INTEGER PRIMARY KEY," + " TEXT," + C_MANAGER_NAME + " TEXT)";
			createTable(db, TABLE_MANAGERS, CREATE_TABLE_MANAGERS);

			// Log.d(TAG, CREATE_TABLE_MANAGERS);

			final String CREATE_TABLE_BAGS = "CREATE TABLE " + TABLE_BAGS + "(" + C_BAG_ID
					+ " TEXT PRIMARY KEY," + C_BAG_SCANNED + " INTEGER," + C_BAG_ASSIGNED
					+ " TEXT," + C_BAG_BARCODE + " TEXT," + C_BAG_NUM_ITEMS + " INTEGER,"
					+ C_BAG_DRIVER_ID + " TEXT," + C_BAG_CREATION_TIME + " TEXT,"
					+ C_BAG_DEST_SUBURB + " TEXT," + C_BAG_DEST_LONG + " TEXT,"
					+ C_BAG_STATUS_REASON + " TEXT," + C_BAG_DEST_LAT + " TEXT," + C_BAG_STOPID
					+ " TEXT," + C_BAG_DEST_HUBNAME + " TEXT," + C_BAG_SUBMISSION_DATE
					+ " DATETIME," + C_BAG_DEST_HUBCODE + " TEXT," + C_BAG_DEST_CONTACT + " TEXT,"
					+ C_BAG_STATUS + " TEXT," + C_BAG_DEST_ADDRESS + " TEXT," + "FOREIGN KEY("
					+ C_BAG_DRIVER_ID + ") REFERENCES " + TABLE_DRIVERS + "(" + C_DRIVER_ID + "))";
			createTable(db, TABLE_BAGS, CREATE_TABLE_BAGS);
			// Log.d(TAG, CREATE_TABLE_BAGS);

			final String CREATE_TABLE_BAGS_TRAINING = "CREATE TABLE " + TABLE_BAGS_TRAINING + "("
					+ C_BAG_ID + " TEXT PRIMARY KEY," + C_BAG_SCANNED + " INTEGER,"
					+ C_BAG_ASSIGNED + " TEXT," + C_BAG_BARCODE + " TEXT," + C_BAG_NUM_ITEMS
					+ " INTEGER," + C_BAG_DRIVER_ID + " TEXT," + C_BAG_CREATION_TIME + " TEXT,"
					+ C_BAG_STATUS_REASON + " TEXT," + C_BAG_SUBMISSION_DATE + " DATETIME,"
					+ C_BAG_DEST_SUBURB + " TEXT," + C_BAG_DEST_LONG + " TEXT," + C_BAG_STOPID
					+ " TEXT," + C_BAG_DEST_LAT + " TEXT," + C_BAG_DEST_HUBNAME + " TEXT,"
					+ C_BAG_DEST_HUBCODE + " TEXT," + C_BAG_DEST_CONTACT + " TEXT," + C_BAG_STATUS
					+ " TEXT," + C_BAG_DEST_ADDRESS + " TEXT," + "FOREIGN KEY(" + C_BAG_DRIVER_ID
					+ ") REFERENCES " + TABLE_DRIVERS + "(" + C_DRIVER_ID + "))";
			createTable(db, TABLE_BAGS_TRAINING, CREATE_TABLE_BAGS_TRAINING);
			// Log.d(TAG, CREATE_TABLE_BAGS);

			final String CREATE_TABLE_WAYBILL = "CREATE TABLE " + TABLE_WAYBILLS + "("
					+ C_WAYBILL_ID + " INTEGER PRIMARY KEY," + C_WAYBILL_SCANNED + " INTEGER,"
					+ C_WAYBILL_BAG_ID + " TEXT," + C_WAYBILL_WEIGHT + " TEXT,"
					+ C_WAYBILL_DEST_LONG + " TEXT," + C_WAYBILL_DEST_LAT + " TEXT,"
					+ C_WAYBILL_BARCODE + " TEXT," + C_WAYBILL_DEST_SUBURB + " TEXT,"
					+ C_WAYBILL_DEST_ADDRESS + " TEXT," + C_WAYBILL_TEL + " TEXT,"
					+ C_wAYBILL_PARCEL_SEQUENCE + " TEXT," + C_WAYBILL_DIMEN + " TEXT,"
					+ C_WAYBILL_PARCELCOUNT + " INTEGER," + C_WAYBILL_CUSTOMER_ID + " TEXT,"
					+ C_WAYBILL_CUSTOMER_CONTACT2 + " TEXT," + C_WAYBILL_CUSTOMER_CONTACT1
					+ " TEXT," + C_WAYBILL_CUSTOMER_NAME + " TEXT," + C_WAYBILL_CUSTOMER_EMAIL
					+ " TEXT," + "FOREIGN KEY(" + C_WAYBILL_BAG_ID + ") REFERENCES " + TABLE_BAGS
					+ "(" + C_BAG_ID + "))";
			createTable(db, TABLE_WAYBILLS, CREATE_TABLE_WAYBILL);
			// Log.d(TAG, CREATE_TABLE_WAYBILL);

			final String CREATE_TABLE_WAYBILL_TRAINING = "CREATE TABLE " + TABLE_WAYBILLS_TRAINING
					+ "(" + C_WAYBILL_ID + " INTEGER PRIMARY KEY," + C_WAYBILL_SCANNED
					+ " INTEGER," + C_WAYBILL_BAG_ID + " TEXT," + C_WAYBILL_WEIGHT + " TEXT,"
					+ C_WAYBILL_DEST_LONG + " TEXT," + C_WAYBILL_DEST_LAT + " TEXT,"
					+ C_WAYBILL_DEST_TOWN + " TEXT," + C_WAYBILL_BARCODE + " TEXT,"
					+ C_WAYBILL_DEST_SUBURB + " TEXT," + C_WAYBILL_DEST_ADDRESS + " TEXT,"
					+ C_WAYBILL_TEL + " TEXT," + C_wAYBILL_PARCEL_SEQUENCE + " TEXT,"
					+ C_WAYBILL_DIMEN + " TEXT," + C_WAYBILL_PARCELCOUNT + " INTEGER,"
					+ C_WAYBILL_CUSTOMER_ID + " TEXT," + C_WAYBILL_CUSTOMER_CONTACT2 + " TEXT,"
					+ C_WAYBILL_CUSTOMER_CONTACT1 + " TEXT," + C_WAYBILL_CUSTOMER_NAME + " TEXT,"
					+ C_WAYBILL_CUSTOMER_EMAIL + " TEXT," + "FOREIGN KEY(" + C_WAYBILL_BAG_ID
					+ ") REFERENCES " + TABLE_BAGS + "(" + C_BAG_ID + "))";
			createTable(db, TABLE_WAYBILLS, CREATE_TABLE_WAYBILL_TRAINING);

			final String CREATE_TABLE_DELAYS = "CREATE TABLE " + TABLE_DELAYS + "(" + C_DELAYS_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT," + C_DELAYS_REASON_ID + " TEXT UNIQUE,"
					+ C_DELAYS_REASON + " TEXT)";
			createTable(db, TABLE_DELAYS, CREATE_TABLE_DELAYS);

			final String CREATE_TABLE_DELAYS_DURATIONS = "CREATE TABLE " + TABLE_DELAYS_DURATIONS
					+ "(" + C_DELAYS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ C_DELAYS_DURATION_ID + " TEXT UNIQUE," + C_DELAYS_DURATION + " TEXT,"
					+ C_DELAYS_REASON_ID + " TEXT," + "FOREIGN KEY(" + C_DELAYS_DURATION_ID
					+ ") REFERENCES " + TABLE_DELAYS + "(" + C_DELAYS_REASON_ID + "))";
			createTable(db, TABLE_DELAYS_DURATIONS, CREATE_TABLE_DELAYS_DURATIONS);

			// Log.d(TAG, CREATE_TABLE_DELAYS);

			final String CREATE_TABLE_CONTACTS = "CREATE TABLE " + TABLE_CONTACTS + "("
					+ C_CONTACTS_ID + " INTEGER PRIMARY KEY," + C_CONTACTS_NAME + " TEXT,"
					+ C_CONTACTS_BAG_ID + " TEXT," + C_CONTACTS_NUMBER + " TEXT," + "FOREIGN KEY("
					+ C_CONTACTS_BAG_ID + ") REFERENCES " + TABLE_BAGS + "(" + C_BAG_ID + "))";
			createTable(db, TABLE_CONTACTS, CREATE_TABLE_CONTACTS);
			// Log.d(TAG, CREATE_TABLE_CONTACTS);

			final String CREATE_TABLE_FAILED_HANDOVER_REASONS = "CREATE TABLE "
					+ TABLE_FAILED_HANDOVER_REASONS + "(" + C_FAILED_HANDOVER_REASONS_ID
					+ " INTEGER PRIMARY KEY," + C_FAILED_HANDOVER_REASONS_NAME + " TEXT)";
			createTable(db, TABLE_FAILED_HANDOVER_REASONS, CREATE_TABLE_FAILED_HANDOVER_REASONS);

			final String CREATE_TABLE_FAILED_HANDOVER_REASONS_TRAINING = "CREATE TABLE "
					+ TABLE_FAILED_HANDOVER_REASONS_TRAINING + "(" + C_FAILED_HANDOVER_REASONS_ID
					+ " INTEGER PRIMARY KEY," + C_FAILED_HANDOVER_REASONS_NAME + " TEXT)";
			createTable(db, TABLE_FAILED_HANDOVER_REASONS_TRAINING,
					CREATE_TABLE_FAILED_HANDOVER_REASONS_TRAINING);

			// Log.d(TAG, CREATE_TABLE_FAILED_HANDOVER_REASONS);

			final String CREATE_TABLE_PARTIAL_DELIVERY_REASONS = "CREATE TABLE "
					+ TABLE_PARTIAL_DELIVERY_REASONS + "(" + C_PARTIAL_DELIVERY_REASONS_ID
					+ " INTEGER PRIMARY KEY," + C_PARTIAL_DELIVERY_REASONS_NAME + " TEXT)";
			createTable(db, TABLE_PARTIAL_DELIVERY_REASONS, CREATE_TABLE_PARTIAL_DELIVERY_REASONS);
			// Log.d(TAG, CREATE_TABLE_PARTIAL_DELIVERY_REASONS);

			final String CREATE_TABLE_CALLQUEUE = "CREATE TABLE " + TABLE_CALLQUEUE + "("
					+ C_CALLQUEUE_ID + " INTEGER PRIMARY KEY," + C_CALLQUEUE_JSON + " TEXT,"
					+ C_CALLQUEUE_URL + " TEXT)";
			createTable(db, TABLE_CALLQUEUE, CREATE_TABLE_CALLQUEUE);
			// Log.d(TAG, CREATE_TABLE_CALLQUEUE);

			final String CREATE_TABLE_COMLOG = "CREATE TABLE " + TABLE_COMLOG + "(" + C_COMLOG_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT," + C_COMLOG_TIMESTAMP + " TEXT,"
					+ C_COMLOG_NOTE + " TEXT," + C_COMLOG_BAGID + " TEXT," + C_COMLOG_USER
					+ " TEXT," + "FOREIGN KEY(" + C_COMLOG_BAGID + ") REFERENCES " + TABLE_BAGS
					+ "(" + C_BAG_ID + "))";
			createTable(db, TABLE_COMLOG, CREATE_TABLE_COMLOG);

			db.setTransactionSuccessful();
		}
		catch (SQLiteException e)
		{ // TODO Auto-generated catch
			// block
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
		Log.e(TAG, "created SQL table: " + table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRIVERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MANAGERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BAGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BAGS_TRAINING);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WAYBILLS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WAYBILLS_TRAINING);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMLOG);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALLQUEUE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELAYS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELAYS_DURATIONS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAILED_HANDOVER_REASONS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAILED_HANDOVER_REASONS_TRAINING);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTIAL_DELIVERY_REASONS);
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
	 * Add a new consignment to the database.
	 * 
	 * Params: Consignment object.
	 * 
	 * Returns boolean of whether the database transaction was successful
	 */
	/*public boolean addBag(Bag bag)
	{
		ContentValues values = new ContentValues();

		values.put(C_BAG_ID, bag.getBagNumber()); // PK
		values.put(C_BAG_DEST_ADDRESS, bag.getDestinationAddress());
		values.put(C_BAG_DEST_CONTACT, bag.getDestinationContact());
		values.put(C_BAG_DEST_HUBCODE, bag.getDestinationHubCode());
		values.put(C_BAG_DEST_HUBNAME, bag.getDestinationHubName());
		values.put(C_BAG_DEST_LAT, bag.getDestinationLat());
		values.put(C_BAG_DEST_LONG, bag.getDestinationLong());
		values.put(C_BAG_DEST_SUBURB, bag.getDestinationSuburb());
		// values.put(C_BAG_DEST_TOWN, bag.getDestinationTown());
		values.put(C_BAG_BARCODE, bag.getBarcode());
		values.put(C_BAG_ASSIGNED, convertBoolToInt(bag.getAssigned()));
		values.put(C_BAG_SCANNED, convertBoolToInt(bag.getScanned()));
		values.put(C_BAG_CREATION_TIME, bag.getCreationTime());
		values.put(C_BAG_NUM_ITEMS, bag.getNumberItems());
		values.put(C_BAG_DRIVER_ID, bag.getDriverId());
		values.put(C_BAG_STATUS, bag.getStatus());
		values.put(C_BAG_STOPID, bag.getStopId());
		values.put(C_BAG_STATUS_REASON, bag.getStatusReason());

		if (bag.getSubmissionDate() != null)
		{
			values.put(C_BAG_SUBMISSION_DATE, bag.getSubmissionDate().getTime());
		}
		return addRow(TABLE_BAGS, values);
	}   */

	/**
	 * Add a new item to the database which corrosponds to a consignment.
	 * 
	 * Params: Item item.
	 * 
	 * Returns boolean of whether the database transaction was successful
	 */
	public boolean addWaybill(Waybill item)
	{
		ContentValues values = new ContentValues();

		values.put(C_WAYBILL_ID, item.getWaybill()); // PK
		values.put(C_WAYBILL_PARCELCOUNT, item.getParcelCount());
		values.put(C_wAYBILL_PARCEL_SEQUENCE, item.getParcelCount());
		values.put(C_WAYBILL_DIMEN, item.getDimensions());
		values.put(C_WAYBILL_CUSTOMER_CONTACT1, item.getCustomerContact1());
		values.put(C_WAYBILL_CUSTOMER_CONTACT2, item.getCustomerContact2());
		values.put(C_WAYBILL_CUSTOMER_NAME, item.getCustomerName());
		values.put(C_WAYBILL_CUSTOMER_ID, item.getCustomerID());
		values.put(C_WAYBILL_CUSTOMER_EMAIL, item.getEmail());
		values.put(C_WAYBILL_BARCODE, item.getBarcode());
		values.put(C_WAYBILL_WEIGHT, item.getWeight());
		values.put(C_WAYBILL_BAG_ID, item.getBagNumber()); // FK

		return addRow(TABLE_WAYBILLS, values);
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
				return db
						.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE) >= 0;
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
	 * Sets the value for whether the particular consingment (number) has been
	 * scanned.
	 * 
	 */
	public void setScanned(String barcode, boolean scanned)
	{
		SQLiteDatabase db = null;
		try
		{

			db = this.getWritableDatabase(); // Open db

			ContentValues values = new ContentValues();
			values.put(C_BAG_SCANNED, convertBoolToInt(scanned));

			db.update(TABLE_BAGS, values, C_BAG_BARCODE + "='" + barcode + "'", null);

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
	}

	/**
	 * Changes the delivery status of a bag.
	 * 
	 */
	/*public int setDeliveryStatus(String bagid, String status, String reason)
	{
		int no_rows = 0; // Number of rows affected

		SQLiteDatabase db = null;
		try
		{

			db = this.getWritableDatabase(); // Open db
			// db = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);

			ContentValues values = new ContentValues();
			values.put(C_BAG_STATUS, status);
			values.put(C_BAG_SUBMISSION_DATE, System.currentTimeMillis());
			values.put(C_BAG_STATUS_REASON, reason);

			training_run = prefs.getBoolean(VariableManager.PREF_TRAINING_MODE, false);

			if (training_run)
			{
				no_rows = db.update(TABLE_BAGS_TRAINING, values, C_BAG_ID + "='" + bagid + "'",
						null);
			}
			else
			{
				no_rows = db.update(TABLE_BAGS, values, C_BAG_ID + "='" + bagid + "'", null);
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
		return no_rows;
	} */

	/**
	 * Changes the delivery status of a bag.
	 * 
	 * @param bagid
	 *            Bag ID
	 * @param status
	 *            Delivery status of bag. (TODO, failed, partial, etc...)
	 * @return Number of rows affected.
	 */
	/*public int setDeliveryStatus(String bagid, String status)
	{
		return setDeliveryStatus(bagid, status, "");
	} */

	/**
	 * Sets the value for whether the particular waybill has been
	 * scanned.
	 * 
	 * @param cons_no
	 * @param scanned
	 */
	/*public void setWaybillScanned(String waybill_no, boolean scanned)
	{
		SQLiteDatabase db = null;
		try
		{

			db = this.getWritableDatabase(); // Open db

			ContentValues values = new ContentValues();
			values.put(C_WAYBILL_SCANNED, convertBoolToInt(scanned));

			db.update(TABLE_WAYBILLS, values, C_WAYBILL_BARCODE + "='" + waybill_no + "'", null);

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
	}    */

	/**
	 * Sets the value for whether a consignment has been scanned for ALL
	 * consignments.
	 * 
	 */
	/*public void setScannedAll(boolean scanned)
	{
		SQLiteDatabase db = null;
		try
		{

			db = this.getWritableDatabase(); // Open db

			ContentValues values = new ContentValues();
			values.put(C_BAG_SCANNED, convertBoolToInt(scanned));

			db.update(TABLE_BAGS, values, null, null);

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
	}    */

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
	 * Get list of all consignments
	 * 
	 * @return ArrayList<Bag>
	 */
	/*public ArrayList<Bag> getBags(String driver_id)
	{
		SQLiteDatabase db = null;
		try
		{

			db = this.getReadableDatabase(); // Open db

			ArrayList<Bag> bags = null;
			String sql = "SELECT * FROM " + TABLE_BAGS + " WHERE " + C_BAG_DRIVER_ID + " LIKE '"
					+ driver_id + "'";
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst())
			{
				bags = new ArrayList<Bag>();

				while (!cursor.isAfterLast())
				{
					Bag bag = createBagFromCursor(cursor);

					bags.add(bag);
					cursor.moveToNext();
				}
			}

			return bags;
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

	
	/*private Bag createBagFromCursor(Cursor cursor)
	{
		Bag bag = new Bag(cursor.getString(cursor.getColumnIndex(C_BAG_ID)));

		bag.setDestinationAddress(cursor.getString(cursor
				.getColumnIndex(C_BAG_DEST_ADDRESS)));
		bag.setAssigned(convertIntToBool(cursor.getInt(cursor
				.getColumnIndex(C_BAG_ASSIGNED))));
		bag.setCreationTime(cursor.getString(cursor.getColumnIndex(C_BAG_CREATION_TIME)));
		bag.setDriverId(cursor.getString(cursor.getColumnIndex(C_BAG_DRIVER_ID)));
		bag.setNumberItems(Integer.parseInt(cursor.getString(cursor
				.getColumnIndex(C_BAG_NUM_ITEMS))));
		bag.setBarcode(cursor.getString(cursor.getColumnIndex(C_BAG_BARCODE)));
		bag.setDestinationContact(cursor.getString(cursor
				.getColumnIndex(C_BAG_DEST_CONTACT)));
		bag.setDestinationHubCode(cursor.getString(cursor
				.getColumnIndex(C_BAG_DEST_HUBCODE)));
		bag.setDestinationHubName(cursor.getString(cursor
				.getColumnIndex(C_BAG_DEST_HUBNAME)));
		bag.setDestinationLat(cursor.getString(cursor.getColumnIndex(C_BAG_DEST_LAT)));
		bag.setDestinationLong(cursor.getString(cursor.getColumnIndex(C_BAG_DEST_LONG)));
		bag.setDestinationSuburb(cursor.getString(cursor
				.getColumnIndex(C_BAG_DEST_SUBURB)));
		// bag.setDestinationTown(cursor.getString(cursor.getColumnIndex(C_BAG_DEST_TOWN)));
		bag.setStatus(cursor.getString(cursor.getColumnIndex(C_BAG_STATUS)));
		
		bag.setSubmissionDate(new Date(cursor.getLong(cursor.getColumnIndex(C_BAG_SUBMISSION_DATE))));
		bag.setStatusReason(cursor.getString(cursor.getColumnIndex(C_BAG_STATUS_REASON)));
		bag.setStopId(cursor.getString(cursor.getColumnIndex(C_BAG_STOPID)));
		
		return bag;
	}    */
	
	/**
	 * Return bag according to bag number
	 * Should only be one.
	 */
	/*public Bag getBag(String bag_id)
	{
		SQLiteDatabase db = null;
		Bag bag = null;
		try
		{

			db = this.getReadableDatabase(); // Open db

			training_run = prefs.getBoolean(VariableManager.PREF_TRAINING_MODE, false);
			String sql = "";

			// ArrayList<Bag> bags = null;

			if (training_run)
			{
				sql = "SELECT * FROM " + TABLE_BAGS_TRAINING + " WHERE " + C_BAG_ID + " LIKE '"
						+ bag_id + "'";
			}
			else
			{
				sql = "SELECT * FROM " + TABLE_BAGS + " WHERE " + C_BAG_ID + " LIKE '" + bag_id
						+ "'";
			}
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst())
			{
				// bags = new ArrayList<Bag>();

				if (!cursor.isAfterLast())
				{
					bag = createBagFromCursor(cursor);
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
		return bag;
	}    */

	/**
	 * Return Waybill according to bag ID.
	 * 
	 * @param bag_id
	 * @return
	 */
	public Waybill getWaybill(String bag_id)
	{
		SQLiteDatabase db = null;
		Waybill waybill = null;
		try
		{

			db = this.getReadableDatabase(); // Open db

			// ArrayList<Bag> bags = null;
			String sql = "SELECT * FROM " + TABLE_WAYBILLS + " WHERE " + C_WAYBILL_BAG_ID
					+ " LIKE '" + bag_id + "'";
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst())
			{
				// bags = new ArrayList<Bag>();

				if (!cursor.isAfterLast())
				{
					waybill = new Waybill(cursor.getString(cursor.getColumnIndex(C_WAYBILL_ID)),
							cursor.getString(cursor.getColumnIndex(C_WAYBILL_BAG_ID)));

					waybill.setBarcode(cursor.getString(cursor.getColumnIndex(C_WAYBILL_BARCODE)));
					waybill.setCustomerContact1(cursor.getString(cursor
							.getColumnIndex(C_WAYBILL_CUSTOMER_CONTACT1)));
					waybill.setCustomerContact2(cursor.getString(cursor
							.getColumnIndex(C_WAYBILL_CUSTOMER_CONTACT2)));
					waybill.setCustomerID(cursor.getString(cursor
							.getColumnIndex(C_WAYBILL_CUSTOMER_ID)));
					waybill.setCustomerName(cursor.getString(cursor
							.getColumnIndex(C_WAYBILL_CUSTOMER_NAME)));
					waybill.setEmail(cursor.getString(cursor
							.getColumnIndex(C_WAYBILL_CUSTOMER_EMAIL)));
					waybill.setDeliveryAddress(cursor.getString(cursor
							.getColumnIndex(C_WAYBILL_DEST_ADDRESS)));
					waybill.setDeliveryLat(cursor.getString(cursor
							.getColumnIndex(C_WAYBILL_DEST_LAT)));
					waybill.setDeliveryLong(cursor.getString(cursor
							.getColumnIndex(C_WAYBILL_DEST_LONG)));
					waybill.setDeliverySuburb(cursor.getString(cursor
							.getColumnIndex(C_WAYBILL_DEST_SUBURB)));
					// waybill.setDeliveryTown(cursor.getString(cursor
					// .getColumnIndex(C_WAYBILL_DEST_TOWN)));
					waybill.setDimensions(cursor.getString(cursor.getColumnIndex(C_WAYBILL_DIMEN)));
					waybill.setWeight(cursor.getString(cursor.getColumnIndex(C_WAYBILL_WEIGHT)));
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
		return waybill;
	}

	/**
	 * Returns list of waybill, per bag, in DeliveryHandoverDataObject format. Used in successful
	 * handover screen.
	 * 
	 * @param bag_id
	 * @return
	 */
	/*
    public ArrayList<DeliveryHandoverDataObject> getWaybillsForHandover(String bag_id)
	{
		SQLiteDatabase db = null;
		try
		{

			db = this.getReadableDatabase(); // Open db

			ArrayList<DeliveryHandoverDataObject> bags = null;

			training_run = prefs.getBoolean(VariableManager.PREF_TRAINING_MODE, false);
			String sql = "";

			if (training_run)
			{
				sql = "SELECT * FROM " + TABLE_WAYBILLS_TRAINING + " WHERE " + C_WAYBILL_BAG_ID
						+ " LIKE '" + bag_id + "'";
			}
			else
			{
				sql = "SELECT * FROM " + TABLE_WAYBILLS + " WHERE " + C_WAYBILL_BAG_ID + " LIKE '"
						+ bag_id + "'";
			}
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst())
			{
				bags = new ArrayList<DeliveryHandoverDataObject>();

				while (!cursor.isAfterLast())
				{
					boolean bool_scanned = false;

					if (cursor.getInt(cursor.getColumnIndex(C_WAYBILL_SCANNED)) == 1)
					{
						bool_scanned = true;
					}

					Log.d(TAG, "DBMANGER: " + bool_scanned);

					DeliveryHandoverDataObject parcel = new DeliveryHandoverDataObject(
							cursor.getString(cursor.getColumnIndex(C_WAYBILL_ID)), bool_scanned);

					parcel.setBarcode(cursor.getString(cursor.getColumnIndex(C_WAYBILL_BARCODE)));

					bags.add(parcel);
					cursor.moveToNext();
				}
			}

			return bags;
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
        return null;
	} */

	/**
	 * Return contact details linked to the specified bag ID.
	 * 
	 * @param bag_id
	 * @return
	 */
	public ArrayList<DialogDataObject> getContacts(String bag_id)
	{
		training_run = prefs.getBoolean(VariableManager.PREF_TRAINING_MODE, false);

		if (training_run)
		{
			ArrayList<DialogDataObject> contacts = new ArrayList<DialogDataObject>();
			// Log.d(TAG, "zeus adding fake contacts");
			contacts.add(new DialogDataObject("Branch Manager", "0822231234"));
			contacts.add(new DialogDataObject("Dispatch Manager", "083421888"));
			contacts.add(new DialogDataObject("Supervisor", "076556445"));

			return contacts;
		}
		else
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
	 * Return number of consignments belonging to a driver
	 * 
	 * @return count
	 */
	public int getBagCount(String driver_id)
	{
		String countQuery = "SELECT  * FROM " + TABLE_BAGS + " WHERE " + C_BAG_DRIVER_ID
				+ " like '" + driver_id + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int cnt = cursor.getCount();
		cursor.close();
		return cnt;
	}

	/**
	 * Return ID of bag at the specified row index.
	 * 
	 * @return Barcode at specified row
	 */
	public String getBarcodeAtRow(String driver_id, int row)
	{
		String countQuery = "SELECT  * FROM " + TABLE_BAGS + " WHERE " + C_BAG_DRIVER_ID
				+ " LIKE '" + driver_id + "'" + " ORDER BY " + DbHandler.C_BAG_SCANNED + " ASC,"
				+ DbHandler.C_BAG_ID + " ASC" + " LIMIT 1 OFFSET " + row;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		String barcode = "";
		if (cursor != null && cursor.moveToFirst())
		{
			barcode = cursor.getString(cursor.getColumnIndex(C_BAG_BARCODE));
		}
		// Log.d(TAG, "zorro cursor bagid " + barcode);
		cursor.close();
		return barcode;
	}

	/**
	 * Get list of consignments not yet scanned
	 * 
	 * @return String
	 */
	public String getConsignmentsNotScanned(String driver_id)
	{
		SQLiteDatabase db = null;
		String list = "";
		try
		{

			db = this.getReadableDatabase(); // Open db

			String sql = "SELECT * FROM " + TABLE_BAGS + " WHERE " + C_BAG_SCANNED + " <> 1 AND "
					+ C_BAG_DRIVER_ID + " LIKE '" + driver_id + "'";
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst())
			{
				while (!cursor.isAfterLast())
				{

					// cursor.getString(cursor.getColumnIndex(C_BAG_ID)));

					// concat string of bag numbers with newline to be displayed in list format
					list = list + cursor.getString(cursor.getColumnIndex(C_BAG_BARCODE)) + "\n";
					cursor.moveToNext();
				}
			}

			return list;
		}
		catch (SQLiteException e)
		{ // TODO Auto-generated catch
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			return list;
		}
		catch (IllegalStateException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			return list;
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
	 * Return bags per delivery status
	 * 
	 * @param driver_id
	 * @param status
	 * @return
	 */
	/*public ArrayList<Bag> getBagsByStatus(String driver_id, String status)
	{
		SQLiteDatabase db = null;
		ArrayList<Bag> list = new ArrayList<Bag>();
		try
		{
			db = this.getReadableDatabase(); // Open db

			// final String driverid = prefs.getString(VariableManager.EXTRA_DRIVER_ID, null);
			training_run = prefs.getBoolean(VariableManager.PREF_TRAINING_MODE, false);

			String sql = "";
			if (training_run)
			{
				// Log.d(TAG, "Loading training bags");
				sql = "SELECT * FROM " + TABLE_BAGS_TRAINING + " WHERE " + C_BAG_STATUS + " LIKE '"
						+ status + "'";
			}
			else
			{
				// Log.d(TAG, "Loading real bags");
				sql = "SELECT * FROM " + TABLE_BAGS + " WHERE " + C_BAG_STATUS + " LIKE '" + status
						+ "' AND " + C_BAG_DRIVER_ID + " LIKE '" + driver_id + "'" + " AND "
						+ C_BAG_SCANNED + " LIKE '" + convertBoolToInt(true) + "'";
			}
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst())
			{
				while (!cursor.isAfterLast())
				{
					Bag bag = createBagFromCursor(cursor);
					
					list.add(bag);
					cursor.moveToNext();
				}
			}

			return list;
		}
		catch (SQLiteException e)
		{ // TODO Auto-generated catch
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			return list;
		}
		catch (IllegalStateException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			return list;
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
	}  */

	/**
	 * Returns coords of all bags
	 * 
	 * @param driver_id
	 * @return ArrayList of HashMaps
	 */
	/*public ArrayList<HashMap<String, String>> getBagCoords(String driver_id)
	{
		SQLiteDatabase db = null;
		try
		{

			db = this.getReadableDatabase(); // Open db

			ArrayList<HashMap<String, String>> bags = null;

			SharedPreferences prefs = context.getSharedPreferences(VariableManager.PREF,
					Context.MODE_PRIVATE);

			// final String driverid = prefs.getString(VariableManager.EXTRA_DRIVER_ID, null);
			final boolean training_mode = prefs.getBoolean(VariableManager.PREF_TRAINING_MODE,
					false);

			String sql = "";
			if (training_mode)
			{
				sql = "SELECT * FROM " + TABLE_BAGS_TRAINING;
			}
			else
			{
				sql = "SELECT * FROM " + TABLE_BAGS + " WHERE " + C_BAG_DRIVER_ID + " LIKE '"
						+ driver_id + "'";
			}

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst())
			{
				bags = new ArrayList<HashMap<String, String>>();

				while (!cursor.isAfterLast())
				{
					String address = cursor.getString(cursor.getColumnIndex(C_BAG_DEST_ADDRESS));

					String hubname = cursor.getString(cursor.getColumnIndex(C_BAG_DEST_HUBNAME));

					String lat = cursor.getString(cursor.getColumnIndex(C_BAG_DEST_LAT));

					String lon = cursor.getString(cursor.getColumnIndex(C_BAG_DEST_LONG));

					HashMap<String, String> bag = new HashMap<String, String>();
					bag.put(VariableManager.EXTRA_BAG_ADDRESS, address);
					bag.put(VariableManager.EXTRA_BAG_HUBNAME, hubname);
					bag.put(VariableManager.EXTRA_BAG_LAT, lat);
					bag.put(VariableManager.EXTRA_BAG_LON, lon);

					bags.add(bag);
					cursor.moveToNext();
				}
			}

			return bags;
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
	}   */

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
	 * Returns list of reasons for milkrun delay
	 * 
	 * @return
	 */
	/*public ArrayList<DialogDataObject> getFailedHandoverReasons()
	{
		SQLiteDatabase db = null;
		try
		{
			db = this.getReadableDatabase(); // Open db

			ArrayList<DialogDataObject> reasons = null;

			SharedPreferences prefs = context.getSharedPreferences(VariableManager.PREF,
					Context.MODE_PRIVATE);

			// final String driverid = prefs.getString(VariableManager.EXTRA_DRIVER_ID, null);
			final boolean training_mode = prefs.getBoolean(VariableManager.PREF_TRAINING_MODE,
					false);

			String sql = "";
			if (training_mode)
			{
				sql = "SELECT * FROM " + TABLE_FAILED_HANDOVER_REASONS_TRAINING;
			}
			else
			{
				sql = "SELECT * FROM " + TABLE_FAILED_HANDOVER_REASONS;
			}

			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst())
			{
				reasons = new ArrayList<DialogDataObject>();

				while (!cursor.isAfterLast())
				{
					String reason_name = cursor.getString(cursor
							.getColumnIndex(C_FAILED_HANDOVER_REASONS_NAME));
					String reason_id = cursor.getString(cursor
							.getColumnIndex(C_FAILED_HANDOVER_REASONS_ID));

					DialogDataObject dialog_data_object = new DialogDataObject(reason_name,
							reason_id);

					reasons.add(dialog_data_object);

					cursor.moveToNext();
				}
			}

			return reasons;
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
	 * Return list of reasons for partial delivery
	 * 
	 * @return ArrayList<DialogDataObject>
	 */
	/*public ArrayList<DialogDataObject> getPartialDeliveryReasons()
	{
		SQLiteDatabase db = null;
		try
		{
			db = this.getReadableDatabase(); // Open db

			ArrayList<DialogDataObject> reasons = null;
			String sql = "SELECT * FROM " + TABLE_PARTIAL_DELIVERY_REASONS;
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst())
			{
				reasons = new ArrayList<DialogDataObject>();

				while (!cursor.isAfterLast())
				{
					String reason_name = cursor.getString(cursor
							.getColumnIndex(C_PARTIAL_DELIVERY_REASONS_NAME));
					String reason_id = cursor.getString(cursor
							.getColumnIndex(C_PARTIAL_DELIVERY_REASONS_ID));

					DialogDataObject dialog_data_object = new DialogDataObject(reason_name,
							reason_id);

					reasons.add(dialog_data_object);

					cursor.moveToNext();
				}
			}

			return reasons;
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
	} */

	/**
	 * Return durations of delay reason
	 * 
	 * @param reason_id
	 * @return
	 */
	/*public ArrayList<DialogDataObject> getMilkrunDelayDurations(String reason_id)
	{
		training_run = prefs.getBoolean(VariableManager.PREF_TRAINING_MODE, false);
		if (training_run)
		{
			ArrayList<DialogDataObject> delays = new ArrayList<DialogDataObject>();
			delays.add(new DialogDataObject("5 minutes", ""));
			delays.add(new DialogDataObject("10 minutes", ""));
			delays.add(new DialogDataObject("15 minutes", ""));
			delays.add(new DialogDataObject("20 minutes", ""));
			delays.add(new DialogDataObject("25 minutes", ""));
			delays.add(new DialogDataObject("30 minutes", ""));

			return delays;
		}
		else
		{
			SQLiteDatabase db = null;
			try
			{
				db = this.getReadableDatabase(); // Open db

				ArrayList<DialogDataObject> delays = null;
				String sql = "SELECT * FROM " + TABLE_DELAYS_DURATIONS + " WHERE "
						+ C_DELAYS_REASON_ID + " LIKE '" + reason_id + "'";
				// sql = "SELECT * FROM " + TABLE_DELAYS;// DEBUG
				Cursor cursor = db.rawQuery(sql, null);

				if (cursor != null && cursor.moveToFirst())
				{
					delays = new ArrayList<DialogDataObject>();

					while (!cursor.isAfterLast())
					{
						String duration = cursor
								.getString(cursor.getColumnIndex(C_DELAYS_DURATION));

						delays.add(new DialogDataObject(duration, ""));

						cursor.moveToNext();
					}
				}
				// Log.d(TAG, "Number of delay durations: " + cursor.getCount());
				return delays;
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
	}     */

	/**
	 * Returns list of reasons for milkrun delay
	 * 
	 * @return
	 */
	/*public ArrayList<DialogDataObject> getMilkrunDelayReasons()
	{
		training_run = prefs.getBoolean(VariableManager.PREF_TRAINING_MODE, false);
		if (training_run)
		{
			ArrayList<DialogDataObject> delays = new ArrayList<DialogDataObject>();

			DialogDataObject dialog_data_object = new DialogDataObject("Traffic", "");
			dialog_data_object.setThirdText("1");
			delays.add(dialog_data_object);

			dialog_data_object = new DialogDataObject("Breakdown", "");
			dialog_data_object.setThirdText("2");
			delays.add(dialog_data_object);

			dialog_data_object = new DialogDataObject("Can't find address", "");
			dialog_data_object.setThirdText("3");
			delays.add(dialog_data_object);

			dialog_data_object = new DialogDataObject("Held up at destination", "");
			dialog_data_object.setThirdText("4");
			delays.add(dialog_data_object);

			dialog_data_object = new DialogDataObject("Other", "");
			dialog_data_object.setThirdText("5");
			delays.add(dialog_data_object);

			return delays;
		}
		else
		{
			SQLiteDatabase db = null;
			try
			{
				db = this.getReadableDatabase(); // Open db

				ArrayList<DialogDataObject> delays = null;
				String sql = "SELECT * FROM " + TABLE_DELAYS;
				Cursor cursor = db.rawQuery(sql, null);

				if (cursor != null && cursor.moveToFirst())
				{
					delays = new ArrayList<DialogDataObject>();

					while (!cursor.isAfterLast())
					{
						String reason = cursor.getString(cursor.getColumnIndex(C_DELAYS_REASON));
						String delay_id = cursor.getString(cursor
								.getColumnIndex(C_DELAYS_REASON_ID));

						DialogDataObject dialog_data_object = new DialogDataObject(reason, "");
						dialog_data_object.setThirdText(delay_id);

						delays.add(dialog_data_object);

						cursor.moveToNext();
					}
				}
				// Log.d(TAG, "Number of delay reasons: " + delays.size());
				return delays;
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
	} */

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
	 * Returns stop id of specified bag
	 * 
	 * @param bagid
	 * @return Stop ID for specified Bag
	 */
	/*public String getStopId()
	{
		String bagid = prefs.getString(VariableManager.PREF_CURRENT_BAGID, "");
		String stopid = "";

		if (training_run)
		{
			stopid = "1";
		}
		else
		{
			String sql = "SELECT  * FROM " + TABLE_BAGS + " WHERE " + C_BAG_ID + " LIKE '" + bagid
					+ "'";
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst())
			{
				stopid = cursor.getString(cursor.getColumnIndex(C_BAG_STOPID));
			}
			// Log.d(TAG, "zorro cursor bagid " + barcode);
			cursor.close();
		}
		return stopid;
	}  */

	/**
	 * Returns list of drivers
	 * 
	 * @return
	 */
	public ArrayList<UserItem> getDrivers()
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

					UserItem person_item = new UserItem(driver_id, driver_name, UserType.DRIVER);

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
	}

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

					UserItem person_item = new UserItem(manager_id, manager_name, UserType.MANAGER);

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
