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
import android.widget.Toast;
import fi.gfarr.mrd.adapters.GenericDialogListAdapter;
import fi.gfarr.mrd.datatype.DialogDataObject;

public class SmsListFragment extends ListFragment
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
		
		FragmentManager fm = getActivity().getSupportFragmentManager();
		GenericResultDialog editNameDialog = GenericResultDialog.newInstance(10);
		editNameDialog.setTargetFragment(this, 1);
        editNameDialog.show(fm, "SMSFragment");
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		values.get(parentItemPosition).setShortDisplayTime(data.getStringExtra(GenericResultDialog.DIALOG_TIME_STRING));
		adapter.notifyDataSetChanged();
	}
	
	public boolean hasMessageSentSuccessfully() {
		//TODO: Implement sending of data here
		sendMessage();
		
		return true;
	}
	
	public void sendMessage() {
		String numbersToReceiveMessage = "";
		for (int i = 0; i < values.size(); i++)
		{
			if (values.get(i).getShortDisplayTime().length() > 0)
			{
				numbersToReceiveMessage = numbersToReceiveMessage + values.get(i).getPhoneNumber() + ",";
			}
		}
		try {

			//TODO: Get message body from server
			numbersToReceiveMessage = numbersToReceiveMessage.substring(0, numbersToReceiveMessage.length()-1);
			Intent sendIntent = new Intent(Intent.ACTION_VIEW);
			sendIntent.putExtra("address", numbersToReceiveMessage);
			sendIntent.putExtra("sms_body", "I am running late by");
			sendIntent.setType("vnd.android-dir/mms-sms");
			startActivity(sendIntent);

		} catch (Exception e) {
			Toast.makeText(getActivity(), "SMS faild, please try again later!", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	
}
