package com.mrdexpress.paperless.db;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import com.mrdexpress.paperless.Paperless;
import com.mrdexpress.paperless.net.NetworkStatus;

/**
 * Created by hannobean on 2014/03/27.
 */
public class Device {
    private static Device _instance = null;
    private static Context _context = null;
    private static String IMEI = null;
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
        return _instance;
    }

    public void setGCMID(String gcmid){
        this.GCMID = gcmid;
    }

    public String getIMEI(){
        return IMEI;
    }

    public void setAppVersion(Integer av){
        this.AppVersion = av;
    }

    public Integer getAppVersion(){
        return this.AppVersion;
    }

    public String getGCMID(){
        return this.GCMID;
    }

    public void setGCMGOOGLEID(String id){
        this.GCMGOOGLEID = id;
    }
    public String getGCMGOOGLEID(){
        return this.GCMGOOGLEID;
    }

    public void setToken(String token){
        this.Token = token;
    }

    public String getToken(){
        return this.Token;
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

}
