package com.mrdexpress.paperless.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mrdexpress.paperless.fragments.DeliveryHandoverFragment;

public class GCMIntentService extends IntentService
{
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	
	public static final String BROADCAST_ACTION = " com.mrdexpress.paperless.service";
    private final Handler handler = new Handler();
    Intent intent;
    int counter = 0;

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

				// TODO: Handle parcels being scanned by branch.

				try
				{
					JSONObject json_object = new JSONObject(extras.getString("data"));
					String cons_no = json_object.get("waybill_no").toString();
					String scanned = json_object.get("scanned").toString();
					
					boolean bool_scanned = false;
					
					if (scanned.equals("true"))
					{
						bool_scanned = true;
					}
					
					intent = new Intent(BROADCAST_ACTION);
					intent.putExtra(DeliveryHandoverFragment.WAYBILL_BARCODE, cons_no);
					intent.putExtra(DeliveryHandoverFragment.WAYBILL_SCANNED, bool_scanned);
	                sendBroadcast(intent);
	                stopService(intent);
					
				}
				catch (JSONException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		GCMBroadcastReceiver.completeWakefulIntent(intent);
	}

	/*
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
	*/
}