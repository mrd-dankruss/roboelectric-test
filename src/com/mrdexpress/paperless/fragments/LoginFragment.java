package com.mrdexpress.paperless.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.adapters.UserAutoCompleteAdapter;
import com.mrdexpress.paperless.db.Users;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.security.PinManager;
import com.mrdexpress.paperless.widget.CustomToast;

import java.util.ArrayList;

public class LoginFragment extends Dialog
{
    public static String MANAGER_AUTH_SUCCESS = "com.mrdexpress.paperless.LoginActivity.auth_success";
    private final String TAG = "LoginFragmemt";
    private ViewHolder holder;
    private View rootView;
    ArrayList<Users.UserData> person_item_list;
    private Users.UserData selectedUser;
    private Dialog globalthis = this;
    private Activity context;
    public CallBackFunction callback;

    public LoginFragment(Activity activity)
    {
        super(activity);
        this.context = activity;
    }
	//Fragment fragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_login_screen);

        initViewHolder();

        //this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        person_item_list = Users.getInstance().managersList;

        UserAutoCompleteAdapter adapter = new UserAutoCompleteAdapter( context, person_item_list);

        holder.text_manager_name.setAdapter(adapter);
        holder.text_manager_name.setThreshold(1);

        if( Users.getInstance().getActiveManager() != null){
            holder.text_manager_name.setText( Users.getInstance().getActiveManager().getFullName());
            selectedUser = Users.getInstance().getActiveManager();
            holder.text_manager_pin.requestFocus();
        }

        holder.text_manager_name.dismissDropDown();

        holder.text_manager_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectedUser = ((Users.UserData) holder.text_manager_name.getAdapter().getItem( position));

                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(holder.text_manager_name.getWindowToken(), 0);
            }
        });

        holder.button_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try{
                if( selectedUser != null){
                    //new ManagerLoginUserTask().execute();
                    String pin = selectedUser.getdriverPin();

                    if ( pin != null && pin.equals( PinManager.toMD5( holder.text_manager_pin.getText().toString()))) {
                        Users.getInstance().setActiveManager(selectedUser);
                        if( callback != null)
                            callback.execute( selectedUser);
                        dismiss();
                    } else {
                        CustomToast toast = new CustomToast( context);
                        toast.setText( context.getString(R.string.text_unauthorised));
                        toast.setSuccess(false);
                        toast.show();
                    }
                } else {
                    CustomToast toast = new CustomToast( context);
                    toast.setText( "Select a manager first");
                    toast.setSuccess(false);
                    toast.show();
                }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        holder.close_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void initViewHolder() {

        if (rootView == null) {

            rootView = this.getWindow().getDecorView().findViewById(android.R.id.content);
            //rootView = inflater.inflate(R.layout.fragment_login_screen, null, false);

            if (holder == null) {
                holder = new ViewHolder();
            }

            Typeface typeface_roboto_bold = Typeface.createFromAsset( context.getAssets(),
                    FontHelper.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
                            FontHelper.STYLE_BOLD));

            Typeface typeface_roboto_regular = Typeface.createFromAsset( context.getAssets(),
                    FontHelper.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
                            FontHelper.STYLE_REGULAR));

            holder.text_manager_name = (AutoCompleteTextView) rootView.findViewById(R.id.text_login_screen_name);
            holder.text_manager_pin = (TextView) rootView.findViewById(R.id.text_login_screen_password);
            //holder.text_manager_pin.setVisibility(View.GONE);
            holder.button_login = (Button) rootView.findViewById(R.id.button_login_screen_select_manager);
            holder.close_button = (ImageButton) rootView.findViewById(R.id.button_manager_auth_closeButton);

            holder.text_manager_name.setTypeface(typeface_roboto_regular);
            holder.text_manager_pin.setTypeface(typeface_roboto_regular);
            holder.button_login.setTypeface(typeface_roboto_bold);


            holder.text_manager_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        rootView.setBackgroundColor(Color.TRANSPARENT);
    }

    // Creates static instances of resources.
    // Increases performance by only finding and inflating resources only once.
    static class ViewHolder {
        AutoCompleteTextView text_manager_name;
        TextView text_manager_pin;
        Button button_login;
        ImageButton close_button;
    }

}
