package com.mrdexpress.paperless.workflow;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

/**
 * Created by gary on 2014/03/17.
 */
public class JSONObjectHelper extends JSONObject 
{
    public static int getIntDef( JSONObject jso, String key, int def)
    {
        if( jso.containsKey( key))
            return (Integer)jso.get(key);
        else
            return def;
    }

    public static Boolean getBooleanDef( JSONObject jso, String key, Boolean def)
    {
        if( jso.containsKey( key))
            return (Boolean)jso.get(key);
        else
            return def;
    }
    
    public static String getStringDef( JSONObject jso, String key, String def)
    {
        if( jso.containsKey( key))
            return (String)jso.get(key);
        else
            return def;
    }

    public static JSONObject getJSONObjectDef( JSONObject jso, String key, JSONObject def)
    {
        if( jso.containsKey( key))
            return (JSONObject)jso.get(key);
        else
            return def;
    }

    public static JSONArray getJSONArrayDef( JSONObject jso, String key, JSONArray def)
    {
        if( jso.containsKey( key))
            return (JSONArray)jso.get(key);
        else
            return def;
    }

}
