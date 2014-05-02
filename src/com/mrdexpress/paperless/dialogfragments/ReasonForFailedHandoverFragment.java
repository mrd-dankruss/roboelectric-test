package com.mrdexpress.paperless.dialogfragments;

import android.app.Dialog;
import android.os.Bundle;
import android.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.mrdexpress.paperless.*;
import com.mrdexpress.paperless.adapters.ReasonForFailedHandoverListAdapter;
import com.mrdexpress.paperless.datatype.DialogDataObject;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.workflow.Workflow;

import java.util.ArrayList;

public class ReasonForFailedHandoverFragment extends DialogFragment
{

	private final String TAG = "ReportDelayActivity";
	private ViewHolder holder;
	private View rootView;
	private ReasonForFailedHandoverListAdapter adapter;
	String delay_id, delay_reason;

	DialogFragment newFragment;
	TextView subText;
	ArrayList<DialogDataObject> values;
	private int parentItemPosition;

    public ReasonForFailedHandoverFragment( CallBackFunction _callback) {
        callback = _callback;
    }

    private static CallBackFunction callback;

    public static ReasonForFailedHandoverFragment newInstance(final CallBackFunction callback)
    {
        ReasonForFailedHandoverFragment f = new ReasonForFailedHandoverFragment( callback);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
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

        values = Workflow.getInstance().getFailedHandoverReasons();

        adapter = new ReasonForFailedHandoverListAdapter(getActivity(), values, false);
        holder.list.setAdapter(adapter);

        holder.list.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                parentItemPosition = position;// (Integer) getListAdapter().getItem(position);

                if (holder.list.getItemAtPosition(position) != null)
                {
                    Log.d("Reason", "ListItem: " + position);
                    setTick(position);
                    delay_id = ((DialogDataObject) holder.list.getItemAtPosition(position)).getSubText();
                    delay_reason = ((DialogDataObject) holder.list.getItemAtPosition(position)).getMainText();
                }
            }
        });

        holder.report_button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                callback.execute(delay_reason);
                dismiss();
            }
        });

        holder.closeDialogButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                callback.execute(null);
                dismiss();
            }
        });
    }

	public void onResume(){
		super.onResume();
	}

	private void setTick(int position)
	{

		values.get(position).setThirdText("true");

		for (int i = 0; i < values.size(); i++)
		{
			if (i != position)
			{
				values.get(i).setThirdText("false");
			}
		}

        holder.report_button.setEnabled(true);
		adapter.notifyDataSetChanged();
	}

	public void initViewHolder(LayoutInflater inflater, ViewGroup container)
	{

		if (rootView == null)
		{

			rootView = inflater.inflate(R.layout.fragment_report_delay_content, null, false);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

            holder.list = (ListView) rootView.findViewById(R.id.fragment_viewDeliveries_container);

            TextView bartitle = (TextView)rootView.findViewById(R.id.delayReasons_actiondeliveriesLabel);
            bartitle.setText("Failed delivery");

            holder.report_button = (Button) rootView.findViewById(R.id.delayReasons_button_submit_action);
            holder.closeDialogButton = (ImageButton) rootView.findViewById(R.id.button_report_delay_closeButton);
            holder.report_button.setText("Report");
            holder.report_button.setEnabled(false);

            //holder.report_button.setBackgroundColor(Color.GRAY);
            holder.report_button.setVisibility( View.VISIBLE );

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
