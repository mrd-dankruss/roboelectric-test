package fi.gfarr.mrd.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import fi.gfarr.mrd.DeliveryHandoverFragmentActivity;
import fi.gfarr.mrd.R;
import fi.gfarr.mrd.ReasonForFailedHandoverActivity;
import fi.gfarr.mrd.adapters.GenericDialogListAdapter;
import fi.gfarr.mrd.datatype.DialogDataObject;

public class UpdateStatusDialog extends DialogFragment
{
	private static String bagid;
	public static String DIALOG_TIME_STRING = "DIALOG_TIME_STRING";
	public static String DIALOG_ITEM_POS = "DIALOG_ITEM_POS";
	private ArrayList<DialogDataObject> temp;

	/**
	 * Create a new instance of MyDialogFragment, providing "num"
	 * as an argument.
	 */
	public static UpdateStatusDialog newInstance(String bag_id)
	{
		UpdateStatusDialog f = new UpdateStatusDialog();

		// Supply num input as an argument.
		// Bundle args = new Bundle();
		// args.putInt("num", bag_id);
		// f.setArguments(args);
		bagid = bag_id;

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		temp = new ArrayList<DialogDataObject>();
		temp.add(new DialogDataObject(getString(R.string.text_process_handover_successful), ""));
		temp.add(new DialogDataObject(getString(R.string.text_process_handover_failed), ""));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.dialog_generic_result, container, false);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		TextView title = (TextView) v.findViewById(R.id.textView_trafficDelay_title);
		title.setText(R.string.title_updateStatusDialog);

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

		final ListView lv = (ListView) v.findViewById(R.id.list_generic_result);
		GenericDialogListAdapter clad = new GenericDialogListAdapter(getActivity(), temp, true);

		lv.setAdapter(clad);

		lv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				// Successful
				if (position == 0)
				{
					Intent intent = new Intent(getActivity(),
							DeliveryHandoverFragmentActivity.class);
					startActivity(intent);
					dismiss();
				}
				// Failed
				if (position == 1)
				{
					Intent intent = new Intent(getActivity(), ReasonForFailedHandoverActivity.class);
					startActivity(intent);
					dismiss();
				}
			}
		});

		return v;
	}

}