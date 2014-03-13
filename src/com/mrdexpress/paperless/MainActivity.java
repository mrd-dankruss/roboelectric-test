package com.mrdexpress.paperless;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mrdexpress.paperless.adapters.UserAutoCompleteAdapter;
import com.mrdexpress.paperless.datatype.UserItem;
import com.mrdexpress.paperless.datatype.UserItem.UserType;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.fragments.UnauthorizedUseDialog;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.security.PinManager;
import com.mrdexpress.paperless.service.LocationService;
import com.mrdexpress.paperless.widget.CustomToast;

public class MainActivity extends Activity
{
	private ViewHolder holder;
	private View root_view;
	private final String TAG = "MainActivity";
	private ArrayList<UserItem> person_item_list;
	private String selected_user_id;
	private String selected_user_name;
	private UserType selected_user_type;

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	private String SENDER_ID = "426772637351";
	private GoogleCloudMessaging gcm;
	private String regid;
	AtomicInteger msgId = new AtomicInteger();
	Context context;
	private boolean is_registration_successful;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		new RequestTokenTask().execute();

		new UpdateApp().execute();

		// Check & store network availability
		/*SharedPreferences settings = getSharedPreferences(VariableManager.PREF, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(
				VariableManager.PREF_NETWORK_AVAILABLE,
				NetworkStateReceiver.getInstance(getApplicationContext())
						.checkNetworkAvailability()).commit();*/

		// Initialize ViewHolder
		initViewHolder();

		// Set global variable holding context
		VariableManager.context = this;
		context = getApplicationContext();

		setTitle(R.string.title_actionbar_mainmenu); // Change actionbar title

		startService(new Intent(this, LocationService.class));

		// Check device for Play Services APK. If check succeeds, proceed with
		// GCM registration.
		if (checkPlayServices())
		{
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);
			is_registration_successful = false;

			if (regid.isEmpty())
			{
				registerInBackground();
			}
		}
		else
		{
			Log.i(TAG, "No valid Google Play Services APK found.");
		}

		person_item_list = new ArrayList<UserItem>();

		initClickListeners();

		UserAutoCompleteAdapter adapter = new UserAutoCompleteAdapter(getApplicationContext(),
				person_item_list);

		// Set the adapter
		holder.text_name.setAdapter(adapter);
		holder.text_name.setThreshold(1);

		holder.text_name.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
			{
				selected_user_id = ((UserItem) holder.text_name.getAdapter().getItem(position))
						.getUserID();
				selected_user_name = ((UserItem) holder.text_name.getAdapter().getItem(position))
						.getUserName();
				selected_user_type = ((UserItem) holder.text_name.getAdapter().getItem(position))
						.getUserType();

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(holder.text_name.getWindowToken(), 0);

			}
		});

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences prefs = getSharedPreferences(VariableManager.PREF, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(VariableManager.LAST_LOGGED_IN_MANAGER_NAME);
		editor.remove(VariableManager.LAST_LOGGED_IN_MANAGER_ID);
		editor.remove(VariableManager.PREF_DRIVERID);
		editor.remove(VariableManager.PREF_CURRENT_BAGID);
		editor.putBoolean(VariableManager.PREF_TRAINING_MODE, false);
		editor.apply();

		holder.text_name.setText("");
		holder.text_password.setText("");
	}

	/**
	 * Initiate click listeners for buttons.
	 */
	private void initClickListeners()
	{
		// Click Start New Milkrun button
		holder.button_login.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				// Perform action on click
				if (checkPin())
				{
					Log.e(TAG, "Type1: " + selected_user_type);
					if (selected_user_type == UserType.DRIVER)
					{
						new DriverLoginUserTask().execute();
					}
					if (selected_user_type == UserType.MANAGER)
					{
						/*
						CustomToast not_yet_implemented = new CustomToast(MainActivity.this);
						not_yet_implemented.setText("Manager login is not implemented.");
						not_yet_implemented.setSuccess(false);
						not_yet_implemented.show();
						*/
						new ManagerLoginUserTask().execute();
					}
				}
			}
		});

		/*
		// Click Start Trainingrun button
		holder.button_start_trainingrun.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				// Perform action on click

				new Thread(new Runnable()
				{
					public void run()
					{
						Intent intent = new Intent(getApplicationContext(), SignatureActivity.class);
						startActivity(intent);
					}
				}).start();
			}
		});
		*/
	}

	/**
	 * Requests token from server.
	 * 
	 * @author greg
	 * 
	 */
	private class RequestTokenTask extends AsyncTask<Void, Void, String>
	{

		private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

		/** progress dialog to show user that the backup is processing. */
		/** application context. */
		@Override
		protected void onPreExecute()
		{
			this.dialog.setMessage("Acquiring token");
			this.dialog.show();
		}

		@Override
		protected String doInBackground(Void... urls)
		{
			// Log.i(TAG, "Fetching token...");
			String token = ServerInterface.getInstance(getApplicationContext()).requestToken();

			if (token.equals("400"))
			{
				return token;
			}
			else
			{
				// Log.d(TAG, "zorro token: " + token);

				// VariableManager.token = token; // Security vulnerability?

				// Download delay reasons
				// change progress spinner text
				if (dialog.isShowing())
				{
					dialog.setTitle("Retrieving driver list");
				}
				ServerInterface.getInstance(getApplicationContext()).getDrivers(
						getApplicationContext());
				if (dialog.isShowing())
				{
					dialog.setTitle("Retrieving manager list");
				}
				ServerInterface.getInstance(getApplicationContext()).getManagers(
						getApplicationContext());
				if (dialog.isShowing())
				{
					dialog.setTitle("Retrieving delay reasons");
				}
				ServerInterface.getInstance(getApplicationContext()).downloadDelays(
						getApplicationContext());

				if (dialog.isShowing())
				{
					dialog.setTitle("Retrieving failed handover reasons");
				}
				ServerInterface.getInstance(getApplicationContext()).downloadFailedDeliveryReasons(
						getApplicationContext());

				if (dialog.isShowing())
				{
					dialog.setTitle("Retrieving partial delivery reasons");
				}
				ServerInterface.getInstance(getApplicationContext())
						.downloadPartialDeliveryReasons(getApplicationContext());

				// Log.i(TAG, "Token aquired.");
			}
			return token;
		}

		@Override
		protected void onPostExecute(String result)
		{

			if (result.equals("400"))
			{
				UnauthorizedUseDialog dialog = new UnauthorizedUseDialog(MainActivity.this);
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				dialog.show();

				LayoutInflater factory = LayoutInflater.from(MainActivity.this);

				final Button button_ok = (Button) dialog
						.findViewById(R.id.button_incomplete_scan_ok);

				button_ok.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						MainActivity.this.finish();
					}
				});
			}
			else
			{
				person_item_list
						.addAll(DbHandler.getInstance(getApplicationContext()).getDrivers());
				person_item_list.addAll(DbHandler.getInstance(getApplicationContext())
						.getManagers());

				// Close progress spinner
				if (dialog.isShowing())
				{
					dialog.dismiss();
				}

				// Retrieve list of drivers in a thread
				// new RequestDriverManagerTask().execute();
			}
		}
	}

	/**
	 * Requests token from server.
	 * 
	 * @author greg
	 * 
	 */
	/*private class RequestDriverManagerTask extends AsyncTask<Void, Void, String>
	{

		private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

		*//** progress dialog to show user that the backup is processing. */
	/*
	*//** application context. */
	/*
	@Override
	protected void onPreExecute()
	{
	this.dialog.setMessage("Retrieving list of drivers/managers");
	this.dialog.show();
	}

	@Override
	protected String doInBackground(Void... urls)
	{
	ServerInterface.getInstance(getApplicationContext()).getDrivers(
	getApplicationContext(), imei_id);
	ServerInterface.getInstance(getApplicationContext()).getManagers(
	getApplicationContext(), imei_id);
	return null;
	}

	@Override
	protected void onPostExecute(String result)
	{
	// TODO: Initilize person_item_list
	try
	{
	person_item_list
	.addAll(DbHandler.getInstance(getApplicationContext()).getDrivers());
	person_item_list.addAll(DbHandler.getInstance(getApplicationContext())
	.getManagers());
	Log.d(TAG, "PersonList: " + person_item_list.size());
	}
	catch (NullPointerException e)
	{
	StringWriter sw = new StringWriter();
	e.printStackTrace(new PrintWriter(sw));
	Log.e(TAG, sw.toString());

	CustomToast toast = new CustomToast(getParent());
	toast.setText("Network error");
	toast.setSuccess(false);
	toast.show();
	}
	// Close progress spinner
	if (dialog.isShowing())
	{
	dialog.dismiss();
	}
	}
	}
	*/

	/**
	 * Requests token from server.
	 * 
	 * @author greg
	 * 
	 */
	private class DriverLoginUserTask extends AsyncTask<Void, Void, Boolean>
	{

		boolean isDriverPinSet = false;
		private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

		/** progress dialog to show user that the backup is processing. */
		/** application context. */
		@Override
		protected void onPreExecute()
		{
			this.dialog.setMessage("Authenticating");
			this.dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... urls)
		{
			String hash = PinManager.toMD5(holder.text_password.getText().toString());
			hash = holder.text_password.getText().toString(); // DEBUG *Remove when hashing is
																// wanted again

			if (DbHandler.getInstance(getApplicationContext()).isDriverPinSet(selected_user_id) == false)
			{
				isDriverPinSet = false;
				return true;
			}
			else
			{
				isDriverPinSet = true;
				String status = ServerInterface.getInstance(getApplicationContext()).authDriver(
						hash, selected_user_id);

				if (status.equals("success"))
				{
					// Store currently selected driverid in shared prefs
					SharedPreferences prefs = context.getSharedPreferences(VariableManager.PREF,
							Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString(VariableManager.PREF_DRIVERID, selected_user_id);
					editor.apply();

					return true;
				}
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result)
		{

			if (result == true)
			{
				if (isDriverPinSet)
				{
					Intent intent = new Intent(getApplicationContext(), DriverHomeActivity.class);

					DbHandler.getInstance(getApplicationContext());
					// Pass driver name on
					intent.putExtra(VariableManager.EXTRA_DRIVER, selected_user_name);

					Log.d(TAG, "Driver ID: " + selected_user_id);
					// intent.putExtra(VariableManager.EXTRA_DRIVER_ID, selected_user_id);

					startActivity(intent);
					// Close progress spinner
					if (dialog.isShowing())
					{
						dialog.dismiss();
					}
				}
				else
				{
					// Store currently selected driverid in shared prefs
					SharedPreferences prefs = context.getSharedPreferences(VariableManager.PREF,
							Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString(VariableManager.PREF_DRIVERID, selected_user_id);
					editor.apply();

					Intent intent = new Intent(getApplicationContext(), CreatePinActivity.class);
					startActivity(intent);
				}

			}
			else
			{
				// Close progress spinner
				if (dialog.isShowing())
				{
					dialog.dismiss();
				}
				CustomToast toast = new CustomToast(MainActivity.this);
				toast.setText(getString(R.string.text_unauthorised));
				toast.setSuccess(false);
				toast.show();
			}
		}
	}

	/**
	 * Requests token from server.
	 * 
	 * @author htdahms
	 * 
	 */
	private class ManagerLoginUserTask extends AsyncTask<Void, Void, Boolean>
	{

		private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

		/** progress dialog to show user that the backup is processing. */
		/** application context. */
		@Override
		protected void onPreExecute()
		{
			this.dialog.setMessage("Authenticating");
			this.dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... urls)
		{
			String hash = PinManager.toMD5(holder.text_password.getText().toString());

			// TODO: Wait for change to API or new API call
			// String status = ServerInterface.authManager(man_id, driver_id, PIN);
			String status = "blah";

			if (status.equals("success"))
			{
				return true;
			}

			return false;
		}

		@Override
		protected void onPostExecute(Boolean result)
		{

			if (result == true)
			{
				new RetrieveManagerBagsTask().execute();
				// Close progress spinner
				if (dialog.isShowing())
				{
					dialog.dismiss();
				}
			}
		}
	}

	/**
	 * Check PIN's validity (data validation)
	 * 
	 * @return True is valid.
	 */
	private boolean checkPin()
	{

		// Check for 4-digit format
		String msg = PinManager.checkPin(holder.text_password.getText().toString(), this);
		if (msg.equals("OK"))
		{

			return true;
		}
		else
		{
			displayToast(msg);
			return false;
		}
	}

	/**
	 * Retrieve list of bags from API in background
	 * 
	 * @author greg
	 * 
	 */
	private class RetrieveBagsTask extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog dialog_progress = new ProgressDialog(MainActivity.this);

		/** progress dialog to show user that the backup is processing. */
		/** application context. */
		@Override
		protected void onPreExecute()
		{
			this.dialog_progress.setMessage("Retrieving consignments");
			this.dialog_progress.show();
		}

		@Override
		protected Void doInBackground(Void... urls)
		{
			ServerInterface.getInstance(getApplicationContext()).downloadBags(
					getApplicationContext(), selected_user_id);
			return null;
		}

		@Override
		protected void onPostExecute(Void nothing)
		{
			// Close progress spinner
			if (dialog_progress.isShowing())
			{
				dialog_progress.dismiss();
			}

			Intent intent = new Intent(getApplicationContext(), DriverHomeActivity.class);

			DbHandler.getInstance(getApplicationContext());
			// Pass driver name on
			intent.putExtra(VariableManager.EXTRA_DRIVER, selected_user_name);

			Log.d(TAG, "Driver ID: " + selected_user_id);
			// intent.putExtra(VariableManager.EXTRA_DRIVER_ID, selected_user_id);

			startActivity(intent);
		}
	}

	private class RetrieveManagerBagsTask extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog dialog_progress = new ProgressDialog(MainActivity.this);

		/** progress dialog to show user that the backup is processing. */
		/** application context. */
		@Override
		protected void onPreExecute()
		{
			this.dialog_progress.setMessage("Retrieving consignments");
			this.dialog_progress.show();
		}

		@Override
		protected Void doInBackground(Void... urls)
		{
			ServerInterface.getInstance(getApplicationContext()).downloadBags(
					getApplicationContext(), selected_user_id);
			return null;
		}

		@Override
		protected void onPostExecute(Void nothing)
		{
			// Close progress spinner
			if (dialog_progress.isShowing())
			{
				dialog_progress.dismiss();
			}

			Intent intent = new Intent(getApplicationContext(), ManagerHomeActivity.class);

			DbHandler.getInstance(getApplicationContext());
			// Pass driver name on
			intent.putExtra(VariableManager.EXTRA_DRIVER, selected_user_name);

			Log.d(TAG, "Driver ID: " + selected_user_id);
			// intent.putExtra(VariableManager.EXTRA_DRIVER_ID, selected_user_id);

			startActivity(intent);
		}
	}

	/**
	 * Display a toast using the custom Toaster class
	 * 
	 * @param msg
	 */
	private void displayToast(String msg)
	{
		CustomToast toast_main_menu = new CustomToast(this);
		toast_main_menu.setSuccess(false);
		toast_main_menu.setText("Please check your PIN length");
		toast_main_menu.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices()
	{
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS)
		{
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
			{
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			}
			else
			{
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context)
	{
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty())
		{
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion)
		{
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context)
	{
		// This sample app persists the registration ID in shared preferences, but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context)
	{
		try
		{
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionCode;
		}
		catch (NameNotFoundException e)
		{
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's shared preferences.
	 */
	private void registerInBackground()
	{
		new AsyncTask<Void, Void, String>()
		{
			@Override
			protected String doInBackground(Void... params)
			{
				String msg = "";
				try
				{
					if (gcm == null)
					{
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					Log.i(TAG, "GCM registration ID: " + regid);
					msg = "Device registered, registration ID=" + regid;

					// You should send the registration ID to your server over HTTP, so it
					// can use GCM/HTTP or CCS to send messages to your app.
					sendRegistrationIdToBackend(regid);

					// For this demo: we don't need to send it because the device will send
					// upstream messages to a server that echo back the message using the
					// 'from' address in the message.

					// Persist the regID - no need to register again.
					storeRegistrationId(context, regid);
				}
				catch (IOException ex)
				{
					msg = "Error :" + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg)
			{
				if (is_registration_successful)
				{
					CustomToast toast = new CustomToast(MainActivity.this);
					toast.setText("Sending device registration ID successful!");
					toast.setSuccess(true);
					toast.show();
				}
				else
				{
					CustomToast toast = new CustomToast(MainActivity.this);
					toast.setText("Sending device registration ID failed!");
					toast.setSuccess(false);
					toast.show();
				}
			}
		}.execute(null, null, null);
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
	 * messages to your app. Not needed for this demo since the device sends upstream messages
	 * to a server that echoes back the message using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend(String regid)
	{
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		String token = ServerInterface.getInstance(getApplicationContext()).registerDeviceGCM(
				telephonyManager.getDeviceId(), regid);

		Log.d(TAG, "Token: " + token);

		if (token.equals("OK"))
		{
			is_registration_successful = true;
		}
	}

	/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId)
	{
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	private class UpdateApp extends AsyncTask<Void, Void, Void>
	{
		String path = "/sdcard/paperless.apk";
		boolean mustInstall = false;

		private ProgressDialog dialog_progress = new ProgressDialog(MainActivity.this);

		/** progress dialog to show user that the backup is processing. */
		/** application context. */
		@Override
		protected void onPreExecute()
		{
			this.dialog_progress.setMessage("Checking for updates");
			this.dialog_progress.show();
		}

		@Override
		protected Void doInBackground(Void... urls)
		{
			try
			{
				PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				Log.d("update", "Current version: " + pInfo.versionCode);

				// Create a URL for the desired page
				URL url = new URL("http://www.htdahms.co.za/version.txt");

				// Read all the text returned by the server
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				String read_line;
				String[] temp_array;
				int versionCode = 0;

				while ((read_line = in.readLine()) != null)
				{
					Log.d("update", "Line: " + read_line);
					if (read_line.contains("versionCode:"))
					{
						temp_array = read_line.split(":");
						versionCode = Integer.parseInt(temp_array[1].trim());
					}
				}
				in.close();

				if (versionCode == 0)
				{
					// TODO: Error code that update directory on server has problem. Warn
					// administrator.
				}
				else
				{
					Log.d("update", "Current version: " + pInfo.versionCode);
					Log.d("update", "Server version: " + versionCode);
					if (versionCode > pInfo.versionCode)
					{
						Log.d("update", "BOOM");
						downloadAPK();
						mustInstall = true;
					}
				}
			}
			catch (MalformedURLException e)
			{
			}
			catch (IOException e)
			{
			}
			catch (NameNotFoundException e)
			{
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void nothing)
		{
			if (mustInstall)
			{
				Intent i = new Intent();
				i.setAction(Intent.ACTION_VIEW);
				i.setDataAndType(Uri.fromFile(new File(path)),
						"application/vnd.android.package-archive");
				Log.d("Lofting", "About to install new .apk");
				startActivity(i);
			}

			// Close progress spinner
			if (dialog_progress.isShowing())
			{
				dialog_progress.dismiss();
			}
		}
	}

	private void downloadAPK()
	{
		String path = "/sdcard/paperless.apk";
		try
		{
			URL url = new URL("http://www.htdahms.co.za/paperless.apk");
			URLConnection connection = url.openConnection();
			connection.connect();

			int fileLength = connection.getContentLength();

			// download the file
			InputStream input = new BufferedInputStream(url.openStream());
			OutputStream output = new FileOutputStream(path);

			byte data[] = new byte[1024];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1)
			{
				total += count;
				// publishProgress((int) (total * 100 / fileLength));
				output.write(data, 0, count);
			}

			output.flush();
			output.close();
			input.close();
		}
		catch (Exception e)
		{
			Log.e("YourApp", "Well that didn't work out so well...");
			Log.e("YourApp", e.getMessage());
		}
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

			Typeface typeface_roboto_bold = Typeface.createFromAsset(getAssets(), FontHelper
					.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
							FontHelper.STYLE_BOLD));
			Typeface typeface_roboto_regular = Typeface.createFromAsset(getAssets(), FontHelper
					.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
							FontHelper.STYLE_REGULAR));

			holder.button_login = (Button) root_view.findViewById(R.id.button_mainmenu_start_login);
			holder.button_login.setTypeface(typeface_roboto_bold);

			holder.text_name = (AutoCompleteTextView) root_view
					.findViewById(R.id.text_mainmenu_name);
			holder.text_password = (EditText) root_view.findViewById(R.id.text_mainmenu_password);

			holder.text_password.setTypeface(typeface_roboto_regular);

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

	/*
	 * Create a PendingIntent that triggers an IntentService in your
	 * app when a geofence transition occurs.
	 */
	/*    private PendingIntent getTransitionPendingIntent() {
	        // Create an explicit Intent
	        Intent intent = new Intent(this,
	                ReceiveTransitionsIntentService.class);
	        
	         * Return the PendingIntent
	         
	        return PendingIntent.getService(
	                this,
	                0,
	                intent,
	                PendingIntent.FLAG_UPDATE_CURRENT);
	    }*/

	// ViewHolder stores static instances of views in order to reduce the number
	// of times that findViewById is called, which affected listview performance
	static class ViewHolder
	{
		Button button_login;
		AutoCompleteTextView text_name;
		EditText text_password;
	}

}
