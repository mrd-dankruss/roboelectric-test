package fi.gfarr.mrd;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ManagerAuthIncompleteScanActivity extends Activity
{

	private ViewHolder holder;
	private View root_view;
	private final String TAG = "ManagerAuthIncompleteScanActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager_auth_incomplete_scan);

		setTitle(R.string.title_actionbar_manager_auth); // Change actionbar title

		// Initialize ViewHolder
		initViewHolder();

		initClickListeners();

		setTitle(R.string.title_actionbar_mainmenu); // Change actionbar title
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manager_auth_incomplete_scan, menu);
		return true;
	}

	/**
	 * Initiate click listeners for buttons.
	 */
	private void initClickListeners()
	{
		
		holder.button_continue.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				// Perform action on click

			}
		});

		
		holder.button_change_manager.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				// Perform action on click

			}
		});
	}

	/**
	 * Allows the views' resources to be found only once, improving performance.
	 */
	public void initViewHolder()
	{
		if (root_view == null)
		{
			root_view = this.getWindow().getDecorView().findViewById(android.R.id.content);

			if (holder == null)
			{
				holder = new ViewHolder();
			}

			holder.button_continue = (Button) root_view.findViewById(R.id.button_manAuth_continue);
			holder.button_change_manager = (Button) root_view
					.findViewById(R.id.button_manAuth_change);
			holder.textView_heading = (TextView) root_view
					.findViewById(R.id.textView_manAuth_heading);
			holder.textView_list = (TextView) root_view
					.findViewById(R.id.textView_manAuth_list);
			holder.textView_name = (TextView) root_view
					.findViewById(R.id.textView_manAuth_manager_name);
			holder.editText_pin = (EditText) root_view
					.findViewById(R.id.editText_manAuth_pin);
			
			// Store the holder with the view.
			root_view.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) root_view.getTag();

			if ((root_view.getParent() != null) && (root_view.getParent() instanceof ViewGroup))
			{
				((ViewGroup) root_view.getParent()).removeAllViewsInLayout();
			}
			else
			{
			}
		}
	}

	// ViewHolder stores static instances of views in order to reduce the number
	// of times that findViewById is called, which affected listview performance
	static class ViewHolder
	{
		Button button_continue, button_change_manager;
		TextView textView_name, textView_heading, textView_list;
		EditText editText_pin;
	}
}
