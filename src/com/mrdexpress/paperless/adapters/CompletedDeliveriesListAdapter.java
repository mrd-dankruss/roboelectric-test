package com.mrdexpress.paperless.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.app.Activity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mrdexpress.paperless.Paperless;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.helper.MiscHelper;
import com.mrdexpress.paperless.ui.ViewHolder;
import com.mrdexpress.paperless.workflow.Workflow;

import java.util.ArrayList;

public class CompletedDeliveriesListAdapter extends BaseAdapter
{
	private final String TAG = "ViewDeliveriesListAdapter";
	private final Activity activity;
	private final Context context;
	private ArrayList<Bag> values;
    private String status;
	private String bag_id;

	private TextView text_address, text_bag_ids, text_failed_time;

	public CompletedDeliveriesListAdapter(Activity activity, String _status)
	{
		super();
		this.activity = activity;
		this.context = activity.getApplicationContext();
		this.status = _status;
        notifyDataSetChanged();
	}

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        this.values = Workflow.getInstance().getBagsByStatus(status);
    }

	@Override
	public View getView(int position, View rowView, ViewGroup parent)
	{
		Typeface typeface_roboto_regular = Typeface.createFromAsset(activity.getAssets(),
				FontHelper.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
						FontHelper.STYLE_REGULAR));

		Typeface typeface_roboto_bold = Typeface.createFromAsset(activity.getAssets(), FontHelper
				.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
						FontHelper.STYLE_BOLD));

		Typeface typeface_roboto_italic = Typeface.createFromAsset(activity.getAssets(), FontHelper
				.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
						FontHelper.STYLE_ITALIC));

        if( rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_completed_deliveries, parent, false);
        }

		text_address = ViewHolder.get(rowView, R.id.text_completed_deliveries_address);
		text_bag_ids = ViewHolder.get( rowView, R.id.text_completed_deliveries_bags);
		text_failed_time = ViewHolder.get( rowView, R.id.text_completed_deliveries_failed_time);

		text_address.setTypeface(typeface_roboto_regular);
		text_bag_ids.setTypeface(typeface_roboto_bold);
		text_failed_time.setTypeface(typeface_roboto_italic);

		text_address.setText(values.get(position).getDestinationAddress());
		text_bag_ids.setText(values.get(position).getBarcode());
        text_address.setText(MiscHelper.getBagFormattedAddress(values.get(position)));
        Bag bag = values.get(position);
        text_failed_time.setText("Delivered on : " + bag.getReasonDate());

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