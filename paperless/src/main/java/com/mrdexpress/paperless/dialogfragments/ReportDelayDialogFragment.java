package com.mrdexpress.paperless.dialogfragments;

import android.app.*;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.adapters.GenericDialogListAdapter;
import com.mrdexpress.paperless.datatype.DialogDataObject;
import com.mrdexpress.paperless.db.Device;
import com.mrdexpress.paperless.db.General;
import com.mrdexpress.paperless.db.Users;
import com.mrdexpress.paperless.fragments.DelayDialog;
import com.mrdexpress.paperless.fragments.ViewDeliveriesFragment;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.widget.CustomToast;
import com.mrdexpress.paperless.workflow.Workflow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReportDelayDialogFragment extends DialogFragment
{
    private final String TAG = "ReportDelayActivity";
    private ViewHolder holder;
    private View rootView;
    private GenericDialogListAdapter adapter;
    private String delay_id;
    private Dialog diag = null;

    DialogFragment newFragment;
    TextView subText;
    private int parentItemPosition;

    public ReportDelayDialogFragment( CallBackFunction _callback) {
        callback = _callback;
    }

    private static CallBackFunction callback;

    public static ReportDelayDialogFragment newInstance(final CallBackFunction callback)
    {
        ReportDelayDialogFragment f = new ReportDelayDialogFragment( callback);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity() , R.style.Dialog_No_Border);
        diag = dialog;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        initViewHolder(inflater, container); // Inflate ViewHolder static instance
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new GenericDialogListAdapter(getActivity(), Workflow.getInstance().getMilkrunDelayReasons(), false);

        if ((adapter != null) & (holder.list != null))
        {
            holder.list.setAdapter(adapter);
        }

        holder.closeDialogButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        holder.list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                parentItemPosition = position;// (Integer) getListAdapter().getItem(position);

                FragmentManager fm = getActivity().getFragmentManager();

                if (holder.list.getItemAtPosition(position) != null)
                {
                    // Cursor c = (Cursor) getListView().getItemAtPosition(position);
                    // String delay_id = c.getString(c.getColumnIndex(DbHandler.C_DELAYS_ID));
                    delay_id = ((DialogDataObject) holder.list.getItemAtPosition(position)).getThirdText();

                    ArrayList<DialogDataObject> delay_reasons = Workflow.getInstance().getMilkrunDelayDurations( delay_id);

                    Device.getInstance().setDelay_id(delay_id);

                    if (delay_reasons.size() > 0) {
                        DelayDialog.newInstance(delay_id, new CallBackFunction() {
                            @Override
                            public boolean execute(Object args) {
                                Intent i = (Intent) args;
                                ((DialogDataObject) adapter.getItem(parentItemPosition)).setSubText(i.getStringExtra(DelayDialog.DIALOG_TIME_STRING));

                                delay_id = i.getStringExtra(VariableManager.EXTRA_DELAY_ID);

                                Device.getInstance().setDelay_id(delay_id);

                                //holder.report_button.setVisibility(View.VISIBLE);
                                holder.report_button.setBackgroundResource(R.drawable.button_custom);
                                holder.list.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                holder.report_button.setEnabled(true);

                                return false;
                            }
                        }).show(fm, getTag());
                    } else {
                        holder.report_button.setBackgroundResource(R.drawable.button_custom);
                        Device.getInstance().setDelay_id(delay_id);
                        holder.list.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        holder.report_button.setEnabled(true);
                    }
                    //editNameDialog.setTargetFragment( getFragmentManager().findFragmentById( R.id.activity_report_delay_container), 1);
                    //editNameDialog.setTargetFragment(  getFragmentManager().findFragmentById( R.id.activity_report_delay_container), 1);
                    //editNameDialog.show(fm, "reportDelayFragment");
                }
            }
        });

        holder.report_button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                // Only perform action if there is a selection made
                if (delay_id != null)
                {
                    try{
                        new ReportDelayTask().execute( Workflow.getInstance().doormat.get(MoreDialogFragment.MORE_BAGID).toString() , Integer.toString( Users.getInstance().getActiveDriver().getid()), delay_id);
                        callback.execute(true);
                    }catch(Exception e){
                        Log.e("MRD-EX" , e.getMessage());
                    }
                }
            }
        });
        //holder.report_button.setVisibility(View.INVISIBLE);
        holder.report_button.setBackgroundResource(R.drawable.button_custom_grey);
        holder.report_button.setEnabled(false);
    }

    public void onResume()
    {
        super.onResume();
    }

    private class ReportDelayTask extends AsyncTask<String, Void, String>
    {

        private ProgressDialog dialog = new ProgressDialog(getActivity());

        /** progress dialog to show user that the backup is processing. */
        /** application context. */
        @Override
        protected void onPreExecute()
        {
            this.dialog.setMessage("Submitting delay report");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... args)
        {
            // TODO: Add com log here : Implemented
            Calendar c = Calendar.getInstance();
            Date datetime = c.getTime();
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            String todate = df.format("yyyy-MM-dd hh:mm:ss", datetime).toString();


            String note = "" + ((DialogDataObject) holder.list.getItemAtPosition(parentItemPosition)).getSubText()
                    + " : "
                    + ((DialogDataObject) holder.list.getItemAtPosition(parentItemPosition))
                    .getMainText() + "";

            General.getInstance().AddComLog( new General.Communications(todate , note , "N") , General.getInstance().getActivebagid());
            try{
                ServerInterface.getInstance().postDelay(args[0], note , args[2]);
            }catch(Exception e){
                Log.e("MRD-EX" , e.getMessage());
            }
            return "success";
        }

        @Override
        protected void onPostExecute(String result)
        {
            // Close progress spinner
            if (dialog.isShowing())
            {
                dialog.dismiss();
            }
            Log.i(TAG, result);
            delay_id = null;

            Device.getInstance().displayInfo("Delay Logged");

            diag.dismiss();
            getDialog().getWindow().closeAllPanels();

            /*FragmentManager fm = getFragmentManager();
            Fragment viewDeliveriesFragment = new ViewDeliveriesFragment();
            fm.beginTransaction().replace(R.id.activity_home_container, viewDeliveriesFragment).commit();*/
        }
    }

    public void initViewHolder(LayoutInflater inflater, ViewGroup container)
    {

        if (rootView == null)
        {
            //rootView = inflater.inflate(R.layout.activity_report_delay, container, false);
            rootView = inflater.inflate(R.layout.fragment_report_delay_content, container, false);

            if (holder == null)
            {
                holder = new ViewHolder();
            }

            TextView bartitle = (TextView)rootView.findViewById(R.id.delayReasons_actiondeliveriesLabel);
            bartitle.setText("Report Delay");

            holder.list = (ListView) rootView.findViewById(R.id.fragment_viewDeliveries_container);
            holder.report_button = (Button) rootView.findViewById(R.id.delayReasons_button_submit_action);
            holder.closeDialogButton = (ImageButton) rootView.findViewById(R.id.button_report_delay_closeButton);
            holder.report_button.setText(getResources().getString(R.string.delivery_more_reportDelay));

            // Store the holder with the view.
            rootView.setTag(holder);

        }
        else
        {
            holder = (ViewHolder) rootView.getTag();

            if ((rootView.getParent() != null) && (rootView.getParent() instanceof ViewGroup))
            {
                ((ViewGroup) rootView.getParent()).removeAllViewsInLayout();
            }
            else
            {
            }
        }
    }

    // Creates static instances of resources.
    // Increases performance by only finding and inflating resources only once.
    static class ViewHolder
    {
        ListView list;
        Button report_button;
        ImageButton closeDialogButton;
    }

}
