package fi.gfarr.mrd.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;
import fi.gfarr.mrd.R;
import fi.gfarr.mrd.adapters.ExpandableListAdapter;
import fi.gfarr.mrd.datatype.DeliveryHandoverDataObject;
import fi.gfarr.mrd.datatype.ReasonPartialDeliveryItem;

public class ReasonPartialDeliveryFragment extends Fragment
{

	private ViewHolder holder;
	private View rootView;
	private ExpandableListAdapter listAdapter;
	private ArrayList<ArrayList<ReasonPartialDeliveryItem>> data;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		initViewHolder(inflater, container); // Inflate ViewHolder static instance

		// preparing list data
		prepareListData();

		listAdapter = new ExpandableListAdapter(holder.list, getActivity(), data);

		// setting list adapter
		holder.list.setAdapter(listAdapter);

		// Listview Group click listener
		holder.list.setOnGroupClickListener(new OnGroupClickListener()
		{

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
					long id)
			{
				// Toast.makeText(getApplicationContext(),
				// "Group Clicked " + listDataHeader.get(groupPosition),
				// Toast.LENGTH_SHORT).show();
				// return false;
				return parent.isGroupExpanded(groupPosition);
			}
		});

		// Listview Group expanded listener
		holder.list.setOnGroupExpandListener(new OnGroupExpandListener()
		{

			@Override
			public void onGroupExpand(int groupPosition)
			{
				// Toast.makeText(getActivity(), listDataHeader.get(groupPosition) + " Expanded",
				// Toast.LENGTH_SHORT).show();
			}
		});

		// Listview Group collasped listener
		holder.list.setOnGroupCollapseListener(new OnGroupCollapseListener()
		{

			@Override
			public void onGroupCollapse(int groupPosition)
			{
				// Toast.makeText(getActivity(), listDataHeader.get(groupPosition) + " Collapsed",
				// Toast.LENGTH_SHORT).show();

			}
		});

		// Listview on child click listener
		holder.list.setOnChildClickListener(new OnChildClickListener()
		{

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
					int childPosition, long id)
			{
				
				setTick(groupPosition, childPosition);
				listAdapter.notifyDataSetChanged();
				
				return false;
			}
		});

		holder.list.expandGroup(0);
		holder.list.expandGroup(1);
		holder.list.expandGroup(2);

		return rootView;
	}

	private void setTick(int groupPosition, int childPosition)
	{
		data.get(groupPosition).get(childPosition).setIsSelected(true);
		for (int i = 0; i < data.get(groupPosition).size(); i++)
		{
			if (i != childPosition)
			{
				data.get(groupPosition).get(i).setIsSelected(false);
			}
		}
	}

	public void initViewHolder(LayoutInflater inflater, ViewGroup container)
	{

		if (rootView == null)
		{

			rootView = inflater.inflate(R.layout.fragment_reason_partial_delivery, null, false);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			holder.list = (ExpandableListView) rootView.findViewById(R.id.lvExp);

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
		ExpandableListView list;
	}

	/*
	 * Preparing the list data
	 * TODO: Remove this
	 */
	private void prepareListData()
	{
		data = new ArrayList<ArrayList<ReasonPartialDeliveryItem>>();

		ArrayList<ReasonPartialDeliveryItem> temp = new ArrayList<ReasonPartialDeliveryItem>();
		temp.add(new ReasonPartialDeliveryItem("00025420254 (TAKEALOT)", "greg_code_1", "Wrong Parcel", false));
		temp.add(new ReasonPartialDeliveryItem("00025420254 (TAKEALOT)", "greg_code_1", "Lost in Transit", true));
		temp.add(new ReasonPartialDeliveryItem("00025420254 (TAKEALOT)", "greg_code_1", "Wrong Branch", false));
		
		data.add(temp);
		
		temp = new ArrayList<ReasonPartialDeliveryItem>();
		temp.add(new ReasonPartialDeliveryItem("00025420255 (FNB)", "greg_code_1", "Wrong Parcel", false));
		temp.add(new ReasonPartialDeliveryItem("00025420255 (FNB)", "greg_code_1", "Lost in Transit", true));
		temp.add(new ReasonPartialDeliveryItem("00025420255 (FNB)", "greg_code_1", "Wrong Branch", false));
		
		data.add(temp);
		
		temp = new ArrayList<ReasonPartialDeliveryItem>();
		temp.add(new ReasonPartialDeliveryItem("00025420256 (FOOD)", "greg_code_1", "Wrong Parcel", false));
		temp.add(new ReasonPartialDeliveryItem("00025420256 (FOOD)", "greg_code_1", "Lost in Transit", true));
		temp.add(new ReasonPartialDeliveryItem("00025420256 (FOOD)", "greg_code_1", "Wrong Branch", false));
		
		data.add(temp);
	}
}
