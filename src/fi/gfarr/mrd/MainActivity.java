package fi.gfarr.mrd;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivity extends Activity {

	private List<Fragment> fragments = new ArrayList<Fragment>(); // List of
																	// screen
																	// fragments
	private ViewHolder holder;
	private View root_view;
	private final String TAG = "MainActivty";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialize ViewHolder
		initViewHolder();

		setTitle(R.string.title_actionbar_mainmenu); // Change actionbar title

		// Click Start New Milkrun button
		holder.button_start_milkrun
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						// Perform action on click

						new Thread(new Runnable() {
							public void run() {
								Intent intent = new Intent(
										getApplicationContext(),
										DriverListActivity.class);
								startActivity(intent);
							}
						}).start();
					}
				});

		// Click Start Trainingrun button
		holder.button_start_trainingrun
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						// Perform action on click

						new Thread(new Runnable() {
							public void run() {
								Intent intent = new Intent(
										getApplicationContext(),
										ScanActivity.class);
								startActivity(intent);
							}
						}).start();
					}
				});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void initViewHolder() {

		if (root_view == null) {

			root_view = this.getWindow().getDecorView()
					.findViewById(android.R.id.content);

			if (holder == null) {
				holder = new ViewHolder();
			}

			holder.button_start_milkrun = (Button) root_view
					.findViewById(R.id.button_mainmenu_start_milkrun);
			holder.button_start_trainingrun = (Button) root_view
					.findViewById(R.id.button_mainmenu_training_run);

			// Store the holder with the view.
			root_view.setTag(holder);

		} else {
			holder = (ViewHolder) root_view.getTag();

			if ((root_view.getParent() != null)
					&& (root_view.getParent() instanceof ViewGroup)) {
				((ViewGroup) root_view.getParent()).removeAllViewsInLayout();
			} else {
			}
		}
	}

	// ViewHolder stores static instances of views in order to reduce the number
	// of times that findViewById is called, which affected listview performance
	static class ViewHolder {
		Button button_start_milkrun;
		Button button_start_trainingrun;
	}

}
