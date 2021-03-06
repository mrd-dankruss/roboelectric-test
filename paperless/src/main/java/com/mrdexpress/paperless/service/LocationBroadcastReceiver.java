package com.mrdexpress.paperless.service;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.*;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.workflow.Workflow;

public class LocationBroadcastReceiver extends BroadcastReceiver implements LocationListener
{
private final String TAG = "LocationBroadcastReceiver";
	private Context mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	// Declaring a Location Manager
	protected LocationManager locationManager;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		mContext = context;

		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
		wl.acquire();

		// Put here YOUR code.
		SharedPreferences prefs = mContext.getSharedPreferences(VariableManager.PREF,
				Context.MODE_PRIVATE);
		//String stop_id = prefs.getInt(VariableManager.PREF_CURRENT_STOPID, -1);
        String stop_id = Workflow.getInstance().currentBagID;
		long time = System.currentTimeMillis() / 1000;

		
		if (stop_id != "")
		{
			location = getLocation();
			new PostDriverPosition().execute( stop_id, String.valueOf(location.getAccuracy()),
					String.valueOf(location.getLatitude()),
					String.valueOf(location.getLongitude()), stop_id, String.valueOf(time));

		}
		else
		{
			Log.d(TAG, "Zeus - locationreciever");
		}

		wl.release();
	}

	public void SetAlarm(Context context)
	{
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, LocationBroadcastReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 30 * 1, pi);
	}

	public void CancelAlarm(Context context)
	{
		Intent intent = new Intent(context, LocationBroadcastReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

	public Location getLocation()
	{
		try
		{
			locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled)
			{
				// no network provider is enabled
			}
			else
			{
				this.canGetLocation = true;
				if (isNetworkEnabled)
				{
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network", "Network");
					if (locationManager != null)
					{
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null)
						{
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled)
				{
					if (location == null)
					{
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null)
						{
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null)
							{
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return location;
	}

	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 * */
	public void stopUsingGPS()
	{
		if (locationManager != null)
		{
			locationManager.removeUpdates(LocationBroadcastReceiver.this);
		}
	}

	/**
	 * Function to get latitude
	 * */
	public double getLatitude()
	{
		if (location != null)
		{
			latitude = location.getLatitude();
		}

		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude()
	{
		if (location != null)
		{
			longitude = location.getLongitude();
		}

		// return longitude
		return longitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation()
	{
		return this.canGetLocation;
	}

	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public void showSettingsAlert()
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location)
	{
	}

	@Override
	public void onProviderDisabled(String provider)
	{
	}

	@Override
	public void onProviderEnabled(String provider)
	{
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	}

	public IBinder onBind(Intent arg0)
	{
		return null;
	}

	private class PostDriverPosition extends AsyncTask<String, Void, Void>
	{

		String status = "";

		@Override
		protected void onPreExecute()
		{
		}

		@Override
		protected Void doInBackground(String... args)
		{
			//status = ServerInterface.getInstance(mContext).postDriverPosition(args[0], args[1],	args[2], args[3], args[4], args[5]);
			return null;
		}

		@Override
		protected void onPostExecute(Void nothing)
		{
			Log.d("service", "Driver Position API Call: " + status);
		}
	}
}