package com.mrdexpress.paperless.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.mrdexpress.paperless.LoginActivity;
import com.mrdexpress.paperless.Paperless;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.db.Device;
import com.mrdexpress.paperless.net.Ajax;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.workflow.Workflow;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import net.minidev.json.JSONObject;
import net.simonvt.messagebar.MessageBar;

import java.util.ArrayList;

/**
 * Created by hannobean on 2014/04/01.
 */

public class PaperlessService extends Service{
    public static Thread ajaxthread;
    public static Integer AJAX_TIMER = 90000;
    public static Integer LOCATION_TIMER = 30000;
    public static Location oldloc = null;
    public LocationListener ls = new LocationListener() {
        @Override
        public void onLocationChanged(Location location)
        {
            int bagid;
            ArrayList<Bag> bags = Workflow.getInstance().getBagsByStatus(Bag.STATUS_TODO);
            if (bags.size() > 0){
                bagid = Workflow.getInstance().currentBagID;
                if (bagid > 0){
                    //bag is set
                }else{
                    //use first bag
                    bagid = -1; //bags.get(0).getBagID();
                }
                JSONObject activestop = Workflow.getInstance().getStopForBagId( bagid );
                if (activestop != null){
                    //Update API with current location
                    if (oldloc == null){
                        ServerInterface.getInstance().postDriverPosition(
                                Float.toString(location.getAccuracy()) ,
                                Double.toString(location.getLatitude()) ,
                                Double.toString(location.getLongitude()) ,
                                activestop.get("id").toString() ,
                                Long.toString(System.currentTimeMillis()) , location
                        );
                    } else if (oldloc.getLatitude() != location.getLatitude()){
                        ServerInterface.getInstance().postDriverPosition(
                                Float.toString(location.getAccuracy()) ,
                                Double.toString(location.getLatitude()) ,
                                Double.toString(location.getLongitude()) ,
                                activestop.get("id").toString() ,
                                Long.toString(System.currentTimeMillis()) , location
                        );
                    }
                    oldloc = location;
                }
                else {
                    ServerInterface.getInstance().postDriverPosition(
                            Float.toString(location.getAccuracy()) ,
                            Double.toString(location.getLatitude()) ,
                            Double.toString(location.getLongitude()) ,
                            "-1" ,
                            Long.toString(System.currentTimeMillis()) , location
                    );
                }

            }
        }

        @Override
        public void onStatusChanged(String provider, int status,
                                    Bundle extras)
        {

        }

        @Override
        public void onProviderEnabled(String provider)
        {

        }

        @Override
        public void onProviderDisabled(String provider)
        {

        }
    };
    LocationManager locationManager;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        locationManager = (LocationManager) Paperless.getContext().getSystemService(Context.LOCATION_SERVICE);
        //Queue Thread
        ajaxthread = new Thread(new Runnable(){
            public void run() {
                // TODO Auto-generated method stub
                while(true)
                {
                    try{
                        Thread.sleep(AJAX_TIMER);
                        Ajax.getInstance().checkQueue();
                        Log.e("MRD-EX" , "Checking Queue");
                    }catch (Exception e){
                        Log.e("MRD-EX" , e.getMessage());
                    }
                }
            }
        });
        ajaxthread.start();
        this.getGPSandSend();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Device.getInstance().displayInfo("Paperless Service Started");
        Log.d("MRD-EX", "onStart");
    }

    @Override
    public void onDestroy() {
        Device.getInstance().displayInfo("Paperless Service Stopped");
        ajaxthread.stop();
        Log.d("MRD-EX", "onDestroy");
    }

    public void getGPSandSend(){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , LOCATION_TIMER , 10 , ls);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER , LOCATION_TIMER , 10 , ls);
    }

}
