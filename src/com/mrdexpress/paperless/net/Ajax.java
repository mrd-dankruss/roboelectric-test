package com.mrdexpress.paperless.net;

import android.app.Activity;
import android.content.Context;
import com.androidquery.AQuery;
import org.json.JSONObject;

/**
 * Created by hannobean on 2014/03/25.
 */
public class Ajax {
    private static Ajax _instance = null;
    public Activity globalactivity;
    public Context globalcontext;
    public AQuery query;
    private static final String API_URL = "http://uat.mrdexpress.com/api/";

    public static Ajax getInstance() {
        if (_instance == null) {
            _instance = new Ajax();
        }
        return _instance;
    }



    public void doAjax(String url , String callback){
        query.ajax(url , JSONObject.class , this , callback);
    }


}
