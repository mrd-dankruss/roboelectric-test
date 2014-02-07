package fi.gfarr.mrd.fragments;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import fi.gfarr.mrd.R;
import fi.gfarr.mrd.adapters.GenericDialogListAdapter;
import fi.gfarr.mrd.datatype.DialogDataObject;
import fi.gfarr.mrd.db.DbHandler;
import fi.gfarr.mrd.helper.VariableManager;
import fi.gfarr.mrd.net.ServerInterface;
import fi.gfarr.mrd.widget.CustomToast;

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
				.getSMSContactPeople(), false);

		holder.list.setAdapter(adapter);

		holder.list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				parentItemPosition = position; // (Integer) adapter.getItem(position);

				String delay_id = ((DialogDataObject) holder.list.getItemAtPosition(position))
						.getThirdText();

				FragmentManager fm = getActivity().getSupportFragmentManager();
				SMSDialog editNameDialog = SMSDialog.newInstance(delay_id);
				editNameDialog
						.setTargetFragment(
								getFragmentManager().findFragmentById(
										R.id.activity_sms_container), 1);
				editNameDialog.show(fm, "SMSFragment");
			}
		});
		
		holder.report_button.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// Only perform action if there is a selection made
				if (VariableManager.delay_id != null)
				{
					new SendSMSTask().execute(
							getActivity().getIntent().getStringExtra(
									VariableManager.EXTRA_NEXT_BAG_ID), getActivity().getIntent()
									.getStringExtra(VariableManager.EXTRA_DRIVER_ID),
							VariableManager.delay_id);
				}
			}
		});
		holder.report_button.setVisibility(View.VISIBLE);
		holder.report_button.setEnabled(false);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		
		((DialogDataObject) adapter.getItem(parentItemPosition)).setSubText(data
				.getStringExtra(DelayDialog.DIALOG_TIME_STRING));

		VariableManager.delay_id = data.getStringExtra(VariableManager.EXTRA_DELAY_ID);

		// holder.report_button.setVisibility(View.VISIBLE);
		holder.report_button.setBackgroundResource(R.drawable.button_custom);
		holder.list.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		holder.report_button.setEnabled(true);
	}

	private class SendSMSTask extends AsyncTask<String, Void, String>
	{

		private ProgressDialog dialog = new ProgressDialog(getActivity());

		/** progress dialog to show user that the backup is processing. */
		/** application context. */
		@Override
		protected void onPreExecute()
		{
			this.dialog.setMessage("Submitting delay report");
			this.dialog.show();
		}

		@Override
		protected String doInBackground(String... args)
		{
			return ServerInterface.postDelay(args[0], args[1], args[2]);
		}

		@Override
		protected void onPostExecute(String result)
		{
			// Close progress spinner
			if (dialog.isShowing())
			{
				dialog.dismiss();
			}
			
			VariableManager.delay_id = null;
			
			CustomToast custom_toast = new CustomToast(getActivity());
			custom_toast.setText("Success");
			custom_toast.setSuccess(true);
			custom_toast.show();
			getActivity().finish();
		}
	}
	
	/*
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
	*/

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
