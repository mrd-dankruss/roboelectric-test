package com.mrdexpress.paperless.db;

import android.os.Parcel;
import android.os.Parcelable;
import com.jayway.jsonpath.internal.JsonReader;
import com.mrdexpress.paperless.datatype.StopItem;
import com.mrdexpress.paperless.workflow.JSONObjectHelper;
import com.mrdexpress.paperless.workflow.ObservableJSONObject;
import com.mrdexpress.paperless.workflow.Workflow;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.ArrayList;

public class Bag implements Parcelable
{
    public ObservableJSONObject data;

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
        JSONObject jso = Workflow.getInstance().getTripStopByBag( this.getBagID() );
        //JSONObject jso1 = Workflow.getInstance().getTripStopByBag( this.getBagID() );
        if( jso != null) {
            jso = JSONObjectHelper.getJSONObjectDef(jso, "destination", null);
            if( jso != null) {
                return JSONObjectHelper.getJSONObjectDef(jso, "extra", null);
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
        if( jso != null)
            return JSONObjectHelper.getStringDef( jso, "address", "!");
        return "!";
    }

    public JSONObject getStop()
    {
        return Workflow.getInstance().getStopForBagId( this.getBagID());
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
        return JSONObjectHelper.getStringDef(flowdata, "barcode", "!");
    }

	public String getStatus()
	{
        JSONObject thestatus = data.getJSONObject("status");
        return JSONObjectHelper.getStringDef(thestatus, "status", "");
        //return status;
	}

    public String getReason()
    {
        JSONObject thestatus = data.getJSONObject("status");
        return JSONObjectHelper.getStringDef(thestatus, "reason", "");
        //return status;
    }

    public String getReasonDate()
    {
        JSONObject thestatus = data.getJSONObject("status");
        return JSONObjectHelper.getStringDef(thestatus, "date", "");
        //return status;
    }

	public ArrayList<Contact> getContacts()
	{
		return new ArrayList<Contact>();
	}


    // parcelable support //////////
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(data.get().toJSONString());
    }

    public void readFromParcel(Parcel in)
    {
        data = new ObservableJSONObject( (JSONObject) new JsonReader().parse( in.readString()));
    }

    public final Parcelable.Creator<Bag> CREATOR = new Parcelable.Creator<Bag>()
    {
        public Bag createFromParcel(Parcel in)
        {
            return new Bag( (JSONObject) new JsonReader().parse( in.readString()));
        }

        public Bag[] newArray(int size)
        {
            return new Bag[size];
        }
    };



}
