package com.mrdexpress.paperless.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.adapters.GenericDialogListAdapter;
import com.mrdexpress.paperless.datatype.DialogDataObject;
import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.db.General;
import com.mrdexpress.paperless.db.Users;
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.net.ServerInterface;
import com.mrdexpress.paperless.widget.CustomToast;
import com.mrdexpress.paperless.workflow.Workflow;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class ReportDelayListFragment extends Fragment
{

	private final String TAG = "ReportDelayActivity";
	private ViewHolder holder;
	private View rootView;
	private GenericDialogListAdapter adapter;
    private String delay_id;

	DialogFragment newFragment;
	TextView subText;
	private int parentItemPosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		initViewHolder(inflater, container); // Inflate ViewHolder static instance

		return rootView;
	}

	public void onResume()
	{
		super.onResume();

		//adapter = new GenericDialogListAdapter(getActivity(), DbHandler.getInstance(getActivity()).getMilkrunDelayReasons(), false);
        adapter = new GenericDialogListAdapter(getActivity(), Workflow.getInstance().getMilkrunDelayReasons(), false);

		if ((adapter != null) & (holder.list != null))
		{
			holder.list.setAdapter(adapter);
		}

		holder.list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				parentItemPosition = position;// (Integer) getListAdapter().getItem(position);

				FragmentManager fm = getActivity().getFragmentManager();

				if (holder.list.getItemAtPosition(position) != null)
				{
					// Cursor c = (Cursor) getListView().getItemAtPosition(position);
					// String delay_id = c.getString(c.getColumnIndex(DbHandler.C_DELAYS_ID));
					delay_id = ((DialogDataObject) holder.list.getItemAtPosition(position)).getThirdText();

					// String delay_id = (String) getListView().getItemAtPosition(position);

					DelayDialog editNameDialog = DelayDialog.newInstance(delay_id);
					editNameDialog.setTargetFragment( getFragmentManager().findFragmentById( R.id.activity_report_delay_container), 1);
					editNameDialog.show(fm, "reportDelayFragment");
				}
			}
		});

		holder.report_button.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				// Only perform action if there is a selection made
				if (delay_id != null)
				{
					//String driverid = Users.getInstance().getActiveDriver().getStringid();
					new ReportDelayTask().execute( Integer.toString( (Integer)Workflow.getInstance().doormat.get(MoreDialogFragment.MORE_BAGID)), Integer.toString( Users.getInstance().getActiveDriver().getid()), delay_id);
                    //String bagid = General.getInstance().activebagid;
                    //new ReportDelayTask().execute(bagid, driverid,VariableManager.delay_id);
				}
			}
		});
		holder.report_button.setVisibility(View.VISIBLE);
		holder.report_button.setEnabled(false);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		((DialogDataObject) adapter.getItem(parentItemPosition)).setSubText(data.getStringExtra(DelayDialog.DIALOG_TIME_STRING));

		delay_id = data.getStringExtra(VariableManager.EXTRA_DELAY_ID);

		// holder.report_button.setVisibility(View.VISIBLE);
		holder.report_button.setBackgroundResource(R.drawable.button_custom);
		holder.list.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		holder.report_button.setEnabled(true);
	}

	private class ReportDelayTask extends AsyncTask<String, Void, String>
	{

		private ProgressDialog dialog = new ProgressDialog(getActivity());

		/** progress dialog to show user that the backup is processing. */
		/** application context. */
		@Override
		protected void onPreExecute()
		{
			this.dialog.setMessage("Submitting delay report");
			this.dialog.show();
		}

		@Override
		protected String doInBackground(String... args)
		{
			// TODO: Add com log here : Implemented
			Calendar c = Calendar.getInstance();
			Date datetime = c.getTime();
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            String todate = df.format("yyyy-MM-dd hh:mm:ss", datetime).toString();


			String note = "" + ((DialogDataObject) holder.list.getItemAtPosition(parentItemPosition)).getSubText()
					+ " : "
					+ ((DialogDataObject) holder.list.getItemAtPosition(parentItemPosition))
							.getMainText() + "";

            General.getInstance().AddComLog( new General.Communications(todate , note , "N") , General.getInstance().getActivebagid());
            ServerInterface.getInstance(getActivity()).postDelay(args[0], note , args[2]);
            return "success";
		}

		@Override
		protected void onPostExecute(String result)
		{
			// Close progress spinner
			if (dialog.isShowing())
			{
				dialog.dismiss();
			}
			Log.i(TAG, result);
			delay_id = null;

			CustomToast custom_toast = new CustomToast(getActivity());
			custom_toast.setText("Delay Logged");
			custom_toast.setSuccess(true);
			custom_toast.show();

			getActivity().finish();
		}
	}

	public void initViewHolder(LayoutInflater inflater, ViewGroup container)
	{

		if (rootView == null)
		{

			rootView = inflater.inflate(R.layout.fragment_view_deliveries_content, null, false);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			holder.list = (ListView) rootView.findViewById(R.id.fragment_viewDeliveries_container);
			holder.report_button = (Button) rootView.findViewById(R.id.button_generic_report);
			holder.report_button.setText(getResources().getString(R.string.delivery_more_reportDelay));

			// Store the holder with the view.
			rootView.setTag(holder);

		}
		else
		{
			holder = (ViewHolder) rootView.getTag();

			if ((rootView.getParent() != null) && (rootView.getParent() instanceof ViewGroup))
			{
				((ViewGroup) rootView.getParent()).removeAllViewsInLayout();
			}
			else
			{
			}
		}
	}

	// Creates static instances of resources.
	// Increases performance by only finding and inflating resources only once.
	static class ViewHolder
	{
		ListView list;
		Button report_button;
	}

}
