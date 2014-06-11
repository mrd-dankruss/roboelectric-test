package com.mrdexpress.paperless.net;

import android.util.Log;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.mrdexpress.paperless.Paperless;
import com.mrdexpress.paperless.db.Device;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hannobean on 2014/03/25.
 */
public class Ajax {
    private static Ajax _instance = null;
    public static ArrayList<Queue> ajaxq = null;

    public static Ajax getInstance() {
        if (_instance == null) {
            _instance = new Ajax();
            ajaxq = new ArrayList<Queue>();
        }
        return _instance;
    }

    public void addQueue(Map<String, Object> params , String Url){
        ajaxq.add(new Queue(params , Url));
    }
    public void addQueue(String Url){
        addQueue(new HashMap<String, Object>() , Url);
    }

    public void checkQueue(){
        if (Device.getInstance().isConnected()){
            Log.e("MRDX" , "QUEUE CAN FIRE");
            //fire queue here
            for(int i=0; i < ajaxq.size(); i++ ){
                Queue q = ajaxq.get(i);
                AQuery aq = new AQuery(Paperless.getContext());
                if (q.getParams().isEmpty()){
                    //Async GET Call
                    aq.ajax(q.getUrl() , JSONObject.class , new AjaxCallback<JSONObject>(){
                        public void callback(String url, JSONObject json, AjaxStatus status) {
                            try{
                                if (json != null){
                                    //Logic here

                                }else{
                                    Log.e("MRD-EX" , "EMPTY JSON");
                                }
                            }catch(Exception e){
                                Log.e("MRD-EX" , e.getMessage());
                            }
                        }
                    });
                }
                else {
                    //Async POST Call
                    aq.ajax(q.getUrl() , q.getParams() , JSONObject.class , new AjaxCallback<JSONObject>(){
                        public void callback(String url, JSONObject json, AjaxStatus status) {
                            try{
                                if (json != null){
                                    //Logic here

                                }else{
                                    Log.e("MRD-EX" , "EMPTY JSON");
                                }
                            }catch(Exception e){
                                Log.e("MRD-EX" , e.getMessage());
                            }
                        }
                    });
                }
            }
        }
    }

    public static class Queue{
        public static Map<String, Object> params = new HashMap<String, Object>();
        public static String Url = null;

        public Queue(){

        }
        public Queue(Map<String, Object> params , String Url){
            setParams(params);
            setUrl(Url);
        }

        public void setUrl(String url){
            Url = url;
        }

        public void setParams(Map<String, Object> parameters){
            params = parameters;
        }

        public String getUrl(){
            return Url;
        }

        public Map<String , Object> getParams(){
            return params;
        }
    }
}
