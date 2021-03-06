package com.mrdexpress.paperless.fragments;

import android.app.DialogFragment;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.mrdexpress.paperless.datatype.StopItem;
import com.mrdexpress.paperless.dialogfragments.DeliveryDetailsDialogFragment;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.adapters.ViewDeliveriesListAdapter;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.db.General;
import com.mrdexpress.paperless.dialogfragments.DriverReturnDialogFragment;
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

        adapter = new ViewDeliveriesListAdapter(getActivity(), Bag.STATUS_TODO);
        adapter.registerDataSetObserver( new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if( adapter.getCount() == 0)
                {
                    holder.list.setVisibility(View.GONE);
                    StringBuilder text = new StringBuilder();
                    text.append("<br /><center><h1>Return to dispatch</h1></center>");
                    holder.return_home_text.setText(Html.fromHtml(text.toString()));
                    holder.return_home_text.setVisibility(View.VISIBLE);
                    holder.return_home_button.setVisibility(View.VISIBLE);
                    holder.return_home_button.setText("I've arrived at Dispatch");
                    holder.return_home_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = MiscHelper.getGoHomeIntent(getActivity());
                            ServerInterface.getInstance().endTrip();
                            startActivity(intent);
                        }
                    });
                    /*
                    DriverReturnDialogFragment.newInstance(new CallBackFunction() {
                        @Override
                        public boolean execute(Object args) {
                            Intent intent = MiscHelper.getGoHomeIntent(getActivity());
                            ServerInterface.getInstance().endTrip();
                            startActivity(intent);
                            return false;
                        }
                    }).show(getFragmentManager(), getTag());
                    */

                }
            }
        });
        holder.list.setAdapter(adapter);
        //if(DbHandler.getInstance(getActivity()).getBagsByStatus(driverid, Bag.STATUS_TODO).size() == 0)

        holder.list.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                final DialogFragment deliveryDetails = DeliveryDetailsDialogFragment.newInstance( new CallBackFunction() {
                    @Override
                    public boolean execute(Object args) {
                        adapter.notifyDataSetChanged();
                        return false;
                    }
                });
                Bundle bundle = new Bundle();
                String stopids = ((StopItem)holder.list.getItemAtPosition(position)).getIDs();
                bundle.putString("STOP_IDS", stopids);
                bundle.putInt("ACTIVE_BAG_POSITION", position);
                if (position == 0)
                    Workflow.getInstance().currentBagID = stopids;
                General.getInstance().setActivebagid(stopids);
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

            holder.return_home_text = (TextView) rootView.findViewById(R.id.textView_return_to_base);

            holder.return_home_button = (Button) rootView.findViewById(R.id.button_return_to_base);

            /*
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
            */
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
        TextView return_home_text;
        Button return_home_button;
	}
}
