package com.mrdexpress.paperless.db;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import com.mrdexpress.paperless.Paperless;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.datatype.ObjectSerializer;
import com.mrdexpress.paperless.net.NetworkStatus;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by hannobean on 2014/03/27.
 */
public class Device {
    private static Device _instance = null;
    private static Context _context = null;
    private static String IMEI = null;
    private static SharedPreferences app_preferences = null;
    private static SharedPreferences.Editor editor;
    public ArrayList<DeviceLog> devicelogs = new ArrayList<DeviceLog>();
    private String Token = "400";
    private String GCMID = null;
    private String GCMGOOGLEID = null;
    private Integer AppVersion = null;
    private long QueryTimeOut = 10000;//10 seconds

    public static Device getInstance() {
        if (_instance == null) {
            _instance = new Device();
            TelephonyManager m = (TelephonyManager) Paperless.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = m.getDeviceId();
        }
        if (app_preferences == null){
            app_preferences = Paperless.getContext().getSharedPreferences("Paperless" , 0);
            editor = app_preferences.edit();
        }
        return _instance;
    }

    private void saveVar(String key , String val)
    {
        editor.putString(key , val);
    }
    public void saveVar(String key , Boolean val)
    {
        editor.putBoolean(key, val);
    }
    private void saveVar(String key , Integer val)
    {
        editor.putInt(key, val);
    }
    public void saveVar(String key , Float val)
    {
        editor.putFloat(key, val);
    }

    public void saveVar(String key , Object val)
    {
        try{
            if (val instanceof String){
                this.saveVar( key , val.toString() );
            } else
            if (val instanceof Integer){
                this.saveVar(key, Integer.parseInt(val.toString()));

            }   
        }catch(Exception e){
            Paperless.handleException(e);
        }
    }

    public void saveArrayList(String key , Serializable list)
    {
        try
        {
            editor.putString( key , ObjectSerializer.serialize(list) );
        }
        catch(Exception e)
        {
            Paperless.handleException(e);
        }
    }

    public Object getArrayList(String key)
    {
        try{
            String str = app_preferences.getString(key , "" );
            return ObjectSerializer.deserialize(str);
        }
        catch(Exception e)
        {
            Paperless.handleException(e);
            Log.e("MRD" , e.getMessage());
            return null;
        }
    }


    public String getIMEI(){
        return IMEI;
    }

    public Integer getAppVersion(){
        return this.AppVersion;
    }

    public void setAppVersion(Integer av){
        this.AppVersion = av;
    }

    public String getGCMID(){
        return this.GCMID;
    }

    public void setGCMID(String gcmid){
        this.GCMID = gcmid;
    }

    public String getGCMGOOGLEID(){
        return this.GCMGOOGLEID;
    }

    public void setGCMGOOGLEID(String id){
        this.GCMGOOGLEID = id;
    }

    public void setIMEI(){
        TelephonyManager m = (TelephonyManager) Paperless.getContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = m.getDeviceId();
    }

    public String getToken(){
        return this.Token;
    }

    public void setToken(String token){
        this.Token = token;
    }

    public Boolean isConnected() {
        return NetworkStatus.getInstance().connected();
    }

    public String getTokenIMEIUrl(){
        return "imei=" + getIMEI() + "&mrdToken=" + getToken();
    }
    public long getQueryTimeOut(){
        return QueryTimeOut;
    }

    public void addDeviceLog(String message){
        DeviceLog dl = new DeviceLog(message);
        this.devicelogs.add(dl);
    }
    public void addDeviceLog(String message , String extra){
        DeviceLog dl = new DeviceLog(message , extra);
        this.devicelogs.add(dl);
    }

    public void printDeviceLog(){
        for(int i = 0; i < this.devicelogs.size(); i++)
        {
            this.devicelogs.get(i).printLog();
        }

    }

    public class DeviceLog {
        public String datetime;
        public String message;
        public String extra;

        public DeviceLog(String message , String extra){
            Time now = new Time();
            now.setToNow();
            this.datetime = now.format2445();
            this.message = message;
            this.extra = extra;
        }

        public DeviceLog(String message){
            new DeviceLog(message , Thread.currentThread().getStackTrace().toString());
        }

        public void printLog(){
            Log.e("Devicelog" , "Time : " + this.datetime + " Message : " + this.message + " Extra : " + this.extra);
        }
    }

    public Style successm = new Style.Builder()
            .setBackgroundColorValue(Color.GREEN)
            .setGravity(Gravity.CENTER)
            .setTextColorValue(Color.BLACK)
            .setHeight(50)
            .setTextSize(15)
            .build();

    public Style failedm = new Style.Builder()
            .setBackgroundColorValue(Color.RED)
            .setGravity(Gravity.CENTER)
            .setTextColorValue(Color.BLACK)
            .setHeight(50)
            .setTextSize(15)
            .build();

    public Style infom = new Style.Builder()
            .setBackgroundColorValue(Paperless.getInstance().getResources().getColor(R.color.colour_yellow))
            .setGravity(Gravity.CENTER)
            .setTextColorValue(Color.BLACK)
            .setHeight(50)
            .setTextSize(15)
            .build();

    public void displayMessage(String message , Style st , Activity act){
        try {
            if (null != act){
                Crouton.makeText(act , message , st ).show();
            } else {
                Crouton.makeText(Paperless.getInstance().getActivity() , message , st ).show();
            }
        } catch (Exception e){
            Log.e("MRD-EX" , e.getMessage());
            Paperless.handleException(e);
        }
    }

    public void displayInfo(String message , Activity act){
        displayMessage(message , infom , act);
    }

    public void displayInfo(String message){
        displayInfo(message , null);
    }

    public void displaySuccess(String message , Activity act){
        displayMessage(message , successm , act);
    }

    public void displaySuccess(String message){
        displaySuccess(message , null);
    }

    public void displayFailed(String message , Activity act){
        displayMessage(message , failedm , act);
    }

    public void displayFailed(String message){
        displayFailed(message , null);
    }


}
