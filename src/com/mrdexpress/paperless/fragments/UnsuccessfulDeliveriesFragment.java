package com.mrdexpress.paperless.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.adapters.UnsuccessfulDeliveriesListAdapter;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.helper.VariableManager;

public class UnsuccessfulDeliveriesFragment extends ListFragment
{

	private static final String TAG = "UnsuccessfulDeliveriesFragment";
	private ViewHolder holder;
	private View rootView;
	private UnsuccessfulDeliveriesListAdapter adapter;

	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		// List<List<String>> values = new ArrayList<List<String>>();

		// use your own layout
		// ViewDeliveriesListAdapter adapter = new ViewDeliveriesListAdapter(getActivity(), values);
		/*
		ViewDeliveriesListAdapter adapter = new ViewDeliveriesListAdapter(getActivity(), DbHandler
				.getInstance(getActivity()).getBags(
						getActivity().getIntent().getStringExtra(VariableManager.EXTRA_DRIVER_ID)));
						*/

		// getListView().setDivider(null);
		// getListView().setDividerHeight(0);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences prefs = getActivity().getSharedPreferences(VariableManager.PREF,
				Context.MODE_PRIVATE);

		String driverid = prefs.getString(VariableManager.EXTRA_DRIVER_ID, null);

		adapter = new UnsuccessfulDeliveriesListAdapter(getActivity(), DbHandler.getInstance(getActivity())
				.getBagsByStatus(driverid, Bag.STATUS_UNSUCCESSFUL));

		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		String item = getListAdapter().getItem(position).toString();
		Toast.makeText(getActivity(), item + " selected", Toast.LENGTH_LONG).show();
	}

	public void initViewHolder(LayoutInflater inflater, ViewGroup container)
	{

		if (rootView == null)
		{

			rootView = inflater.inflate(R.layout.fragment_unsuccessful_deliveries_content, null, false);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			// holder.list = (ListView) rootView.findViewById(listId);

			// Store the holder with the view.
			rootView.setTag(holder);

		}
		else
		{
			holder = (ViewHolder) rootView.getTag();

			if ((rootView.getParent() != null) && (rootView.getParent() instanceof ViewGroup))
			{
				((ViewGroup) rootView.getParent()).removeAllViewsInLayout();
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
		TabHost mTabHost;
	}

}
