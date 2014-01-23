package fi.gfarr.mrd;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import fi.gfarr.mrd.widget.Toaster;

public class MapActivity extends Activity implements OnMapClickListener, LocationListener,
		android.location.LocationListener, OnQueryTextListener
{

	private final String TAG = "MapActivity";
	private ViewHolder holder;
	private View root_view;
	private GoogleMap map;
	private LocationManager location_manager;
	private LatLng lat_long;
	private MarkerOptions marker_options;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

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
			// Getting LocationManager object from System Service LOCATION_SERVICE
			location_manager = (LocationManager) getSystemService(LOCATION_SERVICE);

			// Get current location
			Criteria criteria = new Criteria();
			String provider = location_manager.getBestProvider(criteria, true);
			location_manager.requestLocationUpdates(provider, 20000, 0, this);

		}
		catch (NullPointerException e)
		{
			// displayToast(getString(R.string.text_play_services));
			holder.relativeLayout_toast.setVisibility(View.VISIBLE);
		}

		// Search function
		setupSearchView();

	}

	private void setupSearchView()
	{
		// Start screen with keyboard initially hidden
		holder.search_view.setIconifiedByDefault(true);

		// Listen for text entered in the search field
		holder.search_view.setOnQueryTextListener(this);

		// Remove the silly 'play' button n searchView
		holder.search_view.setSubmitButtonEnabled(false);

		// Text to display when no query is entered
		holder.search_view.setQueryHint(getString(R.string.text_search_hint));
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

			holder.search_view = (SearchView) root_view.findViewById(R.id.searchView_map);

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
		SearchView search_view;
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
}
