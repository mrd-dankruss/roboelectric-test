package com.mrdexpress.paperless.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mrdexpress.paperless.DeliveryDetailsActivity;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.adapters.ViewDeliveriesListAdapter;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.helper.VariableManager;

public class ViewDeliveriesFragment extends Fragment
{

	private static final String TAG = "ViewDeliveriesFragment";
	private ViewHolder holder;
	private View rootView;
	private ViewDeliveriesListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		initViewHolder(inflater, container); // Inflate ViewHolder static instance

		return rootView;
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

		final String driverid = prefs.getString(VariableManager.EXTRA_DRIVER_ID, null);

		adapter = new ViewDeliveriesListAdapter(getActivity(), DbHandler.getInstance(getActivity())
				.getBagsByStatus(driverid, Bag.STATUS_TODO));

		if (DbHandler.getInstance(getActivity()).getBagsByStatus(driverid, Bag.STATUS_TODO).size() == 0)
		{
			rootView.findViewById(R.id.fragment_viewDeliveries_container).setVisibility(View.GONE);
			rootView.findViewById(R.id.fragment_viewDeliveries_linearLayout).setVisibility(
					View.VISIBLE);
		}
		else
		{
			rootView.findViewById(R.id.fragment_viewDeliveries_container).setVisibility(
					View.VISIBLE);
			rootView.findViewById(R.id.fragment_viewDeliveries_linearLayout).setVisibility(
					View.GONE);
		}

		holder.list.setAdapter(adapter);

		holder.list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				// Go to View Deliveries screen
				Intent intent = new Intent(getActivity(),
						DeliveryDetailsActivity.class);
				intent.putExtra(VariableManager.EXTRA_BAG_NO, ((Bag)holder.list.getItemAtPosition(position)).getBagNumber());
				intent.putExtra(VariableManager.EXTRA_DRIVER_ID, driverid);
				intent.putExtra(VariableManager.EXTRA_LIST_POSITION, position + "");
				startActivity(intent);
			}
		});
	}

	public void initViewHolder(LayoutInflater inflater, ViewGroup container)
	{

		if (rootView == null)
		{

			rootView = inflater.inflate(R.layout.fragment_view_deliveries_content, null, false);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			holder.list = (ListView) rootView.findViewById(R.id.fragment_viewDeliveries_container);

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
		ListView list;
	}
}
