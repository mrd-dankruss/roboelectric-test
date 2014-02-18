package com.mrdexpress.paperless;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.commonsware.cwac.loaderex.SQLiteCursorLoader;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.helper.VariableManager;

import fi.gfarr.mrd.R;

public class ViewBagManifestActivity extends ListActivity implements LoaderCallbacks<Cursor>
{

	private ViewHolder holder;
	private View root_view;

	private SimpleCursorAdapter cursor_adapter;

	static final String[] FROM =
	{ DbHandler.C_WAYBILL_ID, DbHandler.C_BAG_ID, DbHandler.C_WAYBILL_WEIGHT,
			DbHandler.C_WAYBILL_DIMEN, DbHandler.C_wAYBILL_PARCEL_SEQUENCE };
	static final int[] TO =
	{ R.id.textView_manifest_waybill, R.id.textView_manifest_consignment_number,
			R.id.textView_manifest_weight, R.id.textView_manifest_volume,
			R.id.textView_manifest_number };

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_bag_manifest);

		// Change actionbar title
		setTitle(R.string.title_actionbar_manifest);

		// Inflate views
		initViewHolder();

		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Initiate database
		getLoaderManager().initLoader(VariableManager.URL_LOADER_BAG_MANIFEST, null, this);

		// Remove padding from textview
		holder.text_view_consignment_destination.setIncludeFontPadding(false);
		holder.text_view_consignment_number.setIncludeFontPadding(false);

		// Set titles
		holder.text_view_consignment_number.setText(getString(R.string.text_consignment) + " #"
				+ getIntent().getStringExtra(VariableManager.EXTRA_CONSIGNMENT_NUMBER) + " ("
				+ getIntent().getStringExtra(VariableManager.EXTRA_CONSIGNMENT_NUMBER_ITEMS)
				+ " items)");
		holder.text_view_consignment_destination
				.setText(getString(R.string.text_destination_branch) + " "
						+ getIntent().getStringExtra(VariableManager.EXTRA_CONSIGNMENT_DESTINATION));
	}

	/**
	 * Manages your Loaders for you. Responsible for dealing with the Activity
	 * or Fragment lifecycle.
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		DbHandler.getInstance(this);
		String rawQuery = "SELECT * FROM " + DbHandler.TABLE_WAYBILLS + " ORDER BY "
				+ DbHandler.C_WAYBILL_ID + " ASC";

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

			cursor_adapter = new SimpleCursorAdapter(this, R.layout.row_manifest, cursor, FROM, TO,
					0);

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_bag_manifest, menu);
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

	public void initViewHolder()
	{

		if (root_view == null)
		{

			root_view = this.getWindow().getDecorView().findViewById(android.R.id.content);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			// holder.list = (ListView)
			// root_view.findViewById(R.id.listView_manifest_list);
			holder.list = getListView();
			holder.text_view_consignment_number = (TextView) root_view
					.findViewById(R.id.textView_manifest_consignment_number);
			holder.text_view_consignment_destination = (TextView) root_view
					.findViewById(R.id.textView_manifest_consignment_destination);
			// holder.text_view_manifest_weight = (TextView) root_view
			// .findViewById(R.id.textView_manifest_weight);

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
		TextView text_view_consignment_number;
		TextView text_view_consignment_destination;
		// TextView text_view_manifest_weight;
		ListView list;

	}

}
