package com.mrdexpress.paperless.workflow;

import android.content.Context;
import android.util.Log;
import com.jayway.jsonpath.*;
import com.mrdexpress.paperless.datatype.DeliveryHandoverDataObject;
import com.mrdexpress.paperless.datatype.DialogDataObject;
import com.mrdexpress.paperless.datatype.UserItem;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.helper.VariableManager;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

/**
 * Created by gary on 2014/03/13.
 */

public class Workflow extends Observable
{
    private static Workflow _instance = null;

    private Context context;

    private ReadContext workflow;
    private ReadContext drivers;
    private ReadContext managers;

    public int currentBagID;

    public HashMap<String,Object> doormat;

    public Workflow()
    {
    }

    public static Workflow getInstance()
    {
        if ( _instance == null)
        {
            _instance = new Workflow();
            _instance.doormat = new HashMap<String, Object>();
            _instance.context = VariableManager.context;    // TODO - where is this used?

        }

        return _instance;
    }

    public ReadContext getWorkflow()
    {
        return workflow;
    }

    public String getWorkflowAsJSON()
    {
        return workflow.json().toString();
    }

    public void setWorkflowFromJSON( String json)
    {
        workflow = JsonPath.parse( json);
        this.notifyObservers();
    }

    public ArrayList<DialogDataObject> getFailedHandoverReasons()
    {
        ArrayList<DialogDataObject> reasons = null;

        reasons = new ArrayList<DialogDataObject>();

        try
        {
            JSONArray r = workflow.read("$.response.workflow.handover[*]");

            if( r instanceof JSONArray)
            {
                for( int i=0; i < r.size(); i++)
                {
                    JSONObject ro = (JSONObject)r.get(i);
                    DialogDataObject reason = new DialogDataObject( JSONObjectHelper.getStringDef( ro, "name", ""), Integer.toString( JSONObjectHelper.getIntDef( ro, "id", -1)));
                    reasons.add(reason);
                }
            }
        }
        catch( PathNotFoundException e)
        {
            Log.e("workflow", e.toString());
        }
        catch( Exception e)
        {
            Log.e("gary", e.toString());
        }

        return reasons;
    }

    public ArrayList<DialogDataObject> getMilkrunDelayReasons()
    {
        ArrayList<DialogDataObject> delays = null;

        delays = new ArrayList<DialogDataObject>();

        try
        {
            JSONArray r = workflow.read("$.response.workflow.delays");

            if( r instanceof JSONArray)
            {
                for( int i=0; i < r.size(); i++)
                {
                    JSONObject ro = (JSONObject)r.get(i);

                    DialogDataObject delay = new DialogDataObject( JSONObjectHelper.getStringDef( ro, "name", ""), ""); //Integer.toString( JSONObjectHelper.getIntDef( ro, "id", -1)));
                    delay.setThirdText( Integer.toString( JSONObjectHelper.getIntDef( ro, "id", -1)));
                    delays.add(delay);
                }
            }
        }
        catch( PathNotFoundException e)
        {
            Log.e("workflow", e.toString());
        }
        catch( Exception e)
        {
            Log.e("gary", e.toString());
        }

        return delays;
    }

    public ArrayList<DialogDataObject> getMilkrunDelayDurations(String reason_id)
    {
        ArrayList<DialogDataObject> delays = null;

        delays = new ArrayList<DialogDataObject>();

        try
        {
            JSONArray r = workflow.read("$.response.workflow.delays[?(@.id==" + reason_id + ")]");

            if( r instanceof JSONArray)
            {
                for( int i=0; i < r.size(); i++)
                {
                    JSONObject ro = (JSONObject)r.get(i);

                    JSONArray items = JSONObjectHelper.getJSONArrayDef( ro, "items", null);
                    if( items != null)
                    {
                        for( int n=0; n < items.size(); n++)
                        {
                            JSONObject item = (JSONObject)items.get(n);

                            DialogDataObject delay = new DialogDataObject( JSONObjectHelper.getStringDef( item, "name", ""), ""); //Integer.toString( JSONObjectHelper.getIntDef( item, "id", -1)));
                            delays.add(delay);
                        }
                    }
                }
            }
        }
        catch( PathNotFoundException e)
        {
            Log.e("workflow", e.toString());
        }
        catch( Exception e)
        {
            Log.e("gary", e.toString());
        }

        return delays;
    }

    public ArrayList<DialogDataObject> getPartialDeliveryReasons()
    {
        ArrayList<DialogDataObject> reasons = null;

        reasons = new ArrayList<DialogDataObject>();

        try
        {
            JSONArray r = workflow.read("$.response.workflow.partial[*]");

            if( r instanceof JSONArray)
            {
                for( int i=0; i < r.size(); i++)
                {
                    JSONObject ro = (JSONObject)r.get(i);
                    DialogDataObject reason = new DialogDataObject( JSONObjectHelper.getStringDef( ro, "name", ""), Integer.toString( JSONObjectHelper.getIntDef( ro, "id", -1)));
                    reasons.add(reason);
                }
            }
        }
        catch( PathNotFoundException e)
        {
            Log.e("workflow", e.toString());
        }
        catch( Exception e)
        {
            Log.e("gary", e.toString());
        }

        return reasons;
    }

    public List<JSONArray> getBagsAsJSONArray()
    {
        List<JSONArray> bags = null;
        try
        {
            bags = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?(@.payload eq 'bag')]");
        }
        catch( PathNotFoundException e)
        {
            Log.e("workflow", e.toString());
        }
        catch( Exception e)
        {
            Log.e("gary", e.toString());
        }
        return bags;
    }

    public JSONObject getBag( int bagid)
    {
        JSONObject bag = null;
        try
        {
            //JSONArray bags = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?(@.payloadid==" + Integer.toString( bagid) + " && @.payload eq 'bag')].flowdata");
            //JSONArray bags = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?].flowdata",
            //        Filter.filter(Criteria.where("payloadid").eq(bagid).and("payload").eq("bag")));
            JSONArray bags = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?]",
                    Filter.filter(Criteria.where("payloadid").eq(bagid).and("payload").eq("bag")));
            if( bags.size() > 0)
                bag = (JSONObject)bags.get(0);
        }
        catch( PathNotFoundException e)
        {
            Log.e("workflow", e.toString());
        }
        catch( Exception e)
        {
             Log.e("gary", e.toString());
        }
        return bag;
    }

    public JSONObject getParcel( int parcelid)
    {
        JSONObject parcel = null;
        try
        {
            //parcel = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[*].flowdata.parcels[?(@.id==" + Integer.toString( parcelid) + "])");
            JSONArray parcels = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[*].flowdata.parcels[?]",
                    Filter.filter(Criteria.where("id").eq(parcelid)));
            if( parcels.size() > 0)
                parcel = (JSONObject) parcels.get(0);
        }
        catch( PathNotFoundException e)
        {
            Log.e("workflow", e.toString());
        }
        catch( Exception e)
        {
            Log.e("gary", e.toString());
        }
        return parcel;
    }

    public List<JSONArray> getBagParcelsOnly( int bagid)
    {
        List<JSONArray> parcels = null;
        try
        {
            //parcels = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?(@.payloadid==" + Integer.toString( bagid) + " && @.payload eq 'bag')].flowdata.parcels");
            parcels = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?].flowdata.parcels",
                    Filter.filter(Criteria.where("payloadid").eq(bagid).and("payload").eq("bag")));
        }
        catch( PathNotFoundException e)
        {
            Log.e("workflow", e.toString());
        }
        catch( Exception e)
        {
            Log.e("gary", e.toString());
        }
        return parcels;

    }

    public ArrayList<DeliveryHandoverDataObject> getBagParcelsAsObjects( int bagid)
    {
        ArrayList<DeliveryHandoverDataObject> parcels = new ArrayList<DeliveryHandoverDataObject>();

        JSONObject bag = this.getBag(bagid);

        if( bag instanceof JSONObject)
        {
            JSONObject jso = (JSONObject)bag.get("flowdata");
            if( jso != null)
            {
                JSONArray ja = (JSONArray)jso.get("parcels");

                if( ja instanceof JSONArray)
                {
                    for( int i=0; i < ja.size(); i++)
                    {
                        ObservableJSONObject bagparcel = new ObservableJSONObject( (JSONObject)ja.get(i));
                        DeliveryHandoverDataObject parcel = new DeliveryHandoverDataObject( bagparcel);
                        parcels.add( parcel);
                    }
                }
            }
        }

        return parcels;
    }

	public ArrayList<Bag> getBags()
	{
        ArrayList<Bag> bags = new ArrayList<Bag>();

        try
        {
            //JSONArray rawbags = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?(@.payload == 'bag')]");
            JSONArray rawbags = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?]",
                    Filter.filter(Criteria.where("payload").eq("bag")));

            for( int i=0; i < rawbags.size(); i++)
            {
                Bag bag = new Bag( (JSONObject)rawbags.get(i));
                bags.add(bag);
            }
        }
        catch( PathNotFoundException e)
        {
            Log.e("workflow", e.toString());
        }
        catch( Exception e)
        {
            Log.e("gary", e.toString());
        }

        return bags;
    }

    public JSONObject getStopForBagId( int bagid)
    {
        JSONObject stop = null;
        try
        {
            JSONArray stops = workflow.read("$.response.workflow.workflow.tripstops[?(@.tripstopdata[0].payload=='bag' && @.tripstopdata[0].payloadid==" + Integer.toString( bagid) + ")]");
            //JSONArray stops = workflow.read("$.response.workflow.workflow.tripstops[?]",
            //        Filter.filter(Criteria.where("payload").eq("bag").and("payloadid").eq(bagid)));
            if( stops.size() > 0)
                stop = (JSONObject) stops.get(0);
        }
        catch( PathNotFoundException e)
        {
            Log.e("workflow", e.toString());
        }
        catch( Exception e)
        {
            Log.e("gary", e.toString());
        }
        return stop;
        //$.response.workflow.workflow.tripstops[?(@.id==20)]
        // $.response.workflow.workflow.tripstops[?(@.tripstopdata[0].id==22)]
    }

    public JSONArray getBagsScanned( Boolean scanned)
    {
        JSONArray rawbags = new JSONArray();
        try
        {
            //JSONArray rawbags = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?(@.payload == 'bag' && @.flowdata.scannedtime)]");
            JSONArray bags = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?]",
                    Filter.filter(Criteria.where("payload").eq("bag")));
                    //Filter.filter(Criteria.where("payload").eq("bag").and("flowdata.scannedtime").notEmpty()));
            // workaround for jsonpath not allowing deeper paths in filter
            for( int i=0; i < bags.size(); i++)
            {
                JSONObject flowdata = JSONObjectHelper.getJSONObjectDef((JSONObject) bags.get(i), "flowdata", null);
                if( flowdata != null) {
                    if( scanned) {
                        if (JSONObjectHelper.exists(flowdata, "scannedtime") && JSONObjectHelper.getIntDef(flowdata, "scannedtime", -1) != -1)
                            rawbags.add(bags.get(i));
                    }
                    else
                    {
                        if( !JSONObjectHelper.exists(flowdata, "scannedtime") || ((JSONObjectHelper.exists(flowdata, "scannedtime") && JSONObjectHelper.getIntDef(flowdata, "scannedtime", -1) == -1)))
                            rawbags.add(bags.get(i));
                    }
                }
            }
        }
        catch( PathNotFoundException e)
        {
            Log.e("workflow", e.toString());
        }
        catch( Exception e)
        {
            Log.e("gary", e.toString());
        }
        if( rawbags == null)
            rawbags = new JSONArray();
        return rawbags;
    }

    public ArrayList<String> getBagBarcodesScanned()
    {
        ArrayList<String> ret = new ArrayList<String>();
        try
        {
            JSONArray bags = this.getBagsScanned(true);
            //JSONArray rawbags = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?(@.payload eq 'bag' && @.flowdata.scannedtime)]");
            //ret = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?].flowdata.barcode",
            //        Filter.filter(Criteria.where("payload").eq("bag").and("scannedtime").notEmpty()));

            for( int i=0; i < bags.size(); i++)
            {
                JSONObject flowdata = JSONObjectHelper.getJSONObjectDef( (JSONObject)bags.get(i), "flowdata", null);
                if ( flowdata != null)
                    ret.add( JSONObjectHelper.getStringDef( flowdata, "barcode", "!"));
            }
        }
        catch( PathNotFoundException e)
        {
            Log.e("workflow", e.toString());
        }
        catch( Exception e)
        {
            Log.e("gary", e.toString());
        }
        return ret;
    }

    public ArrayList<String> getBagBarcodesUnscanned()
    {
        ArrayList<String> ret = new ArrayList<String>();
        try
        {
            JSONArray bags = this.getBagsScanned(false);
            //JSONArray rawbags = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?(@.payload eq 'bag' && @.flowdata.scannedtime)]");
            //ret = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?].flowdata.barcode",
            //        Filter.filter(Criteria.where("payload").eq("bag").and("scannedtime").notEmpty()));

            for( int i=0; i < bags.size(); i++)
            {
                JSONObject flowdata = JSONObjectHelper.getJSONObjectDef( (JSONObject)bags.get(i), "flowdata", null);
                if ( flowdata != null)
                    ret.add( JSONObjectHelper.getStringDef( flowdata, "barcode", "!"));
            }
        }
        catch( PathNotFoundException e)
        {
            Log.e("workflow", e.toString());
        }
        catch( Exception e)
        {
            Log.e("gary", e.toString());
        }
        return ret;
    }

    public ArrayList<Bag> getBagsByStatus( String status)
    {
        JSONArray bags = this.getBagsScanned(true);
        ArrayList<Bag> retbags = new ArrayList<Bag>();

        for( int i=0; i < bags.size(); i++)
        {
            JSONObject jso = JSONObjectHelper.getJSONObjectDef((JSONObject) bags.get(i), "status", new JSONObject());
            if (JSONObjectHelper.getStringDef(jso, "status", Bag.STATUS_TODO).equals(status))
                retbags.add(new Bag((JSONObject) bags.get(i)));
        }

        return retbags;
    }

    public void setWaybillScanned( String barcode, int scanned)
    {
        // TODO: propogate this to the server
    }

    public void setDeliveryStatus( int bagid, String status, String reason)
    {
        JSONObject bag = getBag( bagid);
        if( bag != null)
        {
            // TODO: propogate this to the server
            JSONObject jsostatus = JSONObjectHelper.getJSONObjectDef( bag, "status", new JSONObject());
            jsostatus.put("status", status);
            jsostatus.put("reason", reason);
            bag.put("status", jsostatus);
        }
    }

    public void setParcelDeliveryStatus( int parcelid, String status, String reason)
    {
        JSONObject parcel = getParcel( parcelid);
        if( parcel != null)
        {
            // TODO: propogate this to the server
            JSONObject jsostatus = JSONObjectHelper.getJSONObjectDef( parcel, "status", new JSONObject());
            jsostatus.put("status", status);
            jsostatus.put("reason", reason);
            parcel.put("status", jsostatus);
        }
    }

    public ArrayList<HashMap<String, String>> getBagCoords()
    {
        ArrayList<HashMap<String, String>> bags = null;
        bags = new ArrayList<HashMap<String, String>>();

        /*while (true)
        {
            String address = cursor.getString(cursor.getColumnIndex(C_BAG_DEST_ADDRESS));

            String hubname = cursor.getString(cursor.getColumnIndex(C_BAG_DEST_HUBNAME));

            String lat = cursor.getString(cursor.getColumnIndex(C_BAG_DEST_LAT));

            String lon = cursor.getString(cursor.getColumnIndex(C_BAG_DEST_LONG));

            HashMap<String, String> bag = new HashMap<String, String>();
            bag.put(VariableManager.EXTRA_BAG_ADDRESS, address);
            bag.put(VariableManager.EXTRA_BAG_HUBNAME, hubname);
            bag.put(VariableManager.EXTRA_BAG_LAT, lat);
            bag.put(VariableManager.EXTRA_BAG_LON, lon);

            bags.add(bag);
        } */
        return bags;
    }

    /////////////////////////////////////////
    // DRIVERS
    /////////////////////////////////////////

    public void setDriversFromJSON( String json)
    {
        drivers = JsonPath.parse( json);
        this.notifyObservers();
    }

    public ArrayList<UserItem> getDrivers()
    {
        ArrayList<UserItem> drivers = null;

        drivers = new ArrayList<UserItem>();

        try
        {
            JSONArray r = this.drivers.read("$.response.drivers");

            if( r instanceof JSONArray)
            {
                for( int i=0; i < r.size(); i++)
                {
                    JSONObject ro = (JSONObject)r.get(i);
                    ro.put("role", "{DRIVER}");

                    UserItem user = new UserItem( new ObservableJSONObject(ro));
                    drivers.add( user);
                }
            }
        }
        catch( PathNotFoundException e)
        {
            Log.e("workflow drivers", e.toString());
        }
        catch( Exception e)
        {
            Log.e("gary", e.toString());
        }

        return drivers;

    }

    /////////////////////////////////////////
    // MANAGERS
    /////////////////////////////////////////

    public void setManagersFromJSON( String json)
    {
        managers = JsonPath.parse( json);
        this.notifyObservers();
    }

    public ArrayList<UserItem> getManagers()
    {
        ArrayList<UserItem> managers = null;

        managers = new ArrayList<UserItem>();

        try
        {
            JSONArray r = this.managers.read("$.response.manager");

            if( r instanceof JSONArray)
            {
                for( int i=0; i < r.size(); i++)
                {
                    JSONObject ro = (JSONObject)r.get(i);
                    ro.put("role", "{MANAGER}");

                    UserItem user = new UserItem( new ObservableJSONObject(ro));
                    managers.add(user);
                }
            }
        }
        catch( PathNotFoundException e)
        {
            Log.e("workflow drivers", e.toString());
        }
        catch( Exception e)
        {
            Log.e("gary", e.toString());
        }

        return managers;
    }


}