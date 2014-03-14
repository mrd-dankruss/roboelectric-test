package com.mrdexpress.paperless.workflow;

import android.content.Context;
import android.util.Log;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.mrdexpress.paperless.helper.VariableManager;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONArray;

import java.util.List;

/**
 * Created by gary on 2014/03/13.
 */

public class Workflow
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
    }

    public List<JSONArray> getBags()
    {
        JSONObject test3 = workflow.read("$.response.workflow.workflow.*");
        Log.i("gary", test3.toString());
        //Log.i("gary", test3.getClass().getField("id").toString() );


        List<JSONArray> bags = workflow.read("$.response.workflow.workflow.tripstops[?(@.tripstopdata.payload == 'bag')]");
        return bags;
    }

}