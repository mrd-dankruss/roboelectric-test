package fi.gfarr.mrd;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import fi.gfarr.mrd.fragments.CustomDialog;
import fi.gfarr.mrd.fragments.ReportDelayListFragment;
import fi.gfarr.mrd.helper.VariableManager;
import fi.gfarr.mrd.net.ServerInterface;
import fi.gfarr.mrd.widget.CustomToast;

public class ReportDelayActivity extends FragmentActivity
{

	private final String TAG = "ReportDelayActivity";
	Fragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_delay);

		// Fragment: Home Begin
		FragmentManager fm = getSupportFragmentManager();
		fragment = fm.findFragmentById(R.id.activity_report_delay_container);
		if (fragment == null)
		{
			fragment = new ReportDelayListFragment();
			fm.beginTransaction().add(R.id.activity_report_delay_container, fragment).commit();
		}
		// Fragment: Home End

		/*
		Button reportButton = (Button) findViewById(R.id.button_generic_report);
		reportButton.setVisibility(View.VISIBLE);
		reportButton.setText(R.string.button_report_delay);
		reportButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Only perform action if there is a selection made
				if (VariableManager.delay_id != null)
				{
					new ReportDelayTask().execute(
							getIntent().getStringExtra(VariableManager.EXTRA_NEXT_BAG_ID),
							getIntent().getStringExtra(VariableManager.EXTRA_DRIVER_ID),
							VariableManager.delay_id);
				}
			}
		});
		*/
	}

	private class ReportDelayTask extends AsyncTask<String, Void, String>
	{

		private ProgressDialog dialog = new ProgressDialog(ReportDelayActivity.this);

		/** progress dialog to show user that the backup is processing. */
		/** application context. */
		@Override
		protected void onPreExecute()
		{
			this.dialog.setMessage("Submitting delay report");
			this.dialog.show();
		}

		@Override
		protected String doInBackground(String... args)
		{
			return ServerInterface.postDelay(args[0], args[1], args[2]);

			// Log.i(TAG, "Token aquired.");

		}

		@Override
		protected void onPostExecute(String result)
		{
			// Close progress spinner
			if (dialog.isShowing())
			{
				dialog.dismiss();
			}
			Log.i(TAG, result);
			// CustomToast toast = new CustomToast(this, )
			VariableManager.delay_id = null;
			finish();
		}
	}
}
