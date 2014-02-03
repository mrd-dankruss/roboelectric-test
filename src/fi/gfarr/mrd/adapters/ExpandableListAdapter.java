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
import fi.gfarr.mrd.datatype.DeliveryHandoverDataObject;

public class ExpandableListAdapter extends BaseExpandableListAdapter
{

	private Context _context;
	
	private ArrayList<String> headerNames;
	private HashMap<String, ArrayList<DeliveryHandoverDataObject>> data;
	private ExpandableListView expList;

	public ExpandableListAdapter(ExpandableListView expList, Context context, ArrayList<String> headerNames, HashMap<String, ArrayList<DeliveryHandoverDataObject>> data)
	{
		this._context = context;
		this.headerNames = headerNames;
		this.data = data;
		this.expList = expList;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon)
	{
		return data.get(headerNames.get(groupPosition)).get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
	{

		final String childText = ((DeliveryHandoverDataObject)getChild(groupPosition, childPosition)).getParcelID();

		if (convertView == null)
		{
			LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_item, null);
		}

		TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);
		ImageView imageListChild = (ImageView) convertView.findViewById(R.id.extendableList_imageView_tick);

		txtListChild.setText(childText);

		if (((DeliveryHandoverDataObject)getChild(groupPosition, childPosition)).isParcelScanned())
		{
			imageListChild.setVisibility(View.VISIBLE);
		} else {
			imageListChild.setVisibility(View.INVISIBLE);
		}
		
		if (data.get(headerNames.get(groupPosition)).size()-1 == childPosition)
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
		Log.d("fi.gfarr.mrd", "getChildCount: " + data.get(headerNames.get(groupPosition)).size());
		return data.get(headerNames.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition)
	{
		return headerNames.get(groupPosition);
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
		String headerTitle = (String) getGroup(groupPosition);
		if (convertView == null)
		{
			LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_group, null);
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
