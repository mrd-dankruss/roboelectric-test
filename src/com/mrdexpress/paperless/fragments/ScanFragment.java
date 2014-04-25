package com.mrdexpress.paperless.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.google.zxing.client.android.Intents;
import com.mrdexpress.paperless.*;
import com.mrdexpress.paperless.EnterBarcodeFragment;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.db.Users;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.interfaces.FragmentResultInterface;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.widget.CustomToast;
import com.mrdexpress.paperless.workflow.JSONObjectHelper;
import com.mrdexpress.paperless.workflow.Workflow;

import java.util.Date;
import java.util.List;

public class ScanFragment extends Fragment {

    private ViewHolder holder;
    private View root_view;

    private static final String TAG = "ScanFragment";

    private IncompleteScanDialog dialog;
    private ChangeUserDialog dialog_change_user;
    private NotAssignedToUserDialog dialog_not_assigned;

    static final int REQUEST_MANUAL_BARCODE = 1;
    static final int RESULT_MANAGER_AUTH = 2;
    static final int RESULT_INCOMPLETE_SCAN_AUTH = 3;
    static final int RESULT_LOGIN_ACTIVITY_INCOMPLETE_SCAN = 4;
    static final int RESULT_LOGIN_ACTIVITY_UNAUTH_BARCODE = 5;
    public static final int RESULT_MANUAL_ENTRY = 2002;
    private Intent intent_manual_barcode;
    private String user_name;

    private String last_scanned_barcode;

    List<Bag> bags;
    String driverId;
    BarcodeListAdapter adapter;
    Handler handler;
    AlertDialog.Builder dialog_builder;
    Intent scan_intent;

    public void UpDateBagsForAdapter(String added_id) {
        UpdateBagsCounter();
    }

    /*@Override
    public void onBackPressed()
    {
        // code here to show dialog
        //super.onBackPressed();  // optional depending on your needs
        DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        ((FragmentResultInterface)getActivity()).onFragmentResult(1,2,null);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        if (Workflow.getInstance().getBagsScanned(true).size() > 0)
            dialog_builder.setMessage("You will need to scan all bags again if you cancel the current delivery run").setNegativeButton("No", dialogClickListener2).setPositiveButton("Yes", dialogClickListener2).setTitle("Cancel delivery run for " + Users.getInstance().getActiveDriver().getFullName() ).show();
        else
            getActivity().onBackPressed();
    } */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Paperless.getInstance().setMainActivity(this.getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        initViewHolder(inflater, container);
        return root_view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getData();
    }

    private void getData(){
        class RetrieveBagsTask extends AsyncTask<Void, Void, Void> {
            private ProgressDialog dialog_progress = new ProgressDialog(getActivity());

            /** progress dialog to show user that the backup is processing. */
            /**
             * application context.
             */
            @Override
            protected void onPreExecute() {
                this.dialog_progress.setMessage("Retrieving Workflow");
                this.dialog_progress.show();
            }

            @Override
            protected Void doInBackground(Void... urls) {
                SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences( VariableManager.PREF, Context.MODE_PRIVATE);

                ServerInterface.getInstance(getActivity().getApplicationContext()).getMilkrunWorkflow( getActivity().getApplicationContext());

                return null;
            }

            @Override
            protected void onPostExecute(Void nothing) {
                // Close progress spinner
                if (dialog_progress.isShowing()) {
                    dialog_progress.dismiss();
                }

                gotData();
                //((FragmentResultInterface)getActivity()).onFragmentResult(2,1,null);
                // Start scan activity
            /*Intent intent = new Intent(getActivity(), ScanFragment.class);

            intent.putExtra(VariableManager.EXTRA_DRIVER, Users.getInstance().getActiveDriver().getfirstName());

            startActivity(intent);*/
            }
        }

        new RetrieveBagsTask().execute();
    }

    private void gotData(){
        handler = new Handler();
        driverId = Integer.toString( Users.getInstance().getActiveDriver().getid() );
        bags = Workflow.getInstance().getBags();
        dialog_builder = new AlertDialog.Builder(getActivity());

        // FIXME: Set sizes correctly. Maybe only check if screen size differs from size in spec.
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);


        int width = (int) (size.x - (size.x * 0.1));
        int height = (int) (size.y - (size.y * 0.1));

        Intent intent = getActivity().getIntent();
        intent.setAction(Intents.Scan.ACTION);
        intent.putExtra(Intents.Scan.WIDTH, width);
        intent.putExtra(Intents.Scan.HEIGHT, height);
        scan_intent = new Intent("com.google.zxing.client.android.SCAN");

        try{
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(Exception e){
            Log.e("MRD-EX" , e.getMessage());
        }

        user_name = Users.getInstance().getActiveDriver().getfirstName();

        UpdateBagsCounter();

        // Set click listener for list items (selecting a driver)

        holder.list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // View manifest
                if (holder.list.getItemAtPosition(position) != null) {

                    Bag bag = (Bag) holder.list.getItemAtPosition(position);
                    if (bag != null) {
                        Intent intent = new Intent(getActivity().getApplicationContext(),
                                ViewBagManifestActivity.class);

                        // Pass info to view manifest activity

                        intent.putExtra("bag_id", bag.getBagID() );
                        intent.putExtra("bag_dest", bag.getDestination() );
                        intent.putExtra("bag_items", Integer.toString(bag.getNumberItems()) );

                        startActivityForResult( intent, RESULT_MANUAL_ENTRY);
                    }
                }
            }
        });

        // Start Milkrun
        holder.button_start_milkrun.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Workflow.getInstance().getBagsScanned(true).size() == holder.list.getCount()) & (holder.list.getCount() > 0)) {

                    startDelivery();
                } else {
                    dialog = new IncompleteScanDialog( getActivity());
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    // LayoutInflater factory = LayoutInflater.from(ScanFragment.this);

                    final Button button_continue = (Button) dialog.findViewById(R.id.button_incomplete_scan_continue);

                    button_continue.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity().getApplicationContext(), ManagerAuthIncompleteScanActivity.class);
                            startActivityForResult(intent, ScanFragment.RESULT_LOGIN_ACTIVITY_INCOMPLETE_SCAN);
                            dialog.dismiss();
                        }
                    });

                    final Button button_scan = (Button) dialog.findViewById(R.id.button_incomplete_scan_scan);

                    button_scan.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        holder.button_start_scanning.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBarcodeClick(view);
            }
        });

        adapter = new BarcodeListAdapter(getActivity());
        holder.list.setAdapter(adapter);

        UpdateBagsCounter();
    }

    private void startDelivery()
    {
        ServerInterface.getInstance().startTrip();
        ((FragmentResultInterface)getActivity()).onFragmentResult(2,2,null);
        //finish();
        //Intent intent = new Intent( getApplicationContext(), ViewDeliveriesFragmentActivity.class);
        //startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        if (dialog_change_user != null) {
            if (dialog_change_user.isShowing()) {
                dialog_change_user.dismiss();
            }
        }
        if (dialog_not_assigned != null) {
            if (dialog_not_assigned.isShowing()) {
                dialog_not_assigned.dismiss();
            }
        }
        if (requestCode == REQUEST_MANUAL_BARCODE) {
            if (resultCode == this.getActivity().RESULT_OK) {
                holder.button_start_milkrun.setEnabled(true);
                holder.button_start_milkrun.setBackgroundResource(R.drawable.button_custom);
                handleDecode(data.getStringExtra(EnterBarcodeFragment.MANUAL_BARCODE) , false);
            }
        }

        // TODO: wite this back in
       /* if (requestCode == RESULT_MANAGER_AUTH) {
            if (resultCode == RESULT_OK) {
                if (data.getBooleanExtra(ManagerAuthNotAssignedActivity.MANAGER_AUTH_SUCCESS, false)) {
                    new AddBagToDriver().execute();
                }
            }
        }*/

        if( requestCode == RESULT_MANUAL_ENTRY){
            if( resultCode == RESULT_MANUAL_ENTRY){
                getManualBarcode();
            }
        }

        if (requestCode == RESULT_INCOMPLETE_SCAN_AUTH) {
            if (resultCode == this.getActivity().RESULT_OK) {
                if( Users.getInstance().getActiveManager() != null)
                {
                    Intent intent = new Intent(this.getActivity().getApplicationContext(), ViewDeliveriesFragmentActivity.class);
                    startActivity(intent);
                }
            }
        }
        if (requestCode == RESULT_LOGIN_ACTIVITY_INCOMPLETE_SCAN) {
            if (resultCode == this.getActivity().RESULT_OK) {
                if( Users.getInstance().getActiveManager() != null)
                {
                    startDelivery();
                }
            }
        }
        if (requestCode == RESULT_LOGIN_ACTIVITY_UNAUTH_BARCODE) {
            if (resultCode == this.getActivity().RESULT_OK) {
                if (data.getBooleanExtra( ManagerAuthIncompleteScanActivity.MANAGER_AUTH_INCOMPLETE_SCAN, false))
                {
                    startNotAssignedActivity();
                }
            }
        }
        /*if (requestCode == VariableManager.CALLBACK_SCAN_BARCODE_GENERAL) {
            if (resultCode == this.getActivity().RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");

                if (contents != null) {
                    String upc = contents;
                    handleDecode(upc);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Handle cancel
            }
        }*/
    }

    private void setupChangeUserDialog() {
       /* dialog_change_user = new ChangeUserDialog(this.getActivity().);
        dialog_change_user.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_change_user.show();

        // LayoutInflater factory = LayoutInflater.from(ScanFragment.this);

        final ImageButton button_close = (ImageButton) dialog_change_user
                .findViewById(R.id.button_change_user_closeButton);
        final Button button_cancel = (Button) dialog_change_user
                .findViewById(R.id.button_change_user_cancel);
        final Button button_ok = (Button) dialog_change_user
                .findViewById(R.id.button_change_user_ok);
        final TextView dialog_content = (TextView) dialog_change_user
                .findViewById(R.id.text_change_driver_content);

        dialog_content.setText("Are you sure you want to log out " + user_name + "?");

        button_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_change_user.dismiss();
            }
        });

        button_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_change_user.dismiss();
            }
        });

        button_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(ScanFragment.this, LocationService.class));
                dialog_change_user.dismiss();
                Intent intent = new Intent(ScanFragment.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });       */
    }


    void barCodeScanFailed() {
        CustomToast toast = new CustomToast(getActivity());
        toast.setSuccess(true);
        toast.setText(this.getActivity().getApplicationContext().getString(R.string.manager_login_wrong_password));
        toast.show();
    }

    void onBarcodeMatchSuccess(Boolean scannedalready , Boolean redraw) {
        adapter.notifyDataSetChanged();

        UpdateBagsCounter();

        holder.button_start_milkrun.setEnabled(Workflow.getInstance().getBagsScanned(true).size() > 0);

        if (Workflow.getInstance().getBagsScanned(true).size() == Workflow.getInstance().getBags().size()) {
            CustomToast toast = new CustomToast(this.getActivity());
            toast.setSuccess(true);
            toast.setText(getString(R.string.text_scan_successful));
            toast.show();

        } else {
            CustomToast toast = new CustomToast(this.getActivity());

            if (scannedalready){
                toast.setSuccess(false);
                toast.setText("Already scanned - scan next item");
            } else {
                Workflow.getInstance().setWaybillScanned(last_scanned_barcode , 1);
                toast.setSuccess(true);
                toast.setText(getString(R.string.text_scan_next));
            }

            toast.show();
            //TODO : MAKE IT STAY OPEN HERE
            if (redraw){
                startActivityForResult(scan_intent, VariableManager.CALLBACK_SCAN_BARCODE_GENERAL);
            }
        }
    }


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

    void onBarcodeMatchFail() {
        //dialog_not_assigned = new NotAssignedToUserDialog(ScanFragment.this);
        //dialog_not_assigned.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialog_not_assigned.show();
        //final Button button_continue = (Button) dialog_not_assigned.findViewById(R.id.button_not_assigned_continue);
        AlertDialog.Builder dialog1 = new AlertDialog.Builder(this.getActivity());
        dialog1.setMessage("Manager authorisation required.").setNegativeButton("No" , dialog1ClickListener).setPositiveButton("Yes" , dialog1ClickListener ).setTitle("Do you want to take ownership of this bag ?").show();
    }


    public void onBarcodeClick(View v) {
        //Intent intent = new Intent("com.google.zxing.client.android.SCAN");
       // intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
        startActivityForResult(scan_intent, VariableManager.CALLBACK_SCAN_BARCODE_GENERAL);
    }

    /**
     * Barcode has been successfully scanned.
     */
    //@Override

    public void handleDecode(String barcodeString, Boolean redrawscan) {
        barcodeString = barcodeString.replaceAll("\\s+","");
        Bag scannedBag = null;
        last_scanned_barcode = barcodeString;

        for (int i = 0; i < bags.size(); i++) {
            Bag b = bags.get(i);
            if (b.getBarcode().equals(barcodeString)) {
                scannedBag = b;
                break;
            }
        }

        Runnable decodeCallback = null;
        if (scannedBag != null) {
            boolean wasScanned = scannedBag.getScanned();

            // TODO: when does this get sent to the server?
            if (wasScanned){
                //scannedBag.setScanned(-1);
                onBarcodeMatchSuccess(true , redrawscan);
            }
            else{
                scannedBag.setScanned((int) new Date().getTime() / 1000);
                onBarcodeMatchSuccess(false , redrawscan);
            }


        } else {
            Log.d(TAG, "handleDecode(): no match " + barcodeString);
            /*
    		   First lets see if this barcode exists in the system.
    		 */
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            String new_bag_id = "";
            if (barcodeString.contains("XMRDX"))
                new_bag_id = ServerInterface.getInstance( this.getActivity().getApplicationContext()).scanBag( this.getActivity().getApplicationContext(), barcodeString, Integer.toString( Users.getInstance().getActiveDriver().getid()));


            if (new_bag_id.isEmpty() || new_bag_id.toString().contains("null") || !barcodeString.contains("XMRDX") ) {
                CustomToast toast = new CustomToast(getActivity());
                toast.setSuccess(false);
                toast.setText(this.getActivity().getApplicationContext().getString(R.string.manager_assign_bag_invalid_scan));
                toast.show();

            } else {
                onBarcodeMatchFail();
            }
        }

        // post delayed since dialogs do not show if launched directly from onActivityResult method
        if (decodeCallback != null) {
            handler.postDelayed(decodeCallback, 10);
        }
    }
    public void handleDecode(String barcodeString){
        this.handleDecode(barcodeString , true);
    }


    @Override
    public void onResume() {
        super.onResume();

        // Close dialog if it is showing upon resuming screen.
        // Or else it is still open when backing out of ManagerAuthIncompleteScanActivity
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        if (dialog_change_user != null) {
            if (dialog_change_user.isShowing()) {
                dialog_change_user.dismiss();
            }
        }
        if (dialog_not_assigned != null) {
            if (dialog_not_assigned.isShowing()) {
                dialog_not_assigned.dismiss();
            }
        }

        if (Workflow.getInstance().getBagBarcodesScanned().size() == 0) {
            holder.button_start_milkrun.setEnabled(false);
            holder.button_start_milkrun.setBackgroundResource(R.drawable.button_custom_grey);
        } else {
            holder.button_start_milkrun.setEnabled(true);
            holder.button_start_milkrun.setBackgroundResource(R.drawable.button_custom);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Set all consignments' scanned state to false
        // DbHandler.getInstance(getApplicationContext()).setScannedAll(false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            case R.id.action_scan_enter_barcode:
                Log.d(TAG, "Enter barcode manually");
                getManualBarcode();
                return true;
            case R.id.action_scan_change_driver:
                Log.d(TAG, "Change driver");
                setupChangeUserDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getManualBarcode(){

        EnterBarcodeFragment getBarcode = EnterBarcodeFragment.newInstance( new CallBackFunction() {
            @Override
            public boolean execute(Object args) {
                handleDecode( (String)args);
                return false;
            }
        }  );
        getBarcode.show( getActivity().getFragmentManager(), "");
        //intent_manual_barcode = new Intent(this.getActivity().getApplicationContext(), EnterBarcodeFragment.class);
        //startActivityForResult(intent_manual_barcode, REQUEST_MANUAL_BARCODE);
    }

    public void initViewHolder(LayoutInflater inflater, ViewGroup container){
            root_view = inflater.inflate(R.layout.activity_scan, container, false);

            if (holder == null) {
                holder = new ViewHolder();
            }

            Typeface typeface_robotoBold = Typeface.createFromAsset(this.getActivity().getAssets(), FontHelper
                    .getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
                            FontHelper.STYLE_BOLD));

            holder.list = (ListView) root_view.findViewById(R.id.scan_list);

            holder.button_start_milkrun = (Button) root_view
                    .findViewById(R.id.scan_button_start_milkrun);

            holder.button_start_scanning = (Button) root_view.findViewById(R.id.button_start_scanning);

            holder.textView_toast = (TextView) root_view.findViewById(R.id.textView_scan_toast);

            holder.relativeLayout_toast = (RelativeLayout) root_view.findViewById(R.id.toast_scan);

            holder.button_start_milkrun.setTypeface(typeface_robotoBold);

            holder.textview_scanstatus = (TextView) root_view.findViewById(R.id.activity_scan_textView_scanStatusBar);

            // Store the holder with the view.
            root_view.setTag(holder);

    }

    public interface onDoneListener{
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

    private void startNotAssignedActivity() {
        // Start manager authorization activity
        // TODO: wire this back in
        /*
        Intent intent = new Intent(getApplicationContext(), ManagerAuthNotAssignedActivity.class);
        intent.putExtra(VariableManager.EXTRA_BAGID, last_scanned_barcode);
        startActivityForResult(intent, RESULT_MANAGER_AUTH);*/
    }

    private void startIncompleteScanActivity() {
        // Start manager authorization activity
        Intent intent = new Intent(this.getActivity().getApplicationContext(), ManagerAuthIncompleteScanActivity.class);
        startActivityForResult(intent, RESULT_INCOMPLETE_SCAN_AUTH);
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
            final String driverid = Integer.toString(Users.getInstance().getActiveDriver().getid());
            String new_bag_id = ServerInterface.getInstance(getActivity().getApplicationContext()).scanBag(getActivity().getApplicationContext(), last_scanned_barcode, driverid);
            if (!new_bag_id.isEmpty()) {
                // TODO: Gary wire this back in...!!
                //ServerInterface.getInstance(getApplicationContext()).downloadBag( getApplicationContext(),new_bag_id,driverid);
                adapter.notifyDataSetChanged();
                UpDateBagsForAdapter(last_scanned_barcode);
                handleDecode(last_scanned_barcode);

            } else {
                barCodeScanFailed();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            /**
             * TODO: ADDING A NEW BAG TO THE DRIVER CONSIGNMENTS HERE!!! AND THEN UPDATING THE LISTVIEW DISPLAY
             */
            // handleDecode(new Result(last_scanned_barcode, null, null, null), null, 0);
            // getLoaderManager().restartLoader(URL_LOADER, null, ScanFragment.this);

            // Refresh list
            // cursor_adapter.notifyDataSetChanged();
            // holder.list.setAdapter(cursor_adapter);

            // Close progress spinner
            if (dialog_progress.isShowing()) {
                dialog_progress.dismiss();
            }
        }
    }


    public void UpdateBagsCounter() {
        try {
            holder.textview_scanstatus.setText("BAG" + (Workflow.getInstance().getBagBarcodesScanned().size() == 1 ? "" : "S") + " (" + Workflow.getInstance().getBagBarcodesScanned().size() + " / " + bags.size() + " SCANNED)");
        } catch (Exception e) {
            Log.e("MRDEX:", e.toString());
        }
    }


    class BarcodeListAdapter extends ArrayAdapter<Bag> {
        public BarcodeListAdapter(Context context) {
            super(context, R.layout.row_scan);
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
        public View getView(int position, View convertView, ViewGroup parent) {
            Bag bag = getItem(position);
            Context context = this.getContext();

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.row_scan, null, false);
            }
            TextView text_view_consignment = (TextView) convertView.findViewById(R.id.textView_row_scan);
            TextView text_view_hubcode = (TextView) convertView.findViewById(R.id.testView_row_scan_hubcode);
            TextView text_view_qty = (TextView) convertView.findViewById(R.id.testView_row_scan_qty);
            ImageView image_green_tick = (ImageView) convertView.findViewById(R.id.imageView_row_scan_tick);

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

            return convertView;
        }
    }

}