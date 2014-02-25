package com.mrdexpress.paperless.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.datatype.DialogDataObject;

public class GenericDialogListAdapter extends BaseAdapter
{
	private final FragmentActivity activity;
	private final Context context;
	ArrayList<DialogDataObject> values;
	boolean isDialog;

	public GenericDialogListAdapter(FragmentActivity activity, ArrayList<DialogDataObject> values,
			boolean isDialog)
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
		View rowView = inflater.inflate(R.layout.fragment_report_delay_row, parent, false);

		TextView mainText = (TextView) rowView.findViewById(R.id.reportDelay_textView_mainText);
		TextView subText = (TextView) rowView.findViewById(R.id.reportDelay_textView_subText);

		mainText.setText(values.get(position).getMainText());

		if ((values.get(position).getSubText().length() > 0) && (isDialog == false))
		{
			subText.setText(values.get(position).getSubText());
			subText.setVisibility(View.VISIBLE);
		}

		return rowView;
	}

	@Override
	public int getCount()
	{
		if (values != null)
		{
			return values.size();
		}
		else
		{
			return 0;
		}
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