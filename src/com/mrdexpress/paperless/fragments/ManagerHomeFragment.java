package com.mrdexpress.paperless.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.mrdexpress.paperless.R;
import com.mrdexpress.paperless.helper.FontHelper;
import com.mrdexpress.paperless.helper.VariableManager;

public class ManagerHomeFragment extends Fragment
{

	private final String TAG = "ManagerFragment";
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

		holder.button_milkrun.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// Start scan activity
				Intent intent = new Intent(getActivity(), ScanFragment.class);

				// Pass driver name on
				intent.putExtra(VariableManager.EXTRA_DRIVER, getActivity().getIntent()
						.getStringExtra(VariableManager.EXTRA_DRIVER));

				/*	intent.putExtra(VariableManager.EXTRA_DRIVER_ID, getActivity().getIntent()
							.getStringExtra(VariableManager.EXTRA_DRIVER_ID));*/

				startActivity(intent);
			}
		});

		holder.button_training_run.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO: Implement training runs
			}
		});

	}

	public void initViewHolder(LayoutInflater inflater, ViewGroup container)
	{

		if (rootView == null)
		{

			rootView = inflater.inflate(R.layout.fragment_home_manager, null, false);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			Typeface typeface_robotoBold = Typeface.createFromAsset(getActivity().getAssets(),
					FontHelper.getFontString(FontHelper.FONT_ROBOTO, FontHelper.FONT_TYPE_TTF,
							FontHelper.STYLE_BOLD));

			holder.button_auth_new_delivery_run = (Button) rootView
					.findViewById(R.id.button_home_manager_auth_new_delivery_run);
			holder.button_milkrun = (Button) rootView
					.findViewById(R.id.button_home_manager_milkrun);
			holder.button_training_run = (Button) rootView
					.findViewById(R.id.button_home_manager_training);

			holder.button_auth_new_delivery_run.setTypeface(typeface_robotoBold);
			holder.button_milkrun.setTypeface(typeface_robotoBold);
			holder.button_training_run.setTypeface(typeface_robotoBold);

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
		Button button_auth_new_delivery_run;
		Button button_milkrun;
		Button button_training_run;
	}

}
