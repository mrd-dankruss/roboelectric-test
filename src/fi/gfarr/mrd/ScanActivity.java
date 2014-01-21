package fi.gfarr.mrd;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;
import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;

import fi.gfarr.mrd.adapters.ScanSimpleCursorAdapter;
import fi.gfarr.mrd.db.Bag;
import fi.gfarr.mrd.db.DbHandler;
import fi.gfarr.mrd.db.Waybill;
import fi.gfarr.mrd.helper.VariableManager;
import fi.gfarr.mrd.net.ServerInterface;

public class ScanActivity extends CaptureActivity implements
		LoaderCallbacks<Cursor> {

	private ViewHolder holder;
	private View root_view;

	private static final String TAG = "ScanActivity";
	private static final long BULK_MODE_SCAN_DELAY_MS = 1000L; // Default 1000L

	private ScanSimpleCursorAdapter cursor_adapter;
	private static final int URL_LOADER = 1;// Identifies a particular Loader
											// being used in this component
	static final String[] FROM = { DbHandler.C_BAG_ID };
	static final int[] TO = { R.id.textView_row_scan };

	private int manifest_number_items;
	private ArrayList<String> selected_items;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(fi.gfarr.mrd.R.layout.activity_scan);

		// Start rerieving milkruns list from server
		// Param is driver ID, passed through from DriverListActivity
		new RetrieveBagsTask().execute(getIntent().getStringExtra(
				VariableManager.EXTRA_DRIVER_ID));

		getActionBar().setDisplayHomeAsUpEnabled(true);

		initViewHolder();

		if (savedInstanceState != null) {
			// Restore value of members from saved state
			Log.d(TAG, "restoring savedstate");
			selected_items = savedInstanceState
					.getStringArrayList(VariableManager.EXTRA_LIST_SCANNED_ITEMS);
		} else {
			// initialize members with default values for a new instance
			selected_items = new ArrayList<String>();
			Log.d(TAG, "not restoring savedstate");
		}

		// Initiate database
		getLoaderManager().initLoader(URL_LOADER, null, this);

		// Test data

		// // Adding consignments for debug
		// String[] consignment_numbers = { "6009509309793", "5050582112184",
		// "9780321719904", "9781415201435", "9780620336994",
		// "9781920434366", "5099206011571" };
		// String[] consignment_destinations = { "The best of go west",
		// "The best of Knight Rider", "Undercover UX Design", "Byleveld",
		// "Stripped - The King of Teaze", "Mobinomics", "Logitech" };
		//
		// // Adding items for debug
		// String[] bag_waybills = { "000054120122", "000054120122",
		// "00005540321", "079865448642", "00005540321", "00000445566",
		// "91858128382", "156999548585" };
		// manifest_number_items = bag_waybills.length;
		//
		// for (int i = 0; i < consignment_numbers.length; i++) {
		// Bag consignment = new Bag(consignment_numbers[i],
		// consignment_destinations[i]);
		// consignment.setNumberItems(bag_waybills.length);
		//
		// // Commit to DB
		// DbHandler.getInstance(this).addBag(consignment);
		// }
		//
		// Random random = new Random();
		//
		// for (int o = 0; o < consignment_numbers.length; o++) {
		// for (int i = 0; i < bag_waybills.length; i++) {
		// Waybill item = new Waybill(consignment_numbers[o],
		// bag_waybills[i]);
		// item.setWeight(String.valueOf(Math.random()).substring(0, 4)
		// + "KG");
		// item.setDimensions(String.valueOf(random.nextInt(100)) + "x"
		// + String.valueOf(random.nextInt(100)) + "x"
		// + String.valueOf(random.nextInt(100)));
		// item.setParcelCount(String.valueOf(random.nextInt(2) + 1)
		// + " of " + String.valueOf(random.nextInt(2) + 1));
		//
		// DbHandler.getInstance(this).addWaybill(item);
		// }
		// }

		// Set click listener for list items (selecting a driver)

		holder.list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (holder.list.getItemAtPosition(position) != null) {
					Cursor c = (Cursor) holder.list.getItemAtPosition(position);

					Intent intent = new Intent(getApplicationContext(),
							ViewBagManifestActivity.class);

					DbHandler.getInstance(getApplicationContext());

					// Pass info to view manifest activity
					intent.putExtra(VariableManager.EXTRA_CONSIGNMENT_NUMBER,
							String.valueOf(c.getString(c
									.getColumnIndex(DbHandler.C_BAG_ID))));
					intent.putExtra(
							VariableManager.EXTRA_CONSIGNMENT_DESTINATION,
							String.valueOf(c.getString(c
									.getColumnIndex(DbHandler.C_BAG_DEST_BRANCH))));
					// intent.putExtra(
					// VariableManager.EXTRA_CONSIGNMENT_NUMBER_ITEMS,
					// String.valueOf(manifest_number_items));
					intent.putExtra(
							VariableManager.EXTRA_CONSIGNMENT_NUMBER_ITEMS,
							String.valueOf(c.getString(c
									.getColumnIndex(DbHandler.C_BAG_NUM_ITEMS))));

					startActivity(intent);
				}
			}
		});

		// holder.list.setAdapter(AdapterUtils.createSectionAdapter(this));
		holder.list.setAdapter(cursor_adapter);
		// holder.list.setOnItemClickListener(AdapterUtils
		// .createOnItemClickListener(this));
	}

	private class RetrieveBagsTask extends AsyncTask<String, Void, JSONArray> {
		/**
		 * The system calls this to perform work in a worker thread and delivers
		 * it the parameters given to AsyncTask.execute()
		 */
		@Override
		protected JSONArray doInBackground(String... params) {
			// TODO Auto-generated method stub
			return ServerInterface.getConsignments(params[0]);
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		@Override
		protected void onPostExecute(JSONArray result) {

			if (result != null) {

				// Stores waybill IDs as they are loaded.
				// Used to count the number of occurences
				// For counting multiple packages.
				Hashtable<String, Integer> waybill_IDs = new Hashtable<String, Integer>();

				for (int i = 0; i < result.length(); i++) {
					try {
						// ID
						String id = result.getJSONObject(i).getString("id");

						// Destination branch (bag)
						String dest_branch = result.getJSONObject(i).getString(
								"destbranch");

						// Assigned?
						Boolean assigned = result.getJSONObject(i).getBoolean(
								"assigned");

						// Time Created
						String created_time = result.getJSONObject(i)
								.getString("created");

						// Number items
						int num_items = result.getJSONObject(i).getInt(
								"noitems");

						// Go through temp array to find number of times the
						// current waybill ID occurs.

						// Add bag to DB
						Bag bag = new Bag(id, dest_branch);
						bag.setAssigned(assigned);
						bag.setCreationTime(created_time);
						bag.setNumberItems(num_items);
						DbHandler.getInstance(getApplicationContext()).addBag(
								bag);

						// --- Waybills ---

						JSONArray waybills = result.getJSONObject(i)
								.getJSONArray("waybills");

						// Load each waybill in bag
						for (int j = 0; j < waybills.length(); j++) {

							// Tel
							String tel = waybills.getJSONObject(j).getString(
									"telephone");

							// Weight
							String weight = waybills.getJSONObject(j)
									.getString("weight");

							// Dimensions
							String dimen = waybills.getJSONObject(j).getString(
									"dimensions");

							// Waybill ID
							String waybill_id = waybills.getJSONObject(j)
									.getString("id");

							// email
							String email = waybills.getJSONObject(j).getString(
									"email");						

							// comlog
							String comlog = waybills.getJSONObject(j)
									.getString("comlog");

							// parcel count
							int parcel_count = waybills.getJSONObject(j)
									.getInt("parcelcount");

							// Create Waybill object and add values
							Waybill waybill = new Waybill(waybill_id, id);
							waybill.setTelephone(tel);
							waybill.setEmail(email);
							waybill.setComLog(comlog);
							waybill.setWeight(weight);
							waybill.setDimensions(dimen);
							waybill.setParcelCount(parcel_count);

							// Add ID to hashtable
							Integer current_count = waybill_IDs.get(waybill_id);

							// Calculate how many times the current waybill ID
							// has occurred already
							if (current_count != null) {
								// Increment occurence count of the waybill
								waybill_IDs.put(waybill_id, current_count + 1);

								// nth occurance of this waybill
								waybill.setParcelSeq(current_count + 1);
							} else {
								// First occurance of this waybill
								waybill.setParcelSeq(1);
							}

							DbHandler.getInstance(getApplicationContext())
									.addWaybill(waybill);
						}

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
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.

		if (selected_items.size() > 0) {
			savedInstanceState.putStringArrayList(
					VariableManager.EXTRA_LIST_SCANNED_ITEMS, selected_items);

			String msg = "onSaveInstanceState - "
					+ savedInstanceState.getStringArrayList(
							VariableManager.EXTRA_LIST_SCANNED_ITEMS).get(0);
			Log.d(TAG, msg);
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
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
	private void markScannedItems() {

		if (cursor_adapter != null) {
			Cursor cursor = cursor_adapter.getCursor();

			if (cursor != null & selected_items != null) {
				cursor.moveToFirst();
				/*
				 * Start searching through all consignments for ones matching
				 * barcode just scanned.
				 */
				// for (int i = 0; i < selected_items.size(); i++) {
				for (int i = 0; i < getAllChildren(holder.list).size(); i++) {
					/*
					 * Extract TextView from cursorAdapter
					 */

					RelativeLayout row = (RelativeLayout) holder.list
							.getChildAt(Integer.parseInt(selected_items.get(i)));
					if (row != null) {
						TextView text_view = (TextView) row
								.findViewById(R.id.textView_row_scan);
						text_view.setTextColor(getResources().getColor(
								R.color.colour_green_scan)); // Change colour

						// Make tick
						ImageView image_view_tick = (ImageView) row
								.findViewById(R.id.imageView_row_scan_tick);
						image_view_tick.setVisibility(View.VISIBLE);
					}

					cursor.moveToNext();
				}
			} else {
				if (cursor == null) {
					Log.d(TAG, "markScannedItems() - cursor is null");
				}
				if (selected_items == null) {
					Log.d(TAG, "markScannedItems() - selected_items is null");
				}
				/*
				 * if (selected_items.isEmpty()) { Log.d(TAG,
				 * "markScannedItems() - selected_items is empty"); }
				 */
			}
		} else {
			Log.d(TAG, "markScannedItems() - cursor_adapter is null");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (selected_items != null) {
			Log.d(TAG, String.valueOf(selected_items.size()));
		}

	}

	@Override
	public void onStop() {
		super.onStop();

		// Set all consignments' scanned state to false
		DbHandler.getInstance(getApplicationContext()).setScannedAll(false);
	}

	/**
	 * Barcode has been successfully scanned.
	 */
	@Override
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		Toast.makeText(this.getApplicationContext(),
				"Scanned code " + rawResult.getText(), Toast.LENGTH_LONG)
				.show();

		Cursor cursor = null;

		if (cursor_adapter != null) {
			cursor = cursor_adapter.getCursor();
		}

		if (cursor != null) {

			cursor.moveToFirst();

			/**
			 * Start searching through all consignments for ones matching
			 * barcode just scanned.
			 */
			// for (int i = 0; i < cursor.getCount(); i++) {

			ArrayList<View> all_views_within_top_view = getAllChildren(holder.list);
			int i = 0;
			for (View child : all_views_within_top_view) {

				while (!cursor.isAfterLast()) {
					RelativeLayout row = (RelativeLayout) holder.list
							.getChildAt(i);

					if (child != null) {
						TextView text_view = (TextView) child // row
								.findViewById(R.id.textView_row_scan);
						if (text_view != null) {

							String str = text_view.getText().toString();
							StringTokenizer tokenizer = new StringTokenizer(str);
							String cons_number = tokenizer.nextToken();

							if (cons_number.equals(rawResult.getText())) {
								// Match found. Mark as selected.
								text_view.setTextColor(getResources().getColor(
										R.color.colour_green_scan)); // Change
								// colour

								// Make tick
								ImageView image_view_tick = (ImageView) child
										.findViewById(R.id.imageView_row_scan_tick);
								if (image_view_tick != null) {
									image_view_tick.setVisibility(View.VISIBLE);
								}
							} else {

								Log.d(TAG, "handleDecode(): no match "
										+ cons_number);
							}
						}
						// Add this index to a list
						selected_items.add(String.valueOf(i));

						/*
						 * Update scanned status in db to reorder list
						 */
						DbHandler
								.getInstance(getApplicationContext())
								.setScanned(
										cursor.getString(cursor
												.getColumnIndex(DbHandler.C_BAG_ID)),
										true);

						// Refresh list
						cursor_adapter.notifyDataSetChanged();
					} else {
						Log.d(TAG, "handleDecode(): row is null");
					}
					// }
					cursor.moveToNext();
					i++;
				}
			}
		} else {
			Log.d(TAG, "handleDecode(): cursor_adapter is null");
		}

		// Restart barcode scanner to allow for 'semi-automatic firing'
		restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
	}

	/**
	 * Returns ALL rows in a ListView, not just the visible ones.
	 * 
	 * @param v
	 * @return ArrayList<View>
	 */
	private ArrayList<View> getAllChildren(View v) {

		if (!(v instanceof ViewGroup)) {
			ArrayList<View> viewArrayList = new ArrayList<View>();
			viewArrayList.add(v);
			return viewArrayList;
		}

		ArrayList<View> result = new ArrayList<View>();

		ViewGroup vg = (ViewGroup) v;
		for (int i = 0; i < vg.getChildCount(); i++) {

			View child = vg.getChildAt(i);

			ArrayList<View> viewArrayList = new ArrayList<View>();
			viewArrayList.add(v);
			viewArrayList.addAll(getAllChildren(child));

			result.addAll(viewArrayList);
		}
		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scan, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		DbHandler.getInstance(this);
		String rawQuery = "SELECT * FROM " + DbHandler.TABLE_BAGS
				+ " ORDER BY " + DbHandler.C_BAG_SCANNED + " ASC,"
				+ DbHandler.C_BAG_ID + " ASC";// +
		// ","+
		// DbHandler.C_CONSIGNMENT_NO;

		SQLiteCursorLoader loader = new SQLiteCursorLoader(
				getApplicationContext(),
				DbHandler.getInstance(getApplicationContext()), rawQuery, null);

		return loader;
	}

	/**
	 * Update the UI based on the results of your query.
	 */
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor != null && cursor.getCount() > 0) {

			cursor.moveToFirst();

			/**
			 * Moves the query results into the adapter, causing the ListView
			 * fronting this adapter to re-display
			 */

			cursor_adapter = new ScanSimpleCursorAdapter(this,
					R.layout.row_scan, cursor, FROM, TO, 0);

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
	public void onLoaderReset(Loader<Cursor> loader) {
		/*
		 * * Clears out the adapter's reference to the Cursor. This prevents
		 * memory leaks.
		 */
		if (cursor_adapter != null) {
			cursor_adapter.changeCursor(null);
		}
	}

	public void initViewHolder() {

		if (root_view == null) {

			root_view = this.getWindow().getDecorView()
					.findViewById(android.R.id.content);

			if (holder == null) {
				holder = new ViewHolder();
			}

			holder.list = (ListView) root_view.findViewById(R.id.scan_list);

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

	/**
	 * Creates static instances of resources. Increases performance by only
	 * finding and inflating resources only once.
	 **/
	static class ViewHolder {
		ListView list;
	}

}
