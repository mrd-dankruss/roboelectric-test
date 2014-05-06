package com.mrdexpress.paperless.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.adapters.UnsuccessfulDeliveriesListAdapter;
import com.mrdexpress.paperless.datatype.StopItem;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.db.General;
import com.mrdexpress.paperless.dialogfragments.DeliveryDetailsDialogFragment;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.workflow.Workflow;

public class TabUnsuccessfulDeliveriesFragment extends ListFragment
{

	private static final String TAG = "TabUnsuccessfulDeliveriesFragment";
	private ViewHolder holder;
	private View rootView;
	private UnsuccessfulDeliveriesListAdapter adapter;

	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		// List<List<String>> values = new ArrayList<List<String>>();
        adapter = new UnsuccessfulDeliveriesListAdapter(getActivity(), Bag.STATUS_UNSUCCESSFUL);
        setListAdapter(adapter);




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
	 * @see android.app.Fragment#onResume()
	 */
	@Override
	public void onResume()
	{
		super.onResume();
        adapter.notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		//String item = getListAdapter().getItem(position).toString();
        final DialogFragment deliveryDetails = DeliveryDetailsDialogFragment.newInstance(new CallBackFunction() {
            @Override
            public boolean execute(Object args) {
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        Bundle bundle = new Bundle();
        String stopids = ((StopItem) getListAdapter().getItem(position)).getIDs();
        bundle.putString("STOP_IDS", stopids);
        bundle.putInt("ACTIVE_BAG_POSITION", position);
        if (position == 0)
            Workflow.getInstance().currentBagID = stopids;
        General.getInstance().setActivebagid(stopids);
        deliveryDetails.setArguments(bundle);
        deliveryDetails.show(getFragmentManager(), getTag());
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
            holder.list = (ListView) rootView.findViewById(R.id.fragment_unsuccessful_deliveries_container);

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
            holder.list = (ListView) rootView.findViewById(R.id.fragment_unsuccessful_deliveries_container);
		}
	}

	// Creaic instances of resources.
	// Increases performance by only finding and inflating resources only once.
	static class ViewHolder
	{
		TabHost mTabHost;
        ListView list;
	}

}
