package com.mrdexpress.paperless.fragments;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.widget.CustomToast;

public class SmsMessageFragment extends Fragment
{

	private final String TAG = "SmsMessageFragment";
	private ViewHolder holder;
	private View rootView;

	private static String PHONE_NUMBER = "com.mrdexpress.paperless.fragments.phone_number";
	private static String BAG_ID = "com.mrdexpress.paperless.fragments.bag_id";
	private static String MSG_TYPE = "com.mrdexpress.paperless.fragments.msg_type";

	private String msg_type = "SMS";

	private String phone_number, bag_id;

	public static SmsMessageFragment newInstance(String phone_number, String bag_id)
	{
		SmsMessageFragment f = new SmsMessageFragment();

		Bundle args = new Bundle();
		args.putString(PHONE_NUMBER, phone_number);
		args.putString(BAG_ID, bag_id);
		f.setArguments(args);

		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		initViewHolder(inflater, container); // Inflate ViewHolder static instance

		return rootView;
	}

	public void onResume()
	{
		super.onResume();

		SharedPreferences prefs = getActivity().getSharedPreferences(VariableManager.PREF,
				Context.MODE_PRIVATE);

		final String driverid = prefs.getString(VariableManager.PREF_DRIVERID, null);

		Bundle bundle = this.getArguments();
		if (bundle != null)
		{
			phone_number = bundle.getString(PHONE_NUMBER);
			bag_id = bundle.getString(BAG_ID);
			msg_type = bundle.getString(MSG_TYPE);
		}

		holder.button_send.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				new SendSMSTask().execute(holder.edit_text_message.getText().toString(),
						phone_number, bag_id, msg_type, "true");
			}
		});

	}

	private class SendSMSTask extends AsyncTask<String, Void, String>
	{

		private ProgressDialog dialog = new ProgressDialog(getActivity());

		/** progress dialog to show user that the backup is processing. */
		/** application context. */
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

			Calendar c = Calendar.getInstance();
			SimpleDateFormat sdf_date = new SimpleDateFormat("dd:MM:yyyy");
			String date = sdf_date.format(c.getTime());
			SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
			String time = sdf_time.format(c.getTime());

			DbHandler.getInstance(getActivity()).addComLog("SMS sent at " + date + " at " + time,
					args[0], "SMS", args[2]);

			return ServerInterface.getInstance(getActivity()).postMessage(args[0], args[1],
					args[2], "SMS", result);
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

			CustomToast custom_toast = new CustomToast(getActivity());

			if (result.equals("true"))
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

	public void initViewHolder(LayoutInflater inflater, ViewGroup container)
	{

		if (rootView == null)
		{

			rootView = inflater.inflate(R.layout.fragment_sms_message, null, false);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			holder.button_send = (Button) rootView.findViewById(R.id.button_sms_message_send);
			holder.edit_text_message = (EditText) rootView
					.findViewById(R.id.edit_text_sms_message_message);
			holder.text_title = (TextView) rootView.findViewById(R.id.text_sms_message_title);

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
		Button button_send;
		EditText edit_text_message;
		TextView text_title;
	}

}
