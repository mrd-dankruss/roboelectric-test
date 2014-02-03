package fi.gfarr.mrd.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import fi.gfarr.mrd.adapters.GenericDialogListAdapter;
import fi.gfarr.mrd.datatype.DialogDataObject;
import fi.gfarr.mrd.db.DbHandler;
import fi.gfarr.mrd.net.ServerInterface;

public class ReportDelayListFragment extends ListFragment
{

	DialogFragment newFragment;
	TextView subText;
	GenericDialogListAdapter adapter;
	ArrayList<DialogDataObject> values;
	private int parentItemPosition;

	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		values = new ArrayList<DialogDataObject>();
		values.add(new DialogDataObject("Traffic", ""));
		values.add(new DialogDataObject("Can't find address", ""));
		values.add(new DialogDataObject("Held up at destination", ""));
		values.add(new DialogDataObject("Other", ""));

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
		parentItemPosition = (Integer) getListAdapter().getItem(position);

		FragmentManager fm = getActivity().getSupportFragmentManager();
		GenericResultDialog editNameDialog = GenericResultDialog.newInstance(10);
		editNameDialog.setTargetFragment(this, 1);
		editNameDialog.show(fm, "reportDelayFragment");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		values.get(parentItemPosition).setShortDisplayTime(
				data.getStringExtra(GenericResultDialog.DIALOG_TIME_STRING));
		adapter.notifyDataSetChanged();
	}

	public boolean hasDataSentSuccessfully()
	{
		// TODO: Implement sending of data here

		return false;
	}

}
