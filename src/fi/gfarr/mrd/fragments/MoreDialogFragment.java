package fi.gfarr.mrd.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import fi.gfarr.mrd.CallActivity;
import fi.gfarr.mrd.R;
import fi.gfarr.mrd.ReportDelayActivity;
import fi.gfarr.mrd.SmsActivity;
import fi.gfarr.mrd.ViewDeliveriesFragmentActivity;


public class MoreDialogFragment extends DialogFragment {
    int mNum;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static MoreDialogFragment newInstance(int num) {
    	MoreDialogFragment f = new MoreDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_deliveries_more, container, false);
        
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        
        ImageButton closeDialogButton = (ImageButton) v.findViewById(R.id.button_deliveriesMore_closeButton);
        Button setAsNextDelivery = (Button) v.findViewById(R.id.button_deliveriesMore_setAsNextDelivery);
        Button viewMapButton = (Button) v.findViewById(R.id.button_deliveriesMore_viewMap);
        Button reportDelayButton = (Button) v.findViewById(R.id.button_deliveriesMore_reportDelay);
        Button callButton = (Button) v.findViewById(R.id.button_deliveriesMore_call);
        Button messageButton = (Button) v.findViewById(R.id.button_deliveriesMore_message);
        
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
				//TODO: Set as next delivery button
			}
		});
        
        viewMapButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//TODO: View Map button
			}
		});
        
        reportDelayButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//TODO: Change to new activity
				Intent intent = new Intent(getActivity(), ReportDelayActivity.class);
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