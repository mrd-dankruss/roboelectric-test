package com.mrdexpress.paperless;

import android.app.Activity;
import android.app.Application;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.mrdexpress.paperless.datatype.StopItem;
import com.mrdexpress.paperless.db.Device;
import com.mrdexpress.paperless.db.General;
import com.mrdexpress.paperless.dialogfragments.DeliveryDetailsDialogFragment;
import com.mrdexpress.paperless.dialogfragments.ViewStopDeliveryDetailsFragment;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.workflow.Workflow;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

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
        ottobus = new Bus(ThreadEnforcer.ANY);
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

    public void startViewStopDetailsFragment(StopItem stop , int position , final Activity act){
        final DialogFragment deliveryDetails = ViewStopDeliveryDetailsFragment.newInstance(new CallBackFunction() {
            @Override
            public boolean execute(Object args) {
                //adapter.notifyDataSetChanged();
                return false;
            }
        });

        Bundle bundle = new Bundle();
        String stopids = stop.getIDs();
        bundle.putString("STOP_IDS", stopids);
        bundle.putInt("ACTIVE_BAG_POSITION", position);
        if (position == 0)
            Workflow.getInstance().currentBagID = stopids;
        General.getInstance().setActivebagid(stopids);
        deliveryDetails.setArguments(bundle);
        deliveryDetails.show( act.getFragmentManager(), deliveryDetails.getTag() );
    }
}