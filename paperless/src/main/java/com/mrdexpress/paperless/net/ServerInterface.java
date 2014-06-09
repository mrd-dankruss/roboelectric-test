package com.mrdexpress.paperless.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.maps.model.LatLng;
import com.mrdexpress.paperless.POJO.Tripstop;
import com.mrdexpress.paperless.Paperless;
import com.mrdexpress.paperless.channels.EventBus;
import com.mrdexpress.paperless.db.*;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.interfaces.LoginInterface;
import com.mrdexpress.paperless.workflow.Workflow;
import com.newrelic.com.google.gson.Gson;
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
import java.util.*;


public class ServerInterface {

    private final static String TAG = "ServerInterface";
    //private static final String API_URL = "http://www.mrdexpress.com/api/";
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

    // Return singleton instance of DbHandler
    public static ServerInterface getInstance() {
        if (server_interface == null) {
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

    /** POST **/
    public void setDeliveryStatus(String status , String stopids , String reason){
        String url = API_URL + "v1/workflow/updatestatus?" + Device.getInstance().getTokenIMEIUrl();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("status", status);
        params.put("bagid", stopids);
        params.put("reason", reason);
        if (Device.getInstance().isConnected()){
            aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    try{
                        if (json != null){
                            //Logic here
                            //Device.getInstance().displayInfo("Delivery Status Set");
                        }else{
                            Device.getInstance().addDeviceLog("Null JSON at setDeliveryStatus" , status.getMessage());
                            Log.e("MRD-EX" , "EMPTY JSON");
                        }
                    }catch(Exception e){
                        Device.getInstance().addDeviceLog("Exception setDeliveryStatus" , status.getMessage());
                        Log.e("MRD-EX" , e.getMessage());
                    }
                }
            });
        } else {
            Device.getInstance().addDeviceLog("setDeliveryStatus added to Queue");
            Ajax.getInstance().addQueue(params , url);
        }
    }

    /** POST **/
    public void setParcelDeliveryStatus(String status , String bagid , String reason){
        String url = API_URL + "v1/workflow/updateparcelstatus?" + Device.getInstance().getTokenIMEIUrl();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("status", status);
        params.put("bagid", bagid);
        params.put("reason", reason);
        params.put("statustime", new Date().getTime());
        if (Device.getInstance().isConnected()){
            aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                public void callback(String url, JSONObject json, AjaxStatus status) {

                    try{
                        if (json != null){
                            //Logic here
                            Device.getInstance().displayInfo("Delivery Status Set");

                        }else{
                            Device.getInstance().addDeviceLog("Null JSON at setParcelDeliveryStatus" , status.getMessage());
                            Log.e("MRD-EX" , "EMPTY JSON");
                        }
                    }catch(Exception e){
                        Device.getInstance().addDeviceLog("Exception at setParcelDeliveryStatus" , status.getMessage());
                        Log.e("MRD-EX" , e.getMessage());
                    }
                }
            });
        } else {
            Device.getInstance().addDeviceLog("setParcelDeliveryStatus added to Queue");
            Ajax.getInstance().addQueue(params , url);
        }
    }

    public void reassignStop(String stop , String driverid){
        stop = stop.replace("{" , "").replace("}" , "").toString();
        String url = API_URL + "v1/workflow/reassignstop?" + Device.getInstance().getTokenIMEIUrl() +
                "&driverID=" + driverid +
                "&stopID=" + stop +
                "&olddriverID=" + Users.getInstance().getActiveDriver().getStringid();
        try{
            //String response = getInputStreamFromUrl(url);
        }
        catch(Exception e){
            //Log.e("MRD-EX" , e.getMessage());
        }

        /*Map<String, Object> params = new HashMap<String, Object>();
        params.put("imei" , Device.getInstance().getIMEI());
        params.put("driverID" , driverid);
        params.put("stopID" , stop);
        params.put("olddriverID" , Users.getInstance().getActiveDriver().getStringid());*/
        aq.ajax(url , JSONObject.class , new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject jObject, AjaxStatus ajaxstatus) {
                try{
                    if (jObject != null){
                        if (jObject.has("response"))
                        {
                            Paperless.getInstance().ottobus.post(new EventBus.refreshWorkflow());
                            //Users.getInstance().setUsers(jObject.toString());
                        }
                    }
                }
                catch(Exception e)
                {
                    Log.e("MRD-EX" , "EMPTY JSON");
                    Device.getInstance().addDeviceLog("Exception at getUsers" , status.getMessage());
                }
            }
        });
        /*
        if (Device.getInstance().isConnected()){
            aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    try{
                        if (json != null){
                            //Logic here
                        }else{
                            Log.e("MRD-EX" , "EMPTY JSON");
                            Device.getInstance().addDeviceLog("Exception at reassignStop" , status.getMessage());
                        }
                    }catch(Exception e){
                        Log.e("MRD-EX" , e.getMessage());
                        Device.getInstance().addDeviceLog("Exception at reassignStop" , status.getMessage());
                    }
                }
            });
        } else {
            Ajax.getInstance().addQueue(params , url);
        }*/
    }


    /** POST **/
    public void setBagScanned(String bagid){
        String url = API_URL + "v1/workflow/scanbagtodriver?" + Device.getInstance().getTokenIMEIUrl() + "&driverID=" + Users.getInstance().getActiveDriver().getStringid();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("driverID", Users.getInstance().getActiveDriver());
        params.put("bagid", bagid);
        params.put("scantime", new Date().getTime());
        if (Device.getInstance().isConnected()){
            aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    try{
                        if (json != null){
                            //Logic here
                            //Device.getInstance().displayInfo("Bag Scanned");
                        }else{
                            Log.e("MRD-EX" , "EMPTY JSON");
                            Device.getInstance().addDeviceLog("Exception at setBagScanned" , status.getMessage());
                        }
                    }catch(Exception e){
                        Log.e("MRD-EX" , e.getMessage());
                        Device.getInstance().addDeviceLog("Exception at setBagScanned" , status.getMessage());
                    }
                }
            });
        } else {
            Ajax.getInstance().addQueue(params , url);
        }
    }

    /*
     * Gets Users From The API
     */
    public String getUsersURL(){
        return API_URL + "v1/driver/users?imei=" + Device.getInstance().getIMEI() + "&mrdToken=" + Device.getInstance().getToken();
    }

    public void getUsers( final CallBackFunction callback) {
        String url = getUsersURL();
        aq.ajax(url , JSONObject.class , new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject jObject, AjaxStatus ajaxstatus) {
                try{
                    if (jObject != null){
                        if (jObject.has("response"))
                        {
                            Users.getInstance().setUsers(jObject.toString());
                        }
                        if( callback != null)
                            callback.execute( null);
                    }
                }
                catch(Exception e)
                {
                    Log.e("MRD-EX" , "EMPTY JSON");
                    Device.getInstance().addDeviceLog("Exception at getUsers" , status.getMessage());
                }
            }
        });
    }

    /**
     * Makes API call to request a new session token.
     */
    public String getTokenUrl(){
        return API_URL + "v1/auth/auth?imei=" + Device.getInstance().getIMEI();
    }
    public String requestToken( final CallBackFunction callback) {
        String url = getTokenUrl();
        aq.ajax(url , JSONObject.class , new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject jObject, AjaxStatus ajaxstatus) {
                String Token = null;
                try{
                    if (jObject.has("response"))
                    {
                        try {
                            Token = jObject.getJSONObject("response").getJSONObject("auth").getString("token");
                            Device.getInstance().setToken(Token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (jObject.has("error")) {
                        Token = null;
                    }
                }
                catch(Exception e){
                    Token = null;
                    Device.getInstance().addDeviceLog("Exception at requestToken" , e.getMessage());
                }
                if( callback != null)
                    callback.execute( Token);
            }
        });

        return "";
    }

    /**
     * Registers the device for GCM
     *
     * @param gcm_id The GCM ID returned by Google GCM Service
     * @return
     */

    public void registerDeviceGCM(String gcm_id) {
        String url = API_URL + "v1/push/register?imei=" + Device.getInstance().getIMEI() + "&mrdToken=" + Device.getInstance().getToken() + "&gcmID="
                + gcm_id;
        if (Device.getInstance().isConnected()){
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
                        Device.getInstance().addDeviceLog("Exception at registerDeviceGCM" , e.getMessage());
                    }
                    Device.getInstance().setGCMID(status);
                }
            });
        } else {
            Ajax.getInstance().addQueue(url);
        }
    }

    /** Update Driver Pin
     *
     * @param id
     * @param new_pin
     * @param source
     * @param func
     * @return
     */
    public String updatePIN(String id, String new_pin, String source , CallBackFunction func) {
        String url = API_URL + "v1/auth/driver?driverID=" + id + "&mrdToken=" + Device.getInstance().getToken()
                + "&driverPIN=" + new_pin + "&imei=" + Device.getInstance().getIMEI() + "&source=" + source;

        String response = postData(url);

        String status = "";

        try {
            JSONObject jObject = new JSONObject(response);
            if (jObject.has("response")) {
                status = jObject.getJSONObject("response").getJSONObject("auth").getString("status");
                func.execute(status);
            } else if (jObject.has("error")) {
                status = stripErrorCode(jObject.toString());
                func.execute("error");
            }

        } catch (JSONException e) {
            func.execute("error");
        }

        return status;
    }

    /**
     * Submit driver authentication request (login). Receives success status.
     *
     * @param PIN
     * @return
     */
    public void authDriver(String PIN , final LoginInterface log) {
        String url = API_URL + "v1/auth/driver?imei=" + Device.getInstance().getIMEI() + "&mrdToken=" + Device.getInstance().getToken()
                + "&driverPIN=" + PIN + "&driverID=" + Users.getInstance().getActiveDriver().getStringid() + "&source=" + Users.getInstance().getActiveDriver().getSource();
        AQuery ac = new AQuery(context);
        ac.ajax(url , JSONObject.class , new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if(json != null){
                    try {
                        String jsonstatus = json.getJSONObject("response").getJSONObject("auth").getString("status");
                        if (jsonstatus.equals("success")){
                            //success Path
                            log.onLoginComplete(Paperless.PaperlessStatus.SUCCESS);
                        }else{
                            //failed path
                            log.onLoginComplete(Paperless.PaperlessStatus.SPECIAL);
                        }
                    }catch(Exception e){
                        Log.e("MRD-EX" , e.getMessage());
                        log.onLoginComplete(Paperless.PaperlessStatus.FAILED);
                    }
                } else {
                    //Error Login
                    log.onLoginComplete(Paperless.PaperlessStatus.FAILED);
                }
            }
        });
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
        String token = Device.getInstance().getToken();
        String url = API_URL + "v1/auth/manager?imei=" + imei_id + "&mrdToken=" + token + "&managerPIN=&managerID=" + man_id;

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
        String url = API_URL + "v1/workflow/get-milkrun-workflow?mrdToken=" + token+ "&driverID=" + Users.getInstance().getActiveDriver().getid();
        try {
            String response = getInputStreamFromUrl(url);
            try{
                //Parse the POJO of workflow
                Paperless.getInstance().setWflow(response);
                Tripstop a = Paperless.getInstance().wflow.getResponse().getWorkflow().getWorkflow().findTripStopById("203");
                response.toString();
            } catch(Exception e){
                Paperless.handleException(e);
            }
            Workflow.getInstance().setWorkflowFromJSON(response);
            this.loadComLog();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e(TAG, sw.toString());

        }
    }

    public void checkscanBag(String bagid , final CallBackFunction cb){
        String url = API_URL + "v1/milkruns/checkscan?bagid=" + bagid;
        AQuery ac = new AQuery(context);
        if (Device.getInstance().isConnected()){
            ac.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    cb.execute(json);
                }
            });
        } else {
            Ajax.getInstance().addQueue(url);
        }
    }

    public void startTrip(){
        String token = Device.getInstance().getToken();
        String url = API_URL + "v1/milkruns/start-milkrun?mrdToken=" + token + "&tripID=" + Workflow.getInstance().getTripID();
        AQuery ac = new AQuery(context);
        if (Device.getInstance().isConnected()){
            ac.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    String callstatus = null;
                }
            });
        } else {
            Ajax.getInstance().addQueue(url);
        }
    }

    public void endTrip(){
        String token = Device.getInstance().getToken();
        String url = API_URL + "v1/milkruns/end-trip?mrdToken=" + token + "&tripID=" + Workflow.getInstance().getTripID();
        AQuery ac = new AQuery(context);
        if (Device.getInstance().isConnected()){
            ac.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    String callstatus = null;
                }
            });
        } else {
            Ajax.getInstance().addQueue(url);
        }
    }

    public void endStop(String stopids , String driverid){
        String token = Device.getInstance().getToken();
        String url = API_URL + "v1/milkruns/end-stop?mrdToken=" + token + "&driverID=" + Users.getInstance().getActiveDriver().getid() + "&stopID=" + stopids;
        AQuery ac = new AQuery(context);
        if (Device.getInstance().isConnected()){
            ac.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    String callstatus = null;
                }
            });
        } else {
            Ajax.getInstance().addQueue(url);
        }
    }


    /**
     * Report driver position.
     *
     * @param accuracy
     * @param lat
     * @param longn
     * @param trip_stop_id
     * @param time
     * @return
     */
    public void postDriverPosition(String accuracy, String lat, String longn,
                                     String trip_stop_id, String time , Location loc) {
        String url = API_URL + "v1/trips/tracking?mrdToken=" + Device.getInstance().getToken();

        AQuery ac = new AQuery(context);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("accuracy", accuracy);
        params.put("lat", lat);
        params.put("lon", longn);
        params.put("tripstopid", trip_stop_id);
        params.put("time", time);
        params.put("heading" , loc.getBearing());
        params.put("speed" , loc.getSpeed());

        if (Device.getInstance().isConnected()){
            ac.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    String callstatus = null;
                }
            });
        } else {
            Ajax.getInstance().addQueue(params , url);
        }
    }

    /**
     * Post a delay to API
     *
     * @param bagid
     *
     * @param delayid
     * @return
     */
    public void postDelay(String bagid, String note, String delayid) {
        String url = API_URL + "v1/milkruns/delays?bagid=" + bagid + "&driverid=" + Users.getInstance().getActiveDriver().getStringid()
                + "&mrdToken=" + Device.getInstance().getToken() + "&delayid=" + delayid;
        int a = Workflow.getInstance().getTripID();
        //Object b = Workflow.getInstance().getStopForBagId( Integer.parseInt(bagid) );

        String newid = bagid.replaceAll("\\}","");
        newid = newid.replaceAll("\\{", "");

        //net.minidev.json.JSONObject js = Workflow.getInstance().getTripStop(newid);
        net.minidev.json.JSONObject js2 = Workflow.getInstance().getTripStop(bagid);
        if (js2.containsKey("comlog")){
            try{
                ArrayList<String> arlist = (ArrayList<String>)js2.get("comlog");
                arlist.add("Delay Logged " + note);
                js2.put("comlog" , arlist);
            }catch(Exception e){
                Paperless.getInstance().handleException(e);
            }
        } else {
            ArrayList<String> arlist = new ArrayList<String>();
            arlist.add("Delay Logged " + note);
            js2.put("comlog" , arlist);
        }



        AQuery ac = new AQuery(context);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("delayid", delayid);
        params.put("note", note);
        ac.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                String callstatus = null;
                try{
                    if (json != null){
                        if (json.has("response")) {
                            callstatus = json.getJSONObject("response").getString("status");
                        } else if (json.has("error")) {
                            callstatus = stripErrorCode(json.toString());
                        }
                    }else{
                        Log.e("MRD-EX" , "EMPTY JSON");
                    }
                }catch(Exception e){
                    Log.e("MRD-EX" , e.getMessage());
                }
            }
        });
    }

    public void loadComLog(){
        Boolean as = Device.getInstance().isConnected();
        ArrayList<Bag> bags = Workflow.getInstance().getBags();
        for (int i = 0; i < bags.size(); i++)
        {
            this.retieveComLog( Integer.toString(bags.get(i).getBagID()) );
        }
    }

    public void retieveComLog(final String bagid){
        String url = API_URL + "v1/waybill/communication?id=" + bagid + "&mrdToken=" + Device.getInstance().getToken();
        AQuery ac = new AQuery(context);
        ac.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                String callstatus = null;
                try{
                    if (json != null){
                        if (json.has("response")) {
                            JSONArray ar = json.getJSONObject("response").getJSONArray("waybill");
                            General.getInstance().setComlog(bagid , ar);
                        } else if (json.has("error")) {
                            callstatus = stripErrorCode(json.toString());
                        }
                    }else{
                        Log.e("MRD-EX" , "EMPTY JSON");
                    }
                }catch(Exception e){
                    Log.e("MRD-EX" , e.getMessage());
                }
            }
        });
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

                        ServerInterface.getInstance(context).requestToken( null);
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
        String token = Device.getInstance().getToken();
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

    public void getGoogleDrivingDirections( String key, LatLng myLocation, LatLng destination, final CallBackFunction callback)
    {
        String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + myLocation.latitude+ "," + myLocation.longitude + "&destination=" + destination.latitude+ "," + destination.longitude + "&sensor=false";
        aq.ajax(url , JSONObject.class , new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject jObject, AjaxStatus ajaxstatus) {
                if( callback != null)
                    callback.execute( jObject);
            }
        });

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
