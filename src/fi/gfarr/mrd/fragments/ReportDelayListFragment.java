package fi.gfarr.mrd.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import fi.gfarr.mrd.R;

public class ReportDelayListFragment extends ListFragment
{
	
	DialogFragment newFragment;

	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		String[] values = getResources().getStringArray(R.array.reportDelay_listItems);
		
		// use your own layout
		MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getActivity(), values);
		setListAdapter(adapter);
		
		// getListView().setDivider(null);
		// getListView().setDividerHeight(0);
	}

	public void onResume() {
		super.onResume();
		Log.d("fi.gfarr.mrd", "onResume");
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		String item = (String) getListAdapter().getItem(position);
		Toast.makeText(getActivity(), item + " selected", Toast.LENGTH_LONG).show();
		
		newFragment = TrafficTimeDelayDialog.newInstance(10);
		newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
		//setButtonListeners();
	}

	private void setButtonListeners() {
		
		((TrafficTimeDelayDialog) newFragment).getDelayButton_5min().setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setDelayTime("5 min");
				newFragment.dismiss();
			}
		});
		
		((TrafficTimeDelayDialog) newFragment).getDelayButton_10min().setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setDelayTime("10 min");
				newFragment.dismiss();
			}
		});
		
		((TrafficTimeDelayDialog) newFragment).getDelayButton_20min().setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setDelayTime("20 min");
				newFragment.dismiss();
			}
		});
		
		((TrafficTimeDelayDialog) newFragment).getDelayButton_30min().setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setDelayTime("30 min");
				newFragment.dismiss();
			}
		});
		
		((TrafficTimeDelayDialog) newFragment).getDelayButton_1hour().setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setDelayTime("1 hour");
				newFragment.dismiss();
			}
		});
	}
	
	private void setDelayTime(String time) {
		Log.d("fi.gfarr.mrd", "Time: " + time);
	}
	
	class MySimpleArrayAdapter extends ArrayAdapter<String>
	{
		private final Context context;
		private final String[] values;

		public MySimpleArrayAdapter(Context context, String[] values)
		{
			super(context, R.layout.fragment_report_delay_row, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.fragment_report_delay_row, parent, false);
			
			TextView mainText = (TextView) rowView.findViewById(R.id.reportDelay_textView_mainText);
			TextView subText = (TextView) rowView.findViewById(R.id.reportDelay_textView_subText);
			
			if (position == 0)
			{
				subText.setVisibility(View.VISIBLE);
				subText.setText("10 min");
			}
			
			mainText.setText(values[position]);
			
			return rowView;
		}
	}

}
