package fi.gfarr.mrd;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import fi.gfarr.mrd.adapters.PersonAutoCompleteAdapter;
import fi.gfarr.mrd.datatype.UserItem;
import fi.gfarr.mrd.datatype.UserItem.UserType;
import fi.gfarr.mrd.db.DbHandler;
import fi.gfarr.mrd.helper.VariableManager;
import fi.gfarr.mrd.net.ServerInterface;
import fi.gfarr.mrd.security.PinManager;
import fi.gfarr.mrd.widget.CustomToast;

public class MainActivity extends Activity
{
	private ViewHolder holder;
	private View root_view;
	private final String TAG = "MainActivity";
	ArrayList<UserItem> person_item_list;
	private String selected_user_id;
	private String selected_user_name;
	private UserType selected_user_type;
	String imei_id;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialize ViewHolder
		initViewHolder();

		// Set global variable holding context
		VariableManager.context = this;

		setTitle(R.string.title_actionbar_mainmenu); // Change actionbar title

		person_item_list = new ArrayList<UserItem>();

		new RequestTokenTask().execute();

		initClickListeners();

		PersonAutoCompleteAdapter adapter = new PersonAutoCompleteAdapter(getApplicationContext(),
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

		TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		imei_id = mngr.getDeviceId();

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
			String token = ServerInterface.requestToken(imei_id);

			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(VariableManager.PREF, token);
			editor.commit();

			VariableManager.token = token; // Security vulnerability?

			// Download delay reasons
			// change progress spinner text
			if (dialog.isShowing())
			{
				dialog.setTitle("Retrieving delay reasons");
			}
			ServerInterface.downloadDelays(getApplicationContext());

			if (dialog.isShowing())
			{
				dialog.setTitle("Retrieving failed handover reasons");
			}
			ServerInterface.downloadFailedDeliveryReasons(getApplicationContext());

			if (dialog.isShowing())
			{
				dialog.setTitle("Retrieving partial delivery reasons");
			}
			ServerInterface.downloadPartialDeliveryReasons(getApplicationContext());

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
			ServerInterface.getDrivers(getApplicationContext(), imei_id);
			ServerInterface.getManagers(getApplicationContext(), imei_id);
			return null;
		}

		@Override
		protected void onPostExecute(String result)
		{
			// TODO: Initilize person_item_list
			person_item_list.addAll(DbHandler.getInstance(getApplicationContext()).getDrivers());
			person_item_list.addAll(DbHandler.getInstance(getApplicationContext()).getManagers());
			Log.d(TAG, "PersonList: " + person_item_list.size());

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

			String status = ServerInterface.authDriver(hash, selected_user_id, imei_id);

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
	 * Requests token from server.
	 * 
	 * @author greg
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
			ServerInterface.downloadBags(getApplicationContext(), selected_user_id);
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

	public void initViewHolder()
	{

		if (root_view == null)
		{

			root_view = this.getWindow().getDecorView().findViewById(android.R.id.content);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			holder.button_login = (Button) root_view.findViewById(R.id.button_mainmenu_start_login);
			holder.text_name = (AutoCompleteTextView) root_view
					.findViewById(R.id.text_mainmenu_name);
			holder.text_password = (EditText) root_view.findViewById(R.id.text_mainmenu_password);

			holder.text_password.setTypeface(Typeface.DEFAULT); // TODO: Remove from here. Add
																// proper font setting.

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
