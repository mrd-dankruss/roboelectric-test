/**
 * methods should be refactored at some point
 * 
 */
package com.mrdexpress.paperless.helper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import com.mrdexpress.paperless.DriverHomeActivity;
import com.mrdexpress.paperless.db.Bag;

import java.util.List;

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
			formattedDestination = destBuffer.toString().substring(0, destBuffer.toString().length()-2);
		}
		String suburb = bag.getDestination();

        String ret = (MiscHelper.isNonEmptyString(destinationHubName) ? destinationHubName + subAddressSeparator: "") +
                (MiscHelper.isNonEmptyString(formattedDestination) ? formattedDestination : "");// +
                //(MiscHelper.isNonEmptyString(suburb) ? suburb : "");

		return ret;
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
	public static void setNextDeliveryId(String stopids, Activity activity)
	{
        // TODO - Obsolete?
		// Store in sharedprefs
		SharedPreferences prefs = activity.getSharedPreferences(VariableManager.PREF, activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(VariableManager.PREF_CURRENT_STOPID, stopids);
		editor.apply();
	}

}
