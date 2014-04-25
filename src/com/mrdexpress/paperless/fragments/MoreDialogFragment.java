package com.mrdexpress.paperless.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import com.mrdexpress.paperless.*;
import com.mrdexpress.paperless.datatype.DialogDataObject;
import com.mrdexpress.paperless.db.General;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.widget.CustomToast;
import com.mrdexpress.paperless.workflow.Workflow;
import org.json.JSONObject;

import java.util.ArrayList;

public class MoreDialogFragment extends DialogFragment
{

	private final String TAG = "MoreDialogFragment";
	public static String EXTENDED_DIALOG = "EXTENDED_DIALOG";
    public static String MORE_BAGID = "more_bagid";
	private boolean isExtendedDialaog;
	private static int bagid;
	private Activity activity;

	public interface SetNextDeliveryListener {
        void onSetNextDelivery(boolean is_successful, int bagId);
    }
	
	/**
	 * @param isExtendedDialog
	 *            A boolean option for displaying the normal or extended more dialog box.
	 * @return Returns a new MoreDialogFragment (DialogFragment)
	 */
	public static MoreDialogFragment newInstance(boolean isExtendedDialog)
	{
		MoreDialogFragment f = new MoreDialogFragment();

		Bundle args = new Bundle();
		args.putBoolean(EXTENDED_DIALOG, isExtendedDialog);
		f.setArguments(args);

		return f;
	}

	public static MoreDialogFragment newInstance(boolean isExtendedDialog, int bag_id)
	{
		MoreDialogFragment f = new MoreDialogFragment();

		Bundle args = new Bundle();
		args.putBoolean(EXTENDED_DIALOG, isExtendedDialog);
		f.setArguments(args);

		bagid = bag_id;
        Workflow.getInstance().doormat.put( MORE_BAGID, bagid);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		activity = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_deliveries_more, container, false);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		Bundle args = getArguments();

		if (args != null)
		{
			isExtendedDialaog = args.getBoolean(EXTENDED_DIALOG);
		}
		
		ImageButton closeDialogButton = (ImageButton) v.findViewById(R.id.button_deliveriesMore_closeButton);
		Button setAsNextDelivery = (Button) v.findViewById(R.id.button_deliveriesMore_setAsNextDelivery);
		View setAsNextDeliveryDivider = (View) v.findViewById(R.id.seperator_deliveriesMore_nextDelivery);
		Button viewMapButton = (Button) v.findViewById(R.id.button_deliveriesMore_viewMap);
		Button reportDelayButton = (Button) v.findViewById(R.id.button_deliveriesMore_reportDelay);
		Button callButton = (Button) v.findViewById(R.id.button_deliveriesMore_call);
		Button messageButton = (Button) v.findViewById(R.id.button_deliveriesMore_message);

		if (isExtendedDialaog == true)
		{
			setAsNextDelivery.setVisibility(View.VISIBLE);
			setAsNextDeliveryDivider.setVisibility(View.VISIBLE);
		}

		closeDialogButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});

		setAsNextDelivery.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
                CustomToast custom_toast = new CustomToast(activity);
                custom_toast.setSuccess(true);
                custom_toast.setText("Successfully changed next delivery.");
                custom_toast.show();
                Workflow.getInstance().setNextStop( bagid);
				dismiss();
                getActivity().finish();
			}
		});

		viewMapButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getActivity(), MapActivity.class);

				startActivity(intent);

				dismiss();
			}
		});

		reportDelayButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getActivity(), ReportDelayActivity.class);
                General.getInstance().setActivebagid(bagid);
				startActivity(intent);
				dismiss();
			}
		});

		callButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
                if (Workflow.getInstance().getContactsFromBagId(bagid).size() > 0){
                    Intent intent = new Intent(getActivity(), CallActivity.class);
                    General.getInstance().setActivebagid(bagid);
                    startActivity(intent);
                    dismiss();
                }
                else
                {
                    CustomToast custom_toast = new CustomToast(getActivity());
                    custom_toast.setSuccess(false);
                    custom_toast.setText("No contact numbers available. Report issue your manager.");
                    custom_toast.show();
                }
			}
		});

		messageButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
                if (Workflow.getInstance().getContactsFromBagId(bagid).size() > 0){
                    General.getInstance().setActivebagid(bagid);
                    Intent intent = new Intent(getActivity(), SmsActivity.class);
                    startActivity(intent);
                    dismiss();
                }
                else
                {
                    CustomToast custom_toast = new CustomToast(getActivity());
                    custom_toast.setSuccess(false);
                    custom_toast.setText("No contact numbers available. Report issue your manager.");
                    custom_toast.show();
                }
			}
		});

		return v;
	}
}