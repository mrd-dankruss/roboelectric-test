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
        try
        {
            if( jso.containsKey( key))
                return (Integer)jso.get(key);
        }
        catch( Exception e){}
        return def;
    }

    public static Boolean getBooleanDef( JSONObject jso, String key, Boolean def)
    {
        try
        {
            if( jso.containsKey( key))
                return (Boolean)jso.get(key);
        }
        catch( Exception e){}
        return def;
    }

    public static Number getNumberDef( JSONObject jso, String key, Number def)
    {
        try
        {
            if( jso.containsKey( key))
            {
                Object temp = jso.get(key);
                if( temp instanceof Integer)
                    return ((Integer) temp).intValue();
                if( temp instanceof Float)
                    return ((Float) temp).floatValue();
                if( temp instanceof String)
                    return Float.parseFloat( (String)temp);
            }
        }
        catch( Exception e)
        {
            return def;
        }
        return def;
    }

    public static String getStringDef( JSONObject jso, String key, String def)
    {
        try
        {
            if( jso.containsKey( key))
                return (String)jso.get(key);
        }
        catch( Exception e){}
        return def;
    }

    public static JSONObject getJSONObjectDef( JSONObject jso, String key, JSONObject def)
    {
        try
        {
            if( jso.containsKey( key))
                return (JSONObject)jso.get(key);
        }
        catch( Exception e){}
        return def;
    }

    public static JSONArray getJSONArrayDef( JSONObject jso, String key, JSONArray def)
    {
        try
        {
            if( jso.containsKey( key))
                return (JSONArray)jso.get(key);
        }
        catch( Exception e){}
        return def;
    }

}
