package com.mrdexpress.paperless.db;

import android.content.Context;
import android.telephony.TelephonyManager;
import com.mrdexpress.paperless.Paperless;

/**
 * Created by hannobean on 2014/03/27.
 */
public class Device {
    private static Device _instance = null;
    private static Context _context = null;
    private String IMEI = null;
    private String Token = "400";
    private String GCMID = null;
    private String GCMGOOGLEID = null;

    public static Device getInstance() {
        if (_instance == null) {
            _instance = new Device();
        }
        return _instance;
    }

    public void setGCMID(String gcmid){
        this.GCMID = gcmid;
    }

    public String getIMEI(){
        return IMEI;
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

    public void setIMEI(){
        TelephonyManager m = (TelephonyManager) Paperless.getContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = m.getDeviceId();
    }

    public void setToken(String token){
        this.Token = token;
    }

    public String getToken(){
        return this.Token;
    }

}
