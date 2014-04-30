package com.mrdexpress.paperless.fragments;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import com.mrdexpress.paperless.DeliveryDetailsDialogFragment;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.adapters.ViewDeliveriesListAdapter;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.db.General;
import com.mrdexpress.paperless.helper.MiscHelper;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.workflow.Workflow;

public class TabViewDeliveriesFragment extends Fragment
{

	private static final String TAG = "TabViewDeliveriesFragment";
	private ViewHolder holder;
	private View rootView;
	private ViewDeliveriesListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		initViewHolder(inflater, container); // Inflate ViewHolder static instance

		return rootView;
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        try {
            //adapter.getItem(0);
        }catch(Exception e){

        }


        holder.list.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                DialogFragment deliveryDetails = DeliveryDetailsDialogFragment.newInstance( new CallBackFunction() {
                    @Override
                    public boolean execute(Object args) {
                        return false;
                    }
                });
                Bundle bundle = new Bundle();
                Integer bagid = ((Bag)holder.list.getItemAtPosition(position)).getBagID();
                bundle.putInt("ACTIVE_BAG_ID", bagid);
                bundle.putInt("ACTIVE_BAG_POSITION", position);
                if (position == 0)
                    Workflow.getInstance().currentBagID = bagid;
                General.getInstance().setActivebagid(bagid);
                deliveryDetails.setArguments( bundle);

                deliveryDetails.show( getFragmentManager(), getTag());
            }
        });
    }

	@Override
	public void onResume()
	{
		super.onResume();
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
			//holder.button.setText(this.getResources().getString(R.string.button_ok));
            holder.button.setText("End delivery run");
			holder.button.setBackgroundResource(R.drawable.button_custom);
			holder.button.setEnabled(false);
			holder.button.setVisibility(View.GONE);
			holder.button.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					Intent intent = MiscHelper.getGoHomeIntent(getActivity());
                    ServerInterface.getInstance().endTrip();
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
