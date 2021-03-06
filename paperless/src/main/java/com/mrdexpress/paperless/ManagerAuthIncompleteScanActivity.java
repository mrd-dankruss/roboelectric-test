package com.mrdexpress.paperless;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.mrdexpress.paperless.db.Users;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.ui.ManagerButton;
import com.mrdexpress.paperless.widget.CustomToast;
import com.mrdexpress.paperless.workflow.Workflow;

public class ManagerAuthIncompleteScanActivity extends Activity {

    private ViewHolder holder;
    private View root_view;
    private final String TAG = "ManagerAuthIncompleteScanActivity";

    public static String MANAGER_AUTH_INCOMPLETE_SCAN = "com.mrdexpress.ManagerAuthIncompleteScanActivity.auth_success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_auth_incomplete_scan);

        setTitle(R.string.title_actionbar_manager_auth_not_assigned); // Change actionbar title

        // Initialize ViewHolder
        initViewHolder();

        String barcodes = TextUtils.join("\n", Workflow.getInstance().getBagBarcodesUnscanned());

        holder.text_list.setText(barcodes);

        initClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manager_auth_incomplete_scan, menu);
        return true;
    }

    /**
     * Initiate click listeners for buttons.
     */
    private void initClickListeners() {
        holder.button_continue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //String status = ServerInterface.getInstance(getApplicationContext()).authManager( Integer.toString( Users.getInstance().getActiveManager().getid()), Integer.toString( Users.getInstance().getActiveDriver().getid()), "", Device.getInstance().getIMEI());
                // TODO: wire this back in
                String status = "success";
                if (status.equals("success")) {
                    Intent intent = new Intent();
                    intent.putExtra(MANAGER_AUTH_INCOMPLETE_SCAN, true);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {

                }
            }
        });
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

            holder.button_continue = (ManagerButton) root_view
                    .findViewById(R.id.button_incomplete_scan_activity_continue);
            holder.text_content = (TextView) root_view
                    .findViewById(R.id.textView_incomplete_scan_activity_heading);
            holder.text_list = (TextView) root_view
                    .findViewById(R.id.textView_incomplete_scan_activity_list);

            holder.button_continue.setTypeface(typeface_roboto_bold);
            holder.text_content.setTypeface(typeface_roboto_regular);

            holder.button_continue.setBackgroundResource(R.drawable.button_custom);
            holder.button_continue.setEnabled(true);

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
        ManagerButton button_continue;
        TextView text_content, text_list;
    }
}
