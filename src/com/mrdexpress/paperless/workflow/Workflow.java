package com.mrdexpress.paperless.workflow;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import com.mrdexpress.paperless.datatype.DeliveryHandoverDataObject;
import com.mrdexpress.paperless.datatype.DialogDataObject;
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

    public List<JSONArray> getBags()
    {
        List<JSONArray> bags = null;
        try
        {
            bags = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?(@.payload=='bag')]");
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
            JSONArray bags = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?(@.payloadid==" + Integer.toString( bagid) + " && @.payload=='bag')].flowdata");
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
            parcel = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[*].flowdata.parcels[?(@.id==" + Integer.toString( parcelid) + "])");
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
            parcels = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?(@.payloadid==" + Integer.toString( bagid) + " && @.payload=='bag')].flowdata.parcels");
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
}