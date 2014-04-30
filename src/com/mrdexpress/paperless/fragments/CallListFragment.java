package com.mrdexpress.paperless.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.DialogFragment;
import android.app.Fragment;
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
import com.mrdexpress.paperless.helper.VariableManager;
import com.mrdexpress.paperless.workflow.Workflow;

import java.util.Calendar;
import java.util.Date;

public class CallListFragment extends Fragment
{

	private ViewHolder holder;
	private View rootView;
	private GenericDialogListAdapter adapter;

	DialogFragment newFragment;
	TextView subText;
	private int parentItemPosition;
    int bagid;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        bagid = (Integer)Workflow.getInstance().doormat.get(MoreDialogFragment.MORE_BAGID);

		initViewHolder(inflater, container); // Inflate ViewHolder static instance

		return rootView;
	}

	public void onResume()
	{
		super.onResume();

		adapter = new GenericDialogListAdapter( getActivity(),Workflow.getInstance().getContactsFromBagId(bagid) , false);

		holder.list.setAdapter(adapter);

		holder.list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
					parentItemPosition = position;
					
					Calendar c = Calendar.getInstance();
					Date datetime = c.getTime();

					String note = "Call made to "
							+ ((DialogDataObject) adapter.getItem(position)).getMainText() + "("
							+ ((DialogDataObject) adapter.getItem(position)).getSubText() + ")";

					DbHandler.getInstance(getActivity())
							.addComLog(
									datetime,
									note,
									"SMS",
                                    Integer.toString( bagid));
					
					Intent intent = new Intent(Intent.ACTION_CALL);

					String phone_number = ((DialogDataObject) adapter.getItem(position)).getSubText();

					intent.setData(Uri.parse("tel:" + phone_number));
					getActivity().startActivity(intent);
    			}
		});
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
	}
}
