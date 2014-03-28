package com.mrdexpress.paperless;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import com.mrdexpress.paperless.adapters.UserAutoCompleteAdapter;
import com.mrdexpress.paperless.db.Users;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.widget.CustomToast;

import java.util.ArrayList;

public class LoginActivity extends Activity
{
    public static String MANAGER_AUTH_SUCCESS = "com.mrdexpress.paperless.LoginActivity.auth_success";
    private final String TAG = "LoginActivity";
    private ViewHolder holder;
    private View rootView;
    ArrayList<Users.UserData> person_item_list;
    private Users.UserData selectedUser;
    private Activity globalthis = this;

	//Fragment fragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_login_screen);

        initViewHolder();
	}

    public void onResume() {
        super.onResume();

        person_item_list = Users.getInstance().managersList;

        UserAutoCompleteAdapter adapter = new UserAutoCompleteAdapter(this.getApplicationContext(), person_item_list);

        // Set the adapter
        holder.text_manager_name.setAdapter(adapter);
        holder.text_manager_name.setThreshold(1);

        holder.text_manager_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectedUser = ((Users.UserData) holder.text_manager_name.getAdapter().getItem( position));

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(holder.text_manager_name.getWindowToken(), 0);
            }
        });

        holder.button_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //new ManagerLoginUserTask().execute();
                if ( true) {
                    Users.getInstance().setActiveManager(selectedUser);
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    CustomToast toast = new CustomToast( globalthis);
                    toast.setText(getString(R.string.text_unauthorised));
                    toast.setSuccess(false);
                    toast.show();
                }
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

            Typeface typeface_roboto_bold = Typeface.createFromAsset(getAssets(),
                    FontHelper.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
                            FontHelper.STYLE_BOLD));

            Typeface typeface_roboto_regular = Typeface.createFromAsset(getAssets(),
                    FontHelper.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
                            FontHelper.STYLE_REGULAR));

            holder.text_manager_name = (AutoCompleteTextView) rootView.findViewById(R.id.text_login_screen_name);
            holder.text_manager_pin = (TextView) rootView.findViewById(R.id.text_login_screen_password);
            //holder.text_manager_pin.setVisibility(View.GONE);
            holder.button_login = (Button) rootView.findViewById(R.id.button_login_screen);

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
    }

    // Creates static instances of resources.
    // Increases performance by only finding and inflating resources only once.
    static class ViewHolder {
        AutoCompleteTextView text_manager_name;
        TextView text_manager_pin;
        Button button_login;
    }

}
