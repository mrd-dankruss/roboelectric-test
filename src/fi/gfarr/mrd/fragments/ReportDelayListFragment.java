package fi.gfarr.mrd.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fi.gfarr.mrd.R;
import fi.gfarr.mrd.adapters.GenericDialogListAdapter;
import fi.gfarr.mrd.datatype.DialogDataObject;
import fi.gfarr.mrd.db.DbHandler;
import fi.gfarr.mrd.helper.VariableManager;

public class ReportDelayListFragment extends ListFragment
{

	DialogFragment newFragment;
	TextView subText;
	GenericDialogListAdapter adapter;
	ArrayList<DialogDataObject> values;
	private int parentItemPosition;
	private ListView list;
	private View view;

	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);

		// adapter = new GenericDialogListAdapter(getActivity(), values, false);
		adapter = new GenericDialogListAdapter(getActivity(), DbHandler.getInstance(getActivity())
				.getMilkrunDelayReasons(), false);
		setListAdapter(adapter);
	}

	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		parentItemPosition = position;// (Integer) getListAdapter().getItem(position);

		FragmentManager fm = getActivity().getSupportFragmentManager();

		if (l.getItemAtPosition(position) != null)
		{
			// Cursor c = (Cursor) getListView().getItemAtPosition(position);
			// String delay_id = c.getString(c.getColumnIndex(DbHandler.C_DELAYS_ID));
			String delay_id = ((DialogDataObject) getListView().getItemAtPosition(position))
					.getThirdText();

			// String delay_id = (String) getListView().getItemAtPosition(position);

			DelayDialog editNameDialog = DelayDialog.newInstance(delay_id);
			editNameDialog.setTargetFragment(this, 1);
			editNameDialog.show(fm, "reportDelayFragment");
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		/*	values.get(parentItemPosition).setSubText(
					data.getStringExtra(GenericResultDialog.DIALOG_TIME_STRING));*/
		((DialogDataObject) adapter.getItem(parentItemPosition)).setSubText(data
				.getStringExtra(DelayDialog.DIALOG_TIME_STRING));

		VariableManager.delay_id = data.getStringExtra(VariableManager.EXTRA_DELAY_ID);

		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout ll = new RelativeLayout(getActivity());
		ll = (RelativeLayout) layoutInflater.inflate(R.layout.fragment_view_deliveries_content, ll);
		
		Button reportButton = (Button) ll.findViewById(R.id.button_generic_report);

		// System.out.println("test: " + view.getClass().getName());
		// Button reportButton = (Button) view.findViewById(R.id.button_generic_report);
		reportButton.setVisibility(View.INVISIBLE);
		reportButton.setBackgroundResource(R.drawable.button_custom);

		System.out.println("test: " + data.getStringExtra(DelayDialog.DIALOG_TIME_STRING));
		setListAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	public boolean hasDataSentSuccessfully()
	{
		// TODO: Implement sending of data here

		return false;
	}

}
