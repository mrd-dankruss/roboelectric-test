package fi.gfarr.mrd.fragments;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fi.gfarr.mrd.R;
import fi.gfarr.mrd.adapters.GenericDialogListAdapter;
import fi.gfarr.mrd.datatype.DialogDataObject;
import fi.gfarr.mrd.db.DbHandler;
import fi.gfarr.mrd.helper.VariableManager;

public class ReportDelayListFragment extends Fragment
{

	private ViewHolder holder;
	private View rootView;
	private GenericDialogListAdapter adapter;

	DialogFragment newFragment;
	TextView subText;
	ArrayList<DialogDataObject> values;
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

		adapter = new GenericDialogListAdapter(getActivity(), DbHandler.getInstance(getActivity())
				.getMilkrunDelayReasons(), false);
		holder.list.setAdapter(adapter);

		holder.list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				parentItemPosition = position;// (Integer) getListAdapter().getItem(position);

				FragmentManager fm = getActivity().getSupportFragmentManager();

				if (holder.list.getItemAtPosition(position) != null)
				{
					// Cursor c = (Cursor) getListView().getItemAtPosition(position);
					// String delay_id = c.getString(c.getColumnIndex(DbHandler.C_DELAYS_ID));
					String delay_id = ((DialogDataObject) holder.list.getItemAtPosition(position))
							.getThirdText();

					// String delay_id = (String) getListView().getItemAtPosition(position);

					DelayDialog editNameDialog = DelayDialog.newInstance(delay_id);
					editNameDialog.setTargetFragment(getFragmentManager().findFragmentById(R.id.activity_report_delay_container), 1);
					editNameDialog.show(fm, "reportDelayFragment");
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		/*	values.get(parentItemPosition).setSubText(
					data.getStringExtra(GenericResultDialog.DIALOG_TIME_STRING));*/
		((DialogDataObject) adapter.getItem(parentItemPosition)).setSubText(data
				.getStringExtra(DelayDialog.DIALOG_TIME_STRING));

		VariableManager.delay_id = data.getStringExtra(VariableManager.EXTRA_DELAY_ID);

		holder.report_button.setVisibility(View.VISIBLE);
		//holder.report_button.setBackgroundResource(R.drawable.button_custom);

		System.out.println("test: " + data.getStringExtra(DelayDialog.DIALOG_TIME_STRING));
		holder.list.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	public boolean hasDataSentSuccessfully()
	{
		// TODO: Implement sending of data here

		return false;
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
