package com.mrdexpress.paperless;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mrdexpress.paperless.adapters.PlacesAutoCompleteAdapter;
import com.mrdexpress.paperless.datatype.MapPlacesItem;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.widget.Toaster;

public class MapActivity extends Activity implements OnMapClickListener, LocationListener,
		android.location.LocationListener, OnQueryTextListener
{

	private final String TAG = "MapActivity";
	static final int STREET_LEVEL_ZOOM = 14;
	
	private ViewHolder holder;
	private View root_view;
	private GoogleMap map;
	private LocationManager location_manager;
	private LatLng lat_long;
	private LatLng selected_marker_lat_long;
	private String selected_marker_name;
	private MarkerOptions marker_options;

	private ArrayList<MapPlacesItem> resultList;
	private static String API_KEY;
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_DETAILS = "/details";
	private static final String OUT_JSON = "/json";
	
	LocationClient locationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		API_KEY = getResources().getString(R.string.key_googlemaps_places);
		//API_KEY = getResources().getString(R.string.key_googlemaps_api_debug);
		
		// Change actionbar title
		setTitle(R.string.title_actionbar_map);

		// Inflate views
		initViewHolder();

		/*
		 * The NullPointerException happens if Google Play services is not installed on device
		 */
		holder.relativeLayout_toast.setVisibility(View.INVISIBLE);
		try
		{
			// Try to obtain the map from the SupportMapFragment.
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			map.setOnMapClickListener(this);

			// Enable traffic
			map.setTrafficEnabled(true);

			// Enable LocationLayer of Google Map
			map.setMyLocationEnabled(true);

			// Set default zoom
			map.moveCamera(CameraUpdateFactory.zoomTo(STREET_LEVEL_ZOOM));
			// Move camera to my location
			locationClient = new LocationClient(this,
					new GooglePlayServicesClient.ConnectionCallbacks()
					{
						@Override
						public void onConnected(Bundle arg0) 
						{
							Location location = locationClient.getLastLocation();
							if (location != null) 
							{
						        LatLng myLocation = new LatLng(location.getLatitude(),
						                location.getLongitude());
						    
							    map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
							            STREET_LEVEL_ZOOM));
							}
							
							locationClient.disconnect();
						}

						@Override
						public void onDisconnected() 
						{						
							// ignore
						}
					}, 
					new GooglePlayServicesClient.OnConnectionFailedListener()
					{
						@Override
						public void onConnectionFailed(ConnectionResult arg0) 
						{
							// ignore
						}
					});
			locationClient.connect();
			

			// Getting LocationManager object from System Service LOCATION_SERVICE
			location_manager = (LocationManager) getSystemService(LOCATION_SERVICE);
			
			
			// Get current location
			Criteria criteria = new Criteria();
			String provider = location_manager.getBestProvider(criteria, true);
			location_manager.requestLocationUpdates(provider, 20000, 0, this);

			map.setOnMarkerClickListener(new OnMarkerClickListener()
			{
				@Override
				public boolean onMarkerClick(Marker marker)
				{
					selected_marker_lat_long = marker.getPosition();
					selected_marker_name = marker.getTitle();
					// TODO Auto-generated method stub
					return false;
				}
			});

			holder.autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this,
					R.layout.list_map_item));

			holder.autoCompView.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
				{
					String list_string = ((MapPlacesItem) holder.autoCompView.getAdapter().getItem(
							position)).getLocationName();
					String list_reference = ((MapPlacesItem) holder.autoCompView.getAdapter()
							.getItem(position)).getLocationReference();

					holder.autoCompView.setText(list_string.replaceAll("\n", ", "));

					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(holder.autoCompView.getWindowToken(), 0);

					new AddPlaceMarkerTask().execute(list_string, list_reference);

				}
			});

		}
		catch (NullPointerException e)
		{
			// displayToast(getString(R.string.text_play_services));
			holder.relativeLayout_toast.setVisibility(View.VISIBLE);
		}

		// Search function
		setupSearchView();
		setupMapMarkers();

		// Setup "Navigate here" button
		root_view = this.getWindow().getDecorView().findViewById(android.R.id.content);
		Button button_navigate = (Button) root_view.findViewById(R.id.button_map_navigate_here);
		button_navigate.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				// Only if marker has been selected
				if (selected_marker_lat_long != null)
				{
					/*CustomToast toast = new CustomToast(getParent(), getWindow().getCurrentFocus());
					toast.setText("Ahoy");
					toast.show();*/

					// Get selected marker coords
					double marker_lat = selected_marker_lat_long.latitude;
					double marker_lon = selected_marker_lat_long.longitude;

					// Convert coords to Uri

					// Map point based on address
					/*Uri location = Uri.parse("geo:" + marker_lat + "," + marker_lon + "?z="
							+ zoom_level);*/
					Uri location = Uri.parse("geo:0,0?q=" + marker_lat + "," + marker_lon + "("
							+ selected_marker_name + ")" + "&z=" + STREET_LEVEL_ZOOM);
					// Or map point based on latitude/longitude
					// Uri location = Uri.parse("geo:37.422219,-122.08364?z=14"); // z param is zoom
					// level
					Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

					// Check if there is an App to receive intent (or else it will crash boom bang)
					PackageManager packageManager = getPackageManager();
					List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent,
							0);
					boolean isIntentSafe = activities.size() > 0;

					if (isIntentSafe)
					{
						startActivity(mapIntent);
					}
				}
			}
		});

	}

	/**
	 * Retrieve coords of all the driver's bags and place map markers
	 */
	private void setupMapMarkers()
	{
		SharedPreferences prefs = getSharedPreferences(VariableManager.PREF, Context.MODE_PRIVATE);

		final String driverid = prefs.getString(VariableManager.PREF_DRIVERID, null);

		// Retrieve coords
		ArrayList<HashMap<String, String>> bags = DbHandler.getInstance(getApplicationContext())
				.getBagCoords(driverid);

		// Check coords of each bags
		for (int i = 0; i < bags.size(); i++)
		{
			try
			{
				double lat = Double.parseDouble(bags.get(i).get(VariableManager.EXTRA_BAG_LAT));
				double lon = Double.parseDouble(bags.get(i).get(VariableManager.EXTRA_BAG_LON));
				LatLng location = new LatLng(lon, lat);

				String address = bags.get(i).get(VariableManager.EXTRA_BAG_ADDRESS);
				String hubname = bags.get(i).get(VariableManager.EXTRA_BAG_HUBNAME);

				map.addMarker(new MarkerOptions().title(hubname).snippet(address)
						.position(location));
			}
			catch (NumberFormatException e)
			{
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				Log.e(TAG, sw.toString());
			}
		}
	}

	/**
	 * Setup and initialize the search bar
	 */
	private void setupSearchView()
	{
		// Start screen with keyboard initially hidden
		// holder.search_view.setIconifiedByDefault(true);

		// Listen for text entered in the search field
		// holder.search_view.setOnQueryTextListener(this);

		// Remove the silly 'play' button n searchView
		// holder.search_view.setSubmitButtonEnabled(false);

		// Text to display when no query is entered
		// holder.search_view.setQueryHint(getString(R.string.text_search_hint));
	}

	/**
	 * Display a toast using the custom Toaster class
	 * 
	 * @param msg
	 */
	private void displayToast(String msg)
	{
		Toaster.displayToast(msg, holder.textView_toast, holder.relativeLayout_toast, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	public void initViewHolder()
	{
		if (root_view == null)
		{
			root_view = this.getWindow().getDecorView().findViewById(android.R.id.content);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			// holder.search_view = (SearchView) root_view.findViewById(R.id.searchView_map);
			holder.autoCompView = (AutoCompleteTextView) root_view
					.findViewById(R.id.searchView_map);

			holder.textView_toast = (TextView) root_view.findViewById(R.id.textView_map_toast);

			holder.relativeLayout_toast = (RelativeLayout) root_view.findViewById(R.id.toast_map);

			// Store the holder with the view.
			root_view.setTag(holder);

		}
		else
		{
			holder = (ViewHolder) root_view.getTag();

			if ((root_view.getParent() != null) && (root_view.getParent() instanceof ViewGroup))
			{
				((ViewGroup) root_view.getParent()).removeAllViewsInLayout();
			}
			else
			{
			}
		}
	}

	// Creates static instances of resources.
	// Increases performance by only finding and inflating resources only once.
	static class ViewHolder
	{
		MapFragment map_fragment;
		// SearchView search_view;
		AutoCompleteTextView autoCompView;
		TextView textView_toast;
		RelativeLayout relativeLayout_toast;
	}

	@Override
	public void onLocationChanged(Location arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapClick(LatLng arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		// TODO Auto-generated method stub

	}

	// An AsyncTask class for accessing the GeoCoding Web Service
	private class GeocoderTask extends AsyncTask<String, Void, List<Address>>
	{

		@Override
		protected List<Address> doInBackground(String... locationName)
		{
			// Creating an instance of Geocoder class
			Geocoder geocoder = new Geocoder(getBaseContext());
			List<Address> addresses = null;

			try
			{
				// Getting a maximum of 3 Address that matches the input text
				addresses = geocoder.getFromLocationName(locationName[0], 3);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return addresses;
		}

		@Override
		protected void onPostExecute(List<Address> addresses)
		{

			if (addresses == null || addresses.size() == 0)
			{
				Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
			}

			// Clears all the existing markers on the map
			map.clear();

			// Adding Markers on Google Map for each matching address
			for (int i = 0; i < addresses.size(); i++)
			{

				Address address = (Address) addresses.get(i);

				// Creating an instance of GeoPoint, to display in Google Map
				lat_long = new LatLng(address.getLatitude(), address.getLongitude());

				String addressText = String.format("%s, %s",
						address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
						address.getCountryName());

				marker_options = new MarkerOptions();
				marker_options.position(lat_long);
				marker_options.title(addressText);

				map.addMarker(marker_options);

				// Locate the first location
				if (i == 0) map.animateCamera(CameraUpdateFactory.newLatLng(lat_long));
			}
		}
	}

	@Override
	public boolean onQueryTextChange(String new_text)
	{
		// TODO Auto-generated method stub
		if (new_text != null && !new_text.equals(""))
		{
			new GeocoderTask().execute(new_text);
		}
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query)
	{
		// TODO Auto-generated method stub

		hideSoftKeyboard(this);
		return false;
	}

	public static void hideSoftKeyboard(Activity activity)
	{
		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}

	private ArrayList<String> addPlaceMarker(String location_name, String location_reference)
	{

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		ArrayList<String> resultList = null;

		try
		{
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAILS + OUT_JSON);
			sb.append("?sensor=true&key=" + API_KEY);
			sb.append("&components=country:za");
			sb.append("&reference=" + location_reference);

			Log.d(TAG, "URL: " + sb.toString());

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1)
			{
				jsonResults.append(buff, 0, read);
			}
		}
		catch (MalformedURLException e)
		{
			Log.e(TAG, "Error processing Places API URL", e);
		}
		catch (IOException e)
		{
			Log.e(TAG, "Error connecting to Places API", e);
		}
		finally
		{
			if (conn != null)
			{
				conn.disconnect();
			}
		}

		try
		{
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONObject predsJsonArray = jsonObj.getJSONObject("result").getJSONObject("geometry")
					.getJSONObject("location");

			resultList = new ArrayList<String>();

			resultList.add(location_name);
			resultList.add(location_reference);
			resultList.add(predsJsonArray.get("lat").toString());
			resultList.add(predsJsonArray.get("lng").toString());

		}
		catch (JSONException e)
		{
			Log.e(TAG, "Cannot process JSON results", e);
		}

		return resultList;
	}

	private class AddPlaceMarkerTask extends AsyncTask<String, Void, ArrayList<String>>
	{

		// private ProgressDialog dialog = new ProgressDialog(getApplication());

		/** progress dialog to show user that the backup is processing. */
		/** application context. */
		@Override
		protected void onPreExecute()
		{
			// this.dialog.setMessage("Adding location");
			// this.dialog.show();
		}

		@Override
		protected ArrayList<String> doInBackground(String... args)
		{
			return addPlaceMarker(args[0], args[1]);
		}

		@Override
		protected void onPostExecute(ArrayList<String> result)
		{
			// String address = result.get(0);
			String hubname = result.get(0);

			double lat = Double.parseDouble(result.get(2));
			double lon = Double.parseDouble(result.get(3));
			LatLng location = new LatLng(lat, lon);

			map.addMarker(new MarkerOptions().title(hubname).position(location));

			CameraPosition cameraPosition = new CameraPosition.Builder().target(location)
					.zoom(12.0f).build();
			CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
			map.moveCamera(cameraUpdate);
		}
	}
}
