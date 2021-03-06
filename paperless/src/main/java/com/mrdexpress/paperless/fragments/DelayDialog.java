package com.mrdexpress.paperless.fragments;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.adapters.GenericDialogListAdapter;
import com.mrdexpress.paperless.datatype.DialogDataObject;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.workflow.Workflow;

public class DelayDialog extends DialogFragment
{
	public static String DIALOG_TIME_STRING = "DIALOG_TIME_STRING";
	public static String DIALOG_ITEM_POS = "DIALOG_ITEM_POS";
	private int mNum;
//	private ArrayList<DialogDataObject> durations;
	// ID of delay reason passed from previous screen
	private String delay_id;
    private CallBackFunction callback;

    public DelayDialog(String delay_reason_id, CallBackFunction callback){
        this.callback = callback;
        this.delay_id = delay_reason_id;
    }


	/**
	 * Create a new instance of MyDialogFragment, providing "num"
	 * as an argument.
	 * 
	 * @param delay_reason_id
	 *            ID of delay reason passed from calling activity.
	 */
	public static DelayDialog newInstance(String delay_reason_id, CallBackFunction callback)
	{
		DelayDialog f = new DelayDialog( delay_reason_id, callback);

		/*// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);*/

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

	/*	durations = new ArrayList<DialogDataObject>();
		durations.add(new DialogDataObject("5 min", "5 min"));
		durations.add(new DialogDataObject("10 min", "10 min"));
		durations.add(new DialogDataObject("20 min", "20 min"));
		durations.add(new DialogDataObject("30 min", "30 min"));
		durations.add(new DialogDataObject("1 hour", "1 hour"));*/

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.dialog_generic_result, container, false);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		ImageButton closeDialogButton = (ImageButton) v.findViewById(R.id.button_trafficDelay_closeButton);

		closeDialogButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});

		final ListView list = (ListView) v.findViewById(R.id.list_generic_result);

		// Populate list
		//final GenericDialogListAdapter adapter = new GenericDialogListAdapter(getActivity(), DbHandler.getInstance(getActivity()).getMilkrunDelayDurations(delay_id), true);
        final GenericDialogListAdapter adapter = new GenericDialogListAdapter(getActivity(), Workflow.getInstance().getMilkrunDelayDurations( delay_id), true);
		Log.d("Delay", "Generic Adapter: " + adapter.getCount());

		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
                Intent i = new Intent();

				i.putExtra(DIALOG_TIME_STRING,	((DialogDataObject) adapter.getItem(position)).getMainText());
				i.putExtra(DIALOG_ITEM_POS, position);
				i.putExtra(VariableManager.EXTRA_DELAY_ID, delay_id);
                callback.execute(i);
				dismiss();
			}
		});

		return v;
	}

}