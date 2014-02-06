package fi.gfarr.mrd.db;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import fi.gfarr.mrd.datatype.DialogDataObject;
import fi.gfarr.mrd.helper.VariableManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHandler extends SQLiteOpenHelper
{

	static final String TAG = "DbHandler";
	private static DbHandler dbHandler;

	// DB Info
	public static final int DB_VERSION = 1;
	public static final String DB_NAME = "Drivers.db";

	// Tables
	public static final String TABLE_DRIVERS = "Drivers";
	public static final String TABLE_MANAGERS = "Managers";
	public static final String TABLE_BAGS = "Bag"; // Consignments going to various delivery points
	public static final String TABLE_WAYBILLS = "Waybill"; // Cargo containing items belonging to
															// bags
	public static final String TABLE_CONTACTS = "Contacts";

	public static final String TABLE_DELAYS = "Delays";

	// ------------ Fields - Drivers ---------
	public static final String C_DRIVER_ID = "_id"; // Primary key
	public static final String C_DRIVER_NAME = "dvr_name";
	public static final String C_DRIVER_PIN = "dvr_pin";

	// ------------ Fields - Managers ---------
	public static final String C_MANAGER_ID = "_id"; // Primary key
	public static final String C_MANAGER_NAME = "man_name";

	// ------------ Fields - Delays ---------
	public static final String C_DELAYS_ID = "_id"; // Primary key
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

	/*
	 * Creation date/time
	 */
	public static final String C_BAG_CREATION_TIME = "bag_creation_time";

	public static final String C_BAG_NUM_ITEMS = "bag_number_items";

	// --------- Fields - Waybills ----------

	public static final String C_WAYBILL_ID = "_id"; // ID PK
	public static final String C_WAYBILL_BAG_ID = "bag_id"; // FK

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

	public DbHandler(Context context)
	{
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
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

			final String CREATE_TABLE_BAGS = "CREATE TABLE " + TABLE_BAGS + "(" + C_BAG_ID
					+ " TEXT PRIMARY KEY," + C_BAG_SCANNED + " INTEGER," + C_BAG_ASSIGNED
					+ " TEXT," + C_BAG_BARCODE + " TEXT," + C_BAG_NUM_ITEMS + " INTEGER,"
					+ C_BAG_DRIVER_ID + " TEXT," + C_BAG_CREATION_TIME + " TEXT," + C_BAG_DEST_TOWN
					+ " TEXT," + " TEXT," + C_BAG_DEST_SUBURB + " TEXT," + C_BAG_DEST_LONG
					+ " TEXT," + C_BAG_DEST_LAT + " TEXT," + C_BAG_DEST_HUBNAME + " TEXT,"
					+ C_BAG_DEST_HUBCODE + " TEXT," + C_BAG_DEST_CONTACT + " TEXT," + C_BAG_STATUS
					+ " TEXT," + C_BAG_DEST_ADDRESS + " TEXT)";
			createTable(db, TABLE_BAGS, CREATE_TABLE_BAGS);

			final String CREATE_TABLE_WAYBILL = "CREATE TABLE " + TABLE_WAYBILLS + "("
					+ C_WAYBILL_ID + " INTEGER PRIMARY KEY," + C_WAYBILL_BAG_ID + " TEXT,"
					+ C_WAYBILL_WEIGHT + " TEXT," + C_WAYBILL_DEST_LONG + " TEXT,"
					+ C_WAYBILL_DEST_LAT + " TEXT," + C_WAYBILL_DEST_TOWN + " TEXT,"
					+ C_WAYBILL_DEST_SUBURB + " TEXT," + C_WAYBILL_DEST_ADDRESS + " TEXT,"
					+ C_WAYBILL_TEL + " TEXT," + C_wAYBILL_PARCEL_SEQUENCE + " TEXT,"
					+ C_WAYBILL_DIMEN + " TEXT," + C_WAYBILL_PARCELCOUNT + " INTEGER,"
					+ C_WAYBILL_CUSTOMER_ID + " TEXT," + C_WAYBILL_CUSTOMER_CONTACT2 + " TEXT,"
					+ C_WAYBILL_CUSTOMER_CONTACT1 + " TEXT," + C_WAYBILL_CUSTOMER_NAME + " TEXT,"
					+ C_WAYBILL_CUSTOMER_EMAIL + " TEXT," + "FOREIGN KEY(" + C_WAYBILL_BAG_ID
					+ ") REFERENCES " + TABLE_BAGS + "(" + C_BAG_ID + "))";
			createTable(db, TABLE_WAYBILLS, CREATE_TABLE_WAYBILL);

			final String CREATE_TABLE_DELAYS = "CREATE TABLE " + TABLE_DELAYS + "(" + C_DELAYS_ID
					+ " INTEGER PRIMARY KEY," + C_DELAYS_REASON + " TEXT," + C_DELAYS_DURATION_ID
					+ " TEXT," + C_DELAYS_DURATION + " TEXT)";
			createTable(db, TABLE_DELAYS, CREATE_TABLE_DELAYS);

			final String CREATE_TABLE_CONTACTS = "CREATE TABLE " + TABLE_CONTACTS + "("
					+ C_CONTACTS_ID + " INTEGER PRIMARY KEY," + C_CONTACTS_NAME + " TEXT,"
					+ C_CONTACTS_NUMBER + " INTEGER," + "FOREIGN KEY(" + C_CONTACTS_BAG_ID
					+ ") REFERENCES " + TABLE_BAGS + "(" + C_BAG_ID + "))";
			createTable(db, TABLE_DELAYS, CREATE_TABLE_CONTACTS);

			db.setTransactionSuccessful();
		}
		catch (SQLiteException e)
		{ // TODO Auto-generated catch
			// block
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, DbHandler.class.getName() + " - Error creating table: " + sw.toString());
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
		Log.d(TAG, "created SQL table: " + table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRIVERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MANAGERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BAGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WAYBILLS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELAYS);
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

		return addRow(TABLE_DRIVERS, values);
	}

	/**
	 * Add a manager to the DB.
	 * 
	 * @param id
	 * @param first_name
	 * @param last_name
	 * @return Success. True or false.
	 */
	public boolean addManager(String id, String name)
	{
		ContentValues values = new ContentValues();

		values.put(C_MANAGER_ID, id);
		values.put(C_MANAGER_NAME, name);

		return addRow(TABLE_MANAGERS, values);
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
	 * Add a new consignment to the database.
	 * 
	 * Params: Consignment object.
	 * 
	 * Returns boolean of whether the database transaction was successful
	 */
	public boolean addBag(Bag bag)
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
		values.put(C_BAG_DEST_TOWN, bag.getDestinationTown());
		values.put(C_BAG_BARCODE, bag.getBarcode());
		values.put(C_BAG_ASSIGNED, convertBoolToInt(bag.getAssigned()));
		values.put(C_BAG_SCANNED, convertBoolToInt(bag.getScanned()));
		values.put(C_BAG_CREATION_TIME, bag.getCreationTime());
		values.put(C_BAG_NUM_ITEMS, bag.getNumberItems());
		values.put(C_BAG_DRIVER_ID, bag.getDriverId());
		values.put(C_BAG_STATUS, bag.getStatus());

		return addRow(TABLE_BAGS, values);
	}

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
		values.put(C_wAYBILL_PARCEL_SEQUENCE, item.getParcelSeq() + " of " + item.getParcelCount());
		values.put(C_WAYBILL_DIMEN, item.getDimensions());
		values.put(C_WAYBILL_CUSTOMER_CONTACT1, item.getCustomerContact1());
		values.put(C_WAYBILL_CUSTOMER_CONTACT2, item.getCustomerContact2());
		values.put(C_WAYBILL_CUSTOMER_NAME, item.getCustomerName());
		values.put(C_WAYBILL_CUSTOMER_ID, item.getCustomerID());
		values.put(C_WAYBILL_CUSTOMER_EMAIL, item.getEmail());
		values.put(C_WAYBILL_WEIGHT, item.getWeight());
		values.put(C_WAYBILL_BAG_ID, item.getBagNumber()); // FK

		Log.d(TAG, values.getAsString(C_WAYBILL_DIMEN));
		Log.d(TAG, item.getDimensions());

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
	 * @param cons_no
	 * @param scanned
	 */
	public void setScanned(String cons_no, boolean scanned)
	{
		SQLiteDatabase db = null;
		try
		{

			db = this.getWritableDatabase(); // Open db

			ContentValues values = new ContentValues();
			values.put(C_BAG_SCANNED, convertBoolToInt(scanned));

			db.update(TABLE_BAGS, values, C_BAG_ID + "=" + cons_no, null);

		}
		catch (SQLiteException e)
		{ // TODO Auto-generated catch
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
		}
		catch (IllegalStateException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
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
	 * Sets the value for whether a consignment has been scanned for ALL
	 * consignments.
	 * 
	 * @param cons_no
	 * @param scanned
	 */
	public void setScannedAll(boolean scanned)
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
			Log.d(TAG, sw.toString());
		}
		catch (IllegalStateException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
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
	 * Get list of all consignments
	 * 
	 * @return ArrayList<Bag>
	 */
	public ArrayList<Bag> getBags(String driver_id)
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
					Log.d(TAG, bag.getBagNumber());
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
	}

	/**
	 * Return bag according to bag number
	 * Should only be one.
	 */
	public Bag getBag(String driver_id, String bag_id)
	{
		SQLiteDatabase db = null;
		Bag bag = null;
		try
		{

			db = this.getReadableDatabase(); // Open db

			// ArrayList<Bag> bags = null;
			String sql = "SELECT * FROM " + TABLE_BAGS + " WHERE " + C_BAG_DRIVER_ID + " LIKE '"
					+ driver_id + "'";
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst())
			{
				// bags = new ArrayList<Bag>();

				if (!cursor.isAfterLast())
				{
					bag = new Bag(cursor.getString(cursor.getColumnIndex(C_BAG_ID)));

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
					bag.setDestinationTown(cursor.getString(cursor.getColumnIndex(C_BAG_DEST_TOWN)));

					// bags.add(bag);
					// cursor.moveToNext();
				}
			}
		}
		catch (SQLiteException e)
		{ // TODO Auto-generated catch
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
		}
		catch (IllegalStateException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
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
	}

	/**
	 * Return contact details linked to the specified bag ID.
	 * 
	 * @param bag_id
	 * @return
	 */
	public ArrayList<DialogDataObject> getContacts(String bag_id)
	{
		SQLiteDatabase db = null;
		ArrayList<DialogDataObject> contacts = new ArrayList<DialogDataObject>();
		try
		{

			db = this.getReadableDatabase(); // Open db

			// ArrayList<Bag> bags = null;
			String sql = "SELECT * FROM " + TABLE_BAGS + " WHERE " + C_BAG_ID + " LIKE '" + bag_id
					+ "'";
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst())
			{
				// bags = new ArrayList<Bag>();

				if (!cursor.isAfterLast())
				{
					DialogDataObject contact = new DialogDataObject();

					contact.setMainText(cursor.getString(cursor.getColumnIndex(C_CONTACTS_NAME)));

					contact.setSubText(cursor.getString(cursor.getColumnIndex(C_CONTACTS_NUMBER)));

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
	 * @return BagID at specified row
	 */
	public String getBagIdAtRow(String driver_id, int row)
	{
		String countQuery = "SELECT  * FROM " + TABLE_BAGS + " WHERE " + C_BAG_DRIVER_ID
				+ " LIKE '" + driver_id + "'" + " LIMIT 1 OFFSET " + row;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.moveToFirst();
		String bagid = cursor.getString(cursor.getColumnIndex(C_BAG_ID));
		cursor.close();
		Log.d(TAG, "getBagIdAtRow(): " + bagid);
		return bagid;
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
					// Log.d(TAG, "Not scanned: " +
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
			Log.d(TAG, sw.toString());
			return list;
		}
		catch (IllegalStateException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
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
	public ArrayList<Bag> getBagsByStatus(String driver_id, String status)
	{
		SQLiteDatabase db = null;
		ArrayList<Bag> list = new ArrayList<Bag>();
		try
		{

			db = this.getReadableDatabase(); // Open db

			String sql = "SELECT * FROM " + TABLE_BAGS + " WHERE " + C_BAG_STATUS + " LIKE '"
					+ status + "' AND " + C_BAG_DRIVER_ID + " LIKE '" + driver_id + "'";
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst())
			{
				while (!cursor.isAfterLast())
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
					bag.setDestinationTown(cursor.getString(cursor.getColumnIndex(C_BAG_DEST_TOWN)));
					Log.d(TAG, bag.getDestinationHubName());
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
			Log.d(TAG, sw.toString());
			return list;
		}
		catch (IllegalStateException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
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
	 * Returns coords of all bags
	 * 
	 * @param driver_id
	 * @return ArrayList of HashMaps
	 */
	public ArrayList<HashMap<String, String>> getBagCoords(String driver_id)
	{
		SQLiteDatabase db = null;
		try
		{

			db = this.getReadableDatabase(); // Open db

			ArrayList<HashMap<String, String>> bags = null;
			String sql = "SELECT * FROM " + TABLE_BAGS + " WHERE " + C_BAG_DRIVER_ID + " LIKE '"
					+ driver_id + "'";
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

					Log.d(TAG, hubname + " " + lat + "," + lon);

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
			Log.d(TAG, sw.toString());
			return null;
		}
		catch (IllegalStateException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
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
	 * Returns list of reasons for milkrun delay
	 * 
	 * @return
	 */
	public ArrayList<DialogDataObject> getMilkrunDelayReasons()
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
					String delay_id = cursor.getString(cursor.getColumnIndex(C_DELAYS_ID));

					DialogDataObject dialog_data_object = new DialogDataObject(reason, "");
					dialog_data_object.setThirdText(delay_id);

					delays.add(dialog_data_object);

					cursor.moveToNext();
				}
			}

			return delays;
		}
		catch (SQLiteException e)
		{ // TODO Auto-generated catch
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
			return null;
		}
		catch (IllegalStateException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
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
	 * Return durations of delay reason
	 * 
	 * @param reason_id
	 * @return
	 */
	public ArrayList<DialogDataObject> getMilkrunDelayDurations(String reason_id)
	{
		SQLiteDatabase db = null;
		try
		{

			db = this.getReadableDatabase(); // Open db

			ArrayList<DialogDataObject> delays = null;
			String sql = "SELECT * FROM " + TABLE_DELAYS + " WHERE " + C_DELAYS_ID + " LIKE '"
					+ reason_id + "'";
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst())
			{
				delays = new ArrayList<DialogDataObject>();

				while (!cursor.isAfterLast())
				{
					String duration = cursor.getString(cursor.getColumnIndex(C_DELAYS_DURATION));

					delays.add(new DialogDataObject(duration, ""));

					cursor.moveToNext();
				}
			}

			return delays;
		}
		catch (SQLiteException e)
		{ // TODO Auto-generated catch
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
			return null;
		}
		catch (IllegalStateException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
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
	 * Return list of predefined message to be SMSed. Hardcoded for now.
	 * 
	 * @return
	 */
	public ArrayList<DialogDataObject> getSMSMessages()
	{
		ArrayList<DialogDataObject> msgs = new ArrayList<DialogDataObject>();

		msgs.add(new DialogDataObject("Hijack", ""));
		msgs.add(new DialogDataObject("Accident", ""));
		msgs.add(new DialogDataObject("IED", ""));
		msgs.add(new DialogDataObject("RPG", ""));
		msgs.add(new DialogDataObject("Ambush", ""));

		return msgs;
	}
}
