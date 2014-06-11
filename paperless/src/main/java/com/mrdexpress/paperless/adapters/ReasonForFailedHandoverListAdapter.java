package com.mrdexpress.paperless.adapters;

import android.content.Context;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.datatype.DialogDataObject;

import java.util.ArrayList;

public class ReasonForFailedHandoverListAdapter extends BaseAdapter
{
	private final Activity activity;
	private final Context context;
	ArrayList<DialogDataObject> values;
	boolean isDialog;

	public ReasonForFailedHandoverListAdapter(Activity activity,
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
            mainText.setTypeface(null , Typeface.BOLD);
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