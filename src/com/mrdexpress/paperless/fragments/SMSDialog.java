package com.mrdexpress.paperless.fragments;

import java.util.ArrayList;

import com.mrdexpress.paperless.adapters.GenericDialogListAdapter;
import com.mrdexpress.paperless.datatype.DialogDataObject;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.helper.VariableManager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import fi.gfarr.mrd.R;

public class SMSDialog extends DialogFragment
{
	private int mNum;
	public static String DIALOG_TIME_STRING = "DIALOG_TIME_STRING";
	public static String DIALOG_MESSAGE = "DIALOG_MESSAGE";
	public static String DIALOG_ITEM_POS = "DIALOG_ITEM_POS";

	/**
	 * Create a new instance of MyDialogFragment, providing "num"
	 * as an argument.
	 * 
	 * @param bagid
	 *            ID of bag passed from calling activity.
	 */
	public static SMSDialog newInstance()
	{
		SMSDialog f = new SMSDialog();

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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.dialog_generic_result, container, false);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		ImageButton closeDialogButton = (ImageButton) v
				.findViewById(R.id.button_trafficDelay_closeButton);

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
		final GenericDialogListAdapter adapter = new GenericDialogListAdapter(getActivity(),
				DbHandler.getInstance(getActivity()).getSMSMessages(), true);

		list.setAdapter(adapter);

		list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				// Log.d("fi.gfarr.mrd", "getTargetRequestCode: " + getTargetRequestCode());
				// Log.d("fi.gfarr.mrd", "getTargetFragment: " + getTargetFragment());

				getActivity().getIntent().putExtra(DIALOG_TIME_STRING,
						((DialogDataObject) adapter.getItem(position)).getSubText());
				getActivity().getIntent().putExtra(DIALOG_MESSAGE,
						((DialogDataObject) adapter.getItem(position)).getThirdText());
				getActivity().getIntent().putExtra(DIALOG_ITEM_POS, position);
				// getActivity().getIntent().putExtra(VariableManager.EXTRA_DELAY_ID, bag_id);

				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
						getActivity().getIntent());
				dismiss();
			}
		});

		return v;
	}

}