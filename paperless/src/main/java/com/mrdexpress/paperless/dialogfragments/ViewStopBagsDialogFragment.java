package com.mrdexpress.paperless.dialogfragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.datatype.StopItem;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.workflow.JSONObjectHelper;
import com.mrdexpress.paperless.workflow.Workflow;

import java.util.List;

/**
 * Created by gary on 2014-05-06.
 */
public class ViewStopBagsDialogFragment extends DialogFragment {
    private final String TAG = "ViewStopBagsDialogFragment";
    private String stopids;
    private CallBackFunction callback;
    private Activity activity;
    List<Bag> bags;

    public ViewStopBagsDialogFragment( String stopids, CallBackFunction callback) {
        this.callback = callback;
        this.stopids = stopids;
    }

    public static ViewStopBagsDialogFragment newInstance( String stopids, CallBackFunction _callback)
    {
        ViewStopBagsDialogFragment f = new ViewStopBagsDialogFragment( stopids, _callback);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bags = Workflow.getInstance().getBagsForStop( stopids);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stops_view_bags, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        ListView list = (ListView) v.findViewById(R.id.stop_bag_list);
        list.setAdapter( new StopBagsListAdapter( getActivity()));

        ImageButton closeButton = (ImageButton) v.findViewById(R.id.stop_bag_list_closeButton);
        closeButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }

    class StopBagsListAdapter extends ArrayAdapter<Bag> {
        public StopBagsListAdapter(Context context) {
            super(context, R.layout.row_stop);
        }

        @Override
        public int getCount() {
            return bags.size();
        }

        @Override
        public Bag getItem(int position) {
            return bags.get(position);
        }

        @Override
        public View getView(int position, View rowView, ViewGroup parent) {
            Bag bag = getItem(position);
            Context context = this.getContext();

            if (rowView == null) {
                rowView = LayoutInflater.from(context).inflate(R.layout.row_scan, null, false);
            }
            TextView text_view_consignment = (TextView) rowView.findViewById(R.id.textView_row_scan);
            TextView text_view_hubcode = (TextView) rowView.findViewById(R.id.testView_row_scan_hubcode);
            TextView text_view_qty = (TextView) rowView.findViewById(R.id.testView_row_scan_qty);
            ImageView image_green_tick = (ImageView) rowView.findViewById(R.id.imageView_row_scan_tick);

            text_view_qty.setText("( " + bag.getNumberItems() + " ITEM" + (bag.getNumberItems() == 1 ? "" : "S") + " )");
            text_view_consignment.setText(bag.getBarcode());
            String hcode = " ";
            try {
                hcode = JSONObjectHelper.getStringDef(bag.getDestinationExtra(), "hubcode", "!");
            }catch(Exception e){

            }
            text_view_hubcode.setText(hcode);

            // re-set styling since view may be re-used
            if (bag.getScanned()) {
                image_green_tick.setVisibility(View.VISIBLE);
                text_view_consignment.setTextColor(context.getResources().getColor(
                        R.color.colour_green_scan));
                text_view_consignment.setTextColor(context.getResources().getColor(R.color.colour_green_scan));
            } else {
                image_green_tick.setVisibility(View.INVISIBLE);
                text_view_consignment.setTextColor(context.getResources().getColor(
                        R.color.colour_row_text));
                text_view_consignment.setTextColor(context.getResources().getColor(R.color.colour_row_text));
            }

            return rowView;
        }
    }

}
