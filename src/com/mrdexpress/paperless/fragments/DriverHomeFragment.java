package com.mrdexpress.paperless.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.mrdexpress.paperless.DriverHomeActivity;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.interfaces.FragmentResultInterface;

public class DriverHomeFragment extends Fragment {

    private final String TAG = "HomeFragment";
    private ViewHolder holder;
    private View rootView;
    private SharedPreferences prefs;

    public interface DriverHomeFragmentInterface{
        public void startScan();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences(VariableManager.PREF, Context.MODE_PRIVATE);
        initViewHolder(inflater, container, savedInstanceState); // Inflate ViewHolder static instance

        return rootView;
    }

    public void onResume() {
        super.onResume();

        // Reset training run mode

        SharedPreferences.Editor editor = prefs.edit();
        editor.apply();
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

            holder.button_milkrun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                ((DriverHomeFragmentInterface)getActivity()).startScan();
                 //getActivity().onBackPressed();
                 //new RetrieveBagsTask().execute();
                }
            });

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
}
