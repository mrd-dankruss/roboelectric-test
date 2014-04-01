package com.mrdexpress.paperless.db;

import org.json.JSONArray;

import java.util.LinkedHashMap;

/**
 * Created by hannobean on 2014/04/01.
 */
public class General {
    private static General _instance = null;
    public static LinkedHashMap<String , Communications[]> comlog = null;
    public static String activebagid = null;

    public static General getInstance() {
        if (_instance == null) {
            _instance = new General();
            //comlog = new LinkedHashMap<String, Communications[]>();
        }
        return _instance;
    }
    public void setComlog(String bagid , JSONArray json){

    }
    public void setActivebagid(String bagid){
        activebagid = bagid;
    }
    public void setActivebagid(Integer bagid){
        activebagid = Integer.toString(bagid);
    }
    public String getActivebagid(){
        return activebagid;
    }


    public class Communications{
        public String datetime;
        public String logevent;
        public String logtype;

        public Communications(){

        }
    }


}
