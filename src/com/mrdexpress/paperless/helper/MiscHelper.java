package com.mrdexpress.paperless.helper;

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
		
		String destinationHubName = bag.getDestinationHubName() ;
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
		String suburb = bag.getDestinationSuburb();
		
		return (MiscHelper.isNonEmptyString(destinationHubName) ? destinationHubName + subAddressSeparator: "") +
				(MiscHelper.isNonEmptyString(formattedDestination) ? formattedDestination : "") +
				(MiscHelper.isNonEmptyString(suburb) ? suburb : "");
	}
}
