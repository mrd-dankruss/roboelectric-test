package com.mrdexpress.paperless.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.db.Device;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.widget.CustomToast;

import java.util.Calendar;
import java.util.Date;

public class SmsMessageFragment extends Fragment
{

	private final String TAG = "SmsMessageFragment";
	private ViewHolder holder;
	private View rootView;

	private static String CONTACT_NAME = "com.mrdexpress.paperless.fragments.contact_name";
	private static String PHONE_NUMBER = "com.mrdexpress.paperless.fragments.phone_number";
	private static String BAG_ID = "com.mrdexpress.paperless.fragments.bag_id";
	private static String MSG_TYPE = "com.mrdexpress.paperless.fragments.msg_type";

	private String msg_type = "SMS";

	private String phone_number, bag_id, contact_name;

	public static SmsMessageFragment newInstance(String contact_name, String phone_number, String bag_id)
	{
		SmsMessageFragment f = new SmsMessageFragment();

		Bundle args = new Bundle();
		args.putString(PHONE_NUMBER, phone_number);
		args.putString(BAG_ID, bag_id);
		args.putString(CONTACT_NAME, contact_name);
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
		Bundle bundle = this.getArguments();
		if (bundle != null)
		{
			phone_number = bundle.getString(PHONE_NUMBER);
			bag_id = bundle.getString(BAG_ID);
			msg_type = bundle.getString(MSG_TYPE);
			contact_name = bundle.getString(CONTACT_NAME);
		}

		holder.button_send.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				new SendSMSTask().execute(holder.edit_text_message.getText().toString(),
						phone_number, bag_id, msg_type, "true", contact_name);
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
			this.dialog.setMessage("Sending Message");
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
			Date datetime = c.getTime();
			String note = "SMS sent with content:'" + args[0] + "' to " + args[5] + "(" + args[1] + ")";

			DbHandler.getInstance(getActivity()).addComLog(datetime, note, "SMS", args[2]);

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
                Device.getInstance().displaySuccess("Message send successfully");
                //custom_toast.setText("Message send successfully");
				//custom_toast.setSuccess(true);
			}
			else
			{
                Device.getInstance().displayFailed("Message not send successfully");
				//custom_toast.setText(VariableManager.TEXT_NET_ERROR);
				//custom_toast.setSuccess(false);
			}

			//custom_toast.show();

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
