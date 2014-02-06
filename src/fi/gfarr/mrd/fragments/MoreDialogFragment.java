package fi.gfarr.mrd.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import fi.gfarr.mrd.CallActivity;
import fi.gfarr.mrd.MapActivity;
import fi.gfarr.mrd.R;
import fi.gfarr.mrd.ReportDelayActivity;
import fi.gfarr.mrd.SmsActivity;
import fi.gfarr.mrd.helper.VariableManager;

public class MoreDialogFragment extends DialogFragment
{

	public static String EXTENDED_DIALOG = "EXTENDED_DIALOG";
	private boolean isExtendedDialaog;
	private static String bagid;

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
		Button viewMapButton = (Button) v.findViewById(R.id.button_deliveriesMore_viewMap);
		Button reportDelayButton = (Button) v.findViewById(R.id.button_deliveriesMore_reportDelay);
		Button callButton = (Button) v.findViewById(R.id.button_deliveriesMore_call);
		Button messageButton = (Button) v.findViewById(R.id.button_deliveriesMore_message);

		if (isExtendedDialaog == true)
		{
			setAsNextDelivery.setVisibility(View.VISIBLE);
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
				// TODO: Set as next delivery button
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
				// TODO: View Map button
			}
		});

		reportDelayButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO: Change to new activity
				Intent intent = new Intent(getActivity(), ReportDelayActivity.class);

				intent.putExtra(VariableManager.EXTRA_DRIVER, getActivity().getIntent()
						.getStringExtra(VariableManager.EXTRA_DRIVER));

				intent.putExtra(VariableManager.EXTRA_DRIVER_ID, getActivity().getIntent()
						.getStringExtra(VariableManager.EXTRA_DRIVER_ID));

				intent.putExtra(VariableManager.EXTRA_NEXT_BAG_ID, bagid);

				startActivity(intent);
				/*
				Fragment fragment = new ReportDelayListFragment();
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.realtabcontent, fragment);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				ft.addToBackStack(null);
				ft.commit();
				dismiss();
				*/
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
			}
		});

		messageButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(getActivity(), SmsActivity.class);
				startActivity(intent);
			}
		});

		return v;
	}
}