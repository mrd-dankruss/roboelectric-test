package com.mrdexpress.paperless.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mrdexpress.paperless.MainActivity;
import com.mrdexpress.paperless.R;

public class GCMIntentService extends IntentService
{
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GCMIntentService()
	{
		super("GCMIntentService");
	}

	public static final String TAG = "GCM";

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty())
		{
			Log.d(TAG, extras.toString());
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
			{
				Log.i(TAG, "Send error: " + extras.toString());
			}
			else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
			{
				Log.i(TAG, "Deleted messages on server: " + extras.toString());
			}
			else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
			{
				
				//TODO: Handle parcels being scanned by branch.
				
				// This loop represents the service doing some work.
				for (int i = 0; i < 5; i++)
				{
					Log.i(TAG, "Working... " + (i + 1) + "/5 @ " + SystemClock.elapsedRealtime());
					try
					{
						Thread.sleep(5000);
					}
					catch (InterruptedException e)
					{
					}
				}
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
				// Post notification of received message.
				sendNotification("Received: " + extras.toString());
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		GCMBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg)
	{
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				MainActivity.class), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.mrd_logo_small).setContentTitle("GCM Notification")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg)).setContentText(msg);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
}