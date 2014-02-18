package com.mrdexpress.paperless;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleCursorAdapter;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.helper.VariableManager;

import fi.gfarr.mrd.R;

public class DriverListActivity extends ListActivity implements LoaderCallbacks<Cursor>,
		OnQueryTextListener
{

	private ViewHolder holder;
	private View root_view;
	private SimpleCursorAdapter cursor_adapter;
	private static final int URL_LOADER = 0;// Identifies a particular Loader
											// being used in this component

	static final String[] FROM =
	{ DbHandler.C_DRIVER_NAME };
	static final int[] TO =
	{ R.id.textView_row_driverlist };
	private String filter = ""; // Filter used in SQL query

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_driver_list);
		initViewHolder(); // Inflate ViewHolder static instance

		// Change actionbar title
		setTitle(R.string.title_actionbar_driverlist_select_name);

		// Cancel button click
		// Click Start New Milkrun button
		holder.button_cancel.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				// return to mainmenu
				finish();
			}
		});

		// Configure actionbar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true); // Show up button

		getLoaderManager().initLoader(URL_LOADER, null, this);

		// Test data
		/*	String[] names = { "Bruce Springsteen", "Eric Clapton",
					"Keith Richards", "Angus Young", "Mark Knopfler",
					"John Fogerty", "Tom Petty", "Jethro Tull", "Brian May",
					"Gary Moore", "Mick Jagger", "Freddie Murcury",
					"Brian Johnson", "Bon Scott", "Jimi Hendrix",
					"Stevie Ray Vaugn", "Chuck Berry", "Ozzy Osborne",
					"Alice Cooper" };*/

		// for (int i = 0; i < names.length; i++) {
		// DbHandler.getInstance(this).addDriver(new Driver(i, names[i]));
		// }
		// System.out.println(drivers_jArray.length());

		// Search function
		setupSearchView();

		// Set click listener for list items (selecting a driver)
		holder.list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{

				if (holder.list.getItemAtPosition(position) != null)
				{
					Cursor c = (Cursor) holder.list.getItemAtPosition(position);

					Intent intent;

					// Check if PIN already exists for this driver
					if (!c.getString(c.getColumnIndex(DbHandler.C_DRIVER_PIN)).equals(""))
					{
						intent = new Intent(getApplicationContext(), EnterPinActivity.class);
					}
					else
					{
						intent = new Intent(getApplicationContext(), CreatePinActivity.class);
					}

					// DbHandler.getInstance(getApplicationContext());
					intent.putExtra(VariableManager.EXTRA_DRIVER,
							String.valueOf(c.getString(c.getColumnIndex(DbHandler.C_DRIVER_NAME))));
					intent.putExtra(VariableManager.EXTRA_DRIVER_ID,
							String.valueOf(c.getString(c.getColumnIndex(DbHandler.C_DRIVER_ID))));

					startActivity(intent);
				}
			}
		});
	}

	private void setupSearchView()
	{
		// Start screen with keyboard initially hidden
		holder.search_view.setIconifiedByDefault(true);

		// Listen for text entered in the search field
		holder.search_view.setOnQueryTextListener(this);

		// Remove the silly 'play' button n searchView
		holder.search_view.setSubmitButtonEnabled(false);

		// Text to display when no query is entered
		holder.search_view.setQueryHint(getString(R.string.text_search_hint));

		// Enable filtering of list
		holder.list.setTextFilterEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.driver_list, menu);
		return true;
	}

	/**
	 * Manages your Loaders for you. Responsible for dealing with the Activity
	 * or Fragment lifecycle.
	 */
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{

		DbHandler.getInstance(this);
		String rawQuery = "SELECT * FROM " + DbHandler.TABLE_DRIVERS + " WHERE "
				+ DbHandler.C_DRIVER_NAME + " LIKE " + " '%" + filter + "%'" + " ORDER BY "
				+ DbHandler.C_DRIVER_NAME + " ASC";

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

			cursor_adapter = new SimpleCursorAdapter(this, R.layout.row_driverlist, cursor, FROM,
					TO, 0);

			if (cursor_adapter != null)
			{
				holder.list.setAdapter(cursor_adapter);
				cursor_adapter.changeCursor(cursor);
			}
		}
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

			holder.list = getListView();
			holder.button_cancel = (Button) root_view.findViewById(R.id.button_driverlist_cancel);
			holder.search_view = (SearchView) root_view.findViewById(R.id.searchView_driverlist);

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
		Button button_cancel;
		ListView list;
		SearchView search_view;
	}

	@Override
	public boolean onQueryTextChange(String newText)
	{
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(newText))
		{
			holder.list.clearTextFilter();
			filter = "";
			getLoaderManager().restartLoader(URL_LOADER, null, this);
		}
		else
		{
			holder.list.setFilterText(newText);
			filter = newText;
			getLoaderManager().restartLoader(URL_LOADER, null, this);
		}
		return true;
	}

	/**
	 * When the user presses the 'Search' button
	 */
	@Override
	public boolean onQueryTextSubmit(String query)
	{
		// TODO Auto-generated method stub
		hideSoftKeyboard(this);
		return false;
	}

	public static void hideSoftKeyboard(Activity activity)
	{
		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}

}
