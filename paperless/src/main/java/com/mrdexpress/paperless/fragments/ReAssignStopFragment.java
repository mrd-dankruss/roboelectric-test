package com.mrdexpress.paperless.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import com.androidquery.AQuery;
import com.mrdexpress.paperless.Paperless;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.adapters.UserAutoCompleteAdapter;
import com.mrdexpress.paperless.channels.EventBus;
import com.mrdexpress.paperless.db.Device;
import com.mrdexpress.paperless.db.General;
import com.mrdexpress.paperless.db.Users;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.interfaces.FragmentCallBackFunction;

import java.util.ArrayList;

/**
 * Created by hannobean on 2014/05/08.
 */
public class ReAssignStopFragment extends DialogFragment {

    private ViewHolder holder;
    private View root_view;
    private static FragmentCallBackFunction callback;
    private ArrayList<Users.UserData> person_item_list;
    private Users.UserData selected_user;
    private AQuery ac;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try{
        Paperless.getInstance().setMainActivity( getActivity() );
        Paperless.getInstance().ottobus.register(this);
        super.onCreate(savedInstanceState);
        }catch(Exception e){
            Log.e("MRD-EX", e.getMessage());
        }
    }

    public static ReAssignStopFragment newInstance(final FragmentCallBackFunction callback)
    {
        ReAssignStopFragment f = new ReAssignStopFragment( callback);
        return f;
    }

    public ReAssignStopFragment( FragmentCallBackFunction _callback) {
        callback = _callback;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreateView(inflater, container, savedInstanceState);
        initViewHolder(inflater, container);
        return root_view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        person_item_list = Users.getInstance().driversList;

        UserAutoCompleteAdapter adapter = new UserAutoCompleteAdapter( getActivity().getApplicationContext(), person_item_list);

        // Set the adapter
        holder.userselect.setAdapter(adapter);
        holder.userselect.setThreshold(1);

        holder.userselect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selected_user = ((Users.UserData) holder.userselect.getAdapter().getItem(position));
                //Users.getInstance().setActiveDriverIndex(Users.getInstance().driversList.indexOf(selected_user));
            }
        });
        ac = new AQuery(root_view);
        ac.id(R.id.button_reassign_manager_auth).clicked(this, "triggerManager");

        super.onViewCreated(view, savedInstanceState);
    }

    public void triggerManager(){
        if (null != selected_user){
            LoginFragment dialog;
            final View theView = root_view;
            Activity pa = getActivity();
            dialog = new LoginFragment( pa);
            dialog.callback = new CallBackFunction() {
                @Override
                public boolean execute(Object args) {
                    Users.UserData db = selected_user;
                    General.getInstance().setReassigndriverid( selected_user.getStringid() );
                    Paperless.getInstance().ottobus.post(new EventBus.ManagerBackToDriverHome());
                    return false;
                }
            };
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } else {
            Device.getInstance().displayInfo("Please select a driver first");
        }
    }

    public void initViewHolder(LayoutInflater inflater, ViewGroup container){
        root_view = inflater.inflate(R.layout.fragment_reassign_stop, container, false);

        if (holder == null) {
            holder = new ViewHolder();
        }

        holder.userselect = (AutoCompleteTextView) root_view
                .findViewById(R.id.text_reassigned_choose_driver);

        holder.dialog_continue = (Button) root_view.findViewById(R.id.button_reassign_manager_auth);

        Typeface typeface_robotoBold = Typeface.createFromAsset(this.getActivity().getAssets(), FontHelper
                .getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
                        FontHelper.STYLE_BOLD));

        root_view.setTag(holder);

    }

    static class ViewHolder {
        AutoCompleteTextView userselect;
        Button dialog_continue;
    }


}
