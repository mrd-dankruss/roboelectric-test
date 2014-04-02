package com.mrdexpress.paperless.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by hannobean on 2014/04/01.
 */
public class AjaxQueueService extends Service{
    public static Thread mainthread;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("MRD-EX", "onStart");
        //Queue Thread
        mainthread = new Thread(new Runnable(){
            public void run() {
                // TODO Auto-generated method stub
                while(true)
                {
                    try{
                        Thread.sleep(300000);
                        Log.e("MRD-EX" , "Checking Queue");
                    }catch (Exception e){
                        Log.e("MRD-EX" , e.getMessage());
                    }
                }
            }
        });
        mainthread.start();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Toast.makeText(this, "Ajax Queue Started", Toast.LENGTH_LONG).show();
        Log.d("MRD-EX", "onStart");
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Ajax Queue Service Stopped", Toast.LENGTH_LONG).show();
        Log.d("MRD-EX", "onDestroy");
    }
}
