package com.mrdexpress.paperless;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.security.PinManager;
import com.mrdexpress.paperless.widget.CustomToast;

import java.lang.ref.WeakReference;

public class ManagerAuthNotAssignedActivity extends Activity {

    private ViewHolder holder;
    private View root_view;
    private final String TAG = "ManagerAuthNotAssignedActivity";
    private ProgressDialog dialog_login;
    private String imei_id;
    private String last_logged_in_manager_name;
    private String last_logged_in_manager_id;

    public static String MANAGER_AUTH_SUCCESS = "fi.gfarr.mrd.ManagerAuthNotAssignedActivity.auth_success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_auth_not_assigned);

        setTitle(R.string.title_actionbar_manager_auth_not_assigned); // Change actionbar title

        // Initialize ViewHolder
        initViewHolder();

        TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei_id = mngr.getDeviceId();

        SharedPreferences prefs = getSharedPreferences(VariableManager.PREF, Context.MODE_PRIVATE);

        final String driverid = prefs.getString(VariableManager.PREF_DRIVERID, null);

        // Heading
        holder.text_content.setText("Assigning consignment "
                + getIntent().getStringExtra(VariableManager.EXTRA_BAGID) + " to "
                + DbHandler.getInstance(getApplicationContext()).getDriverName(driverid));

        initClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences(VariableManager.PREF, Context.MODE_PRIVATE);

        last_logged_in_manager_name = prefs.getString(VariableManager.LAST_LOGGED_IN_MANAGER_NAME,
                null);
        last_logged_in_manager_id = prefs
                .getString(VariableManager.LAST_LOGGED_IN_MANAGER_ID, null);

        // Display name of manager
        holder.text_name.setText(last_logged_in_manager_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manager_auth_incomplete_scan, menu);
        return true;
    }

    /**
     * Initiate click listeners for buttons.
     */
    private void initClickListeners() {
        holder.button_continue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                login();
            }
        });

        holder.button_change_manager.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                // startActivity(intent);
                startActivityForResult(intent, ScanActivity.RESULT_MANAGER_AUTH);
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ScanActivity.RESULT_MANAGER_AUTH) {
            if (resultCode == RESULT_OK) {
                if (data.getBooleanExtra(ManagerAuthNotAssignedActivity.MANAGER_AUTH_SUCCESS, false)) {
                    Intent intent = new Intent();
                    intent.putExtra(ManagerAuthNotAssignedActivity.MANAGER_AUTH_SUCCESS, true);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        }
    }

    /**
     * Perform log in procedure. First check validity of PIN, then wait for API call to finish and
     * start next activity.
     */
    public void login() {
        class ActivityThread extends Thread {
            public Activity activity = null;
        }

        if (checkPin()) {
            // Progress spinner
            dialog_login = new ProgressDialog(ManagerAuthNotAssignedActivity.this);
            dialog_login.setMessage("Authenticating");
            dialog_login.show();

			/*
             * Make API call authenticating driver credentials in a thread.
			 * When finished, send msg to thread handler to start ScanActivity
			 */
            final MyHandler handler = new MyHandler(this) {
                @Override
                public void handleMessage(Message msg) {
                    dialog_login.hide();
                    displayToast(msg.obj.toString());
                }
            };
            ActivityThread t = new ActivityThread() {
                @Override
                public void run() {
                    SharedPreferences prefs = getSharedPreferences(VariableManager.PREF,
                            Context.MODE_PRIVATE);
                    final String driver_id = prefs.getString(VariableManager.PREF_DRIVERID, null);
                    String status = "";

                    //String hash; //PinManager.toMD5(holder.editText_pin.getText().toString());
                    String hash = holder.editText_pin.getText().toString(); // DEBUG
                    status = ServerInterface.getInstance(getApplicationContext()).authManager(
                            last_logged_in_manager_id, driver_id, hash, imei_id);
                    if (status.equals("success")) {
                        // handler.sendEmptyMessage(0);
                        Intent intent = new Intent();
                        intent.putExtra(MANAGER_AUTH_SUCCESS, true);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else {
                        Message msg = handler.obtainMessage();
                        msg.obj = getApplicationContext().getString(R.string.manager_login_wrong_password);
                        handler.sendMessage(msg);
                    }
                }
            };
            t.activity = this;
            t.start();
        }
    }

    /**
     * Check PIN's validity (data validation)
     *
     * @return True is valid.
     */
    private boolean checkPin() {
        // Check for 4-digit format
        String msg = PinManager.checkPin(holder.editText_pin.getText().toString(), this);
        if (msg.equals("OK")) {
            return true;
        } else {
            displayToast(msg);
            return false;
        }
    }

    /**
     * Display a toast using the custom Toaster class
     *
     * @param msg
     */
    private void displayToast(String msg) {
        CustomToast toast = new CustomToast(this);
        toast.setText(msg);
        toast.setSuccess(false);
        toast.show();
    }

    /**
     * Custom Handler class that waits for the user authentication API call to complete
     * before continuing. This class uses weak references to alleviate the HandlerLeak error.
     *
     * @author greg
     */
    static class MyHandler extends Handler {
        private WeakReference<ManagerAuthNotAssignedActivity> mActivity;

        MyHandler(ManagerAuthNotAssignedActivity activity) {
            mActivity = new WeakReference<ManagerAuthNotAssignedActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ManagerAuthNotAssignedActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    /**
     * Starts the barcode scan activity after user authentication thread has completed.
     *
     * @param msg
     */
    public void handleMessage(Message msg) {
        if (msg.what == 0) {
            Intent intent = new Intent(getApplicationContext(),
                    ViewDeliveriesFragmentActivity.class);
            // intent.putExtra(EXTRA_MESSAGE, message);

            startActivity(intent);

            // Close progress spinner
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (dialog_login.isShowing()) dialog_login.dismiss();
                }
            });

            // startActivity(intent);
        }
    }

    /**
     * Allows the views' resources to be found only once, improving performance.
     */
    public void initViewHolder() {
        if (root_view == null) {
            root_view = this.getWindow().getDecorView().findViewById(android.R.id.content);

            if (holder == null) {
                holder = new ViewHolder();
            }

            Typeface typeface_roboto_bold = Typeface.createFromAsset(getAssets(), FontHelper
                    .getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
                            FontHelper.STYLE_BOLD));
            Typeface typeface_roboto_regular = Typeface.createFromAsset(getAssets(), FontHelper
                    .getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
                            FontHelper.STYLE_REGULAR));

            holder.button_continue = (Button) root_view
                    .findViewById(R.id.button_not_assigned_activity_continue);
            holder.button_change_manager = (Button) root_view
                    .findViewById(R.id.button_not_assigned_activity_change);
            holder.editText_pin = (EditText) root_view
                    .findViewById(R.id.editText_not_assigned_activity_pin);
            holder.text_name = (TextView) root_view
                    .findViewById(R.id.text_not_assigned_activity_name);
            holder.text_content = (TextView) root_view
                    .findViewById(R.id.textView_not_assigned_activity_heading);

            holder.button_continue.setTypeface(typeface_roboto_bold);
            holder.button_change_manager.setTypeface(typeface_roboto_bold);
            holder.editText_pin.setTypeface(typeface_roboto_regular);
            holder.text_name.setTypeface(typeface_roboto_bold);
            holder.text_content.setTypeface(typeface_roboto_regular);

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

    // ViewHolder stores static instances of views in order to reduce the number
    // of times that findViewById is called, which affected listview performance
    static class ViewHolder {
        Button button_continue, button_change_manager;
        TextView text_name, text_content;
        EditText editText_pin;
    }
}
