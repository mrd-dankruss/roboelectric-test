package fi.gfarr.mrd.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import fi.gfarr.mrd.R;
import fi.gfarr.mrd.adapters.GenericDialogListAdapter;
import fi.gfarr.mrd.datatype.DialogDataObject;
import fi.gfarr.mrd.db.DbHandler;

public class SmsListFragment extends Fragment
{

	private ViewHolder holder;
	private View rootView;
	private GenericDialogListAdapter adapter;

	DialogFragment newFragment;
	TextView subText;
	ArrayList<DialogDataObject> values;
	private int parentItemPosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		initViewHolder(inflater, container); // Inflate ViewHolder static instance

		return rootView;
	}

	public void onResume()
	{
		super.onResume();

		adapter = new GenericDialogListAdapter(getActivity(), DbHandler.getInstance(getActivity())
				.getSMSMessages(), false);

		holder.list.setAdapter(adapter);

		holder.list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				parentItemPosition = position; //(Integer) adapter.getItem(position);

				FragmentManager fm = getActivity().getSupportFragmentManager();
				SMSDialog editNameDialog = SMSDialog.newInstance("delay id"); // DEBUG
				editNameDialog
						.setTargetFragment(
								getFragmentManager().findFragmentById(
										R.id.activity_report_delay_container), 1);
				editNameDialog.show(fm, "SMSFragment");
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		values.get(parentItemPosition).setSubText(
				data.getStringExtra(DelayDialog.DIALOG_TIME_STRING));
		adapter.notifyDataSetChanged();
	}

	public boolean hasMessageSentSuccessfully()
	{
		// TODO: Implement sending of data here
		sendMessage();

		return true;
	}

	public void sendMessage()
	{
		String numbersToReceiveMessage = "";
		for (int i = 0; i < values.size(); i++)
		{
			if (values.get(i).getSubText().length() > 0)
			{
				numbersToReceiveMessage = numbersToReceiveMessage + values.get(i).getSubText()
						+ ",";
			}
		}
		try
		{

			// TODO: Get message body from server
			numbersToReceiveMessage = numbersToReceiveMessage.substring(0,
					numbersToReceiveMessage.length() - 1);
			Intent sendIntent = new Intent(Intent.ACTION_VIEW);
			sendIntent.putExtra("address", numbersToReceiveMessage);
			sendIntent.putExtra("sms_body", "I am running late by");
			sendIntent.setType("vnd.android-dir/mms-sms");
			startActivity(sendIntent);

		}
		catch (Exception e)
		{
			Toast.makeText(getActivity(), "SMS faild, please try again later!", Toast.LENGTH_LONG)
					.show();
			e.printStackTrace();
		}
	}

	public void initViewHolder(LayoutInflater inflater, ViewGroup container)
	{

		if (rootView == null)
		{

			rootView = inflater.inflate(R.layout.fragment_view_deliveries_content, null, false);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			holder.list = (ListView) rootView.findViewById(R.id.fragment_viewDeliveries_container);
			holder.report_button = (Button) rootView.findViewById(R.id.button_generic_report);

			// Store the holder with the view.
			rootView.setTag(holder);

		}
		else
		{
			holder = (ViewHolder) rootView.getTag();

			if ((rootView.getParent() != null) && (rootView.getParent() instanceof ViewGroup))
			{
				((ViewGroup) rootView.getParent()).removeAllViewsInLayout();
			}
			else
			{
			}
		}
	}

	// Creates static instances of resources.
	// Increases performance by only finding and inflating resources only once.
	static class ViewHolder
	{
		ListView list;
		Button report_button;
	}

}
