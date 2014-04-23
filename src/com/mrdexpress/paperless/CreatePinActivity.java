package com.mrdexpress.paperless;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.db.Users;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.security.PinManager;
import com.mrdexpress.paperless.widget.CustomToast;
import com.mrdexpress.paperless.widget.Toaster;

public class CreatePinActivity extends Activity {

    private final String TAG = "CreatePinActivity";
    public ProgressDialog dialog_main;
    private ViewHolder holder;
    private View root_view;
    private CreatePinActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pin);
        context = this;

        // Change actionbar title
        setTitle("Create PIN: " + Users.getInstance().getActiveDriver().getFullName());

        // Inflate views
        initViewHolder();

        // button click
        // Click create button
        holder.button_create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Check if pin is valid then login
                if (checkPin()) {
                    dialog_main.setMessage("Creating a PIN for " + Users.getInstance().getActiveDriver().getFullName() + " please be patient");
                    try {
                        dialog_main.show();
                    } catch (Exception e) {
                        Log.e("MRD-EX", e.getMessage());
                    }
                    new CreatePINTask().execute();
                }
            }
        });
        // Click change driver button
        holder.button_change.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        dialog_main = new ProgressDialog(context);
    }

    private boolean checkPin() {

        // Check if two pins match
        if (holder.editText_pin1.getText().toString()
                .equals(holder.editText_pin2.getText().toString())) {

            // Check for 4-digit format
            String msg = PinManager.checkPin(holder.editText_pin1.getText().toString(), this);
            if (msg.equals("OK")) {
                return true;
            } else {
                displayToast(msg);
                return false;
            }
        } else {
            // strings do not match
            displayToast(getString(R.string.text_create_pin_mismatch));
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
        getMenuInflater().inflate(R.menu.create_pin, menu);
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

            holder.button_create = (Button) root_view.findViewById(R.id.button_create_pin_create);
            holder.button_change = (Button) root_view.findViewById(R.id.button_create_pin_change_driver);
            holder.editText_pin1 = (EditText) root_view.findViewById(R.id.editText_create_pin_1);
            holder.editText_pin2 = (EditText) root_view.findViewById(R.id.editText_create_pin_2);
            holder.textView_toast = (TextView) root_view.findViewById(R.id.textView_create_pin_toast);
            holder.relativeLayout_toast = (RelativeLayout) root_view.findViewById(R.id.toast_create_pin);

            holder.button_create.setTypeface(typeface_roboto_bold);
            holder.button_change.setTypeface(typeface_roboto_bold);
            holder.editText_pin1.setTypeface(typeface_roboto_regular);
            holder.editText_pin2.setTypeface(typeface_roboto_regular);

            holder.button_create.setBackgroundResource(R.drawable.button_custom);

            // Store the holder with the view.
            root_view.setTag(holder);

            holder.editText_pin1.requestFocus();
            //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

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
        Button button_create;
        Button button_change;
        EditText editText_pin1;
        EditText editText_pin2;
        TextView textView_toast;
        RelativeLayout relativeLayout_toast;
    }

    /**
     * Retrieves the list of drivers from server to populate login list.
     *
     * @author greg
     */
    private class CreatePINTask extends AsyncTask<Void, Void, String> {
        public CallBackFunction cback = new CallBackFunction() {
            @Override
            public boolean execute(Object args) {
                String result = args.toString();
                dialog_main.dismiss();
                try {
                    // PIN creation returns from server as successful
                    if (result.equals("success")) {
                        Users.getInstance().getActiveDriver().setdriverPin(holder.editText_pin1.getText().toString());
                        //Log Driver In Automatically
                        context.finish();
                        Intent intent = new Intent(getApplicationContext(), DriverHomeActivity.class);
                        startActivity(intent);
                    } else {
                        // There was a problem
                        //CustomToast toast = new CustomToast(CreatePinActivity.this);
                        //toast.setText(result);
                        //toast.setSuccess(false);
                        //toast.show();
                        //Toast.makeText(getBaseContext(), "Pin created successfully.", Toast.LENGTH_LONG).show();

                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return true;
            }
        };

        /**
         * The system calls this to perform work in a worker thread and delivers
         * it the parameters given to AsyncTask.execute()
         */
        @Override
        protected String doInBackground(Void... params) {
            final String driverid = Users.getInstance().getActiveDriver().getStringid();
            ServerInterface.getInstance(getApplicationContext()).updatePIN(driverid, holder.editText_pin1.getText().toString(), Users.getInstance().getActiveDriver().getSource(), cback);
            return "";
        }

        /**
         * The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground()
         */
        @Override
        protected void onPostExecute(String result) {
            Log.e("MRD-EX", "PIN DONE");
        }
    }
}
