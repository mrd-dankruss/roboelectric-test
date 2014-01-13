package fi.gfarr.mrd.helper;

public class VariableManager {

	public static final String EXTRA_DRIVER = "name";
	public static final String EXTRA_DRIVER_ID = "driver_id";
	public static final String EXTRA_LIST_SCANNED_ITEMS = "list_of_scanned_items";
	public static final String EXTRA_CONSIGNMENT_NUMBER_ITEMS = "cons_number_items";
	public static final String EXTRA_CONSIGNMENT_NUMBER = "cons_no";
	public static final String EXTRA_CONSIGNMENT_DESTINATION = "cons_sdest";
	public static final int URL_LOADER_BAG_MANIFEST = 2;

	
	//JSON keys
	public static final String JSON_KEY_TOKEN = "token";
	public static final String JSON_KEY_EXPIRE = "expire";
	public static final String JSON_KEY_DRIVER_PIN  = "driverPin";
	public static final String JSON_KEY_DRIVER_FIRSTNAME  = "firstName";
	public static final String JSON_KEY_DRIVER_LASTNAME  = "lastName";
	public static final String JSON_KEY_DRIVER_ID  = "id";
	
	// Shared prefs
	public static final String PREF_TOKEN = "fi.gfarr.mrd.token";
	public static final String IMEI_TEST = "490154203237518";

	public static String token = "";	

	// Debug mode
	public static final boolean DEBUG = true;

}
