package com.mrdexpress.paperless.helper;

import android.content.Context;

public class VariableManager
{

	public static final String EXTRA_DRIVER = "com.mrdexpress.paperless.name";
	public static final String EXTRA_DRIVER_ID = "com.mrdexpress.paperless.driver_id";
	public static final String EXTRA_LIST_SCANNED_ITEMS = "com.mrdexpress.paperless.list_of_scanned_items";
	public static final String EXTRA_MANAGER_NAME = "com.mrdexpress.paperless.manager_name";
	public static final String EXTRA_MANAGER_ID = "com.mrdexpress.paperless.manager_id";
	public static final String EXTRA_CONSIGNMENT_NUMBER_ITEMS = "com.mrdexpress.paperless.cons_number_items";
	public static final String EXTRA_CONSIGNMENT_NUMBER = "com.mrdexpress.paperless.cons_no";
	public static final String EXTRA_CONSIGNMENT_DESTINATION = "com.mrdexpress.paperless.cons_sdest";
	public static final String EXTRA_BAG_NO = "com.mrdexpress.paperless.bag_number";
	public static final String EXTRA_BAG_LAT = "com.mrdexpress.paperless.bag_lat";
	public static final String EXTRA_BAG_LON = "com.mrdexpress.paperless.bag_lon";
	public static final String EXTRA_BAG_ADDRESS = "com.mrdexpress.paperless.bag_address";
	public static final String EXTRA_BAG_HUBNAME = "com.mrdexpress.paperless.bag_hubname";
	public static final String EXTRA_BAG_DELIVERY_TYPE = "com.mrdexpress.paperless.bag_deliverytype";
	public static final String EXTRA_BAG_DELIVERY_TYPE_MILKRUN = "com.mrdexpress.paperless.bag_deliverytypemilkrun";
	public static final String EXTRA_NEXT_BAG_ID = "com.mrdexpress.paperless.bag_next_id";
	public static final String EXTRA_DELAY_ID = "com.mrdexpress.paperless.delay_id";
	public static final int URL_LOADER_BAG_MANIFEST = 2;

	// JSON keys
	public static final String JSON_KEY_TOKEN = "token";
	public static final String JSON_KEY_EXPIRE = "expire";
	public static final String JSON_KEY_DRIVER_PIN = "driverPin";
	public static final String JSON_KEY_DRIVER_FIRSTNAME = "firstName";
	public static final String JSON_KEY_DRIVER_LASTNAME = "lastName";
	public static final String JSON_KEY_DRIVER_ID = "id";

	// Shared prefs
	public static final String PREF = "com.mrdexpress.paperless";
	public static final String PREF_TOKEN = "com.mrdexpress.paperless.token";
	public static final String PREF_NETWORK_AVAILABLE = "com.mrdexpress.paperless.network.avail";
	public static final String IMEI_TEST = "490154203237518";

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

	// API error codes
	public static final String API_ERROR_CODE_BADREQUEST = "400";
	public static final String API_ERROR_CODE_UNAUTHORISED = "401";
	public static final String API_ERROR_CODE_NOTFOUND = "402";

	// Last Logged In
	public static final String LAST_LOGGED_IN_MANAGER_NAME = "com.mrdexpress.paperless.last_logged_in_manager_name";
	public static final String LAST_LOGGED_IN_MANAGER_ID = "com.mrdexpress.paperless.last_logged_in_manager_id";

}
