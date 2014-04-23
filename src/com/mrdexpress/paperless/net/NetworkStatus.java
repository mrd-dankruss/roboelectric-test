package com.mrdexpress.paperless.net;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import com.mrdexpress.paperless.Paperless;
import com.mrdexpress.paperless.interfaces.CallBackFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gary on 2014/04/23.
 */
public class NetworkStatus {
    private static NetworkStatus instance = null;
    private boolean wifi = false;
    private boolean mobile = false;
    private static NetworkStateReceiver networkStateReceiver;
    private WifiManager wifiManager;
    private static Context context;

    public static NetworkStatus getInstance(){
        if( instance == null){
            instance = new NetworkStatus();
            networkStateReceiver = new NetworkStateReceiver();
        }
        return instance;
    }

    public void unregister()
    {
        context.unregisterReceiver(networkStateReceiver);
    }

    public void addCallback( CallBackFunction c){
        networkStateReceiver.addCallback(c);
    }

    public void removeCallback( CallBackFunction c){
        networkStateReceiver.removeCallback(c);
    }

    public void register( Context _context){
        context = _context;
        wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        //filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        context.registerReceiver(networkStateReceiver, filter);
         /*wifiManager.startScan();
        List<ScanResult> results = wifiManager.getScanResults();
        for (ScanResult result : results) {
            Toast.makeText(this, result.SSID + " " + result.level, Toast.LENGTH_SHORT).show();
        }*/
    }

    private void check(){
        wifi = false;
        mobile = false;
        ConnectivityManager cm = (ConnectivityManager) Paperless.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    wifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    mobile = true;
        }
    }

    public boolean AddAndEnableWifiNetwork( String SSID, String password, int priority, boolean notBroadcastingSSID){

        WifiManager wifiManager = (WifiManager)Paperless.getInstance().getSystemService(Context.WIFI_SERVICE);

        // setup a wifi configuration
        WifiConfiguration wc = null;

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        if( list != null){
            for( WifiConfiguration i : list){
                if( i.SSID.equals("\"" + SSID + "\"")){
                    wc = i;
                    break;
                }
            }
        }

        if( wc == null){
            wc = new WifiConfiguration();
            wc.SSID = "\"" + SSID + "\"";
        }
        wc.preSharedKey = "\"" + password + "\"";
        //wc.preSharedKey = "55706bbb187ef71e15c60bec31fdc67edeeb4220fea87079231a2a8845a080bd";
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.hiddenSSID = notBroadcastingSSID;
        wc.priority = priority;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        // connect to and enable the connection
        int netId = wifiManager.addNetwork(wc);
        wifiManager.enableNetwork(netId, true);
        wifiManager.setWifiEnabled(true);

        wifiManager.saveConfiguration();

        return true;
    }

    public boolean connected(){
        check();
        return wifi | mobile;
    }

    public boolean hasWifi(){
        check();
        return wifi;
    }

    public boolean hasMobile(){
        check();
        return mobile;
    }
}
