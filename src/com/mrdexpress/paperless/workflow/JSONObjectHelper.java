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
            return Integer.parseInt(jso.get(key).toString());
        else
            return def;
    }

    public static Boolean getBooleanDef( JSONObject jso, String key, Boolean def)
    {
        if( jso.containsKey( key))
            return Boolean.parseBoolean(jso.get(key).toString());
        else
            return def;
    }

    public static float getFloatDef( JSONObject jso, String key, float def)
    {
        if( jso.containsKey( key))
            return Float.parseFloat( jso.get(key).toString());
        else
            return def;
    }

    public static String getStringDef( JSONObject jso, String key, String def)
    {
        if( jso.containsKey( key))
            return jso.get(key).toString();
        else
            return def;
    }

    public static JSONObject getJSONObjectDef( JSONObject jso, String key, JSONObject def)
    {
        if( jso.containsKey( key))
        {
            Object temp = jso.get(key);
            if( temp instanceof JSONObject)
                return (JSONObject)temp;
        }
        return def;
    }

    public static JSONArray getJSONArrayDef( JSONObject jso, String key, JSONArray def)
    {
        if( jso.containsKey( key))
        {
            Object temp = jso.get(key);
            if( temp instanceof JSONArray)
                return (JSONArray)temp;
        }
        return def;
    }

    public static boolean exists( JSONObject jso, String key)
    {
        return jso.containsKey( key);
    }

    public static boolean empty( JSONObject jso, String key)
    {
        if( jso.containsKey( key))
        {
            return ( jso.get( key).toString().isEmpty());
        }
        return true;
    }

}
