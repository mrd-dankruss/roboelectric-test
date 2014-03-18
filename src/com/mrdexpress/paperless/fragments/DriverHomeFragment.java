package com.mrdexpress.paperless.fragments;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.ScanActivity;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;

public class DriverHomeFragment extends Fragment {

    private final String TAG = "HomeFragment";
    private ViewHolder holder;
    private View rootView;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences(VariableManager.PREF, Context.MODE_PRIVATE);
        initViewHolder(inflater, container); // Inflate ViewHolder static instance

        return rootView;
    }

    public void onResume() {
        super.onResume();

        // Reset training run mode

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(VariableManager.PREF_TRAINING_MODE, false);
        editor.apply();

        holder.button_milkrun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveBagsTask().execute();
            }
        });

        holder.button_training_run.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getActivity().getSharedPreferences(VariableManager.PREF,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(VariableManager.PREF_TRAINING_MODE, true);
                editor.apply();

                // Load fake DB entries
                ContentValues values = new ContentValues();

                values.put(DbHandler.C_BAG_ID, "100"); // PK
                values.put(DbHandler.C_BAG_DEST_ADDRESS, "Electric Avenue");
                values.put(DbHandler.C_BAG_DEST_CONTACT, "0124442121");
                values.put(DbHandler.C_BAG_DEST_HUBCODE, "90210");
                values.put(DbHandler.C_BAG_DEST_HUBNAME, "Beverly Hills");
                values.put(DbHandler.C_BAG_DEST_LAT, "-18.1234123");
                values.put(DbHandler.C_BAG_DEST_LONG, "33.1235242");
                values.put(DbHandler.C_BAG_DEST_SUBURB, "New Orleans");
                //values.put(DbHandler.C_BAG_DEST_TOWN, "Paradise City");
                values.put(DbHandler.C_BAG_BARCODE, "XMRDX68322");
                values.put(DbHandler.C_BAG_ASSIGNED, 1);
                values.put(DbHandler.C_BAG_STOPID, "1");
                values.put(DbHandler.C_BAG_SCANNED, 0);
                values.put(DbHandler.C_BAG_CREATION_TIME, "191645B Feb 2014");
                values.put(DbHandler.C_BAG_NUM_ITEMS, "1");
                values.put(DbHandler.C_BAG_DRIVER_ID, VariableManager.TRAININGRUN_MILKRUN_DRIVERID);
                values.put(DbHandler.C_BAG_STATUS, Bag.STATUS_TODO);

                Log.d(TAG,
                        "Added trainingrun bagid: 100 "
                                + DbHandler.getInstance(getActivity()).addRow(
                                DbHandler.TABLE_BAGS_TRAINING, values));

                values = new ContentValues();

                values.put(DbHandler.C_BAG_ID, "101"); // PK
                values.put(DbHandler.C_BAG_DEST_ADDRESS, "21 Baker Street");
                values.put(DbHandler.C_BAG_DEST_CONTACT, "0129991111");
                values.put(DbHandler.C_BAG_DEST_HUBCODE, "1423");
                values.put(DbHandler.C_BAG_DEST_HUBNAME, "Hamburger Hill");
                values.put(DbHandler.C_BAG_DEST_LAT, "-18.1959521");
                values.put(DbHandler.C_BAG_DEST_LONG, "33.5121201");
                values.put(DbHandler.C_BAG_DEST_SUBURB, "Brakpan");
                //values.put(DbHandler.C_BAG_DEST_TOWN, "Saigon");
                values.put(DbHandler.C_BAG_BARCODE, "XMRDX68321");
                values.put(DbHandler.C_BAG_ASSIGNED, 1);
                values.put(DbHandler.C_BAG_SCANNED, 0);
                values.put(DbHandler.C_BAG_CREATION_TIME, "200844B Feb 2014");
                values.put(DbHandler.C_BAG_NUM_ITEMS, "2");
                values.put(DbHandler.C_BAG_DRIVER_ID, VariableManager.TRAININGRUN_MILKRUN_DRIVERID);
                values.put(DbHandler.C_BAG_STATUS, Bag.STATUS_TODO);

                Log.d(TAG,
                        "Added trainingrun bagid: 101 "
                                + DbHandler.getInstance(getActivity()).addRow(
                                DbHandler.TABLE_BAGS_TRAINING, values));

                values = new ContentValues();

                values.put(DbHandler.C_WAYBILL_ID, "123"); // PK
//				values.put(DbHandler.C_WAYBILL_PARCELCOUNT, 1);
                values.put(DbHandler.C_wAYBILL_PARCEL_SEQUENCE, 1 + " of " + 1);
                values.put(DbHandler.C_WAYBILL_DIMEN, "102x13x40cm");
                values.put(DbHandler.C_WAYBILL_CUSTOMER_CONTACT1, "0123341122");
                values.put(DbHandler.C_WAYBILL_CUSTOMER_CONTACT2, "0329987765");
                values.put(DbHandler.C_WAYBILL_CUSTOMER_NAME, "Stone Cold Steve Austin");
                values.put(DbHandler.C_WAYBILL_CUSTOMER_ID, "8712109483123");
                values.put(DbHandler.C_WAYBILL_CUSTOMER_EMAIL, "hulkhogan@wwf.com");
                values.put(DbHandler.C_WAYBILL_BARCODE, "237ASD234X");
                values.put(DbHandler.C_WAYBILL_WEIGHT, "140kg");
                values.put(DbHandler.C_WAYBILL_BAG_ID, "100"); // FK

                DbHandler.getInstance(getActivity()).addRow(DbHandler.TABLE_WAYBILLS_TRAINING,
                        values);

                values = new ContentValues();

                values.put(DbHandler.C_WAYBILL_ID, "321"); // PK
//				values.put(DbHandler.C_WAYBILL_PARCELCOUNT, 2);
                values.put(DbHandler.C_wAYBILL_PARCEL_SEQUENCE, 1 + " of " + 2);
                values.put(DbHandler.C_WAYBILL_DIMEN, "54x131x32cm");
                values.put(DbHandler.C_WAYBILL_CUSTOMER_CONTACT1, "0123341122");
                values.put(DbHandler.C_WAYBILL_CUSTOMER_CONTACT2, "0329987765");
                values.put(DbHandler.C_WAYBILL_CUSTOMER_NAME, "Booker DeWitt");
                values.put(DbHandler.C_WAYBILL_CUSTOMER_ID, "7523201032");
                values.put(DbHandler.C_WAYBILL_CUSTOMER_EMAIL, "ad@columbia.com");
                values.put(DbHandler.C_WAYBILL_BARCODE, "ASF3532DS");
                values.put(DbHandler.C_WAYBILL_WEIGHT, "51kg");
                values.put(DbHandler.C_WAYBILL_BAG_ID, "101"); // FK

                DbHandler.getInstance(getActivity()).addRow(DbHandler.TABLE_WAYBILLS_TRAINING,
                        values);

                values = new ContentValues();

                values.put(DbHandler.C_WAYBILL_ID, "322"); // PK
                values.put(DbHandler.C_WAYBILL_PARCELCOUNT, 2);
                values.put(DbHandler.C_wAYBILL_PARCEL_SEQUENCE, 2 + " of " + 2);
                values.put(DbHandler.C_WAYBILL_DIMEN, "54x131x32cm");
                values.put(DbHandler.C_WAYBILL_CUSTOMER_CONTACT1, "0123341122");
                values.put(DbHandler.C_WAYBILL_CUSTOMER_CONTACT2, "0329987765");
                values.put(DbHandler.C_WAYBILL_CUSTOMER_NAME, "Booker DeWitt");
                values.put(DbHandler.C_WAYBILL_CUSTOMER_ID, "7523201032");
                values.put(DbHandler.C_WAYBILL_CUSTOMER_EMAIL, "ad@columbia.com");
                values.put(DbHandler.C_WAYBILL_BARCODE, "ASF3532DD");
                values.put(DbHandler.C_WAYBILL_WEIGHT, "51kg");
                values.put(DbHandler.C_WAYBILL_BAG_ID, "101"); // FK

                DbHandler.getInstance(getActivity()).addRow(DbHandler.TABLE_WAYBILLS_TRAINING,
                        values);

                values = new ContentValues();

                values.put(DbHandler.C_FAILED_HANDOVER_REASONS_ID, "9");
                values.put(DbHandler.C_FAILED_HANDOVER_REASONS_NAME, "Manager not on duty");

                DbHandler.getInstance(getActivity()).addRow(
                        DbHandler.TABLE_FAILED_HANDOVER_REASONS_TRAINING, values);

                values = new ContentValues();

                values.put(DbHandler.C_FAILED_HANDOVER_REASONS_ID, "3");
                values.put(DbHandler.C_FAILED_HANDOVER_REASONS_NAME, "Branch closed");

                DbHandler.getInstance(getActivity()).addRow(
                        DbHandler.TABLE_FAILED_HANDOVER_REASONS_TRAINING, values);

                // TODO: Implement training runs
                // Start scan activity
                Intent intent = new Intent(getActivity(), ScanActivity.class);

				/*intent.putExtra(VariableManager.EXTRA_DRIVER_ID,
                        VariableManager.TRAININGRUN_MILKRUN_DRIVERID);*/

                startActivity(intent);
            }
        });

    }

    public void initViewHolder(LayoutInflater inflater, ViewGroup container) {

        if (rootView == null) {

            rootView = inflater.inflate(R.layout.fragment_home, null, false);

            if (holder == null) {
                holder = new ViewHolder();
            }

            Typeface typeface_robotoBold = Typeface.createFromAsset(getActivity().getAssets(),
                    FontHelper.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
                            FontHelper.STYLE_BOLD));

            holder.button_milkrun = (Button) rootView.findViewById(R.id.button_home_milkrun);
            holder.button_training_run = (Button) rootView.findViewById(R.id.button_home_training);

            holder.button_milkrun.setTypeface(typeface_robotoBold);
            holder.button_training_run.setTypeface(typeface_robotoBold);
            holder.button_training_run.setVisibility(View.GONE);

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
        Button button_milkrun;
        Button button_training_run;
    }

    /**
     * Retrieve list of bags from API in background
     *
     * @author greg
     */
    private class RetrieveBagsTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog_progress = new ProgressDialog(getActivity());

        /** progress dialog to show user that the backup is processing. */
        /**
         * application context.
         */
        @Override
        protected void onPreExecute() {
            this.dialog_progress.setMessage("Retrieving consignments");
            this.dialog_progress.show();
        }

        @Override
        protected Void doInBackground(Void... urls) {
            SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences(
                    VariableManager.PREF, Context.MODE_PRIVATE);

            final String driverid = prefs.getString(VariableManager.PREF_DRIVERID, null);

            ServerInterface.getInstance(getActivity().getApplicationContext()).downloadBags(
                    getActivity().getApplicationContext(), driverid);
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            // Close progress spinner
            if (dialog_progress.isShowing()) {
                dialog_progress.dismiss();
            }
            // Start scan activity
            Intent intent = new Intent(getActivity(), ScanActivity.class);

            // Pass driver name on
            intent.putExtra(VariableManager.EXTRA_DRIVER,
                    getActivity().getIntent().getStringExtra(VariableManager.EXTRA_DRIVER));

			/*intent.putExtra(VariableManager.EXTRA_DRIVER_ID, getActivity().getIntent()
					.getStringExtra(VariableManager.EXTRA_DRIVER_ID));*/

            startActivity(intent);
        }
    }

}
