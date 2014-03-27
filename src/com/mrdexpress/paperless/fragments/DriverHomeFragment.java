package com.mrdexpress.paperless.fragments;

import android.app.Activity;
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
import com.androidquery.AQuery;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.ScanActivity;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.db.Drivers;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;

public class DriverHomeFragment extends Fragment {

    private final String TAG = "HomeFragment";
    private ViewHolder holder;
    private View rootView;
    private SharedPreferences prefs;
    private AQuery aq;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences(VariableManager.PREF, Context.MODE_PRIVATE);
        initViewHolder(inflater, container, savedInstanceState); // Inflate ViewHolder static instance
        aq = new AQuery(getActivity());
        return rootView;
    }

    public void onResume() {
        super.onResume();

        // Reset training run mode

        SharedPreferences.Editor editor = prefs.edit();
        editor.apply();

        holder.button_milkrun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveBagsTask().execute();
            }
        });
    }

    public void initViewHolder(LayoutInflater inflater, ViewGroup container, Bundle bundle) {

        if (rootView == null) {

            rootView = inflater.inflate(R.layout.fragment_home, null, false);

            if (holder == null) {
                holder = new ViewHolder();
                holder.bundle = bundle;
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
        Bundle bundle;
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
            this.dialog_progress.setMessage("Retrieving deliveries");
            this.dialog_progress.show();
        }

        @Override
        protected Void doInBackground(Void... urls) {
            SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences( VariableManager.PREF, Context.MODE_PRIVATE);
            final String driverid = prefs.getString(VariableManager.PREF_DRIVERID, null);
            ServerInterface.getInstance(getActivity().getApplicationContext()).getMilkrunWorkflow( getActivity().getApplicationContext());
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
            Activity a = getActivity();
            startActivity(intent);
        }
    }

}
