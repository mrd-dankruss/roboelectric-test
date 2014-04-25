package com.mrdexpress.paperless.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.DialogFragment;
import android.app.Fragment;
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
import com.mrdexpress.paperless.ViewDeliveriesFragmentActivity;
import com.mrdexpress.paperless.adapters.ReasonForFailedHandoverListAdapter;
import com.mrdexpress.paperless.datatype.DialogDataObject;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.db.Device;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.widget.CustomToast;
import com.mrdexpress.paperless.workflow.Workflow;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
					delay_id = ((DialogDataObject) holder.list.getItemAtPosition(position)).getSubText();
					delay_reason = ((DialogDataObject) holder.list.getItemAtPosition(position)).getMainText();
				}
			}
		});

		holder.report_button.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
                // FAILED DELIVERY LOGGING
                Workflow.getInstance().setDeliveryStatus( Workflow.getInstance().currentBagID, Bag.STATUS_UNSUCCESSFUL, delay_reason);
                Device.getInstance().displayFailed("Delivery failed." , getActivity());
                //CustomToast toast = new CustomToast(getActivity());
                //toast.setSuccess(false);
                //toast.setText("Delivery failed.");
                //toast.show();
                getActivity().finish();
                Intent intent = new Intent(getActivity().getApplicationContext() , ViewDeliveriesFragmentActivity.class);
                startActivity(intent);
			}
		});
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

        holder.report_button.setEnabled(true);
		adapter.notifyDataSetChanged();
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
            holder.report_button.setEnabled(false);
            //holder.report_button.setBackgroundColor(Color.GRAY);
            holder.report_button.setVisibility( View.VISIBLE );

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
