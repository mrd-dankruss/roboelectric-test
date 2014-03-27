package com.mrdexpress.paperless.net;

import android.os.AsyncTask;
import com.androidquery.AQuery;
import com.androidquery.callback.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.mrdexpress.paperless.Paperless;
import com.mrdexpress.paperless.db.*;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.security.PinManager;
import com.mrdexpress.paperless.workflow.Workflow;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.Hashtable;
import java.util.List;


public class ServerInterface {

    private final static String TAG = "ServerInterface";
    private static final String API_URL = "http://uat.mrdexpress.com/api/";
    public static Handler UIHandler = new Handler(Looper.getMainLooper());
    private static ServerInterface server_interface;
    private static Context context;
    private static SharedPreferences prefs;
    private static AQuery aq;

    public ServerInterface(Context ctx) {
        context = ctx;
    }

    // Return singleton instance of DbHandler
    public static ServerInterface getInstance(Context context) {
        if (server_interface == null) {
            //server_interface = new ServerInterface(context.getApplicationContext());

            server_interface = new ServerInterface(Paperless.getContext());
            if (prefs == null) {
                prefs = Paperless.getContext().getSharedPreferences(VariableManager.PREF, Context.MODE_PRIVATE);
            }

            if (context != null)
            {
                aq = new AQuery(context);
            }
            else
            {
                aq = new AQuery(Paperless.getContext());
            }
        }

        return server_interface;
    }

    public static void displayToast(final String message) {
        UIHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "Test: " + message);
                Toast.makeText(VariableManager.context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     * Gets Users From The API
     */
    public String getUsersURL(){
        return API_URL + "v1/driver/users?imei=" + Device.getInstance().getIMEI() + "&mrdToken=" + Device.getInstance().getToken();
    }
    public void getUsers() {
        String url = API_URL + "v1/driver/users?imei=" + Device.getInstance().getIMEI() + "&mrdToken=" + Device.getInstance().getToken();
        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
        cb.url(url).type(JSONObject.class);
        aq.sync(cb);
        try
        {
            JSONObject json = cb.getResult();
            AjaxStatus status = cb.getStatus();
            if (json != null) {
                //Generate Users Data
                Users.getInstance().setUsers(json.toString());
            }
        } catch (Exception e) {
            Log.e("MRD-EX" , "FIX THIS : " + e.getMessage());
        }
    }

    /**
     * Makes API call to request a new session token.
     */
    public String getTokenUrl(){
        return API_URL + "v1/auth/auth?imei=" + Device.getInstance().getIMEI();
    }
    public String requestToken() {
        String url = API_URL + "v1/auth/auth?imei=" + Device.getInstance().getIMEI();
        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
        cb.url(url).type(JSONObject.class);
        aq.sync(cb);
        String Token = null;
        try
        {
            JSONObject jObject = cb.getResult();
            AjaxStatus status = cb.getStatus();
            if (jObject.has("response"))
            {
                Token = jObject.getJSONObject("response").getJSONObject("auth").getString("token");

            } else if (jObject.has("error")) {
                Token = jObject.toString();
            }
        } catch (JSONException e) {
            Log.e("MRD-EX" , "FIX THIS : " + e.getMessage());
        }
        Device.getInstance().setToken(Token);
        return Token;
    }

    /**
     * Registers the device for GCM
     *
     * @param imei   IMEI of the device
     * @param gcm_id The GCM ID returned by Google GCM Service
     * @return
     */

    public void registerDeviceGCM(String gcm_id) {
        String url = API_URL + "v1/push/register?imei=" + Device.getInstance().getIMEI() + "&mrdToken=" + Device.getInstance().getToken() + "&gcmID="
                + gcm_id;

        aq.ajax(url , JSONObject.class , new AjaxCallback<JSONObject>(){
            String Token = null;
            @Override
            public void callback(String url, JSONObject jObject, AjaxStatus ajaxstatus) {
                String status = null;
                try {
                    status = jObject.getJSONObject("response").getString("push");

                }
                catch (JSONException e) {
                    Log.e("MRD-EX" , "FIX THIS : " + e.getMessage());
                }
                Device.getInstance().setGCMID(status);
            }
        });
    }

    /**
     * Makes API call to update driver PIN.
     */
    public String updatePIN(String id, String new_pin, String imei) {
        String token = prefs.getString(VariableManager.PREF_TOKEN, "");
        String url = API_URL + "v1/auth/driver?driverID=" + id + "&mrdToken=" + token
                + "&driverPIN=" + PinManager.toMD5(new_pin) + "&imei=" + imei;

        String response = postData(url);

        String status = "";

        try {
            JSONObject jObject = new JSONObject(response);
            if (jObject.has("response")) {
                status = jObject.getJSONObject("response").getJSONObject("auth")
                        .getString("status");
            } else if (jObject.has("error")) {
                status = stripErrorCode(jObject.toString());
            }

        } catch (JSONException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(TAG, sw.toString());
            if (VariableManager.DEBUG) {
                displayToast("JSONException: driver?id");
            }
            return "";
            // Oops
        }

        if (VariableManager.DEBUG) {
            Log.d(TAG, "token: " + status);
        }
        return status;
    }

    /**
     * Retrieves list of drivers from server. Used to populate the list at
     * login.
     * switching it over to AJAX
     */
    public void getDrivers(Context context) {
        String token = prefs.getString(VariableManager.PREF_TOKEN, "");
        String url = API_URL + "v1/driver/drivers?imei=" + Device.getInstance().getIMEI() + "&mrdToken=" + token;
        final AQuery aq = new AQuery(Paperless.getContext());
        aq.ajax( url  , JSONObject.class , new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if(json != null){
                    //successful ajax call, show status code and json content
                    Drivers drvs = Drivers.getInstance();
                    drvs.setDrivers(json);
                    Workflow.getInstance().setDriversFromJSON( json.toString() );
                }
            }
        });
    }

    /**
     * Downloads list of managers from API
     *
     * @param context
     */
    public void getManagers(Context context) {
        TelephonyManager mngr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei_id = mngr.getDeviceId();
        String token = prefs.getString(VariableManager.PREF_TOKEN, "");
        String url = API_URL + "v1/driver/managers?imei=" + imei_id + "&mrdToken=" + token;
        String response = getInputStreamFromUrl(url);

        Workflow.getInstance().setManagersFromJSON( response);
    }

    /**
     * Submit driver authentication request (login). Receives success status.
     *
     * @param PIN
     * @return
     */
    public String authDriver(String PIN, String driver_id) {
        // SharedPreferences settings = context.getSharedPreferences(VariableManager.PREF, 0);
        // String token = settings.getString(VariableManager.PREF_TOKEN, "");

        TelephonyManager mngr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei_id = mngr.getDeviceId();
        String token = prefs.getString(VariableManager.PREF_TOKEN, "");
        String url = API_URL + "v1/auth/driver?imei=" + imei_id + "&mrdToken=" + token
                + "&driverPIN=" + PIN + "&driverID=" + driver_id;

        String response = getInputStreamFromUrl(url);

        String status = "";

        try {
            JSONObject jObject = new JSONObject(response);

            if (jObject.has("response")) {
                status = jObject.getJSONObject("response").getJSONObject("auth")
                        .getString("status");
            } else if (jObject.has("error")) {
                status = stripErrorCode(jObject.toString());
            }
        } catch (JSONException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(TAG, sw.toString());
            if (VariableManager.DEBUG) {
                displayToast("JSONException: auth/driver");
            }
        }

        if (VariableManager.DEBUG) {
            Log.d(TAG, "auth/driver: " + status);
        }

        return status;
        // return "success"; // DEBUG!!
    }

    /**
     * Submit manager authentication request.
     *
     * @param man_id
     * @param driver_id
     * @param PIN
     * @return
     */
    public String authManager(String man_id, String driver_id, String PIN, String imei_id) {
        String token = prefs.getString(VariableManager.PREF_TOKEN, "");
        String url = API_URL + "v1/auth/manager?imei=" + imei_id + "&mrdToken=" + token
                + "&managerPIN=" + PIN + "&managerID=" + man_id;

        String response = getInputStreamFromUrl(url);

        // System.out.println(response);

        String status = "";

        try {
            JSONObject jObject = new JSONObject(response);

            if (jObject.has("response")) {
                status = jObject.getJSONObject("response").getJSONObject("auth")
                        .getString("status");
            } else if (jObject.has("error")) {
                status = stripErrorCode(jObject.toString());
            }
        } catch (JSONException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(TAG, sw.toString());
            if (VariableManager.DEBUG) {
                displayToast("JSONException: auth/manager");
            }
        }

        if (VariableManager.DEBUG) {
            // Log.d(TAG, "token: " + token);
        }

        return status;
    }


    /**
     * Retrieves workflow for current token
     *
     * @return
     */
    public void getMilkrunWorkflow(Context context) {
        String token = Device.getInstance().getToken();
        String url = API_URL + "v1/workflow/get-milkrun-workflow?mrdToken=" + Device.getInstance().getToken();

        try {
            String response = getInputStreamFromUrl(url);

            Workflow.getInstance().setWorkflowFromJSON(response);

           /* Workflow.getInstance().registerObserver( new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                }
            });*/

        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(TAG, sw.toString());
            if (VariableManager.DEBUG) {
                displayToast("Exception: workflow/get-milkrun-workflow");
            }
        }
    }


    /**
     * Retrieves consignments (bags), for the specified driver ID and adds to DB
     *
     * @param driver_id of driver.
     * @return
     */
    /*public void downloadBags(Context context, String driver_id) {
        getMilkrunWorkflow(context);

        String token = prefs.getString(VariableManager.PREF_TOKEN, "");
        String url = API_URL + "v1/bags/driver?id=" + driver_id + "&mrdToken=" + token;

        // Log.i(TAG, "Fetching " + url);

        try {
            String response = getInputStreamFromUrl(url);

            JSONObject jObject = new JSONObject(response);

            JSONArray result = jObject.getJSONObject("response").getJSONArray("bags");

            if (result != null) {

                // Stores waybill IDs as they are loaded.
                // Used to count the number of occurences
                // For counting multiple packages.
                // Hashtable<String, Integer> waybill_IDs = new Hashtable<String, Integer>();

                for (int i = 0; i < result.length(); i++) {
                    try {
                        // ID
                        String bag_id = result.getJSONObject(i).getString("id");

                        // Download more details
                        downloadBag(context, bag_id, driver_id);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (VariableManager.DEBUG) {
                            displayToast("JSONException: bags/driver");
                        }
                    }
                }
            }
        } catch (JSONException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(TAG, sw.toString());
            if (VariableManager.DEBUG) {
                displayToast("JSONException: bags/driver");
            }
        }
    } */

    /**
     * Retrieve a single bag object
     *
     */

    /*
    public void downloadBag(Context context, String bag_id, String driver_id) {
        String token = prefs.getString(VariableManager.PREF_TOKEN, "");
        String url = API_URL + "v1/bag/bag?id=" + bag_id + "&mrdToken=" + token;
        Log.i(TAG, "Fetching " + url);
        if (!bag_id.isEmpty() || bag_id == "null"){
        try {
            String response = getInputStreamFromUrl(url);

            JSONObject jObject = new JSONObject(response);

            JSONObject result = jObject.getJSONObject("response").getJSONObject("bag");

            if (result != null) {

                // Stores waybill IDs as they are loaded.
                // Used to count the number of occurences
                // For counting multiple packages.
                Hashtable<String, Integer> waybill_IDs = new Hashtable<String, Integer>();

                try {
                    // ID
                    String id = result.getString("id");

                    // Barcode
                    String barcode = result.getString("barcode");

                    //String mdx = result.getString("mdx");

                    // Status
                    String bag_status = result.getString("status");

                    // Status
                    String bag_stopid = result.getString("stopid");

                    // Waybill count
                    int waybill_count = result.getInt("waybillcount");

                    // scanned?
                    boolean scanned = result.getBoolean("scanned");

                    // ---- Destination
                    // Address
                    String dest_hubname = result.getJSONObject("destination").getString("hubname");
                    String dest_hubcode = result.getJSONObject("destination").getString("hubcode");
                    String dest_address = result.getJSONObject("destination").getJSONObject("address").getString("address");
                    String dest_suburb = result.getJSONObject("destination").getJSONObject("address").getString("suburb");
                    // String dest_town =
                    // result.getJSONObject("destination").getJSONObject("address")
                    // .getString("town");
                    String dest_contact1 = result.getJSONObject("destination").getJSONObject("address").getString("contact1");
                    String dest_lat = result.getJSONObject("destination").getJSONObject("address").getJSONObject("coords").getString("lat");
                    String dest_long = result.getJSONObject("destination").getJSONObject("address").getJSONObject("coords").getString("lon");

                    // Go through temp array to find number of times the
                    // current waybill ID occurs.

                    // Add bag to DB
                    Bag bag = new Bag(id);
                    bag.setDriverId(driver_id);
                    bag.setBarcode(barcode);
                    bag.setStopId(bag_stopid);
                    bag.setDestinationHubCode(dest_hubcode);
                    bag.setDestinationHubName(dest_hubname);
                    bag.setDestinationAddress(dest_address);
                    bag.setDestinationSuburb(dest_suburb);
                    // bag.setDestinationTown(dest_town);
                    bag.setDestinationLat(dest_lat);
                    bag.setDestinationLong(dest_long);
                    bag.setDestinationContact(dest_contact1);
                    bag.setScanned(scanned);
                    bag.setNumberItems(waybill_count);
                    bag.setStatus(bag_status);

                    JSONArray json_contacts = result.getJSONArray("contacts");
                    ArrayList<Contact> contacts = new ArrayList<Contact>();

                    bag.setContacts(contacts);

                    Log.d(TAG, "Bag " + id + " added: "
                            + DbHandler.getInstance(context).addBag(bag));

                    for (int i = 0; i < json_contacts.length(); i++) {
                        String name = json_contacts.getJSONObject(i).getString("name");
                        String number = json_contacts.getJSONObject(i).getString("number");
                        contacts.add(new Contact(name, number));
                        Log.d(TAG, "Contact " + name + " added: "
                                + DbHandler.getInstance(context).addContact(name, number, id));
                    }

                    // --- Waybills ---

                    JSONArray waybills = result.getJSONArray("waybills");

                    // Load each waybill in bag
                    for (int j = 0; j < waybills.length(); j++) {

                        // Tel
                        // String tel = waybills.getJSONObject(j).getString("telephone");

                        // Weight
                        String weight = waybills.getJSONObject(j).getJSONObject("dimensions")
                                .getString("weight")
                                + "kg";

                        // Dimensions
                        String dimen = waybills.getJSONObject(j).getString("dimensions");

                        // Waybill ID
                        String waybill_id = waybills.getJSONObject(j).getString("id");

                        // barcode
                        String waybill_barcode = waybills.getJSONObject(j).getString("barcode");

                        // Dimensions
                        String dimensions = waybills.getJSONObject(j).getJSONObject("dimensions")
                                .getString("width")
                                + "mmX"
                                + waybills.getJSONObject(j).getJSONObject("dimensions")
                                .getString("height")
                                + "mmX"
                                + waybills.getJSONObject(j).getJSONObject("dimensions")
                                .getString("length") + "mm";

                        // status
                        String status = waybills.getJSONObject(j).getString("status");

                        // ---- Delivery address
                        // Address

                        // --- Customer

                        // comlog
                        // comlog is a JSONArray ***
                        // String comlog = waybills.getJSONObject(j).getString("comlog");

                        // parcel count
                        String parcel_count = waybills.getJSONObject(j).getString("parcelcount");

                        // Create Waybill object and add values
                        Waybill waybill = new Waybill(waybill_id, id);
                        // waybill.setEmail(email);
                        waybill.setBarcode(waybill_barcode);
                        waybill.setDimensions(dimensions);
                        waybill.setStatus(status);
                        // waybill.setDeliveryTown(town);
                        // waybill.setDeliverySuburb(suburb);
                        // waybill.setDeliveryAddress(address);
                        // waybill.setDeliveryLat(lat);
                        // waybill.setDeliveryLong(lon);
                        // waybill.setCustomerContact1(contact1);
                        // waybill.setCustomerContact2(contact2);
                        // waybill.setCustomerID(idnumber);
                        // waybill.setCustomerName(name);
                        // waybill.setComLog(comlog);
                        waybill.setWeight(weight);
                        waybill.setParcelCount(parcel_count);

                        // Add ID to hashtable
                        Integer current_count = waybill_IDs.get(waybill_id);

                        // Calculate how many times the current waybill ID
                        // has occurred already
                        if (current_count != null) {
                            // Increment occurence count of the waybill
                            waybill_IDs.put(waybill_id, current_count + 1);

                            // nth occurance of this waybill
                            waybill.setParcelSeq(current_count + 1);
                        } else {
                            // First occurance of this waybill
                            waybill.setParcelSeq(1);
                        }

                        Log.d(TAG,
                                "Waybill " + waybill_id + " added: "
                                        + DbHandler.getInstance(context).addWaybill(waybill));

                        JSONArray comlog = waybills.getJSONObject(j).getJSONArray("commlog");

                        Log.d(TAG,
                                "Comlog added: "
                                        + DbHandler.getInstance(context).addComLog(comlog, id));

                        Log.i(TAG, "Bag list fetched.");
                    }
                } catch (NumberFormatException e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    Log.e(TAG, sw.toString());
                } catch (JSONException e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    Log.e(TAG, sw.toString());
                    if (VariableManager.DEBUG) {
                        displayToast("JSONException: bags/bag");
                    }
                }

            }
        } catch (JSONException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(TAG, sw.toString());
        }
        }
    } */

    /*public void downloadDelays(Context context) {
        String token = prefs.getString(VariableManager.PREF_TOKEN, "");
        String url = API_URL + "v1/milkruns/delays?mrdToken=" + token;

        // Log.i(TAG, "Fetching " + url);

        try {
            String response = getInputStreamFromUrl(url);

            JSONObject jObject = new JSONObject(response);

            JSONArray result = jObject.getJSONArray("response");

            ContentValues values;
            if (result != null) {
                // Stores waybill IDs as they are loaded.
                // Used to count the number of occurences
                // For counting multiple packages.
                // Hashtable<String, Integer> waybill_IDs = new Hashtable<String, Integer>();

                for (int i = 0; i < result.length(); i++) {
                    try {
                        values = new ContentValues();

                        // ID
                        String delay_id = result.getJSONObject(i).getString("id");

                        // Reason
                        String reason = result.getJSONObject(i).getString("name");

                        // Durations
                        JSONArray durations = result.getJSONObject(i).getJSONArray("items");

                        values.put(DbHandler.C_DELAYS_REASON_ID, delay_id);
                        values.put(DbHandler.C_DELAYS_REASON, reason);

                        Log.d(TAG,
                                "Adding : "
                                        + reason
                                        + " "
                                        + DbHandler.getInstance(context).addRow(
                                        DbHandler.TABLE_DELAYS, values));

                        for (int d = 0; d < durations.length(); d++) {
                            // Duration ID
                            String duration_id = durations.getJSONObject(d).getString("id");

                            // Duration
                            String duration = durations.getJSONObject(d).getString("name");

                            values = new ContentValues();

                            values.put(DbHandler.C_DELAYS_DURATION_ID, duration_id);
                            values.put(DbHandler.C_DELAYS_DURATION, duration);
                            values.put(DbHandler.C_DELAYS_REASON_ID, delay_id);

                            Log.d(TAG,
                                    "Adding : "
                                            + reason
                                            + " "
                                            + duration
                                            + " "
                                            + DbHandler.getInstance(context).addRow(
                                            DbHandler.TABLE_DELAYS_DURATIONS, values));
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (VariableManager.DEBUG) {
                            displayToast("JSONException: milkruns/delays");
                        }
                    }
                }
            }
        } catch (JSONException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(TAG, sw.toString());
            if (VariableManager.DEBUG) {
                displayToast("JSONException: milkruns/delays");
            }
        }
    }   */

    /*
    public void downloadFailedDeliveryReasons(Context context) {
        // TODO: uncomment above, delete below.
        String token = prefs.getString(VariableManager.PREF_TOKEN, "");
        String url = API_URL + "v1/milkruns/handover?mrdToken=" + token;

        // Log.i(TAG, "Fetching " + url);

        try {
            String response = getInputStreamFromUrl(url);

            JSONObject jObject = new JSONObject(response);

            JSONArray result = jObject.getJSONArray("response");

            ContentValues values;

            if (result != null) {
                // Stores waybill IDs as they are loaded.
                // Used to count the number of occurences
                // For counting multiple packages.
                // Hashtable<String, Integer> waybill_IDs = new Hashtable<String, Integer>();

                for (int i = 0; i < result.length(); i++) {
                    try {
                        values = new ContentValues();

                        // ID
                        String reason_id = result.getJSONObject(i).getString("id");

                        // Reason
                        String reason_name = result.getJSONObject(i).getString("name");

                        values.put(DbHandler.C_FAILED_HANDOVER_REASONS_ID, reason_id);
                        values.put(DbHandler.C_FAILED_HANDOVER_REASONS_NAME, reason_name);

                        DbHandler.getInstance(context).addRow(
                                DbHandler.TABLE_FAILED_HANDOVER_REASONS, values);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (VariableManager.DEBUG) {
                            displayToast("JSONException: milkruns/handover");
                        }
                    }
                }
            }
        } catch (JSONException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(TAG, sw.toString());
            if (VariableManager.DEBUG) {
                displayToast("JSONException: milkruns/handover");
            }
        }
    }
*/

    /*public void downloadPartialDeliveryReasons(Context context) {
        String token = prefs.getString(VariableManager.PREF_TOKEN, "");
        String url = API_URL + "v1/milkruns/partial?mrdToken=" + token;

        // Log.i(TAG, "Fetching " + url);

        try {
            String response = getInputStreamFromUrl(url);

            JSONObject jObject = new JSONObject(response);

            JSONArray result = jObject.getJSONArray("response");

            ContentValues values;

            if (result != null) {
                // Stores waybill IDs as they are loaded.
                // Used to count the number of occurences
                // For counting multiple packages.
                // Hashtable<String, Integer> waybill_IDs = new Hashtable<String, Integer>();

                for (int i = 0; i < result.length(); i++) {
                    try {
                        values = new ContentValues();

                        // ID
                        String reason_id = result.getJSONObject(i).getString("id");

                        // Reason
                        String reason_name = result.getJSONObject(i).getString("name");

                        values.put(DbHandler.C_PARTIAL_DELIVERY_REASONS_ID, reason_id);
                        values.put(DbHandler.C_PARTIAL_DELIVERY_REASONS_NAME, reason_name);

                        DbHandler.getInstance(context).addRow(
                                DbHandler.TABLE_PARTIAL_DELIVERY_REASONS, values);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (VariableManager.DEBUG) {
                            displayToast("JSONException: milkruns/partial");
                        }
                    }
                }
            }
        } catch (JSONException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(TAG, sw.toString());
            if (VariableManager.DEBUG) {
                displayToast("JSONException: milkruns/partial");
            }
        }
    }  */

    /**
     * Report driver position.
     *
     * @param bagid
     * @param accuracy
     * @param lat
     * @param longn
     * @param trip_stop_id
     * @param time
     * @return
     */
    public String postDriverPosition(String bagid, String accuracy, String lat, String longn,
                                     String trip_stop_id, String time) {
        String token = prefs.getString(VariableManager.PREF_TOKEN, "");
        String url = API_URL + "v1/trips/tracking?mrdToken=" + token + "&accuracy=" + accuracy
                + "&lat=" + lat + "&lon=" + longn + "&tripstopid=" + trip_stop_id + "&time=" + time;
        String result = postData(url);
        // Log.d(TAG,"zeus: "+ result);
        if (result.equals(VariableManager.TEXT_NET_ERROR)) {
            return String.valueOf(DbHandler.getInstance(context).pushCall(url, null));
        } else {
            return result;
        }
    }

    /**
     * Post a delay to API
     *
     * @param bagid
     * @param driverid
     * @param delayid
     * @return
     */
    public String postDelay(String bagid, String driverid, String delayid) {
        String token = prefs.getString(VariableManager.PREF_TOKEN, "");
        String url = API_URL + "v1/milkruns/delays?bagid=" + bagid + "&driverid=" + driverid
                + "&mrdToken=" + token + "&delayid=" + delayid;
        String result = postData(url);
        // Log.d(TAG,"zeus: "+ result);
        if (result.equals(VariableManager.TEXT_NET_ERROR)) {
            return String.valueOf(DbHandler.getInstance(context).pushCall(url, null));
        } else {
            return result;
        }
    }

    /**
     * POST a communication message to server.
     *
     * @param message
     * @param bagid
     * @param type
     * @param result
     * @return
     */
    public String postMessage(String message, String number, String bagid, String type,
                              boolean result) {
        // Convert bool to String
        String result_string = "false";
        if (result) {
            result_string = "true";
        }

        // Make JSON notation
        JSONObject comExtra = new JSONObject();
        try {
            comExtra.put("message", message);
            comExtra.put("number", number);
        } catch (JSONException e) {
            e.printStackTrace();
        }

		/*return postData("http://paperlessapp.apiary.io/v1/waybill/communication?id=" + bagid
				+ "&comType=" + type + "&mrdToken=" + token + "&comResult="
				+ result_string + "&comExtra=" + comExtra.toString());*/

        String status = null;
        String token = prefs.getString(VariableManager.PREF_TOKEN, "");
        String url = API_URL + "v1/waybill/communication?id=" + bagid + "&comType=" + type
                + "&mrdToken=" + token + "&comResult=" + result_string;

        Log.d(TAG, "Zeus: " + url);

        try {
            status = doJSONPOST(url, comExtra, 5000);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

        if (status.equals(VariableManager.TEXT_NET_ERROR)) {
            return String.valueOf(DbHandler.getInstance(context).pushCall(url, comExtra));
        } else {
            return status;
        }
    }

    /**
     * Post failed handover to API.
     *
     * @param bag_id
     * @param reason_id IDs acquired from /milkrun/handover
     * @return
     */
    public String postFailedHandover(String bag_id, String reason_id) {
        String token = prefs.getString(VariableManager.PREF_TOKEN, "");
        String url = API_URL + "v1/waybill/delivery?id=" + bag_id + "&deliveryID=" + reason_id
                + "&mrdToken=" + token + "&extra=failed";
        // Log.d(TAG, "Posting failed delivery: " + url);
        String result = postData(url);

        if (result.equals(VariableManager.TEXT_NET_ERROR)) {
            return String.valueOf(DbHandler.getInstance(context).pushCall(url, null));
        } else {
            return result;
        }

    }

    /**
     * Post failed handover to API.
     */
    public String postSuccessfulDelivery(String bag_id) {
        String token = prefs.getString(VariableManager.PREF_TOKEN, "");
        String url = API_URL + "v1/milkruns/handover?bagid=" + bag_id + "&mrdToken=" + token
                + "&extra=successful";
        // Log.d(TAG, "Posting failed delivery: " + url);
        String result = postData(url);

        if (result.equals(VariableManager.TEXT_NET_ERROR)) {
            return String.valueOf(DbHandler.getInstance(context).pushCall(url, null));
        } else {
            return result;
        }

    }

    /**
     * Post partial delivery to API.
     */
    public String postPartialDelivery(String waybill_id, String status_id) {
        String token = prefs.getString(VariableManager.PREF_TOKEN, "");
        String url = API_URL + "v1/waybill/delivery?id=" + waybill_id + "&deliveryID=" + status_id
                + "&mrdToken=" + token + "&extra=partial";
        // Log.d(TAG, "Posting partial delivery: " + url);
        String result = postData(url);

        if (result.equals(VariableManager.TEXT_NET_ERROR)) {
            return String.valueOf(DbHandler.getInstance(context).pushCall(url, null));
        } else {
            return result;
        }
    }

    public String convertInputStreamToString(InputStream is) throws Exception {
        BufferedReader rd = new BufferedReader(inputStreamToReader(new BufferedInputStream(is)),
                4096);// Checks for BOM, should still work if BOM not present
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
        } catch (Exception e) {
            throw e;
        }

        return sb.toString();
    }

    // This can be used to setup a DefaultHttpClient to ignore certificate errors over https or
    // connect to non default ports
    public DefaultHttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    private void refreshToken(String stream) {
        JSONObject obj;
        try {
            if (stream.length() > 0) {
                obj = new JSONObject(stream);

                if (obj.has("error")) {
                    String error_code = stripErrorCode(stream);
                    if (error_code.equals(VariableManager.API_ERROR_CODE_UNAUTHORISED)) // Unauthorized
                    {
                        Log.i(TAG, "Token expired");
                        // Token has expired

                        ServerInterface.getInstance(context).requestToken();
                    }
					/*else if (error_code.equals("400")) // Bad request
					{

					}
					else if (error_code.equals("404")) // Not found
					{

					}*/
                }
            }
        } catch (JSONException e) {
            // e.printStackTrace();
        }
    }

    /**
     * Perform HTTP GET request.
     *
     * @param url
     * @return
     */
    // http://www.androidsnippets.com/executing-a-http-get-request-with-httpclient
    public String getInputStreamFromUrl(final String url) {
        if (VariableManager.DEBUG) {
            Log.d(TAG, "Fetching " + url);
        }
        try {
            final int CONN_WAIT_TIME = 5000;
            final int CONN_DATA_WAIT_TIME = 8000;

            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, CONN_WAIT_TIME);
            HttpConnectionParams.setSoTimeout(httpParams, CONN_DATA_WAIT_TIME);

            HttpGet request = new HttpGet(url);

            // Depends on your web service
            request.setHeader("Content-type", "application/json");

            // DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            // DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);
            DefaultHttpClient httpclient = getNewHttpClient();

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            InputStream inputStream = entity.getContent();

            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"),
                    8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            // Check if token need refreshing
            ServerInterface.getInstance(context).refreshToken(sb.toString());

            // return status;
            return sb.toString();
        } catch (UnknownHostException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            // sw.toString();
            Log.e(TAG, "No connection");
            Log.e(TAG, sw.toString());
            return "";
        } catch (UnsupportedEncodingException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(TAG, sw.toString());
            return "";
        } catch (SocketTimeoutException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(TAG, "Connection timeout");
            Log.e(TAG, sw.toString());
            if (VariableManager.DEBUG) {
                displayToast("SocketTimeoutException");
            }
            return "";
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(TAG, sw.toString());
            return "";
        }
    }

    /**
     * Perform HTTP POST request.
     *
     * @param url
     * @return
     */
    // http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
    public String postData(String url) {
        Log.i(TAG, "Posting: " + url);

        // Create a new HttpClient and Post Header
        // HttpClient httpclient = new DefaultHttpClient();
        HttpClient httpclient = getNewHttpClient();
        HttpPost httppost = new HttpPost(url);

        try {
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            InputStream inputStream = entity.getContent();

            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"),
                    8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            // String result = sb.toString();
            // Log.d(TAG, "postData: " + result);

            // Check if token need refreshing
            ServerInterface.getInstance(context).refreshToken(sb.toString());

            return sb.toString();

        } catch (ClientProtocolException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(TAG, sw.toString());
            return "";
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(TAG, sw.toString());
            return VariableManager.TEXT_NET_ERROR;
        }
    }

    /**
     * Perform HTTP POST request with a JSON object in the message body.
     *
     * @param url
     * @param json
     * @param timeout
     * @return
     * @throws Exception
     */
    public String doJSONPOST(String url, JSONObject json, int timeout) throws Exception {
        Hashtable<String, String> temp_header_item;
        String toreturn = null;
        StringEntity temp_entity;
        InputStream content = null;
        int default_connection_timeout = 5000;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            // Timeouts
            HttpConnectionParams.setConnectionTimeout(httpclient.getParams(),
                    default_connection_timeout);// throws java.net.SocketTimeoutException : Socket
            // is not connected
            HttpConnectionParams.setSoTimeout(httpclient.getParams(), timeout);// throws
            // java.net.SocketTimeoutException
            // : The operation
            // timed out

            temp_entity = new StringEntity(json.toString());
            temp_entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

            httppost.setEntity(temp_entity);

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            content = response.getEntity().getContent();

            temp_header_item = new Hashtable<String, String>();
            for (int i = 0; i < response.getAllHeaders().length; i++) {
                temp_header_item = new Hashtable<String, String>();
                temp_header_item.put(response.getAllHeaders()[i].getName().toLowerCase(),
                        response.getAllHeaders()[i].getValue());
            }

            try {
                JSONObject obj = new JSONObject(convertInputStreamToString(content));
                // Check if token need refreshing
                ServerInterface.getInstance(context).refreshToken(obj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            toreturn = new JSONObject(convertInputStreamToString(content))
                    .getJSONObject("response").getJSONObject("waybill").getString("status");

            httpclient.getConnectionManager().shutdown();
        } catch (Exception e) {
            toreturn = VariableManager.TEXT_NET_ERROR;
        }
        return toreturn;
    }

    /**
     * Check if BOM is present and use it to determine encoding
     *
     * @param in
     * @return
     * @throws IOException
     */
    public Reader inputStreamToReader(BufferedInputStream in) throws IOException {
        in.mark(3);// Need to decorate InputStream with BufferedInputStream to enable mark and reset
        // functionality
        int byte1 = in.read();
        int byte2 = in.read();

        if (byte1 == 0xFF && byte2 == 0xFE) {
            Log.i("[HttpInterface]", "Stream has BOM and is encoded in UTF-16LE");
            return new InputStreamReader(in, "UTF-16LE");
        } else if (byte1 == 0xFF && byte2 == 0xFF) {
            Log.i("[HttpInterface]", "Stream has BOM and is encoded in UTF-16BE");
            return new InputStreamReader(in, "UTF-16BE");
        } else {
            int byte3 = in.read();
            if (byte1 == 0xEF && byte2 == 0xBB && byte3 == 0xBF) {
                Log.i("[HttpInterface]", "Stream has BOM and is encoded in UTF-8");
                return new InputStreamReader(in, "UTF-8");
            } else {
                Log.i("[HttpInterface]",
                        "Stream has no BOM falling back to ISO 8859_1 (ISO-Latin-1)");
                in.reset();
                return new InputStreamReader(in);
            }
        }
    }

    /**
     * Retrieve a single bag object
     */
    public String scanBag(Context context, String barcode, String driver_id) {
        String id = "";
        String token = prefs.getString(VariableManager.PREF_TOKEN, "");
        String url = API_URL + "v1/bags/scan?barcode=" + barcode + "&mrdToken=" + token + "&id=" + driver_id;

        Log.i(TAG, "Fetching " + url);

        try {
            String response = getInputStreamFromUrl(url);

            JSONObject jObject = new JSONObject(response);

            JSONObject result = jObject.getJSONObject("response").getJSONObject("bags");

            if (result != null) {

                // Stores waybill IDs as they are loaded.
                // Used to count the number of occurences
                // For counting multiple packages.
                Hashtable<String, Integer> waybill_IDs = new Hashtable<String, Integer>();

                try {
                    // ID
                    id = result.getString("id");

                } catch (NumberFormatException e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    Log.e(TAG, sw.toString());
                } catch (JSONException e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    Log.e(TAG, sw.toString());
                    if (VariableManager.DEBUG) {
                        displayToast("JSONException: bags/bag");
                    }
                }

            }
        } catch (JSONException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(TAG, sw.toString());
        }

        return id;
        // return "success";
    }

    public String setNextDelivery( int stopid) {
        // Store in sharedprefs
        //TODO: gary!!! this is a stop, not a bag -- check for ripples

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(VariableManager.PREF_CURRENT_STOPID, stopid);
        editor.apply();

        String token = prefs.getString(VariableManager.PREF_TOKEN, "");

        String url = API_URL + "v1/waybill/setnext?id=" + Integer.toString(stopid) + "&mrdToken=" + token;

        //<TODO, NB!!>
        //String response = postData(url);
        String response = "{'response':{'status':'success'}}";
        //</TODO, NB!!>

        String status = "";

        try {
            JSONObject jObject = new JSONObject(response);
            status = jObject.getJSONObject("response").getString("status");

        } catch (JSONException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(TAG, sw.toString());
            if (VariableManager.DEBUG) {
                displayToast("JSONException: driver?id");
            }
            return "";
            // Oops
        }

        if (VariableManager.DEBUG) {
            Log.d(TAG, "token: " + status);
        }
        return status;
    }

    private String stripErrorCode(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            String error = obj.getJSONObject("error").getString("code");
            return error;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }


    private class QueryTask extends AsyncTask<String, Void, String> {

        String token = null;

        protected String doInBackground(String... url) {
            AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
            cb.url(url[0]).type(JSONObject.class);
            aq.sync(cb);
            String Token = null;
            try
            {
                JSONObject jObject = cb.getResult();
                AjaxStatus status = cb.getStatus();
                if (jObject.has("response"))
                {
                    Token = jObject.getJSONObject("response").getJSONObject("auth").getString("token");

                } else if (jObject.has("error")) {
                    Token = jObject.toString();
                }
            } catch (JSONException e) {
                Log.e("MRD-EX" , "FIX THIS : " + e.getMessage());
            }
            Device.getInstance().setToken(Token);
            token = Token;
            return Token;
        }

        protected void onPostExecute(String result) {
            //showDialog("Downloaded " + result + " bytes");
        }
    }




}
