package com.mrdexpress.paperless.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mrdexpress.paperless.LoginActivity;
import com.mrdexpress.paperless.ManagerAuthActivity;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.ScanActivity;
import com.mrdexpress.paperless.adapters.UserAutoCompleteAdapter;
import com.mrdexpress.paperless.datatype.UserItem;
import com.mrdexpress.paperless.datatype.UserItem.UserType;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.db.Users;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.security.PinManager;
import com.mrdexpress.paperless.widget.CustomToast;

public class LoginFragment extends Fragment {

    private final String TAG = "LoginFragment";
    private ViewHolder holder;
    private View rootView;
    ArrayList<Users.UserData> person_item_list;
    private Users.UserData selectedUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initViewHolder(inflater, container); // Inflate ViewHolder static instance

        return rootView;
    }

    public void onResume() {
        super.onResume();

        person_item_list = Users.getInstance().managersList;

        UserAutoCompleteAdapter adapter = new UserAutoCompleteAdapter(getActivity().getApplicationContext(), person_item_list);

        // Set the adapter
        holder.text_manager_name.setAdapter(adapter);
        holder.text_manager_name.setThreshold(1);

        holder.text_manager_name.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectedUser = ((Users.UserData) holder.text_manager_name.getAdapter().getItem( position));

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService( Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(holder.text_manager_name.getWindowToken(), 0);
            }
        });

        holder.button_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //new ManagerLoginUserTask().execute();
                if ( true) {
                    Users.getInstance().setActiveManager(selectedUser);
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                } else {
                    CustomToast toast = new CustomToast(getActivity());
                    toast.setText(getString(R.string.text_unauthorised));
                    toast.setSuccess(false);
                    toast.show();
                }
            }
        });
    }

	/*
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		((DialogDataObject) adapter.getItem(parentItemPosition)).setThirdText(data
				.getStringExtra(SMSDialog.DIALOG_TIME_STRING));

		// VariableManager.delay_id = data.getStringExtra(VariableManager.EXTRA_DELAY_ID);

		// holder.report_button.setVisibility(View.VISIBLE);
		holder.report_button.setBackgroundResource(R.drawable.button_custom);
		holder.list.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		holder.report_button.setEnabled(true);
	}
	*/

    public void initViewHolder(LayoutInflater inflater, ViewGroup container) {

        if (rootView == null) {

            rootView = inflater.inflate(R.layout.fragment_login_screen, null, false);

            if (holder == null) {
                holder = new ViewHolder();
            }

            Typeface typeface_roboto_bold = Typeface.createFromAsset(getActivity().getAssets(),
                    FontHelper.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
                            FontHelper.STYLE_BOLD));

            Typeface typeface_roboto_regular = Typeface.createFromAsset(getActivity().getAssets(),
                    FontHelper.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
                            FontHelper.STYLE_REGULAR));

            holder.text_manager_name = (AutoCompleteTextView) rootView.findViewById(R.id.text_login_screen_name);
            holder.text_manager_pin = (TextView) rootView.findViewById(R.id.text_login_screen_password);
            //holder.text_manager_pin.setVisibility(View.GONE);
            holder.button_login = (Button) rootView.findViewById(R.id.button_login_screen);

            holder.text_manager_name.setTypeface(typeface_roboto_regular);
            holder.text_manager_pin.setTypeface(typeface_roboto_regular);
            holder.button_login.setTypeface(typeface_roboto_bold);


            holder.text_manager_name.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    selectedUser = ((Users.UserData) holder.text_manager_name.getAdapter().getItem(position));
                    }
                });

            }
            else
        {
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
        AutoCompleteTextView text_manager_name;
        TextView text_manager_pin;
        Button button_login;
    }

}
