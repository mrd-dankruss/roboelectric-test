package com.mrdexpress.paperless;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.fragments.ScanFragment;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.security.PinManager;
import com.mrdexpress.paperless.widget.Toaster;

import java.lang.ref.WeakReference;

public class EnterPinActivity extends Activity {

    private final String TAG = "EnterPinActivity";
    private ViewHolder holder;
    private View root_view;
    private EnterPinActivity context;
    String imei_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pin);

        context = this;

        // Change actionbar title
        setTitle(R.string.title_actionbar_enter_pin);

        // Inflate views
        initViewHolder();

        TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei_id = mngr.getDeviceId();

        // Obtain selected driver's name passed from parent activity and display
        holder.textView_driver.setText(getIntent().getStringExtra(VariableManager.EXTRA_DRIVER));

        // button click
        // Click create button
        holder.button_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });
        // Click change driver button
        holder.button_change.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        holder.editText_pin.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    login();
                    return true;
                }
                return false;
            }
        });

        // Listen for text changes in PIN field
        TextWatcher onPinFieldChangedListener = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // your business logic after text is changed
                if (holder.editText_pin.getText().toString().equals("")) {
                    holder.button_login.setBackgroundResource(R.drawable.button_custom_grey);
                    holder.button_login.setTextColor(getResources().getColor(
                            R.color.colour_text_lightgrey));
                } else {
                    holder.button_login.setBackgroundResource(R.drawable.button_custom);
                    holder.button_login.setTextColor(Color.BLACK);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // your business logic before text is changed
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // your business logic while text has changed
            }
        };
        holder.editText_pin.addTextChangedListener(onPinFieldChangedListener);
    }

    /**
     * Perform log in procedure. First check validity of PIN, then wait for API call to finish and
     * start next activity.
     */
    public void login() {

        if (checkPin()) {
            // Progress spinner
            final ProgressDialog dialog = new ProgressDialog(EnterPinActivity.this);
            dialog.setMessage("Authenticating");
            dialog.show();

			/*
             * Make API call authenticating driver credentials in a thread.
			 * When finished, send msg to thread handler to start ScanFragment
			 * 
			 */
            final MyHandler handler = new MyHandler(this);
            Thread t = new Thread() {
                @Override
                public void run() {

                    String hash = PinManager.toMD5(holder.editText_pin.getText().toString());

                    String status = " ";//ServerInterface.getInstance(getApplicationContext())
                            //.authDriver(hash, imei_id);

                    if (status.equals("success")) {
                        handler.sendEmptyMessage(0);
                    } else {
                        handler.sendEmptyMessage(1);
                    }
                }
            };
            t.start();
        }
		/*
		 * if (holder.editText_pin.getText().toString().equals("1111")) { return
		 * true; } else {
		 * displayToast(getString(R.string.text_enter_pin_incorrect)); return
		 * false; }
		 */

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
        Toaster.displayToast(msg, holder.textView_toast, holder.relativeLayout_toast, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.enter_pin, menu);
        return true;
    }

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

            holder.button_login = (Button) root_view.findViewById(R.id.button_enter_pin_login);
            holder.button_change = (Button) root_view.findViewById(R.id.button_enter_pin_change);
            holder.textView_driver = (TextView) root_view
                    .findViewById(R.id.textView_enter_pin_driver);
            holder.editText_pin = (EditText) root_view.findViewById(R.id.editText_enter_pin);
            holder.textView_toast = (TextView) root_view
                    .findViewById(R.id.textView_enter_pin_toast);
            holder.relativeLayout_toast = (RelativeLayout) root_view
                    .findViewById(R.id.toast_enter_pin);

            holder.button_login.setTypeface(typeface_roboto_bold);
            holder.button_change.setTypeface(typeface_roboto_bold);
            holder.textView_driver.setTypeface(typeface_roboto_bold);
            holder.editText_pin.setTypeface(typeface_roboto_regular);

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

    // Creates static instances of resources.
    // Increases performance by only finding and inflating resources only once.
    static class ViewHolder {
        Button button_login;
        Button button_change;
        TextView textView_driver;
        EditText editText_pin;
        TextView textView_toast;
        RelativeLayout relativeLayout_toast;
    }

    /**
     * Custom Handler class that waits for the user authentication API call to complete
     * before continuing. This class uses weak references to alleviate the HandlerLeak error.
     *
     * @author greg
     */
    static class MyHandler extends Handler {
        private WeakReference<EnterPinActivity> mActivity;

        MyHandler(EnterPinActivity activity) {
            mActivity = new WeakReference<EnterPinActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            EnterPinActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    /**
     * Starts the barcode scan activity after user authenication thread has completed.
     *
     * @param msg
     */
    public void handleMessage(Message msg) {

        if (msg.what == 0) {
            // Retrieve bags for current driver in a thread
            new RetrieveBagsTask().execute();

        }
    }

    /**
     * Retrieve list of bags from API in background
     *
     * @author greg
     */
    private class RetrieveBagsTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog_progress = new ProgressDialog(EnterPinActivity.this);

        /** progress dialog to show user that the backup is processing. */
        /**
         * application context.
         */
        @Override
        protected void onPreExecute() {
            this.dialog_progress.setMessage("Retrieving consignments3");
            this.dialog_progress.show();
        }

        @Override
        protected Void doInBackground(Void... urls) {
            //ServerInterface.getInstance(getApplicationContext()).downloadBags( getApplicationContext(), getIntent().getStringExtra(VariableManager.EXTRA_DRIVER_ID));
            ServerInterface.getInstance(getApplicationContext()).getMilkrunWorkflow( getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            // Close progress spinner
            if (dialog_progress.isShowing()) {
                dialog_progress.dismiss();
            }

            Intent intent = new Intent(getApplicationContext(), ScanFragment.class);

            DbHandler.getInstance(getApplicationContext());
            // Pass driver name on
            intent.putExtra(VariableManager.EXTRA_DRIVER,
                    getIntent().getStringExtra(VariableManager.EXTRA_DRIVER));

            intent.putExtra(VariableManager.EXTRA_DRIVER_ID,
                    getIntent().getStringExtra(VariableManager.EXTRA_DRIVER_ID));

            SharedPreferences prefs = getSharedPreferences(VariableManager.PREF,
                    Context.MODE_PRIVATE);

            prefs.edit().putString(VariableManager.EXTRA_DRIVER_ID, null).commit();

            startActivity(intent);
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

		/*// Close progress spinner
		if (dialog_progress.isShowing())
		{
			dialog_progress.dismiss();
		}*/
    }

}
