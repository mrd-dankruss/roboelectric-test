package fi.gfarr.mrd.fragments;

import java.util.ArrayList;

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
import fi.gfarr.mrd.adapters.GenericDialogListAdapter;
import fi.gfarr.mrd.datatype.DialogDataObject;
import fi.gfarr.mrd.db.DbHandler;
import fi.gfarr.mrd.helper.VariableManager;

public class SMSDialog extends DialogFragment
{
	private int mNum;
	public static String DIALOG_TIME_STRING = "DIALOG_TIME_STRING";
	public static String DIALOG_ITEM_POS = "DIALOG_ITEM_POS";
	private ArrayList<DialogDataObject> durations;

	// ID of delay reason passed from previous screen
	private static String bag_id;

	/**
	 * Create a new instance of MyDialogFragment, providing "num"
	 * as an argument.
	 * 
	 * @param bag_id
	 *            ID of delay reason passed from calling activity.
	 */
	public static SMSDialog newInstance(String delay_reason_id)
	{
		SMSDialog f = new SMSDialog();

		/*// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);*/

		bag_id = delay_reason_id;

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		durations = DbHandler.getInstance(getActivity()).getSMSMessages();

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
				Log.d("fi.gfarr.mrd", "getTargetRequestCode: " + getTargetRequestCode());
				Log.d("fi.gfarr.mrd", "getTargetFragment: " + getTargetFragment());
				
				
				
				getActivity().getIntent().putExtra(DIALOG_TIME_STRING,
						((DialogDataObject) adapter.getItem(position)).getMainText());
				getActivity().getIntent().putExtra(DIALOG_ITEM_POS, position);
				getActivity().getIntent().putExtra(VariableManager.EXTRA_DELAY_ID, bag_id);
				
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
						getActivity().getIntent());
				dismiss();
			}
		});

		return v;
	}

}