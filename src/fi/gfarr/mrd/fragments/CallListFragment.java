package fi.gfarr.mrd.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import fi.gfarr.mrd.adapters.GenericDialogListAdapter;
import fi.gfarr.mrd.datatype.DialogDataObject;

public class CallListFragment extends ListFragment
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
		values.add(new DialogDataObject("Branch", "", "0834533156"));
		values.add(new DialogDataObject("Call centre", "", "0834533156"));
		values.add(new DialogDataObject("Chief operating Officer", "", "0834533156"));
		values.add(new DialogDataObject("Customer", "", "0834533156"));
		
		
		adapter = new GenericDialogListAdapter(getActivity(), values, false);
		setListAdapter(adapter);
	}

	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		parentItemPosition = (Integer) getListAdapter().getItem(position);
		Intent intent = new Intent(Intent.ACTION_CALL);

		intent.setData(Uri.parse("tel:" + values.get(parentItemPosition).getPhoneNumber()));
		getActivity().startActivity(intent);
	}
}
