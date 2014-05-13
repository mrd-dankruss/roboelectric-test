package com.mrdexpress.paperless.dialogfragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.datatype.DeliveryHandoverDataObject;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.fragments.IncompleteScanDialog;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.service.GCMIntentService;
import com.mrdexpress.paperless.ui.ViewHolder;
import com.mrdexpress.paperless.widget.CustomToast;
import com.mrdexpress.paperless.workflow.Workflow;
import net.minidev.json.JSONObject;

import java.util.*;

public class DeliveryHandoverDialogFragment extends DialogFragment {
    public static String WAYBILL_BARCODE = "com.mrdexpress.waybill_barcode";
    public static String WAYBILL_SCANNED = "com.mrdexpress.waybill_scanned";
    private final String TAG = "DeliveryHandoverDialogFragment";
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Receiver: " + intent.getAction());
            if (intent.getAction() == GCMIntentService.BROADCAST_ACTION) {
                updateFromPushNotification(intent.getExtras().getString(WAYBILL_BARCODE), intent.getExtras().getBoolean(WAYBILL_SCANNED));
            }
        }
    };
    ArrayList<DeliveryHandoverDataObject> list;
    String stopids;
    private View rootView;
    private MyViewHolder holder;
    private IncompleteScanDialog dialog;
    private DeliveryHandoverAdapter listAdapter;

    public DeliveryHandoverDialogFragment( CallBackFunction _callback) {
        callback = _callback;
    }

    private static CallBackFunction callback;

    public static DeliveryHandoverDialogFragment newInstance(final CallBackFunction callback)
    {
        DeliveryHandoverDialogFragment f = new DeliveryHandoverDialogFragment( callback);


        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Dialog m_dialog = new Dialog(getActivity() , R.style.Dialog_No_Border);
        return m_dialog;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        initViewHolder(inflater, container); // Inflate ViewHolder static instance

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        stopids = Workflow.getInstance().currentBagID;

        list = Workflow.getInstance().getStopParcelsAsObjects(stopids);

        listAdapter = new DeliveryHandoverAdapter(list, getActivity());

        listAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                int parcelCount = listAdapter.getScannedCount();
                holder.parcelsScanned.setText("PARCEL" + (parcelCount == 1 ? "" : "S") + " (" + parcelCount + " / " + listAdapter.getCount() + " SCANNED)");
            }
        });

        if ((listAdapter != null) & (list != null)) {
            holder.list.setAdapter(listAdapter);
        }

        listAdapter.notifyDataSetChanged();

        holder.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                // for debugging only, to simulate a GCM call
                list.get(position).setParcelScanned((int) new Date().getTime() / 1000);

            }
        });

        holder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (list != null) {
                    if (allParcelsScanned()) {
                        //All parcels delivered
                        Workflow.getInstance().setDeliveryStatus(stopids, Bag.STATUS_COMPLETED, "");
                        CustomToast toast = new CustomToast(getActivity());
                        toast.setSuccess(true);
                        toast.setText("Delivery completed successfully.");
                        toast.show();

                        callback.execute(true);
                        dismiss();

                    } else {

                        dialog = new IncompleteScanDialog(getActivity());
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

                        LayoutInflater factory = LayoutInflater.from(getActivity());

                        final Button button_continue = (Button) dialog.findViewById(R.id.button_incomplete_scan_continue);
                        final TextView subtext = (TextView) dialog.findViewById(R.id.textView_manAuth_enter_pin);

                        button_continue.setText("Log partial delivery");
                        subtext.setText("Branch manager has not scanned all parcels");

                        button_continue.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                Workflow.getInstance().doormat.put(VariableManager.UNSCANNED_PARCELS, getUnscannedParcels(list));
                                Workflow.getInstance().doormat.put("scannedparcels", getScannedParcels(list));

                                ReasonPartialDeliveryDialogFragment logReasons = ReasonPartialDeliveryDialogFragment.newInstance( new CallBackFunction() {
                                    @Override
                                    public boolean execute(Object args) {
                                        Workflow.getInstance().setDeliveryStatus(Workflow.getInstance().currentBagID, Bag.STATUS_PARTIAL, "");
                                        CustomToast toast = new CustomToast(getActivity());
                                        toast.setSuccess(true);
                                        toast.setText("Partial delivery logged");
                                        toast.show();

                                        callback.execute(true);
                                        dismiss();

                                        return false;
                                    }
                                });
                                logReasons.show( getFragmentManager(), getTag());

                                /*Intent intent = new Intent(getActivity(), ReasonPartialDeliveryActivity.class);
                                Bundle b = new Bundle();


                                getActivity().startActivityForResult(intent, VariableManager.ACTIVITY_REQUEST_CODE_PARTIAL_DELIVERY);*/
                            }
                        });

                        final Button button_scan = (Button) dialog.findViewById(R.id.button_incomplete_scan_scan);
                        button_scan.setText("Wait for scan");

                        button_scan.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //listAdapter.unregisterDataSetObserver(); // TODO: is this required?
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(GCMIntentService.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    /**
     * Checks list of parcels and returns a list of parcels which have not yet been scanned.
     *
     * @param list
     * @return
     */
    private ArrayList<DeliveryHandoverDataObject> getUnscannedParcels(ArrayList<DeliveryHandoverDataObject> list) {
        ArrayList<DeliveryHandoverDataObject> list_unscanned = new ArrayList<DeliveryHandoverDataObject>();

        for (int i = 0; i < list.size(); i++) {
            {
                if (!list.get(i).isParcelScanned()) {
                    list_unscanned.add(list.get(i));
                }
            }
        }

        return list_unscanned;
    }

    /**
     * Checks list of parcels and returns a list of parcels which have been scanned.
     *
     * @param list
     * @return
     */
    private ArrayList<DeliveryHandoverDataObject> getScannedParcels(ArrayList<DeliveryHandoverDataObject> list) {
        ArrayList<DeliveryHandoverDataObject> list_scanned = new ArrayList<DeliveryHandoverDataObject>();

        for (int i = 0; i < list.size(); i++) {
            {
                if (list.get(i).isParcelScanned()) {
                    list_scanned.add(list.get(i));
                }
            }
        }

        return list_scanned;
    }

    /**
     * Checks if all parcels have been scanned into the branch
     *
     * @return True boolean value if all parcels have been scanned.
     */
    private boolean allParcelsScanned() {

        boolean allScanned = true;

        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isParcelScanned() == false) {
                    allScanned = false;
                    break;
                }
            }
        } else {
            return false;
        }

        return allScanned;
    }

    // TODO: this needs to be changed to send the scanned timestamp instead of just a boolean
    public void updateFromPushNotification(String parcel_id, boolean scanned) {
        // DbHandler.getInstance(getActivity().getApplicationContext()).setWaybillScanned(waybill_no, scanned);

        JSONObject parcel = Workflow.getInstance().getParcelByParcelBarcode(parcel_id);

        if (parcel != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getBarcode().equals(parcel_id)) {
                    int timestamp = (int) (new Date().getTime() / 1000);
                    list.get(i).setParcelScanned(timestamp);
                }
            }

            listAdapter.notifyDataSetChanged();
        }
    }

    public void initViewHolder(LayoutInflater inflater, ViewGroup container) {
        if (rootView == null) {

            rootView = inflater.inflate(R.layout.fragment_delivery_handover, null, false);

            if (holder == null) {
                holder = new MyViewHolder();
            }

            holder.list = ViewHolder.get(rootView, R.id.deliveryHandover_listView_scannedParcels);
            holder.button = ViewHolder.get(rootView,R.id.button_delivery_handover_complete);
            holder.parcelsScanned = ViewHolder.get(rootView, R.id.deliveryHandover_textView_ParcelsScanned);

            // Store the holder with the view.
            rootView.setTag(holder);
        } else {
            holder = (MyViewHolder) rootView.getTag();

            if ((rootView.getParent() != null) && (rootView.getParent() instanceof ViewGroup)) {
                ((ViewGroup) rootView.getParent()).removeAllViewsInLayout();
            } else {
            }
        }
    }

    // Creates static instances of resources.
    // Increases performance by only finding and inflating resources only once.
    static class MyViewHolder {
        ListView list;
        Button button;
        TextView parcelsScanned;
        ImageView largeicon;
    }

    private class DeliveryHandoverAdapter extends BaseAdapter {

        private final Context context;
        List<DeliveryHandoverDataObject> parcelList;

        public DeliveryHandoverAdapter(ArrayList<DeliveryHandoverDataObject> objects, Activity activity) {
            super();
            context = activity.getApplicationContext();
            parcelList = objects;
        }

        @Override
        public View getView(int position, View rowView, ViewGroup parent) {
            final int thisPosition = position;

            if( rowView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.row_delivery_handover, parent, false);
            }

            TextView parcelTitle = ViewHolder.get(rowView, R.id.row_delivery_parcel);
            TextView waybillTile = ViewHolder.get(rowView, R.id.row_delivery_waybill);
            ImageView hasScannedParcel = ViewHolder.get(rowView, R.id.row_delivery_handover_image);
            ImageView largeparcel = ViewHolder.get(rowView , R.id.large_parcel_image);

            DeliveryHandoverDataObject dhdo = parcelList.get(thisPosition);
            waybillTile.setText(dhdo.getMDX() + " (" + dhdo.getXof() + ")");
            parcelTitle.setText(dhdo.getBarcode());

            if (dhdo.getLarge().equals("Y")){
                largeparcel.setVisibility(View.VISIBLE);
            }
            if (dhdo.isParcelScanned() == true) {
                parcelTitle.setTextColor(getResources().getColor(R.color.green_tick));
                waybillTile.setTypeface(null , Typeface.BOLD);
                parcelTitle.setTypeface(null , Typeface.BOLD);
                hasScannedParcel.setVisibility(View.VISIBLE);
            } else {
                hasScannedParcel.setVisibility(View.GONE);
            }

            if (parcelList.get(thisPosition).data.countObservers() == 0) {
                parcelList.get(thisPosition).data.addObserver(new Observer() {
                    @Override
                    public void update(Observable observable, Object data) {
                        listAdapter.notifyDataSetChanged();
                    }
                });
            }
            parcelList.get(thisPosition).data.forceNotifyAllObservers();

            return rowView;
        }

        @Override
        public int getCount() {
            return parcelList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public int getScannedCount() {
            int scanned = 0;
            for (DeliveryHandoverDataObject item : this.parcelList) {
                if (item.isParcelScanned())
                    scanned++;
            }
            return scanned;
        }
    }
}
