package com.mrdexpress.paperless;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.google.zxing.client.android.Intents;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.db.Users;
import com.mrdexpress.paperless.fragments.ChangeUserDialog;
import com.mrdexpress.paperless.fragments.IncompleteScanDialog;
import com.mrdexpress.paperless.fragments.NotAssignedToUserDialog;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.service.LocationService;
import com.mrdexpress.paperless.widget.CustomToast;
import com.mrdexpress.paperless.workflow.JSONObjectHelper;
import com.mrdexpress.paperless.workflow.Workflow;

import java.util.Date;
import java.util.List;

public class ScanActivity extends FragmentActivity {

    private ViewHolder holder;
    private View root_view;

    private static final String TAG = "ScanActivity";

    private IncompleteScanDialog dialog;
    private ChangeUserDialog dialog_change_user;
    private NotAssignedToUserDialog dialog_not_assigned;

    static final int REQUEST_MANUAL_BARCODE = 1;
    static final int RESULT_MANAGER_AUTH = 2;
    static final int RESULT_INCOMPLETE_SCAN_AUTH = 3;
    static final int RESULT_LOGIN_ACTIVITY_INCOMPLETE_SCAN = 4;
    static final int RESULT_LOGIN_ACTIVITY_UNAUTH_BARCODE = 5;
    private Intent intent_manual_barcode;
    private String user_name;

    private String last_scanned_barcode;

    SharedPreferences prefs;

    //Hashtable<String, Integer> bagsUnscanned;
    //Hashtable<String, Integer> bagsScanned;
    List<Bag> bags;
    String driverId;
    BarcodeListAdapter adapter;
    Handler handler;

    public void UpDateBagsForAdapter(String added_id) {
        UpdateBagsCounter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        // Store currently selected driver id globally
        //prefs = this.getSharedPreferences(VariableManager.PREF, Context.MODE_PRIVATE);

        handler = new Handler();
        driverId = Integer.toString( Users.getInstance().getActiveDriver().getid() );
        bags = Workflow.getInstance().getBags();

        // FIXME: Set sizes correctly. Maybe only check if screen size differs from size in spec.
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = (int) (size.x - (size.x * 0.1));
        int height = (int) (size.y - (size.y * 0.1));

        Intent intent = getIntent();
        intent.setAction(Intents.Scan.ACTION);
        intent.putExtra(Intents.Scan.WIDTH, width);
        intent.putExtra(Intents.Scan.HEIGHT, height);

        try{
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(Exception e){
            Log.e("MRD-EX" , e.getMessage());
        }

        user_name = Users.getInstance().getActiveDriver().getfirstName();

        initViewHolder();
        UpdateBagsCounter();

        // Set click listener for list items (selecting a driver)

        holder.list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // View manifest
                if (holder.list.getItemAtPosition(position) != null) {

                    Bag bag = (Bag) holder.list.getItemAtPosition(position);
                    if (bag != null) {
                        Intent intent = new Intent(getApplicationContext(),
                                ViewBagManifestActivity.class);

                        // Pass info to view manifest activity

                        intent.putExtra("bag_id", bag.getBagID() );
                        intent.putExtra("bag_dest", bag.getDestination() );
                        intent.putExtra("bag_items", Integer.toString(bag.getNumberItems()) );

                        startActivity(intent);
                    }
                }
            }
        });

        // Start Milkrun
        holder.button_start_milkrun.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if all bags have been scanned
                // if (true) // DEBUG
                if ((Workflow.getInstance().getBagsScanned(true).size() == holder.list.getCount()) & (holder.list.getCount() > 0)) {
                    // Go to View Deliveries screen
                    Intent intent = new Intent(getApplicationContext(),
                            ViewDeliveriesFragmentActivity.class);
                    // EditText editText = (EditText) findViewById(R.id.edit_message);
                    // String message = editText.getText().toString();
                    // intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);
                } else {
                    dialog = new IncompleteScanDialog(ScanActivity.this);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    // LayoutInflater factory = LayoutInflater.from(ScanActivity.this);

                    final Button button_continue = (Button) dialog
                            .findViewById(R.id.button_incomplete_scan_continue);

                    button_continue.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new RetrieveManagersTask().execute();
                        }
                    });

                    final Button button_scan = (Button) dialog
                            .findViewById(R.id.button_incomplete_scan_scan);

                    button_scan.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        adapter = new BarcodeListAdapter(this);
        holder.list.setAdapter(adapter);

        UpdateBagsCounter();
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
            if (resultCode == RESULT_OK) {
                holder.button_start_milkrun.setEnabled(true);
                holder.button_start_milkrun.setBackgroundResource(R.drawable.button_custom);
                handleDecode(data.getStringExtra(EnterBarcodeActivity.MANUAL_BARCODE));
            }
        }
        if (requestCode == RESULT_MANAGER_AUTH) {
            if (resultCode == RESULT_OK) {
                if (data.getBooleanExtra(ManagerAuthNotAssignedActivity.MANAGER_AUTH_SUCCESS, false)) {
                    new AddBagToDriver().execute();
                }
            }
        }
        if (requestCode == RESULT_INCOMPLETE_SCAN_AUTH) {
            if (resultCode == RESULT_OK) {
                if (data.getBooleanExtra(
                        ManagerAuthIncompleteScanActivity.MANAGER_AUTH_INCOMPLETE_SCAN, false)) {
                    Intent intent = new Intent(getApplicationContext(),
                            ViewDeliveriesFragmentActivity.class);

                    startActivity(intent);
                }
            }
        }
        if (requestCode == RESULT_LOGIN_ACTIVITY_INCOMPLETE_SCAN) {
            if (resultCode == RESULT_OK) {
                if (data.getBooleanExtra(
                        ManagerAuthIncompleteScanActivity.MANAGER_AUTH_INCOMPLETE_SCAN, false)) {
                    startIncompleteScanActivity();
                }
            }
        }
        if (requestCode == RESULT_LOGIN_ACTIVITY_UNAUTH_BARCODE) {
            if (resultCode == RESULT_OK) {
                if (data.getBooleanExtra(
                        ManagerAuthIncompleteScanActivity.MANAGER_AUTH_INCOMPLETE_SCAN, false)) {
                    startNotAssignedActivity();
                }
            }
        }
        if (requestCode == VariableManager.CALLBACK_SCAN_BARCODE_GENERAL) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");

                if (contents != null) {
                    String upc = contents;
                    handleDecode(upc);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        // Restore UI state from the savedInstanceState.
//        // This bundle has also been passed to onCreate.
//
//        selected_items = savedInstanceState
//                .getStringArrayList(VariableManager.EXTRA_LIST_SCANNED_ITEMS);
//
//        String msg = "onRestoreInstanceState - " + selected_items.get(0);
//        Log.d(TAG, msg);
//    }

    private void setupChangeUserDialog() {
        dialog_change_user = new ChangeUserDialog(ScanActivity.this);
        dialog_change_user.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_change_user.show();

        // LayoutInflater factory = LayoutInflater.from(ScanActivity.this);

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
                stopService(new Intent(ScanActivity.this, LocationService.class));
                dialog_change_user.dismiss();
                Intent intent = new Intent(ScanActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }


    void barCodeScanFailed() {
        CustomToast toast = new CustomToast(this);
        toast.setSuccess(true);
        toast.setText(getApplicationContext().getString(R.string.manager_login_wrong_password));
        toast.show();
    }

    void onBarcodeMatchSuccess() {
        UpdateBagsCounter();

        adapter.notifyDataSetChanged();

        holder.button_start_milkrun.setEnabled(Workflow.getInstance().getBagsScanned(true).size() > 0);

        if (Workflow.getInstance().getBagsScanned(true).size() == Workflow.getInstance().getBags().size()) {
            CustomToast toast = new CustomToast(this);
            toast.setSuccess(true);
            toast.setText(getString(R.string.text_scan_successful));
            toast.show();
        } else {
            CustomToast toast = new CustomToast(this);
            toast.setSuccess(true);
            toast.setText(getString(R.string.text_scan_next));
            toast.show();
        }
    }

    void onBarcodeMatchFail() {
        Log.d(TAG, "onBarcodeMatchFail, dialog_not_assigned = " + dialog_not_assigned);
        // not sure what this code does...
        if (dialog_not_assigned != null) {
            if (dialog_not_assigned.isShowing() == false) {

                dialog_not_assigned = new NotAssignedToUserDialog(ScanActivity.this);
                dialog_not_assigned.getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT));
                dialog_not_assigned.show();
                final Button button_continue = (Button) dialog_not_assigned
                        .findViewById(R.id.button_not_assigned_continue);

                button_continue.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_not_assigned.dismiss();
                        if (prefs.getString(VariableManager.LAST_LOGGED_IN_MANAGER_ID, null) == null) {
                            Intent intent = new Intent(getApplicationContext(),
                                    LoginActivity.class);

                            // startActivity(intent);
                            dialog_not_assigned.dismiss();
                            startActivityForResult(intent, RESULT_LOGIN_ACTIVITY_UNAUTH_BARCODE);
                        } else {
                            dialog_not_assigned.dismiss();
                            startNotAssignedActivity();
                        }
                    }
                });
            }
        } else {
            dialog_not_assigned = new NotAssignedToUserDialog(ScanActivity.this);
            dialog_not_assigned.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
            dialog_not_assigned.show();
            Log.d(TAG, "SHOW dialog_not_assigned = " + dialog_not_assigned);
            final Button button_continue = (Button) dialog_not_assigned
                    .findViewById(R.id.button_not_assigned_continue);

            button_continue.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (prefs.getString(VariableManager.LAST_LOGGED_IN_MANAGER_ID, null) == null) {
                        Intent intent = new Intent(getApplicationContext(),
                                LoginActivity.class);

                        // startActivity(intent);
                        dialog_not_assigned.dismiss();
                        startActivityForResult(intent, RESULT_LOGIN_ACTIVITY_UNAUTH_BARCODE);
                    } else {
                        dialog_not_assigned.dismiss();
                        startNotAssignedActivity();
                    }
                }
            });
        }
    }


    public void onBarcodeClick(View v) {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
        startActivityForResult(intent, VariableManager.CALLBACK_SCAN_BARCODE_GENERAL);
    }

    /**
     * Barcode has been successfully scanned.
     */
    //@Override
    public void handleDecode(String barcodeString) {        
        Bag scannedBag = null;

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
            if (wasScanned)
                scannedBag.setScanned(-1);
            else
                scannedBag.setScanned((int) new Date().getTime() / 1000);

            decodeCallback = new Runnable() {
                @Override
                public void run() {
                    onBarcodeMatchSuccess();
                }
            };
        } else {
            Log.d(TAG, "handleDecode(): no match " + barcodeString);
            /*
    		   First lets see if this barcode exists in the system.
    		 */
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            String new_bag_id = ServerInterface.getInstance(
                    getApplicationContext())
                    .scanBag(getApplicationContext(), barcodeString, Integer.toString(Users.getInstance().getActiveDriver().getid()));

            if (new_bag_id.isEmpty() || new_bag_id.toString().contains("null")) {
                CustomToast toast = new CustomToast(this);
                toast.setSuccess(false);
                toast.setText(getApplicationContext().getString(R.string.manager_assign_bag_invalid_scan));
                toast.show();

            } else {
                decodeCallback = new Runnable() {
                    @Override
                    public void run() {
                        onBarcodeMatchFail();
                    }
                };
            }
        }

        // post delayed since dialogs do not show if launched directly from onActivityResult method
        if (decodeCallback != null) {
            handler.postDelayed(decodeCallback, 10);
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_scan_enter_barcode:
                Log.d(TAG, "Enter barcode manually");
                intent_manual_barcode = new Intent(getApplicationContext(), EnterBarcodeActivity.class);
                startActivityForResult(intent_manual_barcode, REQUEST_MANUAL_BARCODE);
                return true;
            case R.id.action_scan_change_driver:
                Log.d(TAG, "Change driver");
                setupChangeUserDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void initViewHolder() {

        if (root_view == null) {

            root_view = this.getWindow().getDecorView().findViewById(android.R.id.content);

            if (holder == null) {
                holder = new ViewHolder();
            }

            Typeface typeface_robotoBold = Typeface.createFromAsset(getAssets(), FontHelper
                    .getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
                            FontHelper.STYLE_BOLD));

            holder.list = (ListView) root_view.findViewById(R.id.scan_list);

            holder.button_start_milkrun = (Button) root_view
                    .findViewById(R.id.scan_button_start_milkrun);

            holder.textView_toast = (TextView) root_view.findViewById(R.id.textView_scan_toast);

            holder.relativeLayout_toast = (RelativeLayout) root_view.findViewById(R.id.toast_scan);

            holder.button_start_milkrun.setTypeface(typeface_robotoBold);

            holder.textview_scanstatus = (TextView) root_view.findViewById(R.id.activity_scan_textView_scanStatusBar);

            // Store the holder with the view.
            root_view.setTag(holder);

        } else {
            holder = (ViewHolder) root_view.getTag();

            if ((root_view.getParent() != null) && (root_view.getParent() instanceof ViewGroup)) {
                ((ViewGroup) root_view.getParent()).removeAllViewsInLayout();
            } else {
            }
        }
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
    }

    /**
     * Retrieve list of managers from API in background
     *
     * @author greg
     */
    private class RetrieveManagersTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog_progress = new ProgressDialog(ScanActivity.this);

        /** progress dialog to show user that the backup is processing. */
        /**
         * application context.
         */
        @Override
        protected void onPreExecute() {
            this.dialog_progress.setMessage("Retrieving list of managers");
            this.dialog_progress.show();
        }

        @Override
        protected Void doInBackground(Void... urls) {
            // Log.i(TAG, "Fetching token...");
            ServerInterface.getInstance(getApplicationContext()).getManagers(
                    getApplicationContext());

            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            // Close progress spinner
            if (dialog_progress.isShowing()) {
                dialog_progress.dismiss();
            }

            // Start manager authorization activity
            if (prefs.getString(VariableManager.LAST_LOGGED_IN_MANAGER_ID, null) == null) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                // startActivity(intent);
                startActivityForResult(intent, RESULT_LOGIN_ACTIVITY_INCOMPLETE_SCAN);
                dialog.dismiss();
            } else {
                startIncompleteScanActivity();
                dialog.dismiss();
            }
        }
    }

    private void startNotAssignedActivity() {
        // Start manager authorization activity
        Intent intent = new Intent(getApplicationContext(), ManagerAuthNotAssignedActivity.class);
        intent.putExtra(VariableManager.EXTRA_BAGID, last_scanned_barcode);
        startActivityForResult(intent, RESULT_MANAGER_AUTH);
    }

    private void startIncompleteScanActivity() {
        // Start manager authorization activity
        Intent intent = new Intent(getApplicationContext(), ManagerAuthIncompleteScanActivity.class);

        // intent.putExtra(VariableManager.EXTRA_DRIVER_ID,
        // getIntent().getStringExtra(VariableManager.EXTRA_DRIVER_ID));

        startActivityForResult(intent, RESULT_INCOMPLETE_SCAN_AUTH);
    }

    private class AddBagToDriver extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog_progress = new ProgressDialog(ScanActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog_progress.setMessage("Adding bag");
            this.dialog_progress.show();
        }

        @Override
        protected Void doInBackground(Void... urls) {
            SharedPreferences prefs = getSharedPreferences(VariableManager.PREF,
                    Context.MODE_PRIVATE);

            final String driverid = prefs.getString(VariableManager.PREF_DRIVERID, null);

            String new_bag_id = ServerInterface.getInstance(getApplicationContext()).scanBag(getApplicationContext(), last_scanned_barcode, driverid);
            if (!new_bag_id.isEmpty()) {
                //    ServerInterface.getInstance(getApplicationContext()).downloadBag(
                //            getApplicationContext(),new_bag_id,driverid);
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
            // getLoaderManager().restartLoader(URL_LOADER, null, ScanActivity.this);

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
            text_view_hubcode.setText(JSONObjectHelper.getStringDef(bag.getDestinationExtra(), "hubcode", "!"));

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