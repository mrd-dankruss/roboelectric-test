package com.mrdexpress.paperless.workflow;

import com.jayway.jsonpath.internal.JsonReader;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by gary on 2014/03/17.
 */

/*interface OnChangeListener
{
    public void onChange( JSONObject changed);
} */

public class ObservableJSONObject extends Observable
{
    //List<OnChangeListener> listeners = new ArrayList<OnChangeListener>();

    private JSONObject _jsonObject;

    public ObservableJSONObject( JSONObject jso)
    {
        super();
        this.set( jso);
    }
    
    /*public void addListener( OnChangeListener addMe)
    {
        listeners.add( addMe);
    } */

    ////////// getters

    public JSONObject get()
    {
        return _jsonObject;
    }
    
    public int getInt( String key)
    {
        return (Integer)_jsonObject.get(key);
    }

    public Boolean getBoolean( String key)
    {
        return (Boolean)_jsonObject.get(key);
    }

    public String getString( String key)
    {
        return (String)_jsonObject.get(key);
    }

    public JSONObject getJSONObject( String key)
    {
        return (JSONObject)_jsonObject.get(key);
    }

    public JSONArray getJSONArray( String key)
    {
        return (JSONArray)_jsonObject.get(key);
    }

    public String getJSON()
    {
        return _jsonObject.toJSONString();
    }
    
    ///////// setters

    public void set(  JSONObject jso)
    {
        _jsonObject = jso;
        forceNotifyAllObservers();
    }

    public void setInt( String key, int value)
    {
        _jsonObject.put(key, value);
        forceNotifyAllObservers();
    }

    public void setBoolean( String key, Boolean value)
    {
        _jsonObject.put(key, value);
        forceNotifyAllObservers();
    }

    public void setString( String key, String value)
    {
        _jsonObject.put(key, value);
        forceNotifyAllObservers();
    }

    public void setJSONObject( String key, JSONObject value)
    {
        _jsonObject.put(key, value);
        forceNotifyAllObservers();
    }

    public void setJSONArray( String key, JSONArray value)
    {
        _jsonObject.put(key, value);
        forceNotifyAllObservers();
    }    

    public void setJSON( String json)
    {
        _jsonObject = (JSONObject) new JsonReader().parse(json);
    }

    /////////////

    public void forceNotifyAllObservers()
    {
        this.setChanged();
        this.notifyObservers();
    }

    /*public void notifyAllListeners()
    {
        for (OnChangeListener cl : listeners)
            cl.onChange( this);
    } */
}
