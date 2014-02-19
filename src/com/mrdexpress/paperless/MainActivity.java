package com.mrdexpress.paperless;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
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
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.security.PinManager;
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
	private String imei_id;

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	private String SENDER_ID = "Your-Sender-ID";
	private GoogleCloudMessaging gcm;
	private String regid;
	AtomicInteger msgId = new AtomicInteger();
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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

		// Check device for Play Services APK. If check succeeds, proceed with
		// GCM registration.
		if (checkPlayServices())
		{
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);

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

		TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		imei_id = mngr.getDeviceId();

		new RequestTokenTask().execute();

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

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's shared preferences.
	 */
	private void registerInBackground()
	{
		// TODO: Add AsyncTask that can registers a new id if needed.
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
					if (selected_user_type == UserType.DRIVER)
					{
						new DriverLoginUserTask().execute();
					}
					if (selected_user_type == UserType.MANAGER)
					{
						CustomToast not_yet_implemented = new CustomToast(MainActivity.this);
						not_yet_implemented.setText("Manager login is not implemented.");
						not_yet_implemented.setSuccess(false);
						not_yet_implemented.show();
						// new DriverLoginUserTask().execute();
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
			// Log.d(TAG, "zorro token: " + token);

			// VariableManager.token = token; // Security vulnerability?

			// Download delay reasons
			// change progress spinner text
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
			ServerInterface.getInstance(getApplicationContext()).downloadPartialDeliveryReasons(
					getApplicationContext());

			// Log.i(TAG, "Token aquired.");
			return token;
		}

		@Override
		protected void onPostExecute(String result)
		{
			// Close progress spinner
			if (dialog.isShowing())
			{
				dialog.dismiss();
			}

			// Retrieve list of drivers in a thread
			new RequestDriverManagerTask().execute();
		}
	}

	/**
	 * Requests token from server.
	 * 
	 * @author greg
	 * 
	 */
	private class RequestDriverManagerTask extends AsyncTask<Void, Void, String>
	{

		private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

		/** progress dialog to show user that the backup is processing. */
		/** application context. */
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

				/*CustomToast toast = new CustomToast(getParent());
				toast.setText("Network error");
				toast.setSuccess(false);
				toast.show();*/
			}
			// Close progress spinner
			if (dialog.isShowing())
			{
				dialog.dismiss();
			}
		}
	}

	/**
	 * Requests token from server.
	 * 
	 * @author greg
	 * 
	 */
	private class DriverLoginUserTask extends AsyncTask<Void, Void, Boolean>
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
			hash = holder.text_password.getText().toString(); // DEBUG *Remove when hashing is
																// wanted again

			String status = ServerInterface.getInstance(getApplicationContext()).authDriver(hash,
					selected_user_id, imei_id);

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
				new RetrieveBagsTask().execute();
				// Close progress spinner
				if (dialog.isShowing())
				{
					dialog.dismiss();
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
				new RetrieveBagsTask().execute();
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

			Intent intent = new Intent(getApplicationContext(), HomeActivity.class);

			DbHandler.getInstance(getApplicationContext());
			// Pass driver name on
			intent.putExtra(VariableManager.EXTRA_DRIVER, selected_user_name);

			Log.d(TAG, "Driver ID: " + selected_user_id);
			intent.putExtra(VariableManager.EXTRA_DRIVER_ID, selected_user_id);

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
