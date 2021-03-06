package com.mrdexpress.paperless.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mrdexpress.paperless.Paperless;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.datatype.StopItem;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.ui.ViewHolder;
import com.mrdexpress.paperless.workflow.Workflow;

import java.util.List;

public class UnsuccessfulDeliveriesListAdapter extends BaseAdapter
{
	private final String TAG = "ViewDeliveriesListAdapter";
	private final Activity activity;
	private final Context context;
    private List<StopItem> values;
	private String bag_id;
    private String status;

	private TextView text_address, text_bag_ids, text_failed_time, text_failed_reason;

	public UnsuccessfulDeliveriesListAdapter(Activity activity, String _status)
	{
		super();
        status = _status;
		this.activity = activity;
		this.context = activity.getApplicationContext();
		notifyDataSetChanged();
	}

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        this.values = Workflow.getInstance().getStopsByStatus(status);
    }

    @Override
	public View getView(int position, View rowView, ViewGroup parent)
	{
		Typeface typeface_roboto_regular = Typeface.createFromAsset(activity.getAssets(),
				FontHelper.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
						FontHelper.STYLE_REGULAR));
		
		Typeface typeface_roboto_bold = Typeface.createFromAsset(activity.getAssets(),
				FontHelper.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
						FontHelper.STYLE_BOLD));

        if( rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_failed_deliveries, parent, false);
        }


		text_address = ViewHolder.get(rowView, R.id.text_failed_deliveries_address);
		text_bag_ids = ViewHolder.get( rowView,R.id.text_failed_deliveries_bags);
		text_failed_time = ViewHolder.get( rowView,R.id.text_failed_deliveries_failed_time);
		text_failed_reason = ViewHolder.get( rowView,R.id.text_failed_deliveries_failed_reason);

		text_address.setTypeface(typeface_roboto_regular);
		text_bag_ids.setTypeface(typeface_roboto_bold);
		text_failed_time.setTypeface(typeface_roboto_regular);
		text_failed_reason.setTypeface(typeface_roboto_regular);

        StopItem stop = values.get(position);

		text_address.setText( stop.getAddress());
		text_bag_ids.setText( stop.getDestinationDesc());

        String status = "<b>Status</b> : " + Paperless.capitalize(stop.getStatus()) + "<br />";
        String reason = "<b>Reason</b> : " + Paperless.capitalize(stop.getReason());
        String command = "<br /><br /><b>(" + "Tap on this delivery to do the handover again." + ")</b>";
        text_failed_time.setText("Failed delivery on : " + stop.getReasonDate());

        text_failed_reason.setText(Html.fromHtml(status + reason + command));

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