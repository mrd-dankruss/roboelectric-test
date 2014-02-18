package com.mrdexpress.paperless.net;

import org.json.JSONException;
import org.json.JSONObject;

import com.mrdexpress.paperless.db.DbHandler;
import com.mrdexpress.paperless.helper.VariableManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStateReceiver extends BroadcastReceiver
{
	private final static String TAG = "NetworkStateReceiver";

	// private Context context;

	public void onReceive(final Context context, Intent intent)
	{
		// this.context = context;
		Log.d(TAG, "Network connectivity change");
		if (intent.getExtras() != null)
		{
			ConnectivityManager connectivityManager = ((ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE));

			NetworkInfo ni = (NetworkInfo) connectivityManager.getActiveNetworkInfo();
			if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED)
			{
				Log.i(TAG, "Network " + ni.getTypeName() + " connected");

				CallQueueObject call = DbHandler.getInstance(context).popCallQueue();

				// Retrieve call queue from DB
				while (call != null)
				{
					call = DbHandler.getInstance(context).popCallQueue();
					if (call != null)
					{
						Log.d(TAG, "Zorro - Popped: " + call.getUrl());

						final String url = call.getUrl();
						final JSONObject json = call.getJSON();

						new Thread(new Runnable()
						{
							public void run()
							{
								if (json == null)
								{
									ServerInterface.getInstance(context).postData(url);
								}
								else
								{
									try
									{
										String status = ServerInterface.getInstance(context)
												.doJSONPOST(url, json, 5000);

										if (status.equals(VariableManager.TEXT_NET_ERROR))
										{
											String.valueOf(DbHandler.getInstance(context).pushCall(
													url, json));
										}
										else
										{
										}
									}
									catch (Exception e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						}).start();

					}
				}

			}
			else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,
					Boolean.FALSE))
			{
				Log.d(TAG, "There's no network connectivity");
			}
		}
	}

	/**
	 * Check availability of network connection.
	 * 
	 * @param context
	 * @return True is connected.
	 */
	public static boolean checkNetworkAvailability(Context context)
	{
		boolean status = false;
		try
		{
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(0);
			if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
			{
				status = true;
			}
			else
			{
				netInfo = cm.getNetworkInfo(1);
				if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
					status = true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		// Log.d(TAG, "zorro - net avail: " + status);
		return status;

	}

}