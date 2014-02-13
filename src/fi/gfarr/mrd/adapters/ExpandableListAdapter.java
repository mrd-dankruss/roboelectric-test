package fi.gfarr.mrd.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import fi.gfarr.mrd.R;
import fi.gfarr.mrd.datatype.ReasonPartialDeliveryItem;

public class ExpandableListAdapter extends BaseExpandableListAdapter
{

	private Context context;
	private ArrayList<ArrayList<ReasonPartialDeliveryItem>> data;
	private ExpandableListView expList;

	public ExpandableListAdapter(ExpandableListView expList, Context context, ArrayList<ArrayList<ReasonPartialDeliveryItem>> data)
	{
		this.context = context;
		this.data = data;
		this.expList = expList;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon)
	{
		return data.get(groupPosition).get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
	{

		final String childText = ((ReasonPartialDeliveryItem)getChild(groupPosition, childPosition)).getReasonTitle();

		if (convertView == null)
		{
			LayoutInflater inflater_child = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater_child.inflate(R.layout.list_item, null);
		}

		TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);
		ImageView imageListChild = (ImageView) convertView.findViewById(R.id.extendableList_imageView_tick);

		txtListChild.setText(childText);

		if (((ReasonPartialDeliveryItem)getChild(groupPosition, childPosition)).isSelected())
		{
			imageListChild.setVisibility(View.VISIBLE);
		} else {
			imageListChild.setVisibility(View.INVISIBLE);
		}
		
		if (data.get(groupPosition).size()-1 == childPosition)
		{
			convertView.setPadding(0, 0, 0, 8);
			//expList.setDividerHeight(20);
		} else {
			convertView.setPadding(0, 0, 0, 0);
			expList.setDividerHeight(0);
		}

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
//		Log.d("fi.gfarr.mrd", "getChildCount: " + data.get(groupPosition).size());
		return data.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition)
	{
		return data.get(groupPosition).get(0);
	}

	@Override
	public int getGroupCount()
	{
		return data.size();
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
	{
		String headerTitle = ((ReasonPartialDeliveryItem)getGroup(groupPosition)).getGroupName();
		
		if (convertView == null)
		{
			LayoutInflater inflater_group = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater_group.inflate(R.layout.list_group, null);
		}

		TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle);

		//expList.setDividerHeight(0);

		return convertView;
	}

	@Override
	public boolean hasStableIds()
	{
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		return true;
	}

}
