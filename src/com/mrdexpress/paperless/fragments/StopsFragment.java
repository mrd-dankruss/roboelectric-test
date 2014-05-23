package com.mrdexpress.paperless.fragments;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.mrdexpress.paperless.DriverHomeActivity;
import com.mrdexpress.paperless.ManagerAuthIncompleteScanActivity;
import com.mrdexpress.paperless.Paperless;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.datatype.StopItem;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.db.Device;
import com.mrdexpress.paperless.db.Users;
import com.mrdexpress.paperless.dialogfragments.StopItemMenuDialogFragment;
import com.mrdexpress.paperless.dialogfragments.ViewBagManifestDialogFragment;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.interfaces.FragmentCallBackFunction;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.workflow.Workflow;

import java.util.List;

public class StopsFragment extends Fragment {

    private static final String TAG = "StopsFragment";
    List <StopItem> stops;
    String driverId;
    StopListAdapter adapter;
    Handler handler;
    AlertDialog.Builder dialog_builder;
    Intent scan_intent;
    DialogInterface.OnClickListener dialog1ClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    startNotAssignedActivity();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };
    private ViewHolder holder;
    private View root_view;
    private IncompleteScanDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Paperless.getInstance().setMainActivity(this.getActivity());
        Paperless.getInstance().ottobus.register(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        initViewHolder(inflater, container);
        return root_view;
    }


    public void onBackPressed() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        getActivity().finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked

                        break;
                }
            }
        };

        // Do Here what ever you want do on back press;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to logout?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handler = new Handler();
        driverId = Integer.toString( Users.getInstance().getActiveDriver().getid() );

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = (int) (size.x - (size.x * 0.1));
        int height = (int) (size.y - (size.y * 0.1));


        try{
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(Exception e){
            Log.e("MRD-EX" , e.getMessage());
        }

        // Set click listener for list items (selecting a driver)

        holder.list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // View manifest
                if (holder.list.getItemAtPosition(position) != null) {

                    StopItem stop = (StopItem) holder.list.getItemAtPosition(position);
                    if (stop != null) {
                        Paperless.getInstance().startViewStopDetailsFragment(stop , position , getActivity());
                        /*DialogFragment newFragment = StopItemMenuDialogFragment.newInstance( stop, new CallBackFunction() {
                            @Override
                            public boolean execute(Object args) {
                                if( (Boolean)args == true)
                                    getData();
                                return false;
                            }
                        });
                        newFragment.show( getActivity().getFragmentManager(), "menudialog");
                        */
                    }
                }
            }
        });

        // Start Milkrun
        holder.button_start_milkrun.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.list.getCount() > 0){
                    startDelivery();
                } else {
                    Device.getInstance().displayInfo("No deliveries assigned to you yet");
                }

            }
        });

        getData();
    }

    private void getData(){
        class RetrieveBagsTask extends AsyncTask<Void, Void, Void> {
            private ProgressDialog dialog_progress = new ProgressDialog(getActivity());

            @Override
            protected void onPreExecute() {
                this.dialog_progress.setMessage("Retrieving Workflow");
                this.dialog_progress.show();
            }

            @Override
            protected Void doInBackground(Void... urls) {
                ServerInterface.getInstance(getActivity().getApplicationContext()).getMilkrunWorkflow( getActivity().getApplicationContext());
                return null;
            }

            @Override
            protected void onPostExecute(Void nothing) {
                if (dialog_progress.isShowing()) {
                    dialog_progress.dismiss();
                }
                gotData();
            }
        }

        new RetrieveBagsTask().execute();
    }

    private void gotData(){
        stops = Workflow.getInstance().getStops();

        adapter = new StopListAdapter(getActivity());
        holder.list.setAdapter(adapter);

    }

    private void startDelivery()
    {
        // ((FragmentResultInterface)getActivity()).onFragmentResult(DriverHomeActivity.START_DELIVERY,2,null);

        ((StopActivityInterface)getActivity()).stopFragmentDone(DriverHomeActivity.START_DELIVERY, 2, null);
        //getFragmentManager().popBackStack();
        //getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        //Intent intent = new Intent( Paperless.getContext() , TabViewDeliveriesFragment.class);
        //startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        holder.button_start_milkrun.setEnabled(true);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initViewHolder(LayoutInflater inflater, ViewGroup container){
            root_view = inflater.inflate(R.layout.activity_stops, container, false);

            if (holder == null) {
                holder = new ViewHolder();
            }

            Typeface typeface_robotoBold = Typeface.createFromAsset(this.getActivity().getAssets(), FontHelper
                    .getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
                            FontHelper.STYLE_BOLD));

            holder.list = (ListView) root_view.findViewById(R.id.scan_list);

            holder.button_start_milkrun = (Button) root_view.findViewById(R.id.scan_button_start_milkrun);
            holder.button_start_milkrun.setTypeface(typeface_robotoBold);
            root_view.setTag(holder);

    }

    private void startNotAssignedActivity() {
        // Start manager authorization activity
        // TODO: wire this back in
        /*
        Intent intent = new Intent(getApplicationContext(), ManagerAuthNotAssignedActivity.class);
        intent.putExtra(VariableManager.EXTRA_BAGID, last_scanned_barcode);
        startActivityForResult(intent, RESULT_MANAGER_AUTH);*/
    }

    public interface StopActivityInterface{
        public void stopFragmentDone(int requestCode, int resultCode, Object data);
    }

    /**
     * Creates static instances of resources. Increases performance by only
     * finding and inflating resources only once.
     */
    static class ViewHolder {
        ListView list;
        Button button_start_milkrun;
        TextView textView_toast;
        RelativeLayout relativeLayout_toast;
        TextView textview_scanstatus;
        Button button_start_scanning;
    }

    private class AddBagToDriver extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog_progress = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            this.dialog_progress.setMessage("Adding bag");
            this.dialog_progress.show();
        }

        @Override
        protected Void doInBackground(Void... urls) {
            /*final String driverid = Integer.toString(Users.getInstance().getActiveDriver().getid());
            String new_bag_id = ServerInterface.getInstance(getActivity().getApplicationContext()).scanBag(getActivity().getApplicationContext(), last_scanned_barcode, driverid);
            if (!new_bag_id.isEmpty()) {
                // TODO: Gary wire this back in...!!
                //ServerInterface.getInstance(getApplicationContext()).downloadBag( getApplicationContext(),new_bag_id,driverid);
                //adapter.notifyDataSetChanged();

            } else {
            }*/
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            if (dialog_progress.isShowing()) {
                dialog_progress.dismiss();
            }
        }
    }

    class StopListAdapter extends ArrayAdapter<StopItem>{
        public StopListAdapter(Context context) {
            super(context, R.layout.row_stop);
        }

        @Override
        public int getCount() {
            return stops.size();
        }

        @Override
        public StopItem getItem(int position) {
            return stops.get(position);
        }

        @Override
        public View getView(int position, View rowView, ViewGroup parent) {
            final StopItem stop = getItem(position);
            Context context = this.getContext();

            if (rowView == null) {
                rowView = LayoutInflater.from(context).inflate(R.layout.row_stop, null, false);
            }
            TextView text_view_hubcode = com.mrdexpress.paperless.ui.ViewHolder.get(rowView, R.id.row_stop_hubcode);
            TextView text_view_qty = com.mrdexpress.paperless.ui.ViewHolder.get(rowView, R.id.row_stop_qty);
            ImageView deliveryType = com.mrdexpress.paperless.ui.ViewHolder.get(rowView, R.id.row_stop_imageView_deliveryType);

            List bags = Workflow.getInstance().getBagsForStopAsJSONArray( stop.getIDs());
            String bagtext = bags.size() + " Bag" + (bags.size()==1?"":"s");

            List parcels = Workflow.getInstance().getStopParcelsAsObjects( stop.getIDs());
            bagtext = bagtext + " containing " + parcels.size() + " Parcel" + (parcels.size()==1?"":"s");

            text_view_qty.setText( bagtext);

            text_view_hubcode.setText( stop.getDestinationDesc());

            /*
            Button menu_button = com.mrdexpress.paperless.ui.ViewHolder.get(rowView, R.id.row_stop_button_menu);

            menu_button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    DialogFragment newFragment = StopItemMenuDialogFragment.newInstance( stop, new CallBackFunction() {
                        @Override
                        public boolean execute(Object args) {
                            notifyDataSetChanged();
                            return false;
                        }
                    });
                    newFragment.show( getActivity().getFragmentManager(), "menudialog");
                }
            });*/

            return rowView;
        }
    }

}