package com.mrdexpress.paperless.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.adapters.CompletedDeliveriesListAdapter;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.workflow.Workflow;

public class TabPartialDeliveriesFragment extends Fragment
{

	private static final String TAG = "ViewPartialDeliveriesFragment";
	private ViewHolder holder;
	private View rootView;
	private CompletedDeliveriesListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		initViewHolder(inflater, container); // Inflate ViewHolder static instance

        adapter = new CompletedDeliveriesListAdapter(getActivity(), Bag.STATUS_PARTIAL);
        holder.list.setAdapter(adapter);

		return rootView;
	}

	/* (non-Javadoc)
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume()
	{
		super.onResume();

        adapter.notifyDataSetChanged();

		/*holder.button_completed.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				holder.button_completed.setBackgroundResource(R.drawable.partial_tab_white);
				holder.button_partial.setBackgroundResource(R.drawable.partial_tab_grey);
				adapter = new CompletedDeliveriesListAdapter(getActivity(), DbHandler.getInstance(
						getActivity()).getBagsByStatus(driverid, Bag.STATUS_COMPLETED));
				holder.list.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		});

		holder.button_partial.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				holder.button_completed.setBackgroundResource(R.drawable.partial_tab_grey);
				holder.button_partial.setBackgroundResource(R.drawable.partial_tab_white);
				adapter = new CompletedDeliveriesListAdapter(getActivity(), DbHandler.getInstance(
						getActivity()).getBagsByStatus(driverid, Bag.STATUS_PARTIAL));
				holder.list.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		} */
    }

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser)
	{
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser)
		{
			if (holder != null)
			{
				/*if ((holder.button_completed != null) && (holder.button_partial != null))
				{
					holder.button_completed.setBackgroundResource(R.drawable.partial_tab_white);
					holder.button_partial.setBackgroundResource(R.drawable.partial_tab_grey);
				} */
			}
		}
	}

	public void initViewHolder(LayoutInflater inflater, ViewGroup container)
	{

		if (rootView == null)
		{

			rootView = inflater.inflate(R.layout.fragment_successful_deliveries_content, null, false);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			holder.list = (ListView)rootView.findViewById(R.id.fragment_successful_deliveries_container);
			/*holder.button_completed = (Button) rootView
					.findViewById(R.id.fragment_successful_deliveries_completed_button);
			holder.button_partial = (Button) rootView
					.findViewById(R.id.fragment_successful_deliveries_partial_button); */

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
		Button button_completed, button_partial;
	}
}
