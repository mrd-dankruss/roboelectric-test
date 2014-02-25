package com.mrdexpress.paperless.fragments;

import org.json.JSONException;
import org.json.JSONObject;

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
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.widget.CustomToast;

public class SmsMessageFragment extends Fragment
{

	private final String TAG = "SmsMessageFragment";
	private ViewHolder holder;
	private View rootView;

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
		
		holder.button_send.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				//SendSMSTask
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
				status = obj.getJSONObject("response").getString("status");
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}

			if (status.equals("true"))
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
