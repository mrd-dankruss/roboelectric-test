package com.mrdexpress.paperless.fragments;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.adapters.SmsDialogListAdapter;
import com.mrdexpress.paperless.datatype.DialogDataObject;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.widget.CustomToast;

public class SmsListFragment extends Fragment
{

	private final String TAG = "SmsListFragment";
	private ViewHolder holder;
	private View rootView;
	private SmsDialogListAdapter adapter;

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

		// SharedPreferences prefs = getActivity().getSharedPreferences(VariableManager.PREF,
		// Context.MODE_PRIVATE);

		// final String driverid = prefs.getString(VariableManager.PREF_DRIVERID, null);
		final String bag_id = getActivity().getIntent().getStringExtra(
				VariableManager.EXTRA_NEXT_BAG_ID);

		adapter = new SmsDialogListAdapter(getActivity(), DbHandler.getInstance(getActivity())
				.getContacts(bag_id), false);

		holder.list.setAdapter(adapter);

		holder.list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				parentItemPosition = position; // (Integer) adapter.getItem(position);

				DialogDataObject dataObject = (DialogDataObject) holder.list.getItemAtPosition(position);
				SmsMessageFragment fragment_sms = SmsMessageFragment.newInstance(
						dataObject.getMainText(),
						dataObject.getSubText(),
						bag_id);
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.activity_sms_container, fragment_sms);
				ft.commit();

			}
		});

		holder.report_button.setVisibility(View.VISIBLE);
		holder.report_button.setEnabled(false);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		((DialogDataObject) adapter.getItem(parentItemPosition)).setThirdText(data
				.getStringExtra(SMSDialog.DIALOG_TIME_STRING));

		// VariableManager.delay_id = data.getStringExtra(VariableManager.EXTRA_DELAY_ID);

		// holder.report_button.setVisibility(View.VISIBLE);
		holder.report_button.setBackgroundResource(R.drawable.button_custom);
		holder.list.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		holder.report_button.setEnabled(true);
	}
/*
	private class SendSMSTask extends AsyncTask<String, Void, String>
	{

		private ProgressDialog dialog = new ProgressDialog(getActivity());

		*//** progress dialog to show user that the backup is processing. *//*
		*//** application context. *//*
		@Override
		protected void onPreExecute()
		{
			this.dialog.setMessage("Sending SMS");
			this.dialog.show();
		}

		@Override
		protected String doInBackground(String... args)
		{
			boolean result = false;
			if (args[4].equals("true"))
			{
				result = true;
			}

			return ServerInterface.getInstance(getActivity()).postMessage(args[0], args[1],
					args[2], args[3], result);
			// return ""; // DEBUG
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
			String status = "";
			try
			{
				JSONObject obj = new JSONObject(result);
				status = obj.getJSONObject("response").getJSONObject("waybill").getString("status");
				Log.d(TAG, "zorro : " + status);
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				// e.printStackTrace();
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				Log.e(TAG, sw.toString());
			}

			if (status.equals("success"))
			{
				custom_toast.setText("Success");
				custom_toast.setSuccess(true);
			}
			else
			{
				custom_toast.setText(VariableManager.TEXT_NET_ERROR);
				custom_toast.setSuccess(false);
			}

			custom_toast.show();

			getActivity().finish();
		}
	}
*/
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
			holder.report_button.setVisibility(View.INVISIBLE);

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
