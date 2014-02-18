package com.mrdexpress.paperless.adapters;

import java.util.ArrayList;

import com.mrdexpress.paperless.datatype.DialogDataObject;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fi.gfarr.mrd.R;

public class ReasonForFailedHandoverListAdapter extends BaseAdapter
{
	private final FragmentActivity activity;
	private final Context context;
	ArrayList<DialogDataObject> values;
	boolean isDialog;

	public ReasonForFailedHandoverListAdapter(FragmentActivity activity,
			ArrayList<DialogDataObject> values, boolean isDialog)
	{
		super();
		this.activity = activity;
		this.context = activity.getApplicationContext();
		this.values = values;
		this.isDialog = isDialog;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_reason_failed_delivery, parent, false);

		TextView mainText = (TextView) rowView.findViewById(R.id.row_reason_failed_handover_title);
		ImageView image = (ImageView) rowView.findViewById(R.id.row_reason_failed_handover_image);

		mainText.setText(values.get(position).getMainText());
		
		if (values.get(position).getThirdText().contentEquals("true"))
		{
			image.setVisibility(View.VISIBLE);
		}
		else
		{
			image.setVisibility(View.INVISIBLE);
		}

		return rowView;
	}

	@Override
	public int getCount()
	{
		return values.size();
	}

	@Override
	public Object getItem(int position)
	{
		return values.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

}