package com.mrdexpress.paperless.fragments;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
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
	String delay_id;

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

		values = DbHandler.getInstance(getActivity()).getFailedHandoverReasons();

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
			return ServerInterface.getInstance(getActivity()).postFailedHandover(args[0], args[1]);
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

			try
			{
				JSONObject result_object = new JSONObject(result);
				String status = result_object.getString("response");
				Log.d(TAG, "postFailedHandover: " + status);
				if (status.equals("success"))
				{
					CustomToast custom_toast = new CustomToast(getActivity());
					custom_toast.setText("Success");
					custom_toast.setSuccess(true);
					custom_toast.show();
					String bagid = getActivity().getIntent().getStringExtra(
							VariableManager.EXTRA_NEXT_BAG_ID);
					Bag bag = DbHandler.getInstance(getActivity()).getBag(bagid);
					bag.setStatus(Bag.STATUS_UNSUCCESSFUL);
					DbHandler.getInstance(getActivity()).addBag(bag);
				}
				else
				{
					CustomToast custom_toast = new CustomToast(getActivity());
					custom_toast.setText("Failed");
					custom_toast.setSuccess(false);
					custom_toast.show();
				}
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
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
