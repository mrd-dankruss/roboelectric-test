package fi.gfarr.mrd;

import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;
import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;

import fi.gfarr.mrd.adapters.ScanSimpleCursorAdapter;
import fi.gfarr.mrd.db.DbHandler;
import fi.gfarr.mrd.fragments.IncompleteScanDialog;
import fi.gfarr.mrd.helper.VariableManager;
import fi.gfarr.mrd.net.ServerInterface;
import fi.gfarr.mrd.widget.Toaster;

public class ScanActivity extends CaptureActivity implements LoaderCallbacks<Cursor>
{

	private ViewHolder holder;
	private View root_view;

	private static final String TAG = "ScanActivity";
	private static final long BULK_MODE_SCAN_DELAY_MS = 1000L; // Default 1000L

	private ScanSimpleCursorAdapter cursor_adapter;
	private static final int URL_LOADER = 1;// Identifies a particular Loader
											// being used in this component
	static final String[] FROM =
	{ DbHandler.C_BAG_ID };
	static final int[] TO =
	{ R.id.textView_row_scan };

	private ArrayList<String> selected_items;

	private IncompleteScanDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(fi.gfarr.mrd.R.layout.activity_scan);

		// Start rerieving milkruns list from server
		// Param is driver ID, passed through from DriverListActivity
		// new
		// RetrieveBagsTask().execute(getIntent().getStringExtra(VariableManager.EXTRA_DRIVER_ID));

		getActionBar().setDisplayHomeAsUpEnabled(true);

		try
		{
			Log.d(TAG,
					DbHandler
							.getInstance(getApplicationContext())
							.getBag(getIntent().getStringExtra(VariableManager.EXTRA_DRIVER_ID),
									"42").getDestination());
		}
		catch (NullPointerException e)
		{
			Log.e(TAG, "bag null");
		}

		// Store currently selected driver id globally
		VariableManager.current_driver_id = getIntent().getStringExtra(
				VariableManager.EXTRA_DRIVER_ID);

		initViewHolder();

		if (savedInstanceState != null)
		{
			// Restore value of members from saved state
			Log.d(TAG, "restoring savedstate");
			selected_items = savedInstanceState
					.getStringArrayList(VariableManager.EXTRA_LIST_SCANNED_ITEMS);
		}
		else
		{
			// initialize members with default values for a new instance
			selected_items = new ArrayList<String>();
			Log.d(TAG, "not restoring savedstate");
		}

		// Set click listener for list items (selecting a driver)

		holder.list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{

				// View manifest
				if (holder.list.getItemAtPosition(position) != null)
				{
					Cursor c = (Cursor) holder.list.getItemAtPosition(position);

					Intent intent = new Intent(getApplicationContext(),
							ViewBagManifestActivity.class);

					DbHandler.getInstance(getApplicationContext());

					// Pass info to view manifest activity
					intent.putExtra(VariableManager.EXTRA_CONSIGNMENT_NUMBER,
							String.valueOf(c.getString(c.getColumnIndex(DbHandler.C_BAG_ID))));
					intent.putExtra(VariableManager.EXTRA_CONSIGNMENT_DESTINATION, String.valueOf(c
							.getString(c.getColumnIndex(DbHandler.C_BAG_DEST_HUBNAME))));
					// intent.putExtra(
					// VariableManager.EXTRA_CONSIGNMENT_NUMBER_ITEMS,
					// String.valueOf(manifest_number_items));
					intent.putExtra(VariableManager.EXTRA_CONSIGNMENT_NUMBER_ITEMS, String
							.valueOf(c.getString(c.getColumnIndex(DbHandler.C_BAG_NUM_ITEMS))));

					startActivity(intent);
				}
			}
		});

		// Start Milkrun
		holder.button_start_milkrun.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub

				// Check if all bags have been scanned
				if ((selected_items.size() == holder.list.getCount()) & (selected_items.size() > 0))
				{
					// Go to View Deliveries screen
					Intent intent = new Intent(getApplicationContext(),
							ViewDeliveriesFragmentActivity.class);
					intent.putExtra(VariableManager.EXTRA_DRIVER_ID,
							getIntent().getStringExtra(VariableManager.EXTRA_DRIVER_ID));
					// EditText editText = (EditText) findViewById(R.id.edit_message);
					// String message = editText.getText().toString();
					// intent.putExtra(EXTRA_MESSAGE, message);
					startActivity(intent);
				}
				else
				{
					dialog = new IncompleteScanDialog(ScanActivity.this);
					dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					dialog.show();

					LayoutInflater factory = LayoutInflater.from(ScanActivity.this);

					final Button button_continue = (Button) dialog
							.findViewById(R.id.button_incomplete_scan_continue);

					button_continue.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							new RetrieveManagersTask().execute();
						}
					});

					final Button button_scan = (Button) dialog
							.findViewById(R.id.button_incomplete_scan_scan);

					button_scan.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							dialog.dismiss();
						}
					});
				}
			}
		});

		// Initiate database
		getLoaderManager().initLoader(URL_LOADER, null, this);

		// holder.list.setAdapter(AdapterUtils.createSectionAdapter(this));
		holder.list.setAdapter(cursor_adapter);
		// holder.list.setOnItemClickListener(AdapterUtils
		// .createOnItemClickListener(this));
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		super.onSaveInstanceState(savedInstanceState);
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.

		if (selected_items.size() > 0)
		{
			savedInstanceState.putStringArrayList(VariableManager.EXTRA_LIST_SCANNED_ITEMS,
					selected_items);

			String msg = "onSaveInstanceState - "
					+ savedInstanceState.getStringArrayList(
							VariableManager.EXTRA_LIST_SCANNED_ITEMS).get(0);
			Log.d(TAG, msg);
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.

		selected_items = savedInstanceState
				.getStringArrayList(VariableManager.EXTRA_LIST_SCANNED_ITEMS);

		String msg = "onRestoreInstanceState - " + selected_items.get(0);
		Log.d(TAG, msg);
	}

	/**
	 * Ticks consignments off as they are scanned.
	 */
	private void markScannedItems()
	{
		if (cursor_adapter != null)
		{
			Cursor cursor = cursor_adapter.getCursor();

			if (cursor != null & selected_items != null)
			{
				cursor.moveToFirst();
				/*
				 * Start searching through all consignments for ones matching
				 * barcode just scanned.
				 */
				// for (int i = 0; i < selected_items.size(); i++) {
				for (int i = 0; i < getAllChildren(holder.list).size(); i++)
				{
					/*
					 * Extract TextView from cursorAdapter
					 */

					RelativeLayout row = (RelativeLayout) holder.list.getChildAt(Integer
							.parseInt(selected_items.get(i)));
					if (row != null)
					{
						TextView text_view = (TextView) row.findViewById(R.id.textView_row_scan);
						text_view.setTextColor(getResources().getColor(R.color.colour_green_scan)); // Change
																									// colour

						// Make tick
						ImageView image_view_tick = (ImageView) row
								.findViewById(R.id.imageView_row_scan_tick);
						image_view_tick.setVisibility(View.VISIBLE);
					}
					cursor.moveToNext();
				}
			}
			else
			{
				if (cursor == null)
				{
					Log.d(TAG, "markScannedItems() - cursor is null");
				}
				if (selected_items == null)
				{
					Log.d(TAG, "markScannedItems() - selected_items is null");
				}
				/*
				 * if (selected_items.isEmpty()) { Log.d(TAG,
				 * "markScannedItems() - selected_items is empty"); }
				 */
			}
		}
		else
		{
			Log.d(TAG, "markScannedItems() - cursor_adapter is null");
		}
	}

	/**
	 * Search through list of scanned items to find if the current has been scanned already.
	 * 
	 * @param list
	 *            The selected_items ArrayList
	 * @param bag_id
	 *            Currently selected driver ID
	 * @return true is duplicate.
	 */
	private boolean checkSelectedBagDuplicate(ArrayList<String> list, String bag_id)
	{
		// Log.d(TAG, "checkDupe bag: " + bag_id);
		// Iterate through each element until a dupe is found.
		for (int i = 0; i < list.size(); i++)
		{
			// Log.d(TAG, "list: " + list.get(i));
			if (DbHandler
					.getInstance(getApplicationContext())
					.getBagIdAtRow(getIntent().getStringExtra(VariableManager.EXTRA_DRIVER_ID),
							Integer.parseInt(list.get(i))).equals(bag_id))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Barcode has been successfully scanned.
	 */
	@Override
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor)
	{
		// Toast.makeText(this.getApplicationContext(), "Scanned code " + rawResult.getText(),
		// Toast.LENGTH_LONG).show();

		Cursor cursor = null;
		int total_bags = DbHandler.getInstance(getApplicationContext()).getBagCount(
				getIntent().getStringExtra(VariableManager.EXTRA_DRIVER_ID));

		if (cursor_adapter != null)
		{
			cursor = cursor_adapter.getCursor();
		}

		if (cursor != null)
		{

			cursor.moveToFirst();

			/**
			 * Start searching through all consignments for ones matching
			 * barcode just scanned.
			 */
			// for (int i = 0; i < cursor.getCount(); i++) {

			ArrayList<View> all_views_within_top_view = getAllChildren(holder.list);

			int i = 0;
			for (View child : all_views_within_top_view)
			{

				while (!cursor.isAfterLast())
				{
					RelativeLayout row = (RelativeLayout) holder.list.getChildAt(i);

					if (child != null)
					{
						TextView text_view = (TextView) child // row
								.findViewById(R.id.textView_row_scan);
						if (text_view != null)
						{

							String str = text_view.getText().toString();
							StringTokenizer tokenizer = new StringTokenizer(str);
							String cons_number = tokenizer.nextToken();

							if (cons_number.equals(rawResult.getText()))
							{
								// Match found. Mark as selected.
								text_view.setTextColor(getResources().getColor(
										R.color.colour_green_scan)); // Change
								// colour

								// Make tick
								ImageView image_view_tick = (ImageView) child
										.findViewById(R.id.imageView_row_scan_tick);
								if (image_view_tick != null)
								{
									image_view_tick.setVisibility(View.VISIBLE);
								}

								// Check if selected item has already been added.
								if (!checkSelectedBagDuplicate(selected_items, rawResult.getText()))
								{
									// Add this index to a list
									selected_items.add(String.valueOf(i));
								}

								// Log.d(TAG, selected_items.size() + "/" + total_bags);

								// Make toast, with strawberry jam
								if (selected_items.size() == total_bags) // All bags scanned
								{
									displayToast(getString(R.string.text_scan_successful));
								}
								else
								// Another bag scanned, not everything yet.
								{
									displayToast(getString(R.string.text_scan_next));
								}
							}
							else
							{

								Log.d(TAG, "handleDecode(): no match " + cons_number);
							}
						}
						// // Add this index to a list
						// selected_items.add(String.valueOf(i));

						/*
						 * Update scanned status in db to reorder list.						 
						 */
						DbHandler.getInstance(getApplicationContext()).setScanned(
								cursor.getString(cursor.getColumnIndex(DbHandler.C_BAG_ID)), true);

						// Refresh list
						cursor_adapter.notifyDataSetChanged();
					}
					else
					{
						Log.d(TAG, "handleDecode(): row is null");
					}
					// }
					cursor.moveToNext();
					i++;
				}
			}
		}
		else
		{
			Log.d(TAG, "handleDecode(): cursor_adapter is null");
		}

		// Restart barcode scanner to allow for 'semi-automatic firing'
		restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (selected_items != null)
		{
			Log.d(TAG, "Items selected: " + String.valueOf(selected_items.size()));
		}

		// Close dialog if it is showing upon resuming screen.
		// Or else it is still open when backing out of ManagerAuthIncompleteScanActivity
		if (dialog != null)
		{
			if (dialog.isShowing())
			{
				dialog.dismiss();
			}
		}
	}

	@Override
	public void onStop()
	{
		super.onStop();

		// Set all consignments' scanned state to false
		DbHandler.getInstance(getApplicationContext()).setScannedAll(false);
	}

	/**
	 * Returns ALL rows in a ListView, not just the visible ones.
	 * 
	 * @param v
	 * @return ArrayList<View>
	 */
	private ArrayList<View> getAllChildren(View v)
	{

		if (!(v instanceof ViewGroup))
		{
			ArrayList<View> viewArrayList = new ArrayList<View>();
			viewArrayList.add(v);
			return viewArrayList;
		}

		ArrayList<View> result = new ArrayList<View>();

		ViewGroup vg = (ViewGroup) v;
		for (int i = 0; i < vg.getChildCount(); i++)
		{

			View child = vg.getChildAt(i);

			ArrayList<View> viewArrayList = new ArrayList<View>();
			viewArrayList.add(v);
			viewArrayList.addAll(getAllChildren(child));

			result.addAll(viewArrayList);
		}
		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scan, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Manages your Loaders for you. Responsible for dealing with the Activity
	 * or Fragment lifecycle.
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		DbHandler.getInstance(this);
		String rawQuery = "SELECT * FROM " + DbHandler.TABLE_BAGS + " WHERE "
				+ DbHandler.C_BAG_DRIVER_ID + " LIKE '"
				+ getIntent().getStringExtra(VariableManager.EXTRA_DRIVER_ID) + "'" + " ORDER BY "
				+ DbHandler.C_BAG_SCANNED + " ASC," + DbHandler.C_BAG_ID + " ASC";// +
		// ","+
		// DbHandler.C_CONSIGNMENT_NO;

		SQLiteCursorLoader loader = new SQLiteCursorLoader(getApplicationContext(),
				DbHandler.getInstance(getApplicationContext()), rawQuery, null);

		return loader;
	}

	/**
	 * Update the UI based on the results of your query.
	 */
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
	{
		if (cursor != null && cursor.getCount() > 0)
		{

			cursor.moveToFirst();

			/**
			 * Moves the query results into the adapter, causing the ListView
			 * fronting this adapter to re-display
			 */

			cursor_adapter = new ScanSimpleCursorAdapter(this, R.layout.row_scan, cursor, FROM, TO,
					0);

			holder.list.setAdapter(cursor_adapter);

			cursor_adapter.changeCursor(cursor);

		}
		markScannedItems();
	}

	/**
	 * This method allows you to release any resources you hold, so that the
	 * Loader can free them. You can set any references to the cursor object you
	 * hold to null. But do not close the cursor – the Loader does this for you.
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{
		/*
		 * * Clears out the adapter's reference to the Cursor. This prevents
		 * memory leaks.
		 */
		if (cursor_adapter != null)
		{
			cursor_adapter.changeCursor(null);
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

			holder.list = (ListView) root_view.findViewById(R.id.scan_list);

			holder.button_start_milkrun = (Button) root_view
					.findViewById(R.id.scan_button_start_milkrun);

			holder.textView_toast = (TextView) root_view.findViewById(R.id.textView_scan_toast);

			holder.relativeLayout_toast = (RelativeLayout) root_view.findViewById(R.id.toast_scan);

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

	/**
	 * Creates static instances of resources. Increases performance by only
	 * finding and inflating resources only once.
	 **/
	static class ViewHolder
	{
		ListView list;
		Button button_start_milkrun;
		TextView textView_toast;
		RelativeLayout relativeLayout_toast;
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
	 * Retrieve list of managers from API in background
	 * 
	 * @author greg
	 * 
	 */
	private class RetrieveManagersTask extends AsyncTask<Void, Void, Void>
	{

		private ProgressDialog dialog_progress = new ProgressDialog(ScanActivity.this);

		/** progress dialog to show user that the backup is processing. */
		/** application context. */
		@Override
		protected void onPreExecute()
		{
			this.dialog_progress.setMessage("Retrieving list of managers");
			this.dialog_progress.show();
		}

		@Override
		protected Void doInBackground(Void... urls)
		{
			// Log.i(TAG, "Fetching token...");
			ServerInterface.getManagers(getApplicationContext());

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

			// Start manager authorization activity
			Intent intent = new Intent(getApplicationContext(), ManagerListActivity.class);

			// Pass driver name on
			intent.putExtra(VariableManager.EXTRA_DRIVER,
					getIntent().getStringExtra(VariableManager.EXTRA_DRIVER));

			intent.putExtra(VariableManager.EXTRA_DRIVER_ID,
					getIntent().getStringExtra(VariableManager.EXTRA_DRIVER_ID));

			startActivity(intent);
		}
	}

}
