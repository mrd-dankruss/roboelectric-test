package com.mrdexpress.paperless.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.adapters.GenericDialogListAdapter;
import com.mrdexpress.paperless.datatype.DialogDataObject;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.db.Device;
import com.mrdexpress.paperless.dialogfragments.DeliveryHandoverDialogFragment;
import com.mrdexpress.paperless.dialogfragments.ReasonForFailedHandoverFragment;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.interfaces.CallBackFunction;
import com.mrdexpress.paperless.workflow.Workflow;

import java.util.ArrayList;

public class UpdateStatusDialog extends DialogFragment
{
	private static String stopids;
	public static String DIALOG_TIME_STRING = "DIALOG_TIME_STRING";
	public static String DIALOG_ITEM_POS = "DIALOG_ITEM_POS";
	private ArrayList<DialogDataObject> temp;

    private CallBackFunction callback;

    public UpdateStatusDialog(String _stopids, CallBackFunction _callback) {
        stopids = _stopids;
        callback = _callback;
    }

    /**
	 * Create a new instance of MyDialogFragment, providing "num"
	 * as an argument.
	 */
	public static UpdateStatusDialog newInstance(String _stopids, CallBackFunction _callback)
	{
		UpdateStatusDialog f = new UpdateStatusDialog( _stopids, _callback);

		// Supply num input as an argument.
		// Bundle args = new Bundle();
		// args.putInt("num", bag_id);
		// f.setArguments(args);

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

		Typeface typeface_roboto_bold = Typeface.createFromAsset(getActivity().getAssets(), FontHelper
				.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
						FontHelper.STYLE_BOLD));
		
		TextView title = (TextView) v.findViewById(R.id.textView_trafficDelay_title);
		title.setText(R.string.title_dialog_handover);
		
		title.setTypeface(typeface_roboto_bold);

		ImageButton closeDialogButton = (ImageButton) v.findViewById(R.id.button_trafficDelay_closeButton);

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
                Workflow.getInstance().currentBagID = stopids;

				if (position == 0)
				{
                    DeliveryHandoverDialogFragment.newInstance(new CallBackFunction() {
                        @Override
                        public boolean execute(Object args) {
                            if ((Boolean) args == true) {
                            }
                            callback.execute(args);
                            dismiss();
                            return true;
                        }
                    }).show(getFragmentManager(), getTag());
                }

				if (position == 1)
				{
                    (ReasonForFailedHandoverFragment.newInstance(new CallBackFunction() {
                        @Override
                        public boolean execute(Object args) {
                            if (args != null) {
                                Workflow.getInstance().setDeliveryStatus(Workflow.getInstance().currentBagID, Bag.STATUS_UNSUCCESSFUL, (String) args);
                                Device.getInstance().displayInfo("Delivery set as failed", getActivity());
                            }
                            callback.execute(args);
                            dismiss();
                            return false;
                        }
                    })).show( getFragmentManager(), getTag());
				}
			}
		});

		return v;
	}

}