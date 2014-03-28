/**
 * Thanks to http://corner.squareup.com/2010/07/smooth-signatures.html
 */

package com.mrdexpress.paperless;

import android.content.Context;
import android.graphics.*;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.*;

public class SignatureView extends View {

	private final String TAG = "SignatureView";

	private static final float STROKE_WIDTH = 3f;

	/** Need to track this so the dirty region can accommodate the stroke. **/
	private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;

	private Paint paint = new Paint();
	private Path path = new Path();

	/**
	 * Optimizes painting by invalidating the smallest possible area.
	 */
	private float lastTouchX;
	private float lastTouchY;
	private final RectF dirtyRect = new RectF();

	public SignatureView(Context context, AttributeSet attrs) {
		super(context, attrs);

		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeWidth(STROKE_WIDTH);
	}

	/**
	 * Erases the signature.
	 */
	public void clear() {
		path.reset();

		// Repaints the entire view.
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawPath(path, paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float eventX = event.getX();
		float eventY = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			path.moveTo(eventX, eventY);
			lastTouchX = eventX;
			lastTouchY = eventY;
			// There is no end point yet, so don't waste cycles invalidating.
			return true;

		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_UP:
			// Start tracking the dirty region.
			resetDirtyRect(eventX, eventY);

			// When the hardware tracks events faster than they are delivered,
			// the
			// event will contain a history of those skipped points.
			int historySize = event.getHistorySize();
			for (int i = 0; i < historySize; i++) {
				float historicalX = event.getHistoricalX(i);
				float historicalY = event.getHistoricalY(i);
				expandDirtyRect(historicalX, historicalY);
				path.lineTo(historicalX, historicalY);
			}

			// After replaying history, connect the line to the touch point.
			path.lineTo(eventX, eventY);
			break;

		default:
			// debug("Ignored touch event: " + event.toString());
			return false;
		}

		// Include half the stroke width to avoid clipping.
		invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
				(int) (dirtyRect.top - HALF_STROKE_WIDTH),
				(int) (dirtyRect.right + HALF_STROKE_WIDTH),
				(int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

		lastTouchX = eventX;
		lastTouchY = eventY;

		return true;
	}

	/**
	 * Called when replaying history to ensure the dirty region includes all
	 * points.
	 */
	private void expandDirtyRect(float historicalX, float historicalY) {
		if (historicalX < dirtyRect.left) {
			dirtyRect.left = historicalX;
		} else if (historicalX > dirtyRect.right) {
			dirtyRect.right = historicalX;
		}
		if (historicalY < dirtyRect.top) {
			dirtyRect.top = historicalY;
		} else if (historicalY > dirtyRect.bottom) {
			dirtyRect.bottom = historicalY;
		}
	}

	/**
	 * Resets the dirty region when the motion event occurs.
	 */
	private void resetDirtyRect(float eventX, float eventY) {

		// The lastTouchX and lastTouchY were set when the ACTION_DOWN
		// motion event occurred.
		dirtyRect.left = Math.min(lastTouchX, eventX);
		dirtyRect.right = Math.max(lastTouchX, eventX);
		dirtyRect.top = Math.min(lastTouchY, eventY);
		dirtyRect.bottom = Math.max(lastTouchY, eventY);
	}

	/**
	 * Method to check whether external media available and writable. This is
	 * adapted from
	 * http://developer.android.com/guide/topics/data/data-storage.html
	 * #filesExternal
	 */

	private boolean checkExternalMedia() {
		boolean external_storage_available = false;
		boolean external_storage_writeable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// Can read and write the media
			external_storage_available = external_storage_writeable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// Can only read the media
			external_storage_available = true;
			external_storage_writeable = false;
		} else {
			// Can't read or write
			external_storage_available = external_storage_writeable = false;
		}
		if (external_storage_available == false) {
			Log.d(TAG, "External storage unavailable");
		}
		if (external_storage_writeable == false) {
			Log.d(TAG, "External storage not writable");
		}
		return external_storage_writeable;
	}

	/**
	 * Saves canvas to file.
	 * 
	 * @param filename
	 */
	public boolean saveToFile(String filename) {

		// First check storage writability

		if (checkExternalMedia()) {
			// This is the SD card
			// final String path =
			// Environment.getExternalStorageDirectory().toString();
			final String path = Environment.getExternalStoragePublicDirectory(
					"DIRECTORY_PICTURES").toString();
			final String full_path = path + "/MrD/" + filename + ".jpg";

			Log.d(TAG, full_path);

			setDrawingCacheEnabled(true);
			Bitmap bmp = getDrawingCache();
			try {
				bmp.compress(CompressFormat.JPEG, 95, new FileOutputStream(
						full_path));
				return true; // Successful
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block

				Writer writer = new StringWriter();
				PrintWriter printWriter = new PrintWriter(new StringWriter());
				e.printStackTrace(printWriter);
				String s = writer.toString();
				Log.e(TAG, s);
				return false;
			}
		}
		else
		{
			return false;
		}
	}
}