package com.mrdexpress.paperless.fragments;

import java.util.ArrayList;

import com.mrdexpress.paperless.workflow.Workflow;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import com.mrdexpress.paperless.adapters.ReasonForFailedHandoverListAdapter;
import com.mrdexpress.paperless.datatype.DialogDataObject;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.widget.CustomToast;

public class ReasonForFailedHandoverFragment extends Fragment
{

	private final String TAG = "ReportDelayActivity";
	private ViewHolder holder;
	private View rootView;
	private ReasonForFailedHandoverListAdapter adapter;
	String delay_id, delay_reason;

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

		//values = DbHandler.getInstance(getActivity()).getFailedHandoverReasons();
        values = Workflow.getInstance().getFailedHandoverReasons();

		adapter = new ReasonForFailedHandoverListAdapter(getActivity(), values, false);
		holder.list.setAdapter(adapter);

		holder.list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				parentItemPosition = position;// (Integer) getListAdapter().getItem(position);

				if (holder.list.getItemAtPosition(position) != null)
				{
					Log.d("Reason", "ListItem: " + position);
					setTick(position);
					delay_id = ((DialogDataObject) holder.list.getItemAtPosition(position))
							.getSubText();
					delay_reason = ((DialogDataObject) holder.list.getItemAtPosition(position))
							.getMainText();
					holder.report_button.setBackgroundResource(R.drawable.button_custom);
					holder.report_button.setEnabled(true);
				}
			}
		});

		holder.report_button.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				new ReportReasonForFailTask()
						.execute(
								getActivity().getIntent().getStringExtra(
										VariableManager.EXTRA_NEXT_BAG_ID), delay_id);
			}
		});
		holder.report_button.setVisibility(View.VISIBLE);
		holder.report_button.setEnabled(false);
	}

	private void setTick(int position)
	{

		values.get(position).setThirdText("true");

		for (int i = 0; i < values.size(); i++)
		{
			if (i != position)
			{
				values.get(i).setThirdText("false");
			}
		}

		adapter.notifyDataSetChanged();
	}

	private class ReportReasonForFailTask extends AsyncTask<String, Void, String>
	{

		private ProgressDialog dialog = new ProgressDialog(getActivity());

		/** progress dialog to show user that the backup is processing. */
		/** application context. */
		@Override
		protected void onPreExecute()
		{
			this.dialog.setMessage("Submitting failed handover report");
			this.dialog.show();
		}

		@Override
		protected String doInBackground(String... args)
		{
			SharedPreferences prefs = getActivity().getSharedPreferences(VariableManager.PREF,
					Context.MODE_PRIVATE);
			boolean training_mode = prefs.getBoolean(VariableManager.PREF_TRAINING_MODE, false);

			if (training_mode)
			{
				return "success";
			}
			else
			{
				JSONObject result_object;
				String status = "";
				try
				{
					String json_string = ServerInterface.getInstance(getActivity())
							.postFailedHandover(args[0], args[1]);
					Log.d(TAG, json_string);
					
					result_object = new JSONObject(json_string);
					
					// <TODO, NB!!>: API returns incorrect values so issue MOB-20 requires the following hardcoded + incorrect code.
					result_object = new JSONObject("{'response':{'waybill':{'status':'success'}}}");
					// </TODO, NB!!>
					
					status = result_object.getJSONObject("response").getJSONObject("waybill")
							.getString("status");
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return status;
			}
		}

		@Override
		protected void onPostExecute(String result)
		{
			// Close progress spinner
			if (dialog.isShowing())
			{
				dialog.dismiss();
			}

			// VariableManager.delay_id = null;

			Log.d(TAG, "postFailedHandover: " + result);
			if (result.equals("success"))
			{
				String bagid = getActivity().getIntent().getStringExtra(
						VariableManager.EXTRA_NEXT_BAG_ID);

				int no_rows_affected = DbHandler.getInstance(getActivity()).setDeliveryStatus(
						bagid, Bag.STATUS_UNSUCCESSFUL, delay_reason);

				if (no_rows_affected > 0)
				{
					CustomToast custom_toast = new CustomToast(getActivity());
					custom_toast.setText("Success");
					custom_toast.setSuccess(true);
					custom_toast.show();
				}
				else
				{
					CustomToast custom_toast = new CustomToast(getActivity());
					custom_toast.setText("Failed delivery status update failed");
					custom_toast.setSuccess(true);
					custom_toast.show();
				}

				/*Bag bag = DbHandler.getInstance(getActivity()).getBag(bagid);
				bag.setStatus(Bag.STATUS_UNSUCCESSFUL);
				DbHandler.getInstance(getActivity()).addBag(bag);*/
			}
			else
			{
				CustomToast custom_toast = new CustomToast(getActivity());
				custom_toast.setText("Failed");
				custom_toast.setSuccess(false);
				custom_toast.show();
			}

			if (getActivity() != null)
			{
				getActivity().finish();
			}
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
