package com.mrdexpress.paperless.workflow;

import android.content.Context;
import android.util.Log;
import com.jayway.jsonpath.*;
import com.mrdexpress.paperless.Paperless;
import com.mrdexpress.paperless.datatype.DeliveryHandoverDataObject;
import com.mrdexpress.paperless.datatype.DialogDataObject;
import com.mrdexpress.paperless.datatype.StopItem;
import com.mrdexpress.paperless.datatype.UserItem;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.db.Device;
import com.mrdexpress.paperless.db.Users;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.*;

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

    class StopComparator implements Comparator<StopItem>
    {
        @Override
        public int compare(StopItem lhs, StopItem rhs) {
            if( lhs.getTripOrder() > rhs.getTripOrder()) return 1;
            return 0;
        }
    }

    class StopJSONObjectComparator implements Comparator<JSONObject>
    {
        @Override
        public int compare(JSONObject lhs, JSONObject rhs) {
            if( JSONObjectHelper.getIntDef( lhs, "triporder", -1)  > JSONObjectHelper.getIntDef( rhs, "triporder", -1)) return 1;
            if( JSONObjectHelper.getIntDef( lhs, "triporder", -1) < JSONObjectHelper.getIntDef( rhs, "triporder", -1)) return -1;
            return 0;
        }
    }

    public void setNextStop( int bagid)
    {
        JSONObject stop = getStopForBagId( bagid);
        if( stop != null)
        {
            List<JSONObject> stops = workflow.read("$.response.workflow.workflow.tripstops[*]");
            int thisidx = stops.indexOf( stop);
            StopItem thisstop = new StopItem( new ObservableJSONObject( (JSONObject)stops.get( thisidx)));

            for( int i=0; i < stops.size(); i++)
            {
                StopItem istop = new StopItem( new ObservableJSONObject( (JSONObject)stops.get( i)));
                if( istop.getTripOrder() == 1)
                {
                    istop.setTripOrder( thisstop.getTripOrder());
                    thisstop.setTripOrder(1);
                    break;
                }
            }

            //Collections.sort( stops, new StopJSONObjectComparator());
            currentBagID = bagid;
        }
    }

    public Integer getTripID()
    {
        Integer id = -1;
        try
        {
            JSONObject jso = workflow.read("$.response.workflow.workflow.id");
            if( jso != null)
                id = JSONObjectHelper.getIntDef( jso, "id", -1);
        }
        catch( PathNotFoundException e)
        {
            Log.e("workflow", e.toString());
        }
        catch( Exception e)
        {
            Log.e("gary", e.toString());
        }
        return id;
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

    public JSONObject getParcelByParcelBarcode( String barcode)
    {
        JSONObject parcel = null;
        try
        {
            JSONArray parcels = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[*].flowdata.parcels[?]",
                    Filter.filter(Criteria.where("barcode").eq(barcode)));
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
            JSONArray rawbags = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?]",
                    Filter.filter(Criteria.where("payload").eq("bag")));

            for( int i=0; i < rawbags.size(); i++)
            {
                Bag bag = new Bag( (JSONObject)rawbags.get(i));
                String devid = Device.getInstance().getIMEI();
                //if (devid.equals("356779059317726")){ bag.setScanned(1); }
                //bag.setScanned(1);
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

    public JSONObject getTripStopByBag( int bagid ){
        JSONObject stop = null;
        try
        {
            JSONArray stops = workflow.read("$.response.workflow.workflow.tripstops[?(@.tripstopdata[0].payload=='bag')]");
            for(int i = 0; i < stops.size(); i++){
                 JSONObject p = (JSONObject)stops.get(i);
                if (p.containsKey("tripstopdata")){
                    try{
                        JSONArray jr = (JSONArray)p.get("tripstopdata");
                        if (jr.size() > 0){
                            for(int j = 0; j < jr.size(); j++){
                                JSONObject pp = (JSONObject)jr.get(j);
                                if (pp.containsKey("payloadid")){
                                    if (pp.get("payloadid").toString().equals(Integer.toString(bagid)) ){
                                        stop =  p;
                                        break;
                                    }
                                }
                            }
                        }
                    }catch(Exception e){
                        Log.e("MRD-EX" , e.getMessage());
                    }
                }
            }

        }
        catch(Exception e)
        {
            //Log.e("MRD-EX" , e.getMessage());
        }
        return stop;
    }

    public JSONObject getStopForBagId( int bagid)
    {
        return this.getTripStopByBag( bagid );
        /*JSONObject stop = null;
        try
        {
            JSONArray stops = workflow.read("$.response.workflow.workflow.tripstops[?(@.tripstopdata[0].payload=='bag' && @.tripstopdata[0].payloadid==" + Integer.toString(bagid) + ")]");
            if( stops.size() > 0)
                stop = (JSONObject) stops.get(0);
        }
        catch( PathNotFoundException e)
        {
            Log.e("MRD-EX-2", e.toString());
        }
        catch( Exception e)
        {
            Log.e("MRD-EX-1", e.toString());
        }
        return stop;
        */
    }

    public JSONArray getBagsScanned( Boolean scanned)
    {
        JSONArray rawbags = new JSONArray();
        try
        {
            JSONArray bags = workflow.read("$.response.workflow.workflow.tripstops[*].tripstopdata[?]", Filter.filter(Criteria.where("payload").eq("bag")));
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


    class BagComparator implements Comparator<Bag>
    {
        @Override
        public int compare(Bag lhs, Bag rhs) {
            if( JSONObjectHelper.getIntDef( lhs.getStop(), "triporder", -1) > JSONObjectHelper.getIntDef( rhs.getStop(), "triporder", -1)) return 1;
            if( JSONObjectHelper.getIntDef( lhs.getStop(), "triporder", -1) < JSONObjectHelper.getIntDef( rhs.getStop(), "triporder", -1)) return -1;
            return 0;
        }
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

        Collections.sort( retbags, new BagComparator());

        return retbags;
    }

    public void setWaybillScanned( String barcode, int scanned)
    {
        ServerInterface.getInstance().setBagScanned(barcode);
    }

    public void setDeliveryStatus( int bagid, String status, String reason)
    {
        JSONObject bag = getBag(bagid);
        if( bag != null)
        {
            JSONObject jsostatus = JSONObjectHelper.getJSONObjectDef( bag, "status", new JSONObject());
            jsostatus.put("status", status);
            jsostatus.put("reason", reason);
            bag.put("status", jsostatus);

            if ( status.equals(Bag.STATUS_COMPLETED) ){

                //100% complete bag was delivered
                ServerInterface.getInstance().setDeliveryStatus(status , Integer.toString(bagid) , reason);
                ServerInterface.getInstance().endStop( ((JSONObject)Workflow.getInstance().getStopForBagId(bagid)).get("id").toString() , Users.getInstance().getActiveDriver().getStringid() );

            } else if (status.equals(Bag.STATUS_PARTIAL) ){
                ServerInterface.getInstance().setDeliveryStatus(status , Integer.toString(bagid) , reason);
                //Partial Bag Delivery ( some parcels was already failed , we need to success the ones left
                //ArrayList<DeliveryHandoverDataObject> unscanned = (ArrayList<DeliveryHandoverDataObject>) Workflow.getInstance().doormat.get(VariableManager.UNSCANNED_PARCELS);
                //for(int i = 0; i < unscanned.size(); i++ )
                //{
                    //DeliveryHandoverDataObject temp = unscanned.get(i);
                    //ServerInterface.getInstance().setDeliveryStatus(status , temp.getMDX() , "Parcel " + temp.getBarcode() + " could not be delivered during the delivery run (Reason: " + reason + " )");
                //}

                /*ArrayList<DeliveryHandoverDataObject> scanned = (ArrayList<DeliveryHandoverDataObject>) Workflow.getInstance().doormat.get("scannedparcels");
                for(int i = 0; i < scanned.size(); i++ )
                {
                    DeliveryHandoverDataObject temp = scanned.get(i);
                    ServerInterface.getInstance().setDeliveryStatus(status , temp.getMDX() , "Parcel " + temp.getBarcode() + " could not be delivered during the delivery run (Reason: " + reason + " )");
                }*/

            }

        }
    }

    public void setParcelDeliveryStatus( int parcelid, String status, String reason)
    {
        JSONObject parcel = getParcel(parcelid);
        if( parcel != null)
        {
            JSONObject jsostatus = JSONObjectHelper.getJSONObjectDef( parcel, "status", new JSONObject());
            jsostatus.put("status", status);
            jsostatus.put("reason", reason);
            parcel.put("status", jsostatus);
            // TODO: propogate this to the server - Hook into this Logic
            ServerInterface.getInstance().setParcelDeliveryStatus(status, Integer.toString(parcelid), reason);
        }
    }

    public ArrayList<HashMap<String, String>> getBagsCoords()
    {
        ArrayList<HashMap<String, String>> bags = null;
        bags = new ArrayList<HashMap<String, String>>();

        List<JSONObject> stops = workflow.read("$.response.workflow.workflow.tripstops[*]");

        for( int i=0; i < stops.size(); i++)
        {
            StopItem istop = new StopItem( new ObservableJSONObject( (JSONObject)stops.get( i)));

            HashMap<String, String> bag = new HashMap<String, String>();
            bag.put(VariableManager.EXTRA_BAG_ADDRESS, istop.getAddress());
            bag.put(VariableManager.EXTRA_BAG_HUBNAME, istop.getDestinationDesc());
            bag.put(VariableManager.EXTRA_BAG_LAT, Float.toString(JSONObjectHelper.getFloatDef(istop.getCoOrds(), "lat", 0)));
            bag.put(VariableManager.EXTRA_BAG_LON, Float.toString( JSONObjectHelper.getFloatDef( istop.getCoOrds(), "lon", 0)));
            bags.add(bag);
        }

        return bags;
    }

    public HashMap<String, String> getBagCoords( int bagid)
    {
        JSONObject jsostop = getStopForBagId( bagid);
        StopItem stop = new StopItem( new ObservableJSONObject( jsostop));
        HashMap<String, String> bag = new HashMap<String, String>();
        bag.put(VariableManager.EXTRA_BAG_LAT, Float.toString( JSONObjectHelper.getFloatDef( stop.getCoOrds(), "lat", 0)));
        bag.put(VariableManager.EXTRA_BAG_LON, Float.toString( JSONObjectHelper.getFloatDef( stop.getCoOrds(), "lon", 0)));
        return bag;
    }

    public ArrayList<DialogDataObject> getContactsFromBagId(int bagid)
    {
        JSONObject trip_stop = getStopForBagId(bagid);
        JSONObject contacts = (JSONObject) trip_stop.get("contacts");
        ArrayList<org.json.JSONObject> contactslist = new ArrayList<org.json.JSONObject>();
        ArrayList<DialogDataObject> clist = new ArrayList<DialogDataObject>();

        try
        {
            if (contacts.size() > 0 )
            {
                clist.add(new DialogDataObject(contacts.get("contact").toString() , contacts.get("tel").toString() ) );
                //JSONObject temp = (JSONObject)contacts.get(1);
                //for(int i = 0; i < contacts.size(); i++)
                //{
                //    contactslist.add( (org.json.JSONObject)contacts.get(i) );
                //}
            }
        }
        catch(Exception e)
        {
            Log.e("MRD-EX" , "Contacts Parsing Error : " + e.getMessage());
        }
        return clist;
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