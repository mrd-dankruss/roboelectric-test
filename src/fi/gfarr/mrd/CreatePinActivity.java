package fi.gfarr.mrd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fi.gfarr.mrd.db.DbHandler;
import fi.gfarr.mrd.security.PinManager;
import fi.gfarr.mrd.widget.Toaster;

public class CreatePinActivity extends Activity {

	private final String TAG = "CreatePinActivity";
	private ViewHolder holder;
	private View root_view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_pin);

		// Change actionbar title
		setTitle(R.string.title_actionbar_create_pin);

		// Inflate views
		initViewHolder();

		// button click
		// Click create button
		holder.button_create.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// Check if pin is valid then login
				if (checkPin()) {
					Intent intent = new Intent(getApplicationContext(),
							ScanActivity.class);

					DbHandler.getInstance(getApplicationContext());
					startActivity(intent);
				}
			}
		});
		// Click change driver button
		holder.button_change.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

	}

	private boolean checkPin() {

		// Check if two pins match
		if (holder.editText_pin1.getText().toString()
				.equals(holder.editText_pin2.getText().toString())) {

			// Check for 4-digit format
			String msg = PinManager.checkPin(holder.editText_pin1.getText()
					.toString(), this);
			if (msg.equals("OK")) {
				return true;
			} else {
				displayToast(msg);
				return false;
			}
		} else {
			// strings do not match
			displayToast(getString(R.string.text_create_pin_mismatch));
			return false;
		}
	}

	/**
	 * Display a toast using the custom Toaster class
	 * 
	 * @param msg
	 */
	private void displayToast(String msg) {
		Toaster.displayToast(msg, holder.textView_toast,
				holder.relativeLayout_toast, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_pin, menu);
		return true;
	}

	public void initViewHolder() {

		if (root_view == null) {

			root_view = this.getWindow().getDecorView()
					.findViewById(android.R.id.content);

			if (holder == null) {
				holder = new ViewHolder();
			}

			holder.button_create = (Button) root_view
					.findViewById(R.id.button_create_pin_create);
			holder.button_change = (Button) root_view
					.findViewById(R.id.button_create_pin_change_driver);
			holder.editText_pin1 = (EditText) root_view
					.findViewById(R.id.editText_create_pin_1);
			holder.editText_pin2 = (EditText) root_view
					.findViewById(R.id.editText_create_pin_2);
			holder.textView_toast = (TextView) root_view
					.findViewById(R.id.textView_create_pin_toast);
			holder.relativeLayout_toast = (RelativeLayout) root_view
					.findViewById(R.id.toast_create_pin);

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

	// Creates static instances of resources.
	// Increases performance by only finding and inflating resources only once.
	static class ViewHolder {
		Button button_create;
		Button button_change;
		EditText editText_pin1;
		EditText editText_pin2;
		TextView textView_toast;
		RelativeLayout relativeLayout_toast;
	}

}
