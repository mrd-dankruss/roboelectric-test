package fi.gfarr.mrd;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fi.gfarr.mrd.EnterPinActivity.MyHandler;
import fi.gfarr.mrd.db.DbHandler;
import fi.gfarr.mrd.helper.VariableManager;
import fi.gfarr.mrd.net.ServerInterface;
import fi.gfarr.mrd.security.PinManager;
import fi.gfarr.mrd.widget.Toaster;

public class ManagerAuthIncompleteScanActivity extends Activity
{

	private ViewHolder holder;
	private View root_view;
	private final String TAG = "ManagerAuthIncompleteScanActivity";
	ProgressDialog dialog_login;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager_auth_incomplete_scan);

		setTitle(R.string.title_actionbar_manager_auth); // Change actionbar title

		// Initialize ViewHolder
		initViewHolder();

		// Display name of manager
		holder.textView_name
				.setText(getIntent().getStringExtra(VariableManager.EXTRA_MANAGER_NAME));

		// Heading
		holder.textView_heading.setText(getString(R.string.text_manAuth_heading));

		// List of unscanned bags
		new RetrieveUnScannedConsignmentsTask().execute();

		initClickListeners();

		setTitle(R.string.title_actionbar_mainmenu); // Change actionbar title
	}

	private class RetrieveUnScannedConsignmentsTask extends AsyncTask<Void, Void, Void>
	{

		private ProgressDialog dialog_progress = new ProgressDialog(
				ManagerAuthIncompleteScanActivity.this);

		private String list = "";

		/** progress dialog to show user that the backup is processing. */
		/** application context. */
		@Override
		protected void onPreExecute()
		{
			this.dialog_progress.setMessage("Retrieving unscanned consignments");
			this.dialog_progress.show();
		}

		@Override
		protected Void doInBackground(Void... urls)
		{
			list = DbHandler.getInstance(getApplicationContext()).getConsignmentsNotScanned(
					getIntent().getStringExtra(VariableManager.EXTRA_DRIVER_ID));
			System.out.println("list: " + list);
			return null;
		}

		@Override
		protected void onPostExecute(Void nothing)
		{
			holder.textView_list.setText(list);

			// Close progress spinner
			if (dialog_progress.isShowing())
			{
				dialog_progress.dismiss();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manager_auth_incomplete_scan, menu);
		return true;
	}

	/**
	 * Initiate click listeners for buttons.
	 */
	private void initClickListeners()
	{
		holder.button_continue.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				// Perform action on click
				login();
			}
		});

		holder.button_change_manager.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				// Perform action on click
				finish();
			}
		});
	}

	/**
	 * Perform log in procedure. First check validity of PIN, then wait for API call to finish and
	 * start next activity.
	 */
	public void login()
	{

		if (checkPin())
		{
			// Progress spinner
			dialog_login = new ProgressDialog(ManagerAuthIncompleteScanActivity.this);
			dialog_login.setMessage("Authenticating");
			dialog_login.show();

			/*
			 * Make API call authenticating driver credentials in a thread. 
			 * When finished, send msg to thread handler to start ScanActivity
			 * 
			 */
			final MyHandler handler = new MyHandler(this);
			Thread t = new Thread()
			{
				@Override
				public void run()
				{

					String hash = PinManager.toMD5(holder.editText_pin.getText().toString());

					String man_id = getIntent().getStringExtra(VariableManager.EXTRA_MANAGER_ID);
					String driver_id = getIntent().getStringExtra(VariableManager.EXTRA_DRIVER_ID);

					String status = ServerInterface.authManager(man_id, driver_id, hash);

					if (status.equals("success"))
					{
						handler.sendEmptyMessage(0);
					}
					else
					{
						handler.sendEmptyMessage(1);
					}
				}
			};
			t.start();
		}
		/*
		 * if (holder.editText_pin.getText().toString().equals("1111")) { return
		 * true; } else {
		 * displayToast(getString(R.string.text_enter_pin_incorrect)); return
		 * false; }
		 */

	}

	/**
	 * Check PIN's validity (data validation)
	 * 
	 * @return True is valid.
	 */
	private boolean checkPin()
	{

		// Check for 4-digit format
		String msg = PinManager.checkPin(holder.editText_pin.getText().toString(), this);
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
	 * Display a toast using the custom Toaster class
	 * 
	 * @param msg
	 */
	private void displayToast(String msg)
	{
		Toaster.displayToast(msg, holder.textView_toast, holder.relativeLayout_toast, this);
	}

	/**
	 * Custom Handler class that waits for the user authentication API call to complete
	 * before continuing. This class uses weak references to alleviate the HandlerLeak error.
	 * 
	 * @author greg
	 * 
	 */
	static class MyHandler extends Handler
	{
		private WeakReference<ManagerAuthIncompleteScanActivity> mActivity;

		MyHandler(ManagerAuthIncompleteScanActivity activity)
		{
			mActivity = new WeakReference<ManagerAuthIncompleteScanActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg)
		{
			ManagerAuthIncompleteScanActivity activity = mActivity.get();
			if (activity != null)
			{
				activity.handleMessage(msg);
			}
		}
	}

	/**
	 * Starts the barcode scan activity after user authenication thread has completed.
	 * 
	 * @param msg
	 */
	public void handleMessage(Message msg)
	{

		if (msg.what == 0)
		{
			Intent intent = new Intent(getApplicationContext(),
					ViewDeliveriesFragmentActivity.class);
			// intent.putExtra(EXTRA_MESSAGE, message);
			intent.putExtra(VariableManager.EXTRA_DRIVER_ID,
					getIntent().getStringExtra(VariableManager.EXTRA_DRIVER_ID));
			startActivity(intent);

			// Close progress spinner
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (dialog_login.isShowing()) dialog_login.dismiss();
				}
			});

			// startActivity(intent);
		}
	}

	/**
	 * Allows the views' resources to be found only once, improving performance.
	 */
	public void initViewHolder()
	{
		if (root_view == null)
		{
			root_view = this.getWindow().getDecorView().findViewById(android.R.id.content);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			holder.button_continue = (Button) root_view.findViewById(R.id.button_manAuth_continue);
			holder.button_change_manager = (Button) root_view
					.findViewById(R.id.button_manAuth_change);
			holder.textView_heading = (TextView) root_view
					.findViewById(R.id.textView_manAuth_heading);
			holder.textView_list = (TextView) root_view.findViewById(R.id.textView_manAuth_list);
			holder.textView_name = (TextView) root_view
					.findViewById(R.id.textView_manAuth_manager_name);
			holder.editText_pin = (EditText) root_view.findViewById(R.id.editText_manAuth_pin);
			holder.textView_toast = (TextView) root_view.findViewById(R.id.textView_man_auth_toast);

			holder.relativeLayout_toast = (RelativeLayout) root_view
					.findViewById(R.id.toast_man_auth);

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

	// ViewHolder stores static instances of views in order to reduce the number
	// of times that findViewById is called, which affected listview performance
	static class ViewHolder
	{
		Button button_continue, button_change_manager;
		TextView textView_name, textView_heading, textView_list;
		EditText editText_pin;
		TextView textView_toast;
		RelativeLayout relativeLayout_toast;
	}
}
