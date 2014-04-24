package com.mrdexpress.paperless;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.mrdexpress.paperless.db.Device;

/**
 * Created by hannobean on 2014/03/27.
 */

public class Paperless extends Application {
    private static Paperless instance;
    private static Activity mainActivity;

    public static Paperless getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    public Activity getActivity() { return mainActivity; }

    public enum PaperlessStatus {SUCCESS,FAILED,SPECIAL};

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    public void setMainActivity(Activity act){ mainActivity = act;}

    public static void handleException(Exception e){
        Device.getInstance().addDeviceLog("Exception setDeliveryStatus" , e.getMessage());
        Log.e("MRD-EX", e.getMessage());
    }
}