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

    public static Device getInstance() {
        if (_instance == null) {
            _instance = new Device();
        }
        return _instance;
    }
    public static Device getInstance(Context context) {
        if (_instance == null) {
            _instance = new Device();
        }
        _context = context;
        return _instance;
    }

    public String getIMEI(){
        return IMEI;
    }
    public void setIMEI(){
        TelephonyManager m = (TelephonyManager) Paperless.getContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = m.getDeviceId();
    }

}
