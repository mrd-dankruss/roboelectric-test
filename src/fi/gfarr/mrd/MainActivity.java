package fi.gfarr.mrd;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import fi.gfarr.mrd.helper.VariableManager;
import fi.gfarr.mrd.net.ServerInterface;

public class MainActivity extends Activity
{

	private List<Fragment> fragments = new ArrayList<Fragment>(); // List of
																	// screen
																	// fragments
	private ViewHolder holder;
	private View root_view;
	private final String TAG = "MainActivity";

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

		new RequestTokenTask().execute();

		initClickListeners();
	}

	/**
	 * Initiate click listeners for buttons.
	 */
	private void initClickListeners()
	{
		// Click Start New Milkrun button
		holder.button_start_milkrun.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				// Perform action on click

				new Thread(new Runnable()
				{
					public void run()
					{
						Intent intent = new Intent(getApplicationContext(),
								DriverListActivity.class);
						startActivity(intent);
					}
				}).start();
			}
		});

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
			String token = ServerInterface.requestToken();

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

			// Progress spinner
			final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
			dialog.setMessage("Retrieving list of drivers");
			dialog.show();

			// Retrieve list of drivers from server, after token has been acquired
			// JSONArray drivers_jArray = ServerInterface.getDrivers();

			// Retrieve list of drivers in a thread
			Runnable r = new Runnable()
			{
				@Override
				public void run()
				{
					// Log.i(TAG, "Fetching list of drivers");
					ServerInterface.getDrivers(getApplicationContext());
					// Log.i(TAG, "Driver list updated.");

					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							if (dialog.isShowing()) dialog.dismiss();
						}
					});
				}
			};

			Thread thread = new Thread(r);
			thread.start();
		}
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

			holder.button_start_milkrun = (Button) root_view
					.findViewById(R.id.button_mainmenu_start_milkrun);
			holder.button_start_trainingrun = (Button) root_view
					.findViewById(R.id.button_mainmenu_training_run);

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
		Button button_start_milkrun;
		Button button_start_trainingrun;
	}

}
