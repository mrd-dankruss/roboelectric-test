package com.mrdexpress.paperless;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.security.PinManager;
import com.mrdexpress.paperless.widget.CustomToast;
import com.mrdexpress.paperless.widget.Toaster;

public class CreatePinActivity extends Activity
{

	private final String TAG = "CreatePinActivity";
	private ViewHolder holder;
	private View root_view;
	private CreatePinActivity context;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_pin);
		context = this;

		// Change actionbar title
		setTitle(R.string.title_actionbar_create_pin);

		// Inflate views
		initViewHolder();

		// button click
		// Click create button
		holder.button_create.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{

				// Check if pin is valid then login
				if (checkPin())
				{
					new CreatePINTask().execute();

					// Intent intent = new Intent(getApplicationContext(),
					// ScanActivity.class);
					//
					// DbHandler.getInstance(getApplicationContext());
					// startActivity(intent);

				}
			}
		});
		// Click change driver button
		holder.button_change.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				finish();
			}
		});

	}

	/**
	 * Retrieves the list of drivers from server to populate login list.
	 * 
	 * @author greg
	 * 
	 */
	private class CreatePINTask extends AsyncTask<Void, Void, String>
	{
		/**
		 * The system calls this to perform work in a worker thread and delivers
		 * it the parameters given to AsyncTask.execute()
		 */
		@Override
		protected String doInBackground(Void... params)
		{
			SharedPreferences prefs = getSharedPreferences(VariableManager.PREF,
					Context.MODE_PRIVATE);

			final String driverid = prefs.getString(VariableManager.PREF_DRIVERID, null);

			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			return ServerInterface.getInstance(getApplicationContext()).updatePIN(driverid,
					holder.editText_pin1.getText().toString(), telephonyManager.getDeviceId());

		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		@Override
		protected void onPostExecute(String result)
		{

			try
			{
				// PIN creation returns from server as successful
				if (result.equals("success"))
				{
					// Retrieve bags for current driver in a thread
					new RetrieveConsignmentsTask().execute();
				}
				else
				{
					// There was a problem
					CustomToast toast = new CustomToast(CreatePinActivity.this);
					toast.setText(result);
					toast.setSuccess(false);
					toast.show();
				}
			}
			catch (NumberFormatException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private boolean checkPin()
	{

		// Check if two pins match
		if (holder.editText_pin1.getText().toString()
				.equals(holder.editText_pin2.getText().toString()))
		{

			// Check for 4-digit format
			String msg = PinManager.checkPin(holder.editText_pin1.getText().toString(), this);
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
		else
		{
			// strings do not match
			displayToast(getString(R.string.text_create_pin_mismatch));
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_pin, menu);
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

			Typeface typeface_roboto_bold = Typeface.createFromAsset(getAssets(), FontHelper
					.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
							FontHelper.STYLE_BOLD));
			Typeface typeface_roboto_regular = Typeface.createFromAsset(getAssets(), FontHelper
					.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
							FontHelper.STYLE_REGULAR));

			holder.button_create = (Button) root_view.findViewById(R.id.button_create_pin_create);
			holder.button_change = (Button) root_view
					.findViewById(R.id.button_create_pin_change_driver);
			holder.editText_pin1 = (EditText) root_view.findViewById(R.id.editText_create_pin_1);
			holder.editText_pin2 = (EditText) root_view.findViewById(R.id.editText_create_pin_2);
			holder.textView_toast = (TextView) root_view
					.findViewById(R.id.textView_create_pin_toast);
			holder.relativeLayout_toast = (RelativeLayout) root_view
					.findViewById(R.id.toast_create_pin);

			holder.button_create.setTypeface(typeface_roboto_bold);
			holder.button_change.setTypeface(typeface_roboto_bold);
			holder.editText_pin1.setTypeface(typeface_roboto_regular);
			holder.editText_pin2.setTypeface(typeface_roboto_regular);

			holder.button_create.setBackgroundResource(R.drawable.button_custom);

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
		Button button_create;
		Button button_change;
		EditText editText_pin1;
		EditText editText_pin2;
		TextView textView_toast;
		RelativeLayout relativeLayout_toast;
	}

	/**
	 * Retrieve list of consignments from API in background
	 * 
	 * @author greg
	 * 
	 */
	private class RetrieveConsignmentsTask extends AsyncTask<Void, Void, Void>
	{

		private ProgressDialog dialog_progress = new ProgressDialog(CreatePinActivity.this);

		/** progress dialog to show user that the backup is processing. */
		/** application context. */
		@Override
		protected void onPreExecute()
		{
			this.dialog_progress.setMessage("Retrieving Workflow");
			this.dialog_progress.show();
		}

		@Override
		protected Void doInBackground(Void... urls)
		{
			SharedPreferences prefs = getSharedPreferences(VariableManager.PREF, Context.MODE_PRIVATE);

			final String driverid = prefs.getString(VariableManager.PREF_DRIVERID, null);

            ServerInterface.getInstance(getApplicationContext()).getMilkrunWorkflow(context);
			//ServerInterface.getInstance(getApplicationContext()).downloadBags( getApplicationContext(), driverid);

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

			Intent intent = new Intent(getApplicationContext(), ScanActivity.class);

			DbHandler.getInstance(getApplicationContext());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

}
