package com.mrdexpress.paperless.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import com.mrdexpress.paperless.DeliveryDetailsActivity;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.adapters.ViewDeliveriesListAdapter;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.helper.MiscHelper;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.workflow.Workflow;

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

        adapter = new ViewDeliveriesListAdapter(getActivity(), Workflow.getInstance().getBagsByStatus(Bag.STATUS_TODO));
 		if (adapter.getCount() == 0)
		{
			holder.button.setVisibility(View.VISIBLE);
			holder.button.setEnabled(true);
		}
		else
		{
			holder.button.setVisibility(View.GONE);
			holder.button.setEnabled(false);
		}

		//if(DbHandler.getInstance(getActivity()).getBagsByStatus(driverid, Bag.STATUS_TODO).size() == 0)
        if( adapter.getCount() == 0)
		{
			rootView.findViewById(R.id.fragment_viewDeliveries_container).setVisibility(View.GONE);
			rootView.findViewById(R.id.fragment_viewDeliveries_linearLayout).setVisibility(	View.VISIBLE);
		}
		else
		{
			rootView.findViewById(R.id.fragment_viewDeliveries_container).setVisibility( View.VISIBLE);
			rootView.findViewById(R.id.fragment_viewDeliveries_linearLayout).setVisibility(	View.GONE);
		}

		holder.list.setAdapter(adapter);

		holder.list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				// Go to View Deliveries screen
				Intent intent = new Intent(getActivity(), DeliveryDetailsActivity.class);
				intent.putExtra(VariableManager.EXTRA_BAG_NO, ((Bag)holder.list.getItemAtPosition(position)).getBagID());
//				intent.putExtra(VariableManager.EXTRA_DRIVER_ID, driverid);
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
			
			holder.button = (Button) rootView.findViewById(R.id.button_generic_report);
			holder.button.setText(this.getResources().getString(R.string.button_ok));
			holder.button.setBackgroundResource(R.drawable.button_custom);
			holder.button.setEnabled(false);
			holder.button.setVisibility(View.GONE);
			holder.button.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					Intent intent = MiscHelper.getGoHomeIntent(getActivity());
	                startActivity(intent);
				}
			});

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
		Button button;
	}
}
