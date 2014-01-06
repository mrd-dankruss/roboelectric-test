package fi.gfarr.mrd.security;

import android.app.Activity;
import fi.gfarr.mrd.R;

public class PinManager {

	private final static String PIN = "1234";
	
	public static String checkPin(String pin, Activity activity)
	{
		//Check for 4-digit format
		if (pin.length() == 4)
		{						
			return "OK";			
		}
		else
		{		
			return activity.getString(R.string.text_create_pin_toast_too_short);
		}

	}
	
	
}
