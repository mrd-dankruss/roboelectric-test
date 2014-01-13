package fi.gfarr.mrd.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import fi.gfarr.mrd.helper.VariableManager;
import fi.gfarr.mrd.security.PinManager;

public class ServerInterface {

	private final static String TAG = "ServerInterface";

	private static JSONArray drivers_jArray = null;

	/**
	 * Makes API call to request a new session token.
	 * 
	 * @param IMEI
	 *            number.
	 */
	public static String requestToken() {
		String url = "http://paperlessapp.apiary.io/v1/auth?imei="
				+ VariableManager.IMEI_TEST;
		String response = getInputStreamFromUrl(url);

		String token = "";

		try {
			JSONObject jObject = new JSONObject(response);
			token = jObject.getJSONObject("response").getJSONObject("auth")
					.getString("token");

		} catch (JSONException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
			return "";
			// Oops
		}

		if (VariableManager.DEBUG) {
			Log.d(TAG, "token: " + token);
		}
		return token;
	}

	/**
	 * Makes API call to update driver PIN.
	 * 
	 * @param ID
	 *            of driver.
	 * @param New
	 *            PIN number.
	 * @return
	 */
	public static String updatePIN(String id, String new_pin) {

		String url = "http://paperlessapp.apiary.io/v1/driver?id=" + id
				+ "&mrdToken=" + VariableManager.token + "&driverPin="
				+ PinManager.toMD5(new_pin);

		String response = postData(url);

		String status = "";

		try {
			JSONObject jObject = new JSONObject(response);
			status = jObject.getJSONObject("response").getJSONObject("driver")
					.getString("status");

		} catch (JSONException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
			return "";
			// Oops
		}

		if (VariableManager.DEBUG) {
			Log.d(TAG, "token: " + status);
		}
		return status;
	}

	/**
	 * Retrieves list of drivers from server. Used to populate the list at
	 * login.
	 * 
	 * @param IMEI
	 * @param token
	 * @return
	 */
	public static JSONArray getDrivers() {

		String url = "http://paperlessapp.apiary.io/v1/drivers?imei="
				+ VariableManager.IMEI_TEST + "&mrdtoken="
				+ VariableManager.token;
		String response = getInputStreamFromUrl(url);

		try {
			JSONObject jObject = new JSONObject(response);
			ServerInterface.drivers_jArray = jObject.getJSONObject("response")
					.getJSONArray("drivers");

		} catch (JSONException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
		}

		if (VariableManager.DEBUG) {
			// Log.d(TAG, "token: " + token);
		}

		return ServerInterface.drivers_jArray;
	}

	/**
	 * Submit driver authentication request (login). Receives success status.
	 * 
	 * @param PIN
	 * @return
	 */
	public static String authDriver(String PIN) {
		String url = "http://paperlessapp.apiary.io/v1/auth/driver?imei="
				+ VariableManager.IMEI_TEST + "&mrdtoken="
				+ VariableManager.token + "&driverPIN=" + PIN;

		String response = getInputStreamFromUrl(url);

		// System.out.println(response);

		String status = "";

		try {
			JSONObject jObject = new JSONObject(response);
			status = jObject.getJSONObject("response").getJSONObject("auth")
					.getString("status");

		} catch (JSONException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
		}

		if (VariableManager.DEBUG) {
			// Log.d(TAG, "token: " + token);
		}

		return status;
	}

	/**
	 * Retrieves consignments (bags), for the specified driver ID.
	 * 
	 * @param userid
	 *            of driver.
	 * @return
	 */
	public static JSONArray getConsignments(String userid) {

		String url = "http://paperlessapp.apiary.io/v1/bags/driver?id="
				+ userid + "&mrdToken=" + VariableManager.token;

		String response = getInputStreamFromUrl(url);

		try {
			JSONObject jObject = new JSONObject(response);

			return jObject.getJSONObject("response").getJSONArray(
					"bags");

		} catch (JSONException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
			return null;
		}
	}

	/**
	 * Perform HTTP GET request.
	 * 
	 * @param url
	 * @return
	 */
	// http://www.androidsnippets.com/executing-a-http-get-request-with-httpclient
	public static String getInputStreamFromUrl(String url) {
		if (VariableManager.DEBUG) {
			Log.d(TAG, "Fetching JSON from " + url);
		}

		try {
			HttpGet request = new HttpGet(url);

			// Depends on your web service
			request.setHeader("Content-type", "application/json");

			DefaultHttpClient httpclient = new DefaultHttpClient(
					new BasicHttpParams());

			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();

			InputStream inputStream = entity.getContent();

			// json is UTF-8 by default
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			String result = sb.toString();

			return result;

		} catch (UnknownHostException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			// sw.toString();
			Log.d("updateDb()", sw.toString());
			return "";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
			return "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.d(TAG, sw.toString());
			return "";
		}
	}

	/**
	 * Perform HTTP POST request.
	 * 
	 * @param url
	 * @return
	 */
	// http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
	public static String postData(String url) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		try {
			// Add your data
			// List<NameValuePair> nameValuePairs = new
			// ArrayList<NameValuePair>(2);
			// nameValuePairs.add(new BasicNameValuePair("id", "12345"));
			// nameValuePairs.add(new BasicNameValuePair("stringdata",
			// "AndDev is Cool!"));
			// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();

			InputStream inputStream = entity.getContent();

			// json is UTF-8 by default
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			String result = sb.toString();

			return result;

		} catch (ClientProtocolException e) {
			return "";
			// TODO Auto-generated catch block
		} catch (IOException e) {
			return "";
			// TODO Auto-generated catch block
		}
	}

}
