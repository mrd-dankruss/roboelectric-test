package fi.gfarr.mrd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import fi.gfarr.mrd.db.DbHandler;
import fi.gfarr.mrd.db.Driver;
import fi.gfarr.mrd.helper.VariableManager;
import fi.gfarr.mrd.net.ServerInterface;

public class MainActivity extends Activity {

	private List<Fragment> fragments = new ArrayList<Fragment>(); // List of
																	// screen
																	// fragments
	private ViewHolder holder;
	private View root_view;
	private final String TAG = "MainActivty";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialize ViewHolder
		initViewHolder();

		setTitle(R.string.title_actionbar_mainmenu); // Change actionbar title

		new RequestTokenTask().execute();

		// Retrieve list of drivers from server
		// JSONArray drivers_jArray = ServerInterface.getDrivers();
		new RetrieveDriversTask().execute();
		initClickListeners();
	}

	/**
	 * Initiate click listeners for buttons.
	 */
	private void initClickListeners() {
		// Click Start New Milkrun button
		holder.button_start_milkrun
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						// Perform action on click

						new Thread(new Runnable() {
							public void run() {
								Intent intent = new Intent(
										getApplicationContext(),
										DriverListActivity.class);
								startActivity(intent);
							}
						}).start();
					}
				});

		// Click Start Trainingrun button
		holder.button_start_trainingrun
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						// Perform action on click

						new Thread(new Runnable() {
							public void run() {
								Intent intent = new Intent(
										getApplicationContext(),
										ScanActivity.class);
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
	private class RequestTokenTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... urls) {

			String token = ServerInterface.requestToken();

			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(VariableManager.PREF_TOKEN, token);
			editor.commit();

			VariableManager.token = token; // Security vulnerability?

			return token;
		}
	}

	/**
	 * Retrieves the list of drivers from server to populate login list.
	 * 
	 * @author greg
	 * 
	 */
	private class RetrieveDriversTask extends AsyncTask<Void, Void, JSONArray> {
		/**
		 * The system calls this to perform work in a worker thread and delivers
		 * it the parameters given to AsyncTask.execute()
		 */
		@Override
		protected JSONArray doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return ServerInterface.getDrivers();
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		@Override
		protected void onPostExecute(JSONArray result) {
			for (int i = 0; i < result.length(); i++) {
				try {
					// ID
					int id = Integer.parseInt(result.getJSONObject(i)
							.getString(VariableManager.JSON_KEY_DRIVER_ID));

					// Name
					String name = result.getJSONObject(i)
							.getString(VariableManager.JSON_KEY_DRIVER_FIRSTNAME)
							+ " "
							+ result.getJSONObject(i).getString(VariableManager.JSON_KEY_DRIVER_LASTNAME);

					// PIN
					String pin = result.getJSONObject(i).getString(VariableManager.JSON_KEY_DRIVER_PIN);

					DbHandler.getInstance(getApplicationContext()).addDriver(
							new Driver(id, name, pin));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void initViewHolder() {

		if (root_view == null) {

			root_view = this.getWindow().getDecorView()
					.findViewById(android.R.id.content);

			if (holder == null) {
				holder = new ViewHolder();
			}

			holder.button_start_milkrun = (Button) root_view
					.findViewById(R.id.button_mainmenu_start_milkrun);
			holder.button_start_trainingrun = (Button) root_view
					.findViewById(R.id.button_mainmenu_training_run);

			// Store the holder with the view.
			root_view.setTag(holder);

		} else {
			holder = (ViewHolder) root_view.getTag();

			if ((root_view.getParent() != null)
					&& (root_view.getParent() instanceof ViewGroup)) {
				((ViewGroup) root_view.getParent()).removeAllViewsInLayout();
			} else {
			}
		}
	}

	// ViewHolder stores static instances of views in order to reduce the number
	// of times that findViewById is called, which affected listview performance
	static class ViewHolder {
		Button button_start_milkrun;
		Button button_start_trainingrun;
	}

}
