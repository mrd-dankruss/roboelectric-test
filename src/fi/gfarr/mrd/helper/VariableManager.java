package fi.gfarr.mrd.helper;

import android.content.Context;

public class VariableManager
{

	public static final String EXTRA_DRIVER = "name";
	public static final String EXTRA_DRIVER_ID = "driver_id";
	public static final String EXTRA_LIST_SCANNED_ITEMS = "list_of_scanned_items";
	public static final String EXTRA_MANAGER_NAME = "manager_name";
	public static final String EXTRA_MANAGER_ID = "manager_id";
	public static final String EXTRA_CONSIGNMENT_NUMBER_ITEMS = "cons_number_items";
	public static final String EXTRA_CONSIGNMENT_NUMBER = "cons_no";
	public static final String EXTRA_CONSIGNMENT_DESTINATION = "cons_sdest";
	public static final String EXTRA_BAG_NO = "bag_number";
	public static final String EXTRA_BAG_LAT = "bag_lat";
	public static final String EXTRA_BAG_LON = "bag_lon";
	public static final String EXTRA_BAG_ADDRESS = "bag_address";
	public static final String EXTRA_BAG_HUBNAME = "bag_hubname";
	public static final String EXTRA_BAG_DELIVERY_TYPE = "bag_deliverytype";
	public static final String EXTRA_BAG_DELIVERY_TYPE_MILKRUN = "bag_deliverytypemilkrun";
	public static final String EXTRA_NEXT_BAG_ID = "bag_next_id";
	public static final String EXTRA_DELAY_ID = "delay_id";
	public static final int URL_LOADER_BAG_MANIFEST = 2;

	// JSON keys
	public static final String JSON_KEY_TOKEN = "token";
	public static final String JSON_KEY_EXPIRE = "expire";
	public static final String JSON_KEY_DRIVER_PIN = "driverPin";
	public static final String JSON_KEY_DRIVER_FIRSTNAME = "firstName";
	public static final String JSON_KEY_DRIVER_LASTNAME = "lastName";
	public static final String JSON_KEY_DRIVER_ID = "id";

	// Shared prefs
	public static final String PREF = "fi.gfarr.mrd";
	public static final String IMEI_TEST = "490154203237518";

	public static String token = "";

	public static String next_bag_id;
	public static String delay_id;
	public static Context context;

	// Debug mode
	public static final boolean DEBUG = true;
	
	// List extras
	public static final String EXTRA_LIST_POSITION = "list_position"; 

}
