package com.mrdexpress.paperless.helper;

import android.app.Activity;
import android.content.Intent;

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
}
