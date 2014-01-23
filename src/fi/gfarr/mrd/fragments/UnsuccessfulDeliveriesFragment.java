package fi.gfarr.mrd.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import fi.gfarr.mrd.R;
import fi.gfarr.mrd.adapters.ViewDeliveriesListAdapter;
import fi.gfarr.mrd.adapters.ViewDeliveriesListAdapter.Company;
import fi.gfarr.mrd.adapters.ViewDeliveriesListAdapter.DeliveryType;



public class UnsuccessfulDeliveriesFragment extends ListFragment {
	
	private static final String TAG = "ViewDeliveriesFragment";
	private ViewHolder holder;
	private View rootView;
	
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		List<List<String>> values =  new ArrayList<List<String>>();
				
		// use your own layout
		ViewDeliveriesListAdapter adapter = new ViewDeliveriesListAdapter(getActivity(), values);
		setListAdapter(adapter);
		
		//getListView().setDivider(null);
		//getListView().setDividerHeight(0);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		String item = getListAdapter().getItem(position).toString();
		Toast.makeText(getActivity(), item + " selected", Toast.LENGTH_LONG).show();
	}


	
	
	public void initViewHolder(LayoutInflater inflater, ViewGroup container) {

		if (rootView == null) {

			rootView = inflater.inflate(R.layout.fragment_view_deliveries_content, null, false);

			if (holder == null) {
				holder = new ViewHolder();
			}

			//holder.list = (ListView) rootView.findViewById(listId);			 

			// Store the holder with the view.
			rootView.setTag(holder);

		} else {
			holder = (ViewHolder) rootView.getTag();

			if ((rootView.getParent() != null)
					&& (rootView.getParent() instanceof ViewGroup)) {
				((ViewGroup) rootView.getParent()).removeAllViewsInLayout();
			} else {
			}
		}
	}
	
	//Creates static instances of resources.
	//Increases performance by only finding and inflating resources only once.
	static class ViewHolder {
		TabHost mTabHost;
	}		
}
