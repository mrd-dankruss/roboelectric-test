package com.mrdexpress.paperless.net;

import android.app.Activity;
import android.content.Context;
import com.androidquery.AQuery;
import org.json.JSONObject;

/**
 * Created by hannobean on 2014/03/25.
 */
public class Ajax {
    public Activity globalactivity;
    public Context globalcontext;
    public AQuery query;
    private static final String API_URL = "http://uat.mrdexpress.com/api/";

    public Ajax(Activity act){
        globalactivity = act;
        query = new AQuery(act);
    }

    public Ajax(Context ctx){
        globalcontext = ctx;
        query = new AQuery(ctx);
    }

    public void doAjax(String url , String callback){
        query.ajax(url , JSONObject.class , this , callback);
    }


}
