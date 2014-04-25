package com.mrdexpress.paperless;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.mrdexpress.paperless.db.Device;
import com.squareup.otto.Bus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hannobean on 2014/03/27.
 */

public class Paperless extends Application {
    private static Paperless instance;
    private static Activity mainActivity;
    public Bus ottobus;
    public Bus gcmbus;

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
        ottobus = new Bus();
        gcmbus = new Bus();
        super.onCreate();
    }

    public static String capitalize(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static String getFormattedDate(String time) {
        long timeUnix = Long.parseLong(time);
        Date myDate = new Date(timeUnix * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d yy");
        return simpleDateFormat.format(myDate);
    }

    public static String getFormattedTime(String time) {
        long timeUnix = Long.parseLong(time);
        Date myDate = new Date(timeUnix * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        return simpleDateFormat.format(myDate);
    }

    public static String getFormattedDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat spl = new SimpleDateFormat("dd/MM/yyyy");
        return spl.format(c.getTime());
    }

    public static String getFormattedTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat spl = new SimpleDateFormat("HH:mm:ss a");
        return spl.format(c.getTime());
    }

    public void setMainActivity(Activity act){ mainActivity = act;}

    public static void handleException(Exception e){
        Device.getInstance().addDeviceLog("Exception setDeliveryStatus" , e.getMessage());
        Log.e("MRD-EX", e.getMessage());
    }
}