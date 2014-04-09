package com.mrdexpress.paperless.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.adapters.ExpandableListAdapter;
import com.mrdexpress.paperless.datatype.DeliveryHandoverDataObject;
import com.mrdexpress.paperless.datatype.DialogDataObject;
import com.mrdexpress.paperless.datatype.ReasonPartialDeliveryItem;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.workflow.Workflow;

import java.util.ArrayList;

public class ReasonPartialDeliveryFragment extends Fragment {

    private final String TAG = "ReasonPartialDeliveryFragment";
    private ViewHolder holder;
    private View rootView;
    private ExpandableListAdapter listAdapter;
    private ArrayList<ArrayList<ReasonPartialDeliveryItem>> data;
    private boolean button_enabled = false;
    private ArrayList<PartialDeliveryObject> partial_deliveries = new ArrayList<PartialDeliveryObject>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        initViewHolder(inflater, container); // Inflate ViewHolder static instance

        // Perform API call
        holder.button_continue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button_enabled) {
                    for (int i = 0; i < data.size(); i++) {
                        ArrayList<ReasonPartialDeliveryItem> reasons = data.get(i);

                        for (int r = 0; r < reasons.size(); r++) {
                            if (reasons.get(r).isSelected()) {
                                //Workflow.getInstance().setParcelDeliveryStatus(reasons.get(r).parcelid, reasons.get(r).getReasonID(), reasons.get(r).getReasonTitle());
                            }
                        }
                    }
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
                // new PartialDeliveryTask().execute();
            }
        });

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(holder.list, getActivity(), data);

        // setting list adapter
        holder.list.setAdapter(listAdapter);

        // Listview Group click listener
        holder.list.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
                                        long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                // return false;
                return parent.isGroupExpanded(groupPosition);
            }
        });

        // Listview Group expanded listener
        holder.list.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                // Toast.makeText(getActivity(), listDataHeader.get(groupPosition) + " Expanded",
                // Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        holder.list.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                // Toast.makeText(getActivity(), listDataHeader.get(groupPosition) + " Collapsed",
                // Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        holder.list.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {

                setTick(groupPosition, childPosition);
                listAdapter.notifyDataSetChanged();

                return false;
            }
        });

        for (int i = 0; i < data.size(); i++) {
            holder.list.expandGroup(0);
        }

        return rootView;
    }

    private boolean allTicked() {
        int cnt = 0;
        for (int i = 0; i < data.size(); i++) {
            ArrayList<ReasonPartialDeliveryItem> reasons = data.get(i);

            for (int r = 0; r < reasons.size(); r++) {
                if (reasons.get(r).isSelected()) {
                    cnt++;
                }
            }
        }
        return cnt == data.size();
    }

    private void setTick(int groupPosition, int childPosition) {
        data.get(groupPosition).get(childPosition).setIsSelected(true);
        for (int i = 0; i < data.get(groupPosition).size(); i++) {
            if (i != childPosition) {
                data.get(groupPosition).get(i).setIsSelected(false);
            }
        }
        // Enable button
        button_enabled = allTicked();
        holder.button_continue.setEnabled(button_enabled);
        //holder.button_continue.setBackgroundResource(R.drawable.button_custom);
    }

    public void initViewHolder(LayoutInflater inflater, ViewGroup container) {

        if (rootView == null) {

            rootView = inflater.inflate(R.layout.fragment_reason_partial_delivery, null, false);

            if (holder == null) {
                holder = new ViewHolder();
            }

            holder.list = (ExpandableListView) rootView.findViewById(R.id.lvExp);
            holder.button_continue = (Button) rootView.findViewById(R.id.button_reason_partial_delivery);

            holder.button_continue.setEnabled(false);
            // Store the holder with the view.
            rootView.setTag(holder);

        } else {
            holder = (ViewHolder) rootView.getTag();

            if ((rootView.getParent() != null) && (rootView.getParent() instanceof ViewGroup)) {
                ((ViewGroup) rootView.getParent()).removeAllViewsInLayout();
            } else {
            }
        }
    }

    // Creates static instances of resources.
    // Increases performance by only finding and inflating resources only once.
    static class ViewHolder {
        ExpandableListView list;
        Button button_continue;
    }

    private void prepareListData() {
        data = new ArrayList<ArrayList<ReasonPartialDeliveryItem>>();

        ArrayList<DeliveryHandoverDataObject> waybill_IDs = (ArrayList<DeliveryHandoverDataObject>) Workflow.getInstance().doormat.get(VariableManager.UNSCANNED_PARCELS);

        //ArrayList<DialogDataObject> reasons = DbHandler.getInstance(getActivity()).getPartialDeliveryReasons();
        ArrayList<DialogDataObject> reasons = Workflow.getInstance().getPartialDeliveryReasons();

        // Each waybill / (group in extendable list)
        for (int i = 0; i < waybill_IDs.size(); i++) {
            ArrayList<ReasonPartialDeliveryItem> reason_items = new ArrayList<ReasonPartialDeliveryItem>();

            DeliveryHandoverDataObject d = waybill_IDs.get(i);

            for (int r = 0; r < reasons.size(); r++) {
                reason_items.add(new ReasonPartialDeliveryItem(d.getID(), d.getBarcode(), reasons.get(r).getSubText(), reasons.get(r).getMainText(), false));
            }
            data.add(reason_items);
        }
    }

    private class PartialDeliveryObject {
        String waybill_id, status_id, extra;

        PartialDeliveryObject(String waybillid, String statusid) {
            waybill_id = waybillid;
            status_id = statusid;
            extra = "";
        }
    }
}
