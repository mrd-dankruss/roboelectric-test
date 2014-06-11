package com.mrdexpress.paperless.dialogfragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import com.mrdexpress.paperless.DriverHomeActivity;
import com.mrdexpress.paperless.MapDialogFragment;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.datatype.StopItem;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.db.General;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.interfaces.FragmentCallBackFunction;
import com.mrdexpress.paperless.widget.CustomToast;
import com.mrdexpress.paperless.workflow.Workflow;

import java.util.ArrayList;

public class StopItemMenuDialogFragment extends DialogFragment
{
	private final String TAG = "StopMenuDialogFragment";
	private StopItem stopitem;
    private CallBackFunction callback;
	private Activity activity;

    public StopItemMenuDialogFragment( StopItem stopitem, CallBackFunction callback) {
        this.callback = callback;
        this.stopitem = stopitem;
    }

	public static StopItemMenuDialogFragment newInstance( StopItem _stopitem, CallBackFunction _callback)
	{
		StopItemMenuDialogFragment f = new StopItemMenuDialogFragment( _stopitem, _callback);
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
		View v = inflater.inflate(R.layout.fragment_stopitem_menu, container, false);

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		ImageButton closeDialogButton = (ImageButton) v.findViewById(R.id.button_deliveriesMore_closeButton);
		Button unallocatestop = (Button) v.findViewById(R.id.button_stopmenu_unallocate_stop);
        Button info = (Button) v.findViewById(R.id.button_stopmenu_info);

		closeDialogButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});

		unallocatestop.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
                CustomToast custom_toast = new CustomToast(activity);
                custom_toast.setSuccess(true);
                custom_toast.setText("Stop successfully removed from trip");
                custom_toast.show();
                //Workflow.getInstance().setNextStop( stopids);
				dismiss();
                callback.execute(true);
			}
		});

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewStopBagsDialogFragment viewbags = ViewStopBagsDialogFragment.newInstance(stopitem.getIDs(), new CallBackFunction() {
                    @Override
                    public boolean execute(Object args) {
                        callback.execute( args);
                        return false;
                    }
                });
                viewbags.show(getActivity().getFragmentManager(), getTag());
            }
        });

		return v;
	}

    public interface SetNextDeliveryListener {
        void onSetNextDelivery(boolean is_successful, String stopids);
    }
}