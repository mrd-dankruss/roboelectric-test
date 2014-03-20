package com.mrdexpress.paperless.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.helper.FontHelper;

public class UnsuccessfulDeliveriesListAdapter extends BaseAdapter
{
	private final String TAG = "ViewDeliveriesListAdapter";
	private final FragmentActivity activity;
	private final Context context;
	ArrayList<Bag> values;
	private String bag_id;

	private TextView text_address, text_bag_ids, text_failed_time, text_failed_reason;

	public UnsuccessfulDeliveriesListAdapter(FragmentActivity activity, ArrayList<Bag> values)
	{
		super();
		this.activity = activity;
		this.context = activity.getApplicationContext();
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Typeface typeface_roboto_regular = Typeface.createFromAsset(activity.getAssets(),
				FontHelper.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
						FontHelper.STYLE_REGULAR));
		
		Typeface typeface_roboto_bold = Typeface.createFromAsset(activity.getAssets(),
				FontHelper.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
						FontHelper.STYLE_BOLD));

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_failed_deliveries, parent, false);

		text_address = (TextView) rowView.findViewById(R.id.text_failed_deliveries_address);
		text_bag_ids = (TextView) rowView.findViewById(R.id.text_failed_deliveries_bags);
		text_failed_time = (TextView) rowView.findViewById(R.id.text_failed_deliveries_failed_time);
		text_failed_reason = (TextView) rowView
				.findViewById(R.id.text_failed_deliveries_failed_reason);

		text_address.setTypeface(typeface_roboto_regular);
		text_bag_ids.setTypeface(typeface_roboto_bold);
		text_failed_time.setTypeface(typeface_roboto_regular);
		text_failed_reason.setTypeface(typeface_roboto_regular);

		text_address.setText(values.get(position).getDestinationAddress());
		text_bag_ids.setText(values.get(position).getBarcode());
		
		/*Date date = values.get(position).getSubmissionDate();
		// java.text.DateFormat date_format = android.text.format.DateFormat.getDateFormat(activity
		// .getApplicationContext());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		

		text_failed_time.setText("Delivery at " + format.format(date));*/
		
		
//		text_failed_time.setText("Failed delivery at " + "21/07/2013 15:47"); // TODO: Remove hardcoded
																			// values
		
				
		//text_failed_reason.setText("Reason: " + values.get(position).getStatusReason()); // TODO: Remove hardcoded values

        text_failed_reason.setText("Reason: TODO...");

		return rowView;
	}

	@Override
	public int getCount()
	{
		try
		{
			return values.size();
		}
		catch (NullPointerException e)
		{
			Log.e(TAG, "get(): NullPointerException");
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