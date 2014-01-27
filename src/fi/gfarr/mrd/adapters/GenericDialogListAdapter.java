package fi.gfarr.mrd.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fi.gfarr.mrd.R;
import fi.gfarr.mrd.datatype.DialogDataObject;
import fi.gfarr.mrd.fragments.GenericResultDialog;
import fi.gfarr.mrd.fragments.MoreDialogFragment;

public class GenericDialogListAdapter extends BaseAdapter
{
	private final FragmentActivity activity;
	private final Context context;
	ArrayList<DialogDataObject> values;
	boolean isDialog;

	public GenericDialogListAdapter(FragmentActivity activity, ArrayList<DialogDataObject> values, boolean isDialog)
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
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.fragment_report_delay_row, parent, false);
		
		TextView mainText = (TextView) rowView.findViewById(R.id.reportDelay_textView_mainText);
		TextView subText = (TextView) rowView.findViewById(R.id.reportDelay_textView_subText);
		
		mainText.setText(values.get(position).getLongDisplayTime());
		
		if ((values.get(position).getShortDisplayTime().length() > 0) && (isDialog == false))
		{
			subText.setText(values.get(position).getShortDisplayTime());
			subText.setVisibility(View.VISIBLE);
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
		return position;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

}