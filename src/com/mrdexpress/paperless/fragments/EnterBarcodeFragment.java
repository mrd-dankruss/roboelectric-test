package com.mrdexpress.paperless.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.mrdexpress.paperless.R;

public class EnterBarcodeFragment extends Fragment
{

	private final String TAG = "EnterBarcodeFragment";
	private ViewHolder holder;
	private View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{

		initViewHolder(inflater, container); // Inflate ViewHolder static instance

		return rootView;
	}

	public void onResume()
	{
		super.onResume();

		holder.button_ok.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public void initViewHolder(LayoutInflater inflater, ViewGroup container)
	{

		if (rootView == null)
		{

			rootView = inflater.inflate(R.layout.fragment_enter_barcode, null, false);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			holder.text_barcode = (EditText) rootView.findViewById(R.id.text_enter_barcode);
			holder.button_ok = (Button) rootView.findViewById(R.id.button_enter_barcode_ok);

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
		EditText text_barcode;
		Button button_ok;
	}

}
