package fi.gfarr.mrd.db;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHandler extends SQLiteOpenHelper {

	static final String TAG = "DbHandler";
	private static DbHandler dbHandler;

	// DB Info
	public static final int DB_VERSION = 1;
	public static final String DB_NAME = "Drivers.db";

	// Tables
	public static final String TABLE_DRIVERS = "Drivers";
	public static final String TABLE_BAGS = "Bag"; // Consignments
															// going to
															// various
															// dilivery
															// points
	public static final String TABLE_WAYBILLS = "Waybill"; // Cargo containing items
														// belonging to
														// consignments

	// ------------ Fields - Drivers ---------
	public static final String C_ID = "_id"; // Primary key
	public static final String C_NAME = "dvr_name";
	public static final String C_PIN = "dvr_pin";

	// ------------ Fields - Bags -------------
	public static final String C_BAG_ID = "_id"; // Consignment number
													// (PK)
	public static final String C_BAG_DEST_BRANCH = "cons_dest"; // Destination

	/*
	 * Has bag been scanned? used to move consignments to bottom of list as they
	 * are scanned.
	 */
	public static final String C_BAG_SCANNED = "cons_scanned";

	/*
	 * Is bag assigned?
	 */
	public static final String C_BAG_ASSIGNED = "bag_assigned";

	/*
	 * Creation date/time
	 */
	public static final String C_BAG_CREATION_TIME = "bag_creation_time";

	public static final String C_BAG_NUM_ITEMS = "bag_number_items";

	// --------- Fields - Waybills ----------

	public static final String C_WAYBILL_ID = "_id"; // ID PK
	public static final String C_WAYBILL_BAG_ID = "bag_id"; // FK

	// Waybill dimensions (volumetrics)
	public static final String C_WAYBILL_DIMEN = "waybill_dimensions";

	// Mass X gravitational force
	public static final String C_WAYBILL_WEIGHT = "waybill_weight";

	// Parcel's destination
	public static final String C_WAYBILL_DEST = "waybill_destination";

	// Recipient's tel number
	public static final String C_WAYBILL_TEL = "waybill_tel";

	// Recipient's email address
	public static final String C_WAYBILL_EMAIL = "waybill_email";

	// number of items in waybill. to be passed to ScanSimpleCursorAdapter
	public static final String C_WAYBILL_PARCELCOUNT = "waybill_parcelcount";

	public DbHandler(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	// Return singleton instance of DbHandler
	public static DbHandler getInstance(Context context) {
		if (dbHandler == null) {
			dbHandler = new DbHandler(context.getApplicationContext());
		}
		return dbHandler;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		try {
			db.beginTransaction();

			final String CREATE_TABLE_DRIVERS = "CREATE TABLE " + TABLE_DRIVERS
					+ "(" + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ C_NAME + " TEXT," + C_PIN + " TEXT)";
			createTable(db, TABLE_DRIVERS, CREATE_TABLE_DRIVERS);

			final String CREATE_TABLE_BAGS = "CREATE TABLE " + TABLE_BAGS + "("
					+ C_BAG_ID + " TEXT PRIMARY KEY," + C_BAG_SCANNED
					+ " INTEGER," + C_BAG_ASSIGNED + " INTEGER,"
					+ C_BAG_NUM_ITEMS + " INTEGER," + C_BAG_CREATION_TIME
					+ " TEXT," + C_BAG_DEST_BRANCH + " TEXT)";
			createTable(db, TABLE_BAGS, CREATE_TABLE_BAGS);

			final String CREATE_TABLE_WAYBILL = "CREATE TABLE "
					+ TABLE_WAYBILLS + "(" + C_WAYBILL_ID
					+ " INTEGER PRIMARY KEY," + C_WAYBILL_BAG_ID + " TEXT,"
					+ C_WAYBILL_DIMEN + " TEXT," + C_WAYBILL_WEIGHT + " TEXT,"
					+ C_WAYBILL_DEST + " TEXT," + C_WAYBILL_TEL + " TEXT,"
					+ C_WAYBILL_PARCELCOUNT + " INTEGER," + C_WAYBILL_EMAIL
					+ " TEXT," + "FOREIGN KEY(" + C_WAYBILL_BAG_ID + ") REFERENCES "
					+ TABLE_BAGS + "(" + C_BAG_ID + "))";
			createTable(db, TABLE_WAYBILLS, CREATE_TABLE_WAYBILL);

			db.setTransactionSuccessful();
		} catch (SQLiteException e) { // TODO Auto-generated catch
			// block
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, DbHandler.class.getName() + " - Error creating table: "
					+ sw.toString());
		} finally {
			db.endTransaction();
		}

	}

	/**
	 * Create new table from raw SQL query.
	 * 
	 * @param table
	 * @param raw_query
	 */
	private void createTable(SQLiteDatabase db, String table, String raw_query) {
		db.execSQL(raw_query);
		Log.d(TAG, "created SQL table: " + table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRIVERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BAGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WAYBILLS);
		onCreate(db);
	}

	/**
	 * Add a new driver to the database.
	 * 
	 * Params: Driver object. Name of table to insert into
	 * 
	 * Returns boolean of whether the database transaction was successful
	 */
	public boolean addDriver(Driver driver) {
		ContentValues values = new ContentValues();

		values.put(C_ID, driver.getId());
		values.put(C_NAME, driver.getName());
		values.put(C_PIN, driver.getPin());

		return addRow(TABLE_DRIVERS, values);
	}

	/**
	 * Add a new consignment to the database.
	 * 
	 * Params: Consignment object.
	 * 
	 * Returns boolean of whether the database transaction was successful
	 */
	public boolean addBag(Bag bag) {
		ContentValues values = new ContentValues();

		values.put(C_BAG_ID, bag.getBagNumber()); // PK
		values.put(C_BAG_DEST_BRANCH, bag.getDestination());
		values.put(C_BAG_ASSIGNED, convertBoolToInt(bag.getAssigned()));
		values.put(C_BAG_SCANNED, convertBoolToInt(bag.getScanned()));
		values.put(C_BAG_CREATION_TIME, bag.getCreationTime());
		values.put(C_BAG_NUM_ITEMS, bag.getNumberItems());

		return addRow(TABLE_BAGS, values);
	}

	/**
	 * Add a new item to the database which corrosponds to a consignment.
	 * 
	 * Params: Item item.
	 * 
	 * Returns boolean of whether the database transaction was successful
	 */
	public boolean addWaybill(Waybill item) {
		ContentValues values = new ContentValues();

		values.put(C_WAYBILL_ID, item.getWaybill()); // PK
		values.put(C_WAYBILL_PARCELCOUNT, item.getParcelCount());
		values.put(C_WAYBILL_DIMEN, item.getDimensions());
		values.put(C_WAYBILL_TEL, item.getTelephone());
		values.put(C_WAYBILL_EMAIL, item.getEmail());
		values.put(C_WAYBILL_WEIGHT, item.getWeight());
		values.put(C_BAG_ID, item.getBagNumber()); // FK

		return addRow(TABLE_WAYBILLS, values);
	}

	/**
	 * Inserts a row into the database.
	 * 
	 * @param table
	 * @param values
	 * @return
	 */
	public boolean addRow(String table, ContentValues values) {

		SQLiteDatabase db = null;
		try {

			db = this.getWritableDatabase(); // Open db
			// Write to db and return success status
			return db.insertOrThrow(table, null, values) >= 0;

		} catch (SQLiteException e) { // TODO Auto-generated catch
			// block
			try {
				return db.insertWithOnConflict(table, null, values,
						SQLiteDatabase.CONFLICT_REPLACE) >= 0;
			} catch (Exception err) {
				return false; // Db transaction failed
			}
		} catch (IllegalStateException err) {
			return false;
		} finally {

			if (db != null) {
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
	public void setScanned(String cons_no, boolean scanned) {
		SQLiteDatabase db = null;
		try {

			db = this.getWritableDatabase(); // Open db

			ContentValues values = new ContentValues();
			values.put(C_BAG_SCANNED, convertBoolToInt(scanned));

			db.update(TABLE_BAGS, values, C_BAG_ID + "=" + cons_no, null);

		} catch (SQLiteException e) { // TODO Auto-generated catch
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
		} catch (IllegalStateException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
		} finally {

			if (db != null) {
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
	public void setScannedAll(boolean scanned) {
		SQLiteDatabase db = null;
		try {

			db = this.getWritableDatabase(); // Open db

			ContentValues values = new ContentValues();
			values.put(C_BAG_SCANNED, convertBoolToInt(scanned));

			db.update(TABLE_BAGS, values, null, null);

		} catch (SQLiteException e) { // TODO Auto-generated catch
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
		} catch (IllegalStateException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
		} finally {

			if (db != null) {
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
	public static String getDbName() {
		return DB_NAME;
	}

	/**
	 * Converts boolean datatype to int, because SQLite doesn't have boolean
	 * primitives.
	 * 
	 * @param bool
	 * @return
	 */
	public int convertBoolToInt(boolean bool) {
		if (bool) {
			return 1;
		} else {
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
	public boolean convertIntToBool(int integer) {
		if (integer == 1) {
			return true;
		} else {
			return false;
		}
	}

	public ArrayList<Bag> getConsignments() {
		SQLiteDatabase db = null;
		try {

			db = this.getReadableDatabase(); // Open db

			ArrayList<Bag> consignments = null;
			String sql = "SELECT * FROM " + TABLE_BAGS;
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst()) {
				consignments = new ArrayList<Bag>();

				while (!cursor.isAfterLast()) {
					Bag consignment = new Bag(cursor.getString(cursor
							.getColumnIndex(C_BAG_ID)), "");
				}

			}

			return consignments;
		} catch (SQLiteException e) { // TODO Auto-generated catch
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
			return null;
		} catch (IllegalStateException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
			return null;
		} finally {

			if (db != null) {
				if (db.isOpen()) // check if db is already open
				{
					db.close(); // close db
				}
			}
		}

	}

}
