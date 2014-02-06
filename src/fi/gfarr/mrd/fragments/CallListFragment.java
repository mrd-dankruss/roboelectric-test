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
import fi.gfarr.mrd.db.Contact;
import fi.gfarr.mrd.db.DbHandler;
import fi.gfarr.mrd.helper.VariableManager;

public class CallListFragment extends ListFragment
{

	DialogFragment newFragment;
	TextView subText;
	GenericDialogListAdapter adapter;
	private int parentItemPosition;

	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);

		adapter = new GenericDialogListAdapter(getActivity(),
				DbHandler.getInstance(getActivity())
						.getContacts(
								getActivity().getIntent().getStringExtra(
										VariableManager.EXTRA_NEXT_BAG_ID)), false);
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
		Intent intent = new Intent(Intent.ACTION_CALL);

		String phone_number = ((DialogDataObject) getListView().getItemAtPosition(position))
				.getSubText();

		intent.setData(Uri.parse("tel:" + phone_number));
		getActivity().startActivity(intent);
	}
}
