/**
 * methods should be refactored at some point
 * 
 */
package com.mrdexpress.paperless.helper;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import com.mrdexpress.paperless.DriverHomeActivity;
import com.mrdexpress.paperless.db.Bag;

public class MiscHelper {
	
	public static boolean isNonEmptyString(String s)
	{
		if ((s != null) && (s.length() > 0) && !"null".equals(s))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	public static String getBagFormattedAddress(Bag bag)
	{
		String subAddressSplit = ",";
		String subAddressSeparator = subAddressSplit+"\n";
		
		String destinationHubName = bag.getDestination();
		String destination = bag.getDestinationAddress();
		String formattedDestination = "";
		if (MiscHelper.isNonEmptyString(destination))
		{
			StringBuffer destBuffer = new StringBuffer("");
			String[] dests = destination.split(subAddressSplit);
			for (String subDest : dests)
			{
				destBuffer.append(subDest.trim() + subAddressSeparator);
			}
			formattedDestination = destBuffer.toString();
		}
		String suburb = bag.getDestination();
		
		return (MiscHelper.isNonEmptyString(destinationHubName) ? destinationHubName + subAddressSeparator: "") +
				(MiscHelper.isNonEmptyString(formattedDestination) ? formattedDestination : "") +
				(MiscHelper.isNonEmptyString(suburb) ? suburb : "");
	}
	
	
	public static Intent getGoHomeIntent(Activity activity)
	{
		Intent intent = new Intent(activity, DriverHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
        return intent;
	}
	
	/*
	 * persists next delivery's id
	 * remove id if bagId == null
	 */
	public static void setNextDeliveryId(int bagId, Activity activity)
	{
		// Store in sharedprefs
		SharedPreferences prefs = activity.getSharedPreferences(VariableManager.PREF,
				activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(VariableManager.PREF_CURRENT_STOPID, bagId);
		editor.apply();
	}
	
	public static int getNextDeliveryId(Activity activity)
	{
		SharedPreferences prefs = activity.getSharedPreferences(VariableManager.PREF,
				activity.MODE_PRIVATE);
		return prefs.getInt(VariableManager.PREF_CURRENT_STOPID, -1);
	}
	
	
	public static int validateNextDeliveryId(List<Bag> todoBags, Activity activity)
	{
		int nextBagId = getNextDeliveryId(activity);
		if( nextBagId != -1)
		{
			boolean validId = false;
			for (Bag bag : todoBags)
			{
				if (nextBagId == bag.getBagID())
				{
					validId = true;
					break;
				}
			}
			
			if (!validId)
			{
				nextBagId = -1;
				setNextDeliveryId(-1, activity);
			}
		}
		else
		{
			nextBagId = -1;
		}
		
		return nextBagId;
	}
}
