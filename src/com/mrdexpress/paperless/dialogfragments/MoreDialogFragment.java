package com.mrdexpress.paperless.dialogfragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import com.mrdexpress.paperless.*;
import com.mrdexpress.paperless.db.Device;
import com.mrdexpress.paperless.db.General;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.widget.CustomToast;
import com.mrdexpress.paperless.workflow.Workflow;

public class MoreDialogFragment extends DialogFragment
{
	public static String EXTENDED_DIALOG = "EXTENDED_DIALOG";
    public static String MORE_BAGID = "more_bagid";
	private final String TAG = "MoreDialogFragment";
	private boolean isExtendedDialaog;
	private String stopids;
    private CallBackFunction callback;
	private Activity activity;

    public MoreDialogFragment(boolean isExtendedDialaog, String stopids, CallBackFunction callback) {
        this.isExtendedDialaog = isExtendedDialaog;
        this.callback = callback;
        this.stopids = stopids;
        Workflow.getInstance().doormat.put( MORE_BAGID, stopids);
    }

	public static MoreDialogFragment newInstance(boolean isExtendedDialog, String _stopids, CallBackFunction _callback)
	{
		MoreDialogFragment f = new MoreDialogFragment( isExtendedDialog, _stopids, _callback);

		Bundle args = new Bundle();
		args.putBoolean(EXTENDED_DIALOG, isExtendedDialog);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		activity = getActivity();
	}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity() , R.style.Dialog_No_Border);
        return dialog;
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
		final Button callButton = (Button) v.findViewById(R.id.button_deliveriesMore_call);
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
                Workflow.getInstance().setNextStop( stopids);
				dismiss();
                callback.execute(true);
                //getActivity().finish();
			}
		});

		viewMapButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
                MapDialogFragment map = MapDialogFragment.newInstance(new CallBackFunction() {
                    @Override
                    public boolean execute(Object args) {
                        return false;
                    }
                });
                Bundle bundle = new Bundle();
                bundle.putString("stopids", stopids);
                map.setArguments(bundle);
                map.show(getActivity().getFragmentManager(), getTag());
			}
		});

		reportDelayButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
                General.activebagid = stopids;
                ReportDelayDialogFragment.newInstance(new CallBackFunction() {
                    @Override
                    public boolean execute(Object args) {
                        return false;
                    }
                }).show(getActivity().getFragmentManager(), getTag());
				/*Intent intent = new Intent(getActivity(), ReportDelayActivity.class);
                General.getInstance().setActivebagid(bagid);
				startActivity(intent);
				dismiss();*/
			}
		});

		callButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
                // TODO -rewire
                /*if (Workflow.getInstance().getContactsFromBagId(bagid).size() > 0){
                    Intent intent = new Intent(getActivity(), CallActivity.class);
                    General.getInstance().setActivebagid(bagid);
                    startActivity(intent);
                    dismiss();
                }
                else
                {*/
                    /*CustomToast custom_toast = new CustomToast(getActivity());
                    custom_toast.setSuccess(false);
                    custom_toast.setText("No contact numbers available. Report issue your manager.");
                    custom_toast.show();
                    */
                Device.getInstance().displayFailed("No contact numbers available. Report issue to your manager." , getActivity());
                // }
			}
		});

		messageButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
                // TODO -rewire
                /*if (Workflow.getInstance().getContactsFromBagId(bagid).size() > 0){
                    General.getInstance().setActivebagid(bagid);
                    Intent intent = new Intent(getActivity(), SmsActivity.class);
                    startActivity(intent);
                    dismiss();
                }
                else
                {*/
                Device.getInstance().displayFailed("No contact numbers available. Report issue to your manager." , getActivity());
                // }
			}
		});

		return v;
	}

    public interface SetNextDeliveryListener {
        void onSetNextDelivery(boolean is_successful, String stopids);
    }
}