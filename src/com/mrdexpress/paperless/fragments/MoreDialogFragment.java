package com.mrdexpress.paperless.fragments;

import com.mrdexpress.paperless.CallActivity;
import com.mrdexpress.paperless.MapActivity;
import com.mrdexpress.paperless.ReportDelayActivity;
import com.mrdexpress.paperless.SmsActivity;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import fi.gfarr.mrd.R;

public class MoreDialogFragment extends DialogFragment
{

	private final String TAG = "MoreDialogFragment";
	public static String EXTENDED_DIALOG = "EXTENDED_DIALOG";
	private boolean isExtendedDialaog;
	private static String bagid;
	private Activity activity;

	public interface SetNextDeliveryListener {
        void onSetNextDelivery(boolean is_successful);
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

	public static MoreDialogFragment newInstance(boolean isExtendedDialog, String bag_id)
	{
		MoreDialogFragment f = new MoreDialogFragment();

		Bundle args = new Bundle();
		args.putBoolean(EXTENDED_DIALOG, isExtendedDialog);
		f.setArguments(args);

		bagid = bag_id;

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
		
		ImageButton closeDialogButton = (ImageButton) v
				.findViewById(R.id.button_deliveriesMore_closeButton);
		Button setAsNextDelivery = (Button) v
				.findViewById(R.id.button_deliveriesMore_setAsNextDelivery);
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
				dismiss();
				new SetNextDelivery().execute();
			}
		});

		viewMapButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getActivity(), MapActivity.class);

				// Pass driver name on
				intent.putExtra(VariableManager.EXTRA_DRIVER, getActivity().getIntent()
						.getStringExtra(VariableManager.EXTRA_DRIVER));

				intent.putExtra(VariableManager.EXTRA_DRIVER_ID, getActivity().getIntent()
						.getStringExtra(VariableManager.EXTRA_DRIVER_ID));

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

				intent.putExtra(VariableManager.EXTRA_DRIVER, getActivity().getIntent()
						.getStringExtra(VariableManager.EXTRA_DRIVER));

				intent.putExtra(VariableManager.EXTRA_DRIVER_ID, getActivity().getIntent()
						.getStringExtra(VariableManager.EXTRA_DRIVER_ID));

				intent.putExtra(VariableManager.EXTRA_NEXT_BAG_ID, bagid);

				startActivity(intent);

				dismiss();
			}
		});

		callButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getActivity(), CallActivity.class);
				intent.putExtra(VariableManager.EXTRA_NEXT_BAG_ID, bagid);
				startActivity(intent);

				dismiss();
			}
		});

		messageButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getActivity(), SmsActivity.class);
				Log.d(TAG, "Next bag ID - clickListener: " + bagid);
				intent.putExtra(VariableManager.EXTRA_NEXT_BAG_ID, bagid);
				startActivity(intent);

				dismiss();
			}
		});

		return v;
	}
	
	private class SetNextDelivery extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... params)
		{
			return ServerInterface.getInstance(getActivity()).setNextDelivery(bagid);
		}

		@Override
		protected void onPostExecute(String result)
		{

			try
			{
				if (result.equals("success"))
				{
					SetNextDeliveryListener activity_next_delivery = (SetNextDeliveryListener) activity;
					activity_next_delivery.onSetNextDelivery(true);
		            dismiss();
				}
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
	}
}