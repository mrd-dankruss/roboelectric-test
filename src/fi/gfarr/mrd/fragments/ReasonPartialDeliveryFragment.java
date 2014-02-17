package fi.gfarr.mrd.fragments;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import fi.gfarr.mrd.R;
import fi.gfarr.mrd.adapters.ExpandableListAdapter;
import fi.gfarr.mrd.datatype.DialogDataObject;
import fi.gfarr.mrd.datatype.ReasonPartialDeliveryItem;
import fi.gfarr.mrd.db.DbHandler;
import fi.gfarr.mrd.db.Waybill;
import fi.gfarr.mrd.helper.VariableManager;
import fi.gfarr.mrd.net.ServerInterface;
import fi.gfarr.mrd.widget.CustomToast;

public class ReasonPartialDeliveryFragment extends Fragment
{

	private final String TAG = "ReasonPartialDeliveryFragment";
	private ViewHolder holder;
	private View rootView;
	private ExpandableListAdapter listAdapter;
	private ArrayList<ArrayList<ReasonPartialDeliveryItem>> data;
	private boolean button_enabled = false;
	private ArrayList<PartialDeliveryObject> partial_deliveries = new ArrayList<PartialDeliveryObject>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		initViewHolder(inflater, container); // Inflate ViewHolder static instance

		// Perform API call
		holder.button_continue.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (button_enabled)
				{
					new PartialDeliveryTask().execute();
				}
			}
		});

		// preparing list data
		prepareListData();

		listAdapter = new ExpandableListAdapter(holder.list, getActivity(), data);

		// setting list adapter
		holder.list.setAdapter(listAdapter);

		// Listview Group click listener
		holder.list.setOnGroupClickListener(new OnGroupClickListener()
		{

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
					long id)
			{
				// Toast.makeText(getApplicationContext(),
				// "Group Clicked " + listDataHeader.get(groupPosition),
				// Toast.LENGTH_SHORT).show();
				// return false;
				return parent.isGroupExpanded(groupPosition);
			}
		});

		// Listview Group expanded listener
		holder.list.setOnGroupExpandListener(new OnGroupExpandListener()
		{

			@Override
			public void onGroupExpand(int groupPosition)
			{
				// Toast.makeText(getActivity(), listDataHeader.get(groupPosition) + " Expanded",
				// Toast.LENGTH_SHORT).show();
			}
		});

		// Listview Group collasped listener
		holder.list.setOnGroupCollapseListener(new OnGroupCollapseListener()
		{

			@Override
			public void onGroupCollapse(int groupPosition)
			{
				// Toast.makeText(getActivity(), listDataHeader.get(groupPosition) + " Collapsed",
				// Toast.LENGTH_SHORT).show();

			}
		});

		// Listview on child click listener
		holder.list.setOnChildClickListener(new OnChildClickListener()
		{
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
					int childPosition, long id)
			{

				setTick(groupPosition, childPosition);
				listAdapter.notifyDataSetChanged();

				return false;
			}
		});

		for (int i = 0; i < data.size(); i++)
		{
			holder.list.expandGroup(0);
		}

		return rootView;
	}

	private class PartialDeliveryTask extends AsyncTask<Void, Void, String>
	{

		private ProgressDialog dialog = new ProgressDialog(getActivity());

		/** progress dialog to show user that the backup is processing. */
		/** application context. */
		@Override
		protected void onPreExecute()
		{
			this.dialog.setMessage("Sending partial delivery report");
			this.dialog.show();
		}

		@Override
		protected String doInBackground(Void... args)
		{
			// ServerInterface.postPartialDelivery(waybill_id, status_id, extra)
			String result = "success";

			for (int i = 0; i < data.size(); i++)
			{
				ArrayList<ReasonPartialDeliveryItem> reasons = data.get(i);

				for (int r = 0; r < reasons.size(); r++)
				{
					if (reasons.get(r).isSelected())
					{
						try
						{
							String result_JSON_string = ServerInterface.getInstance(getActivity())
									.postPartialDelivery(reasons.get(r).getGroupName(),
											reasons.get(r).getReasonID(), "");

							JSONObject result_JSON = new JSONObject(result_JSON_string);

							String result_status = result_JSON.getJSONObject("response")
									.getJSONObject("waybill").getString("status");

							if (!result_status.equalsIgnoreCase("success"))
							{
								result = "failed";
							}
						}
						catch (JSONException e)
						{
							StringWriter sw = new StringWriter();
							e.printStackTrace(new PrintWriter(sw));
							Log.e(TAG, sw.toString());
						}

						// Log.d(TAG, "zorro# waybill:" + reasons.get(r).getGroupName()
						// + " reasonTitle:" + reasons.get(r).getReasonTitle());
					}
				}
			}

			return result;
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
			if (result.equalsIgnoreCase("success"))
			{
				CustomToast custom_toast = new CustomToast(getActivity());
				custom_toast.setText("Partial handover logged");
				custom_toast.setSuccess(true);
				custom_toast.show();
				getActivity().setResult(Activity.RESULT_OK);
			}
			else
			{
				if ((result == null) | (!(result.length() > 0)))
				{
					result = "Partial handover failed";
				}

				CustomToast custom_toast = new CustomToast(getActivity());
				custom_toast.setText(result);
				custom_toast.setSuccess(false);
				custom_toast.show();
				getActivity().setResult(Activity.RESULT_CANCELED);
			}

			getActivity().finish();
		}
	}

	private void setTick(int groupPosition, int childPosition)
	{
		data.get(groupPosition).get(childPosition).setIsSelected(true);
		for (int i = 0; i < data.get(groupPosition).size(); i++)
		{
			if (i != childPosition)
			{
				data.get(groupPosition).get(i).setIsSelected(false);
			}
		}
		// Enable button
		button_enabled = true;
		holder.button_continue.setBackgroundResource(R.drawable.button_custom);
	}

	public void initViewHolder(LayoutInflater inflater, ViewGroup container)
	{

		if (rootView == null)
		{

			rootView = inflater.inflate(R.layout.fragment_reason_partial_delivery, null, false);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			holder.list = (ExpandableListView) rootView.findViewById(R.id.lvExp);
			holder.button_continue = (Button) rootView
					.findViewById(R.id.button_reason_partial_delivery);

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
		ExpandableListView list;
		Button button_continue;
	}

	/*
	 * Preparing the list data
	 * TODO: Remove this
	 */
	private void prepareListData()
	{
		// temp.add(new ReasonPartialDeliveryItem("00025420254 (TAKEALOT)", "greg_code_1",
		// "Wrong Parcel", false));

		data = new ArrayList<ArrayList<ReasonPartialDeliveryItem>>();

		String[] waybill_IDs = getActivity().getIntent().getStringArrayExtra(
				VariableManager.EXTRA_UNSCANNED_PARCELS_BUNDLE);
		// Log.d(TAG, "unscanned bagid length: " + bag_IDs.length);
		// Get reasons
		ArrayList<DialogDataObject> reasons = DbHandler.getInstance(getActivity())
				.getPartialDeliveryReasons();

		/*	Log.d(TAG,
					"zorro - next bag id: "
							+ getActivity().getIntent().getStringExtra(
									VariableManager.EXTRA_NEXT_BAG_ID));*/

		// Each waybill / (group in extendable list)
		for (int i = 0; i < waybill_IDs.length; i++)
		{
			ArrayList<ReasonPartialDeliveryItem> reason_items = new ArrayList<ReasonPartialDeliveryItem>();

			// Get current driver
			// SharedPreferences prefs = getActivity().getSharedPreferences(VariableManager.PREF,
			// 0);
			// String driver = prefs.getString(VariableManager.EXTRA_DRIVER_ID, "");

			// Bag bag = DbHandler.getInstance(getActivity()).getBag(driver, waybill_IDs[i]);

			Waybill waybill = DbHandler.getInstance(getActivity()).getWaybill(
					getActivity().getIntent().getStringExtra(VariableManager.EXTRA_NEXT_BAG_ID));
			// Log.d(TAG, "zorro - waybill barcode: " + waybill.getBarcode());
			for (int r = 0; r < reasons.size(); r++)
			{
				reason_items.add(new ReasonPartialDeliveryItem(waybill.getBarcode(), reasons.get(r)
						.getSubText(), reasons.get(r).getMainText(), false));
			}
			data.add(reason_items);
		}
	}

	private class PartialDeliveryObject
	{
		String waybill_id, status_id, extra;

		PartialDeliveryObject(String waybillid, String statusid)
		{
			waybill_id = waybillid;
			status_id = statusid;
			extra = "";
		}
	}
}
