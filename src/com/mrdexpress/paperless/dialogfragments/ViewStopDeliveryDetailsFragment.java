package com.mrdexpress.paperless.dialogfragments;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.datatype.DeliveryHandoverDataObject;
import com.mrdexpress.paperless.datatype.StopItem;
import com.mrdexpress.paperless.fragments.ReAssignStopFragment;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.helper.MiscHelper;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.interfaces.FragmentCallBackFunction;
import com.mrdexpress.paperless.widget.CustomToast;
import com.mrdexpress.paperless.workflow.ObservableJSONObject;
import com.mrdexpress.paperless.workflow.Workflow;
import net.minidev.json.JSONObject;

import java.util.ArrayList;

public class ViewStopDeliveryDetailsFragment extends DialogFragment implements MoreDialogFragment.SetNextDeliveryListener
{

    private ViewHolder holder;
    private View rootView;
    private StopItem stop;
    private String stopids;
    private ArrayList<DeliveryHandoverDataObject> waybills;
    private int position;
    Intent intent;

    public ViewStopDeliveryDetailsFragment(CallBackFunction _callback) {
        callback = _callback;
    }

    private static CallBackFunction callback;

    public static ViewStopDeliveryDetailsFragment newInstance(final CallBackFunction callback)
    {
        ViewStopDeliveryDetailsFragment d = new ViewStopDeliveryDetailsFragment(callback);
        return d;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        initViewHolder(inflater, container); // Inflate ViewHolder static instance

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        position = bundle.getInt("ACTIVE_BAG_POSITION", -1);
        stopids = bundle.getString("STOP_IDS", "{}");

        JSONObject jso =  Workflow.getInstance().getTripStop( stopids);
        stop = new StopItem( new ObservableJSONObject( jso));

        holder.text_delivery_number.setText("#" + stop.getTripOrder());
        holder.text_delivery_title.setText("MILKRUN DELIVERY"); // TODO: Change
        holder.text_delivery_addressee.setText(stop.getDestinationDesc());
        holder.text_delivery_address.setText(stop.getAddress());

        holder.text_delivery_communication_title.setVisibility(View.GONE);
        holder.text_delivery_communication_log.setVisibility(View.GONE);
        holder.button_more.setVisibility(View.GONE);

        holder.button_update_status.setText("Re-assign Hub");

        StringBuilder bagtext = new StringBuilder();
        waybills = Workflow.getInstance().getStopParcelsAsObjects( stop.getIDs());

        bagtext.append("Parcel(s) to be delivered : <br />");
        int teller = 1;
        while (waybills.size() > 0){
            DeliveryHandoverDataObject temp = waybills.remove(0);
            bagtext.append("<b>" + teller + ". " + temp.getBarcode() + "</b><<br />");
            teller += 1;
        }
        holder.text_delivery_bad_id.setText(Html.fromHtml(bagtext.toString()));

        // TODO:Set image here one day when app is extended.
        holder.button_update_status.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /*
                DialogFragment newFragment = UpdateStatusDialog.newInstance(stop.getIDs(), new CallBackFunction() {
                    @Override
                    public boolean execute(Object args) {
                        //callback.execute( args);
                        dismiss();
                        callback.execute(true);
                        return false;
                    }
                });
                newFragment.show(getFragmentManager(), "dialog");
                */
                /*ReAssignStopFragment reaf = new ReAssignStopFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.layout.fragment_delivery_details, reaf);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();*/
                ReAssignStopFragment raf = ReAssignStopFragment.newInstance(new FragmentCallBackFunction() {
                    @Override
                    public boolean onFragmentResult(int requestCode, int resultCode, Intent data) {
                        return false;
                    }
                });
                raf.show(getActivity().getFragmentManager(), getTag());
            }
        });

        //holder.button_more.setText("More Options");

        holder.button_more.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                //boolean isNextBag = bag.getBagID() == MiscHelper.getNextDeliveryId( getActivity());
                boolean isNextBag = stop.getTripOrder() == 1;
                String curbagid = Workflow.getInstance().currentBagID;

                if (stop.getIDs() == curbagid){
                    DialogFragment newFragment = MoreDialogFragment.newInstance(false, stop.getIDs(), new CallBackFunction() {
                        @Override
                        public boolean execute(Object args) {
                            callback.execute(true);
                            dismiss();
                            return false;
                        }
                    });
                    newFragment.show(getFragmentManager(), "dialog");
                } else {
                    DialogFragment newFragment = MoreDialogFragment.newInstance(!isNextBag,	stop.getIDs(), new CallBackFunction() {
                        @Override
                        public boolean execute(Object args) {
                            callback.execute(true);
                            dismiss();
                            return false;
                        }
                    });
                    newFragment.show(getFragmentManager(), "dialog");
                }
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onSetNextDelivery(boolean is_successful, String stopids)
    {
        if (is_successful)
        {
            CustomToast custom_toast = new CustomToast(getActivity());
            custom_toast.setSuccess(true);
            custom_toast.setText("Successfully changed next delivery.");
            custom_toast.show();
            Workflow.getInstance().currentBagID = stopids;
            MiscHelper.setNextDeliveryId(stopids, getActivity());

            callback.execute( true);

            dismiss();
        }

    }

    public void initViewHolder(LayoutInflater inflater, ViewGroup container)
    {

        if (rootView == null)
        {
            //rootView = inflater.inflate(R.layout.activity_report_delay, container, false);
            rootView = inflater.inflate(R.layout.fragment_delivery_details, container, false);

            if (holder == null)
            {
                holder = new ViewHolder();
            }

            Typeface typeface_roboto_bold = Typeface.createFromAsset(getActivity().getAssets(), FontHelper
                    .getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
                            FontHelper.STYLE_BOLD));
            Typeface typeface_roboto_regular = Typeface.createFromAsset(getActivity().getAssets(), FontHelper
                    .getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
                            FontHelper.STYLE_REGULAR));

            holder.text_delivery_number = (TextView) rootView.findViewById(R.id.deliveryDetails_textView_deliveryNumber);
            holder.text_delivery_title = (TextView) rootView.findViewById(R.id.deliveryDetails_textView_titleDetail);
            holder.text_delivery_addressee = (TextView) rootView.findViewById(R.id.deliveryDetails_textView_addressee);
            holder.text_delivery_address = (TextView) rootView.findViewById(R.id.deliveryDetails_textView_address);
            holder.text_delivery_bad_id = (TextView) rootView.findViewById(R.id.deliveryDetails_textView_id);
            holder.text_delivery_communication_title = (TextView) rootView.findViewById(R.id.deliveryDetails_textView_communicationTitle);
            holder.text_delivery_communication_log = (TextView) rootView.findViewById(R.id.deliveryDetails_textView_communicationLog);
            holder.image_company_logo = (ImageView) rootView.findViewById(R.id.deliveryDetails_imageView_companyLogo);
            holder.button_update_status = (Button) rootView.findViewById(R.id.deliveryDetails_button_updateStatus);
            holder.button_more = (Button) rootView.findViewById(R.id.deliveryDetails_button_more);

            holder.text_delivery_number.setTypeface(typeface_roboto_bold);
            holder.text_delivery_title.setTypeface(typeface_roboto_bold);
            holder.text_delivery_communication_title.setTypeface(typeface_roboto_bold);
            holder.button_update_status.setTypeface(typeface_roboto_bold);
            holder.button_more.setTypeface(typeface_roboto_bold);

            holder.text_delivery_addressee.setTypeface(typeface_roboto_regular);
            holder.text_delivery_address.setTypeface(typeface_roboto_regular);
            holder.text_delivery_bad_id.setTypeface(typeface_roboto_regular);
            holder.text_delivery_communication_log.setTypeface(typeface_roboto_regular);

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
        TextView text_delivery_number;
        TextView text_delivery_title;
        TextView text_delivery_addressee;
        TextView text_delivery_address;
        TextView text_delivery_bad_id;
        TextView text_delivery_communication_log;
        TextView text_delivery_communication_title;
        ImageView image_company_logo;
        Button button_update_status;
        Button button_more;
    }
}