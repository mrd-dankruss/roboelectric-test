package com.mrdexpress.paperless;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mrdexpress.paperless.shake.ShakeDetectActivity;
import com.mrdexpress.paperless.shake.ShakeDetectActivityListener;

public class SignatureActivity extends Activity {

	private ViewHolder holder;
	private View root_view;
	private final String TAG = "SignatureActivty";
	private ShakeDetectActivity shake_detect_activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signature);

		// Initialize ViewHolder
		initViewHolder();

		setTitle(R.string.title_actionbar_signature); // Change actionbar title
		
		// Configure actionbar
		ActionBar actionBar = getActionBar(); 
	    actionBar.setDisplayHomeAsUpEnabled(true); //Show up button
	    

		// Click Start New Milkrun button
		holder.button_complete_collection
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						// Save to file
						String consignment_number = "delivery_sig"; // to be taken from
																// consignment
																// object in
																// future
						System.out.println("save signature to file: "+holder.signatureView.saveToFile(consignment_number));
						
						//See in gallery
						sendBroadcast(new Intent(
								Intent.ACTION_MEDIA_MOUNTED,
								            Uri.parse("file://" + Environment.getExternalStorageDirectory())));
					}
				});

		shake_detect_activity = new ShakeDetectActivity(this);
		shake_detect_activity.addListener(new ShakeDetectActivityListener() {
			@Override
			public void shakeDetected() {
				SignatureActivity.this.triggerShakeDetected();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		shake_detect_activity.onResume();
	}

	@Override
	protected void onPause() {
		shake_detect_activity.onPause();
		super.onPause();
	}

	/**
	 * Actions to perform upon shake detection
	 */
	public void triggerShakeDetected() {
		// do something!
		holder.signatureView.clear(); // Clear signature canvas
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.signature, menu);
		return true;
	}

	public void initViewHolder() {

		if (root_view == null) {

			root_view = this.getWindow().getDecorView()
					.findViewById(android.R.id.content);

			if (holder == null) {
				holder = new ViewHolder();
			}

			holder.button_complete_collection = (Button) root_view
					.findViewById(R.id.button_signature_complete);

			holder.signatureView = (SignatureView) root_view
					.findViewById(R.id.signatureView);

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
		Button button_complete_collection;
		SignatureView signatureView;
	}

}
