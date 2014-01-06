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
import fi.gfarr.mrd.objects.Consignment;
import fi.gfarr.mrd.objects.Item;

public class DbHandler extends SQLiteOpenHelper {

	static final String TAG = "DbHandler";
	private static DbHandler dbHandler;

	// DB Info
	public static final int DB_VERSION = 1;
	public static final String DB_NAME = "Drivers.db";

	// Tables
	public static final String TABLE_DRIVERS = "Drivers";
	public static final String TABLE_CONSIGNMENTS = "Consignments"; // Consignments
																	// going to
																	// various
																	// dilivery
																	// points
	public static final String TABLE_BAG = "Bag"; // Cargo containing items
													// belonging to consignments

	// Fields - Drivers
	public static final String C_ID = "_id"; // Primary key
	public static final String C_NAME = "dvr_name";

	// Fields - Consignments
	public static final String C_CONSIGNMENT_NO = "_id"; // Consignment number
															// (PK)
	public static final String C_CONSIGNMENT_DEST = "cons_dest"; // Destination

	/*
	 * Has bag been scanned? used to move consignments to bottom of list as they
	 * are scanned.
	 */
	public static final String C_CONSIGNMENT_SCANNED = "cons_scanned";

	/*
	 * number of items in bag. to be passed to ScanSimpleCursorAdapter
	 */
	public static final String C_CONSIGNMENT_NUMBER_ITEMS = "bag_number_items";

	// Fields - Bag
	public static final String C_BAG_ID = "_id"; // ID PK
	public static final String C_BAG_CONSIGNMENT_NUMBER = "cons_no"; // ID PK
	public static final String C_BAG_WAYBILL = "bag_waybill"; // Waybill of
																// bag
	public static final String C_BAG_NUMBER = "bag_number"; // number of bag

	public static final String C_BAG_VOLUME = "bag_volume"; // volumetric of bag
	public static final String C_BAG_WEIGHT = "bag_weight"; // weight volumetric
															// of bag

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
					+ C_NAME + " TEXT)";
			createTable(db, TABLE_DRIVERS, CREATE_TABLE_DRIVERS);

			final String CREATE_TABLE_CONSIGNMENTS = "CREATE TABLE "
					+ TABLE_CONSIGNMENTS + "(" + C_CONSIGNMENT_NO
					+ " TEXT PRIMARY KEY," + C_CONSIGNMENT_NUMBER_ITEMS
					+ " INTEGER," + C_CONSIGNMENT_SCANNED + " INTEGER,"
					+ C_CONSIGNMENT_DEST + " TEXT)";
			createTable(db, TABLE_CONSIGNMENTS, CREATE_TABLE_CONSIGNMENTS);

			final String CREATE_TABLE_BAG = "CREATE TABLE " + TABLE_BAG + "("
					+ C_BAG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ C_BAG_CONSIGNMENT_NUMBER + " TEXT," + C_BAG_WAYBILL
					+ " TEXT," + C_BAG_WEIGHT + " TEXT," + C_BAG_NUMBER
					+ " TEXT," + C_BAG_VOLUME + " TEXT," + "FOREIGN KEY("
					+ C_BAG_CONSIGNMENT_NUMBER + ") REFERENCES "
					+ TABLE_CONSIGNMENTS + "(" + C_CONSIGNMENT_NO + "))";
			createTable(db, TABLE_BAG, CREATE_TABLE_BAG);

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
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONSIGNMENTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BAG);
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

		return addRow(TABLE_DRIVERS, values);
	}

	/**
	 * Add a new consignment to the database.
	 * 
	 * Params: Consignment object.
	 * 
	 * Returns boolean of whether the database transaction was successful
	 */
	public boolean addConsignment(Consignment consignment) {
		ContentValues values = new ContentValues();

		values.put(C_CONSIGNMENT_NO, consignment.getConsignmentNumber()); // PK
		values.put(C_CONSIGNMENT_DEST, consignment.getDestination());
		values.put(C_CONSIGNMENT_SCANNED,
				convertBoolToInt(consignment.getScanned()));
		values.put(C_CONSIGNMENT_NUMBER_ITEMS, consignment.getNumberItems());

		return addRow(TABLE_CONSIGNMENTS, values);
	}

	/**
	 * Add a new item to the database which corrosponds to a consignment.
	 * 
	 * Params: Item item.
	 * 
	 * Returns boolean of whether the database transaction was successful
	 */
	public boolean addItem(Item item) {
		ContentValues values = new ContentValues();

		values.put(C_BAG_WAYBILL, item.getWaybill());
		values.put(C_BAG_NUMBER, item.getNumber());
		values.put(C_BAG_VOLUME, item.getVolume());
		values.put(C_BAG_WEIGHT, item.getWeight());
		values.put(C_CONSIGNMENT_NO, item.getConsignmentNumber()); // PK

		return addRow(TABLE_BAG, values);
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
			values.put(C_CONSIGNMENT_SCANNED, convertBoolToInt(scanned));

			db.update(TABLE_CONSIGNMENTS, values, C_CONSIGNMENT_NO + "="
					+ cons_no, null);

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
	 * Sets the value for whether a consingment has been scanned for ALL
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
			values.put(C_CONSIGNMENT_SCANNED, convertBoolToInt(scanned));

			db.update(TABLE_CONSIGNMENTS, values, null, null);

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

	public ArrayList<Consignment> getConsignments() {
		SQLiteDatabase db = null;
		try {

			db = this.getReadableDatabase(); // Open db

			ArrayList<Consignment> consignments = null;
			String sql = "SELECT * FROM " + TABLE_CONSIGNMENTS;
			Cursor cursor = db.rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst()) {
				consignments = new ArrayList<Consignment>();

				while (!cursor.isAfterLast()) {
Consignment consignment = new Consignment(cursor.getString(cursor.getColumnIndex(C_CONSIGNMENT_NO)),"");
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
