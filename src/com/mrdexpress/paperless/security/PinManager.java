package com.mrdexpress.paperless.security;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.util.Log;

import com.mrdexpress.paperless.R;

public class PinManager {

	private final static String PIN = "1234";

	/**
	 * Checks validity of PIN
	 * 
	 * @param pin
	 * @param activity
	 * @return
	 */
	public static String checkPin(String pin, Activity activity) {
		// Check for 4-digit format
		if (pin.length() == 4) {
			return "OK";
		} else {
			return activity.getString(R.string.text_create_pin_toast_too_short);
		}

	}

	/**
	 * Generates MD5 hash of passed parameter.
	 * 
	 * @param plaintext
	 * @return
	 */
	// http://androidbridge.blogspot.com/2011/11/how-to-create-md5-hash-in-android.html
	public static String toMD5(String plaintext) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(plaintext.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String md5 = number.toString(16);

			while (md5.length() < 32)
				md5 = "0" + md5;

			return md5;
		} catch (NoSuchAlgorithmException e) {
			Log.e("MD5", e.getLocalizedMessage());
			return null;
		}
	}

}
