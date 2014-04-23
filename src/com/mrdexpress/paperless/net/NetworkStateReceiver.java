package com.mrdexpress.paperless.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NetworkStateReceiver extends BroadcastReceiver
{
	private final static String TAG = "NetworkStateReceiver";
    private ArrayList<CallBackFunction> callbacks = new ArrayList<CallBackFunction>();

	// private Context context;

    public void addCallback( CallBackFunction c){
        callbacks.add(c);
    }

    public void removeCallback( CallBackFunction c){
        callbacks.remove(c);
    }

	public void onReceive(final Context context, Intent intent)
	{
        /*String action = intent.getAction();
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            handleWifiStateChanged( intent.getIntExtra( WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
        } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
            if (!mConnected.get()) {
                handleStateChanged( WifiInfo.getDetailedStateOf((SupplicantState) intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE)));
            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(  WifiManager.EXTRA_NETWORK_INFO);
            mConnected.set( info.isConnected());
            handleStateChanged( info.getDetailedState());
        } */

		// this.context = context;
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		Log.d("wifi", "Network connectivity change");
		if (intent.getExtras() != null)
		{
            String action = intent.getAction();
            if( action == WifiManager.SUPPLICANT_STATE_CHANGED_ACTION){

                Log.i("wifi", intent.toString());

                if (intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE) == SupplicantState.INACTIVE) {
                    WifiInfo wi = wifiManager.getConnectionInfo();
                    if (wi != null) {
                        Log.i("wifi", wi.toString());
                    }
                }
                else if (intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE) == SupplicantState.COMPLETED) {
                    WifiInfo wi = wifiManager.getConnectionInfo();
                    if (wi != null) {
                        Log.i("wifi", wi.toString());
                        Toast.makeText(context, "Connected to: " + wi.getSSID(), Toast.LENGTH_SHORT).show();

                        try {
                            for (CallBackFunction c : callbacks) {
                                if(c.execute( wi))
                                    callbacks.remove(c);
                            }
                        }
                        catch( Exception e){}
                    }
                }
                else{
                    Object c = intent.getParcelableExtra( WifiManager.EXTRA_NEW_STATE);
                    if( c != null)
                        Log.i("wifi", c.toString());
                }

                /*ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));

                NetworkInfo ni = (NetworkInfo) connectivityManager.getActiveNetworkInfo();
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED)
                {
                    Log.i("wifi", "Network " + ni.getTypeName() + " connected");

                    CallQueueObject call = DbHandler.getInstance(context).popCallQueue();

                    // Retrieve call queue from DB
                    while (call != null){
                        call = DbHandler.getInstance(context).popCallQueue();
                        if (call != null)
                        {
                            Log.d(TAG, "Zorro - Popped: " + call.getUrl());

                            final String url = call.getUrl();
                            final JSONObject json = call.getJSON();

                            new Thread(new Runnable()
                            {
                                public void run()
                                {
                                    if (json == null)
                                    {
                                        ServerInterface.getInstance(context).postData(url);
                                    }
                                    else
                                    {
                                        try
                                        {
                                            String status = ServerInterface.getInstance(context).doJSONPOST(url, json, 5000);

                                            if (status.equals(VariableManager.TEXT_NET_ERROR))
                                            {
                                                String.valueOf(DbHandler.getInstance(context).pushCall( url, json));
                                            }
                                            else
                                            {
                                            }
                                        }
                                        catch (Exception e)
                                        {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).start();

                        }
                    }
                }
                else if (ni != null && ni.getState() == NetworkInfo.State.CONNECTING){
                    Log.d("wifi", "Connecting");
                }
                else if (ni != null && (ni.getState() == NetworkInfo.State.DISCONNECTED || ni.getState() == NetworkInfo.State.DISCONNECTING)){
                    Log.d("wifi", "Disconnected");
                }
                else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)){
                    Log.d("wifi", "Offline");
                }
                else{
                    Log.d("wifi", "No connection");
                }*/
            }
            else{
                Log.i("wifi", intent.toString());
            }
		}
	}

	/**
	 * Check availability of network connection.
	 * 
	 * @param context
	 * @return True is connected.
	 */
	public static boolean checkNetworkAvailability(Context context)
	{
		/*boolean status = false;
		try
		{
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(0);
			if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
			{
				status = true;
			}
			else
			{
				netInfo = cm.getNetworkInfo(1);
				if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
					status = true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		// Log.d(TAG, "zorro - net avail: " + status);
		return status;
        */
        return NetworkStatus.getInstance().connected();
	}

}