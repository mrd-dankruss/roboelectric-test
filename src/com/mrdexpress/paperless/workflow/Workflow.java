package com.mrdexpress.paperless.workflow;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.jayway.jsonpath.*;
import com.mrdexpress.paperless.datatype.DeliveryHandoverDataObject;
import com.mrdexpress.paperless.datatype.DialogDataObject;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.helper.VariableManager;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
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

    public Workflow()
    {
        context = VariableManager.context;
    }

    public static Workflow getInstance()
    {
        if ( _instance == null)
        {
            _instance = new Workflow();
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
            JSONArray bags = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?].flowdata",
                    Filter.filter(Criteria.where("payloadid").eq(bagid).and("payload").eq("bag")));
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
            parcel = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[*].flowdata.parcels[?]",
                    Filter.filter(Criteria.where("id").eq(parcelid)));
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
            JSONArray ja = (JSONArray)bag.get("parcels");

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
        return getBags();
    }

    public void setWaybillScanned( String barcode, int scanned)
    {

    }
}