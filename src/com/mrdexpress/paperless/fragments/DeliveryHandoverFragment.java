package com.mrdexpress.paperless.fragments;

import java.util.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.ReasonPartialDeliveryActivity;
import com.mrdexpress.paperless.datatype.DeliveryHandoverDataObject;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.service.GCMIntentService;
import com.mrdexpress.paperless.widget.CustomToast;
import com.mrdexpress.paperless.workflow.ObservableJSONObject;
import com.mrdexpress.paperless.workflow.Workflow;
import net.minidev.json.JSONObject;

public class DeliveryHandoverFragment extends Fragment
{
	private final String TAG = "DeliveryHandoverFragment";
	public static String WAYBILL_BARCODE = "com.mrdexpress.waybill_barcode";
	public static String WAYBILL_SCANNED = "com.mrdexpress.waybill_scanned";

	private ViewHolder holder;
	private View rootView;
	private IncompleteScanDialog dialog;
	ArrayList<DeliveryHandoverDataObject> list;
	private DeliveryHandoverAdapter listAdapter;
	String bagid;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		initViewHolder(inflater, container); // Inflate ViewHolder static instance

		bagid = getActivity().getIntent().getStringExtra(VariableManager.EXTRA_NEXT_BAG_ID);
		// Log.d(TAG, "Zorro - Bag ID: " + bagid);

		//list = DbHandler.getInstance(getActivity()).getWaybillsForHandover(bagid);

        list = Workflow.getInstance().getBagParcelsAsObjects( Integer.parseInt( bagid));

		listAdapter = new DeliveryHandoverAdapter(list);

        listAdapter.registerDataSetObserver( new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                int parcelCount = listAdapter.getScannedCount();
                holder.parcelsScanned.setText( "PARCEL" + (parcelCount==1?"":"S") + " (" + parcelCount + " / " + listAdapter.getCount() + " SCANNED)");
            }
        });

		if ((listAdapter != null) & (list != null))
		{
			holder.list.setAdapter(listAdapter);
		}

        listAdapter.notifyDataSetChanged();

		holder.list.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
			{
                // for debugging only, to simulate a GCM call
                list.get( position).setParcelScanned((int) new Date().getTime() / 1000);
			}
		});

		holder.button.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (list != null)
				{
					if (allParcelsScanned())
					{

						int no_rows_affected = DbHandler.getInstance(getActivity()).setDeliveryStatus(bagid, Bag.STATUS_COMPLETED);

						if (no_rows_affected > 0)
						{
							CustomToast custom_toast = new CustomToast(getActivity());
							custom_toast.setText("Delivery completed successfully");
							custom_toast.setSuccess(true);
							custom_toast.show();
						}
						else
						{
							CustomToast custom_toast = new CustomToast(getActivity());
							custom_toast.setText("Successful delivery status update failed");
							custom_toast.setSuccess(true);
							custom_toast.show();
						}

						getActivity().finish();
						/*	CustomToast toast = new CustomToast(getActivity());
							toast.setSuccess(true);
							toast.setText("Delivery completed successfully!");
							toast.show();	*/
					}
					else
					{

						dialog = new IncompleteScanDialog(getActivity());
						dialog.getWindow().setBackgroundDrawable( new ColorDrawable(Color.TRANSPARENT));
						dialog.show();

						LayoutInflater factory = LayoutInflater.from(getActivity());

						final Button button_continue = (Button) dialog.findViewById(R.id.button_incomplete_scan_continue);

						button_continue.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								dialog.dismiss();
								/*
								FragmentTransaction ft = getActivity().getSupportFragmentManager()
										.beginTransaction();
								Fragment reasonFragment = new ReasonPartialDeliveryFragment();
								ft.replace(R.id.activity_reason_partial_delivery_container,
										reasonFragment);
								ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
								ft.addToBackStack(null);
								ft.commit();*/
								Intent intent = new Intent(getActivity(), ReasonPartialDeliveryActivity.class);

								Bundle b = new Bundle();
								/*b.putParcelableArrayList(
										VariableManager.EXTRA_UNSCANNED_PARCELS_BUNDLE,
										getUnscannedParcels(list));*/
								intent.putExtra( VariableManager.EXTRA_UNSCANNED_PARCELS_BUNDLE,	getUnscannedParcels(list));
								intent.putExtra( VariableManager.EXTRA_NEXT_BAG_ID,	getActivity().getIntent().getStringExtra( VariableManager.EXTRA_NEXT_BAG_ID));
								// intent.putExtra(VariableManager.EXTRA_UNSCANNED_PARCELS_BUNDLE,
								// b);
								// intent.putExtra(VariableManager.EXTRA_BAG_NO,
								// ((Bag)holder.list.getItemAtPosition(position)).getBagNumber());
								// startActivity(intent);
								getActivity().startActivityForResult(intent, VariableManager.ACTIVITY_REQUEST_CODE_PARTIAL_DELIVERY);
							}
						});

						final Button button_scan = (Button) dialog.findViewById(R.id.button_incomplete_scan_scan);

						button_scan.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								dialog.dismiss();
							}
						});
					}
				}
			}
		});

		return rootView;
	}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //listAdapter.unregisterDataSetObserver(); // TODO: is this required?
    }

    @Override
	public void onResume()
	{
		super.onResume();
		getActivity().registerReceiver(broadcastReceiver, new IntentFilter(GCMIntentService.BROADCAST_ACTION));
	}

	@Override
	public void onPause()
	{
		super.onPause();
		getActivity().unregisterReceiver(broadcastReceiver);
	}

	/**
	 * Checks list of parcels and returns a list of parcels which have not yet been scanned.
	 * 
	 * @param list
	 * @return
	 */
	private Integer[] getUnscannedParcels(ArrayList<DeliveryHandoverDataObject> list)
	{
		ArrayList<Integer> arraylist_unscanned = new ArrayList<Integer>();

		// TODO conver arraylist to array properly
		for (int i = 0; i < list.size(); i++)
		{
			if (!list.get(i).isParcelScanned())
			{
				// list_unscanned[i] = (list.get(i).getParcelID());
				arraylist_unscanned.add(list.get(i).getParcelID());
			}
		}
		Integer[] list_unscanned = new Integer[arraylist_unscanned.size()];

		for (int i = 0; i < arraylist_unscanned.size(); i++)
		{
			list_unscanned[i] = arraylist_unscanned.get(i);
			Log.d(TAG, "unscanned bagid: " + i + ") " + list_unscanned[i]);
		}

		return list_unscanned;
	}

	/**
	 * Checks if all parcels have been scanned into the branch
	 * 
	 * @return True boolean value if all parcels have been scanned.
	 */
	private boolean allParcelsScanned()
	{

		boolean allScanned = true;

		if (list != null)
		{
			for (int i = 0; i < list.size(); i++)
			{
				if (list.get(i).isParcelScanned() == false)
				{
					allScanned = false;
					break;
				}
			}
		}
		else
		{
			return false;
		}

		return allScanned;
	}


    // TODO: this needs to be changed to send the scanned timestamp instead of just a boolean
	public void updateFromPushNotification(String parcel_id, boolean scanned)
	{
		// DbHandler.getInstance(getActivity().getApplicationContext()).setWaybillScanned(waybill_no, scanned);

        JSONObject parcel = Workflow.getInstance().getParcel( Integer.parseInt( parcel_id));

        if( parcel != null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                if (list.get(i).getBarcode().equals( parcel_id))
                {
                    int timestamp = (int)(new Date().getTime() / 1000);
                    list.get(i).setParcelScanned( timestamp);
                }
            }

            listAdapter.notifyDataSetChanged();
        }
	}

	private class DeliveryHandoverAdapter extends BaseAdapter
	{

		List<DeliveryHandoverDataObject> parcelList;

		public DeliveryHandoverAdapter(ArrayList<DeliveryHandoverDataObject> objects)
		{
			super();
			parcelList = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
            final int thisPosition = position;

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row_delivery_handover, parent, false);
            TextView parcelTitle = (TextView) rowView.findViewById(R.id.row_delivery_parcel);
            TextView waybillTile = (TextView) rowView.findViewById(R.id.row_delivery_waybill);
            ImageView hasScannedParcel = (ImageView) rowView.findViewById(R.id.row_delivery_handover_image);

            DeliveryHandoverDataObject dhdo = parcelList.get(thisPosition);
            waybillTile.setText( dhdo.getMDX() + " (" + dhdo.getXof() + ")");
            parcelTitle.setText( dhdo.getBarcode());

            if( dhdo.isParcelScanned() == true)
            {
                parcelTitle.setTextColor(getResources().getColor(R.color.green_tick));
                hasScannedParcel.setVisibility(View.VISIBLE);
            }
            else
            {
                hasScannedParcel.setVisibility(View.GONE);
            }

            /*waybillTile.setOnClickListener( new OnClickListener() {
                @Override
                public void onClick(View v) {
                    parcelList.get( thisPosition).setParcelScanned((int) new Date().getTime() / 1000);
                }
            });*/


            if( parcelList.get( thisPosition).data.countObservers() == 0)
            {
                parcelList.get( thisPosition).data.addObserver( new Observer() {
                    @Override
                    public void update(Observable observable, Object data) {
                        listAdapter.notifyDataSetChanged();
                    }
                });
            }

            parcelList.get( thisPosition).data.forceNotifyAllObservers();

			return rowView;
		}

		@Override
		public int getCount()
		{
			return parcelList.size();
		}

		@Override
		public Object getItem(int position)
		{
			return position;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

        public int getScannedCount()
        {
            int scanned = 0;
            for( DeliveryHandoverDataObject item : this.parcelList )
            {
                if( item.isParcelScanned())
                    scanned++;
            }
            return scanned;
        }
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.d(TAG, "Receiver: " + intent.getAction());
			if (intent.getAction() == GCMIntentService.BROADCAST_ACTION)
			{
				updateFromPushNotification(intent.getExtras().getString(WAYBILL_BARCODE), intent.getExtras().getBoolean(WAYBILL_SCANNED));
			}
		}
	};

	public void initViewHolder(LayoutInflater inflater, ViewGroup container)
	{
		if (rootView == null)
		{
			rootView = inflater.inflate(R.layout.fragment_delivery_handover, null, false);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			holder.list = (ListView) rootView.findViewById(R.id.deliveryHandover_listView_scannedParcels);
			holder.button = (Button) rootView.findViewById(R.id.button_delivery_handover_complete);
            holder.parcelsScanned = (TextView) rootView.findViewById(R.id.deliveryHandover_textView_ParcelsScanned);

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
		Button button;
        TextView parcelsScanned;
	}
}
