package fi.gfarr.mrd.widget;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Toaster {

	private static RelativeLayout layout;
	private static final int DELAY = 3000;
	
	/**
	 * Display toast notification
	 * @param Message
	 * @param text_view
	 * @param layout
	 * @param activity
	 */
	public static void displayToast(String msg, TextView text_view, RelativeLayout l,
			Activity activity) {
		text_view.setText(msg);

		layout = l;

		activity.runOnUiThread(new Runnable() {
			public void run() {

				layout.setVisibility(View.VISIBLE);
				layout.bringToFront();
			}
		});

		Handler h = new Handler();

		h.postDelayed(new Runnable() {

			@Override
			public void run() {
				// DO DELAYED STUFF
				layout.setVisibility(View.INVISIBLE);
			}
		}, DELAY); // e.g. 3000 milliseconds
	}
}
