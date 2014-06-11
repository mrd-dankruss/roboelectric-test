package com.mrdexpress.paperless.adapters;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import com.mrdexpress.paperless.datatype.MapPlacesItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class PlacesAutoCompleteAdapter extends ArrayAdapter<MapPlacesItem> implements Filterable {
    
	private final String TAG = "MapAdapter";
	
	private ArrayList<MapPlacesItem> resultList;
    private static final String API_KEY = "AIzaSyAG4j23urq6PDPP3MSo_CjjEdTzMJJ3M_Y";
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";

    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public MapPlacesItem getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }
    
    private ArrayList<MapPlacesItem> autocomplete(String input) {
	    ArrayList<MapPlacesItem> resultList = null;

	    HttpURLConnection conn = null;
	    StringBuilder jsonResults = new StringBuilder();
	    try {
	        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
	        sb.append("?sensor=true&key=" + API_KEY);
	        sb.append("&components=country:za");
	        sb.append("&input=" + URLEncoder.encode(input, "utf8"));

	        Log.d(TAG, "URL: " + sb.toString());
	        
	        URL url = new URL(sb.toString());
	        conn = (HttpURLConnection) url.openConnection();
	        InputStreamReader in = new InputStreamReader(conn.getInputStream());

	        // Load the results into a StringBuilder
	        int read;
	        char[] buff = new char[1024];
	        while ((read = in.read(buff)) != -1) {
	            jsonResults.append(buff, 0, read);
	        }
	    } catch (MalformedURLException e) {
	        Log.e(TAG, "Error processing Places API URL", e);
	        return resultList;
	    } catch (IOException e) {
	        Log.e(TAG, "Error connecting to Places API", e);
	        return resultList;
	    } finally {
	        if (conn != null) {
	            conn.disconnect();
	        }
	    }

	    try {
	        // Create a JSON object hierarchy from the results
	        JSONObject jsonObj = new JSONObject(jsonResults.toString());
	        JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
	        String temp_string = "";

	        // Extract the Place descriptions from the results
	        resultList = new ArrayList<MapPlacesItem>(predsJsonArray.length());
	        for (int i = 0; i < predsJsonArray.length(); i++) {
	        	temp_string = predsJsonArray.getJSONObject(i).getString("description");
	        	temp_string = temp_string.replaceFirst(", ", "\n").substring(0, temp_string.indexOf(",", temp_string.indexOf(",")+1)-1);
	            resultList.add(new MapPlacesItem(temp_string, predsJsonArray.getJSONObject(i).getString("reference")));
	        }
	    } catch (JSONException e) {
	        Log.e(TAG, "Cannot process JSON results", e);
	    }

	    return resultList;
	}
}