package com.mrdexpress.paperless.db;

import android.util.Log;
import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * Created by hannobean on 2014/04/01.
 */
public class General {
    private static General _instance = null;
    public static LinkedHashMap<String , ArrayList<Communications>> comlog = new LinkedHashMap<String, ArrayList<Communications>>();
    public static String activebagid = null;

    public static General getInstance() {
        if (_instance == null) {
            _instance = new General();
        }
        return _instance;
    }
    public void setComlog(String bagid , JSONArray json){
        for(int i = 0; i < json.length(); i++)
        {
            try{
                Communications com = new Communications(
                  json.getJSONObject(i).get("datetime").toString() , json.getJSONObject(i).get("logevent").toString() , json.getJSONObject(i).get("logtype").toString()
                );
               this.AddComLog( com , bagid);
            }catch(Exception e){
                Log.e("MRD-EX", "Comlog parse error!!!");
            }
        }
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

    public void AddComLog(Communications com , String bagid){
        if (comlog.containsKey(bagid)){
            comlog.get(bagid).add(com);
        }else{
            comlog.put(bagid , new ArrayList<Communications>(Collections.singletonList(com)) );
        }
    }

    public ArrayList<Communications> getComLogFromBagId(String bagid){
        if (comlog.containsKey(bagid)){
            return comlog.get(bagid);
        }else{
            return null;
        }
    }

    public ArrayList<Communications> getComLogFromBagId(Integer bagid){
        if (comlog.containsKey( Integer.toString(bagid) )){
            return comlog.get( Integer.toString(bagid) );
        }else{
            return null;
        }
    }


    public static class Communications implements Serializable{
        public String datetime;
        public String logevent;
        public String logtype;

        public Communications(){

        }

        public Communications(String time , String event , String type){
            datetime = time;
            logevent = event;
            logtype = type;
        }

        public Communications(String time , String event){
            new Communications(time , event , "N");
        }

        public String getDatetime(){
            return datetime;
        }
        public String getLogevent(){
            return logevent;
        }
        public String getLogtype(){
            return logtype;
        }
    }


}
