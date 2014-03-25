package com.mrdexpress.paperless.db;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.commonsware.cwac.loaderex.SQLiteCursorLoader;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.workflow.JSONObjectHelper;
import com.mrdexpress.paperless.workflow.ObservableJSONObject;
import com.mrdexpress.paperless.workflow.Workflow;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class Bag
{
    ObservableJSONObject data;

    private final String TAG = "Bag";

	private String status;
	public static final String STATUS_COMPLETED = "completed";
	public static final String STATUS_PARTIAL = "partial";
	public static final String STATUS_TODO = "incomplete";
	public static final String STATUS_UNSUCCESSFUL = "unsuccessful";

	public Bag( JSONObject bag)
	{
        data = new ObservableJSONObject( bag);
	}

	public int getBagID()
	{
        //JSONObject flowdata = data.getJSONObject("flowdata");
		//return JSONObjectHelper.getIntDef( flowdata, "", -1);
        return JSONObjectHelper.getIntDef(data.get(), "payloadid", -1);
	}

	public String getDestination()
	{
        JSONObject jso = Workflow.getInstance().getStopForBagId( this.getBagID());
        if( jso != null) {
            jso = JSONObjectHelper.getJSONObjectDef(jso, "destination", null);
            if( jso != null) {
                return JSONObjectHelper.getStringDef(jso, "desc", "!");
            }
        }
        return "!";
	}

    public JSONObject getDestinationExtra()
    {
        JSONObject jso = Workflow.getInstance().getStopForBagId( this.getBagID());
        if( jso != null) {
            jso = JSONObjectHelper.getJSONObjectDef(jso, "destination", null);
            if( jso != null) {
                return JSONObjectHelper.getJSONObjectDef( jso, "extra", null);
            }
        }
        return null;
    }

	public int getNumberItems()
	{
        JSONObject flowdata = data.getJSONObject("flowdata");
        JSONArray parcels = JSONObjectHelper.getJSONArrayDef(flowdata, "parcels", null);
        return parcels.size();
	}

	public boolean getScanned()
	{
        JSONObject flowdata = data.getJSONObject("flowdata");
        return !JSONObjectHelper.empty( flowdata, "scannedtime");
        //return JSONObjectHelper.getBooleanDef( flowdata, "scannedtime", false);
	}

    public void setScanned( int unixtime)
    {
        JSONObject flowdata = data.getJSONObject("flowdata");
        if( unixtime == -1)
            flowdata.remove("scannedtime");
        else
            flowdata.put( "scannedtime", unixtime);
        // TODO: check if this is propogated to the server
        data.forceNotifyAllObservers();
    }

	public String getDestinationAddress()
	{
        JSONObject jso = Workflow.getInstance().getStopForBagId( this.getBagID());
        return JSONObjectHelper.getStringDef( jso, "address", "!");
    }

	public String getDestinationContact()
	{
		return "Not Implemented Yet";
	}

	public float getDestinationLat()
	{
        JSONObject flowdata = data.getJSONObject("flowdata");
        JSONObject coords = JSONObjectHelper.getJSONObjectDef(flowdata, "coords", null);
        return JSONObjectHelper.getFloatDef(coords, "lat", 0);
	}

	public float getDestinationLong()
	{
        JSONObject flowdata = data.getJSONObject("flowdata");
        JSONObject coords = JSONObjectHelper.getJSONObjectDef(flowdata, "coords", null);
        return JSONObjectHelper.getFloatDef(coords, "lon", 0);
    }

	public String getBarcode()
	{
        JSONObject flowdata = data.getJSONObject("flowdata");
        return JSONObjectHelper.getStringDef(flowdata, "barcode", "!");		}

	public String getStatus()
	{
		return status;
	}

	public ArrayList<Contact> getContacts()
	{
		return new ArrayList<Contact>();
	}
}
