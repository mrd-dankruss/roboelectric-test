package fi.gfarr.mrd.helper;

import android.content.Context;

public class VariableManager
{

	public static final String EXTRA_DRIVER = "fi.gfarr.mrd.name";
	public static final String EXTRA_DRIVER_ID = "fi.gfarr.mrd.driver_id";
	public static final String EXTRA_LIST_SCANNED_ITEMS = "fi.gfarr.mrd.list_of_scanned_items";
	public static final String EXTRA_MANAGER_NAME = "fi.gfarr.mrd.manager_name";
	public static final String EXTRA_MANAGER_ID = "fi.gfarr.mrd.manager_id";
	public static final String EXTRA_CONSIGNMENT_NUMBER_ITEMS = "fi.gfarr.mrd.cons_number_items";
	public static final String EXTRA_CONSIGNMENT_NUMBER = "fi.gfarr.mrd.cons_no";
	public static final String EXTRA_CONSIGNMENT_DESTINATION = "fi.gfarr.mrd.cons_sdest";
	public static final String EXTRA_BAG_NO = "fi.gfarr.mrd.bag_number";
	public static final String EXTRA_BAG_LAT = "fi.gfarr.mrd.bag_lat";
	public static final String EXTRA_BAG_LON = "fi.gfarr.mrd.bag_lon";
	public static final String EXTRA_BAG_ADDRESS = "fi.gfarr.mrd.bag_address";
	public static final String EXTRA_BAG_HUBNAME = "fi.gfarr.mrd.bag_hubname";
	public static final String EXTRA_BAG_DELIVERY_TYPE = "fi.gfarr.mrd.bag_deliverytype";
	public static final String EXTRA_BAG_DELIVERY_TYPE_MILKRUN = "fi.gfarr.mrd.bag_deliverytypemilkrun";
	public static final String EXTRA_NEXT_BAG_ID = "fi.gfarr.mrd.bag_next_id";
	public static final String EXTRA_DELAY_ID = "fi.gfarr.mrd.delay_id";
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
	public static final String PREF_NETWORK_AVAILABLE = "fi.gfarr.mrd.network.avail";
	public static final String IMEI_TEST = "490154203237518";

	public static String token = "";

	public static String next_bag_id;
	public static String delay_id;
	public static Context context;
	public static final String TEXT_NET_ERROR = "Connection error";

	// Debug mode
	public static final boolean DEBUG = true;

	// List extras
	public static final String EXTRA_LIST_POSITION = "list_position";
	public static final String EXTRA_UNSCANNED_PARCELS_BUNDLE = "unscanned_parcels_bundles";

	// Activity result codes
	public static final int ACTIVITY_REQUEST_CODE_PARTIAL_DELIVERY = 0;
	
	//Last Logged In
	public static final String LAST_LOGGED_IN_MANAGER_NAME = "fi.gfarr.mrd.last_logged_in_manager_name";
	public static final String LAST_LOGGED_IN_MANAGER_ID = "fi.gfarr.mrd.last_logged_in_manager_id";

}
