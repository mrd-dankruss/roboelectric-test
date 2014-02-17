package fi.gfarr.mrd.net;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import fi.gfarr.mrd.db.Bag;
import fi.gfarr.mrd.db.Contact;
import fi.gfarr.mrd.db.DbHandler;
import fi.gfarr.mrd.db.Driver;
import fi.gfarr.mrd.db.Waybill;
import fi.gfarr.mrd.helper.VariableManager;
import fi.gfarr.mrd.security.PinManager;

public class ServerInterface
{

	private final static String TAG = "ServerInterface";

	public static Handler UIHandler = new Handler(Looper.getMainLooper());

	public static void displayToast(final String message)
	{
		UIHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				Log.e(TAG, "Test: " + message);
				Toast.makeText(VariableManager.context, message, Toast.LENGTH_SHORT).show();

				/*CustomToast toast = new CustomToast(VariableManager.CONTEXT, view);
				 toast.setSuccess(true);
				 toast.setText("Delivery completed successfully!");
				 toast.show();*/
			}
		});
	}

	/**
	 * Makes API call to request a new session token.
	 * 
	 * @param IMEI
	 *            number.
	 */
	public static String requestToken(String imei_id)
	{
		String url = "http://paperlessapp.apiary.io/v1/auth/auth?imei=" + imei_id;
		String response = getInputStreamFromUrl(url, null, null);
		// Log.d(TAG, "requestToken(): " + response);
		String token = "";

		try
		{
			JSONObject jObject = new JSONObject(response);
			token = jObject.getJSONObject("response").getJSONObject("auth").getString("token");

		}
		catch (JSONException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());

			if (VariableManager.DEBUG)
			{
				displayToast("JSONException: auth/auth");
			}
			return "";
			// Oops
		}

		if (VariableManager.DEBUG)
		{
			Log.d(TAG, "token: " + token);
		}
		return token;
		// return "WEUasduio768SDUIYS7667uiuasdasd"; // DEBUG!!
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
	public static String updatePIN(String id, String new_pin)
	{

		String url = "http://paperlessapp.apiary.io/v1/driver?id=" + id + "&mrdToken="
				+ VariableManager.token + "&driverPin=" + PinManager.toMD5(new_pin);

		String response = postData(url);

		String status = "";

		try
		{
			JSONObject jObject = new JSONObject(response);
			status = jObject.getJSONObject("response").getJSONObject("driver").getString("status");

		}
		catch (JSONException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			if (VariableManager.DEBUG)
			{
				displayToast("JSONException: driver?id");
			}
			return "";
			// Oops
		}

		if (VariableManager.DEBUG)
		{
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
	public static void getDrivers(Context context, String imei_id)
	{

		String url = "http://paperlessapp.apiary.io/v1/driver/drivers?imei=" + imei_id
				+ "&mrdtoken=" + VariableManager.token;

		String response = getInputStreamFromUrl(url, null, null);

		JSONArray drivers_jArray = null;

		try
		{
			JSONObject jObject = new JSONObject(response);
			drivers_jArray = jObject.getJSONObject("response").getJSONArray("drivers");
		}
		catch (JSONException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
		}

		if (drivers_jArray != null)
		{
			for (int i = 0; i < drivers_jArray.length(); i++)
			{
				try
				{
					// ID
					int id = Integer.parseInt(drivers_jArray.getJSONObject(i).getString(
							VariableManager.JSON_KEY_DRIVER_ID));

					// Name
					String name = drivers_jArray.getJSONObject(i).getString(
							VariableManager.JSON_KEY_DRIVER_FIRSTNAME)
							+ " "
							+ drivers_jArray.getJSONObject(i).getString(
									VariableManager.JSON_KEY_DRIVER_LASTNAME);

					Log.d(TAG, "Driver retrieved: " + name);

					// PIN
					String pin = drivers_jArray.getJSONObject(i).getString(
							VariableManager.JSON_KEY_DRIVER_PIN);

					DbHandler.getInstance(context).addDriver(new Driver(id, name, pin));
				}
				catch (NumberFormatException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					if (VariableManager.DEBUG)
					{
						displayToast("JSONException: driver/drivers");
					}
				}
			}
		}
	}

	/**
	 * Downloads list of managers from API
	 * 
	 * @param context
	 */
	public static void getManagers(Context context, String imei_id)
	{
		String url = "http://paperlessapp.apiary.io/v1/manager/managers?imei=" + imei_id
				+ "&mrdtoken=" + VariableManager.token;
		String response = getInputStreamFromUrl(url, null, null);

		JSONArray managers_jArray = null;

		try
		{
			JSONObject jObject = new JSONObject(response);
			managers_jArray = jObject.getJSONObject("response").getJSONArray("managers");
		}
		catch (JSONException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
		}

		if (managers_jArray != null)
		{
			for (int i = 0; i < managers_jArray.length(); i++)
			{
				try
				{
					// ID
					String id = managers_jArray.getJSONObject(i).getString("id");

					// First Name
					String first_name = managers_jArray.getJSONObject(i).getString("firstName");

					// Last name
					String last_name = managers_jArray.getJSONObject(i).getString("lastName");

					DbHandler.getInstance(context).addManager(id, first_name + " " + last_name);
				}
				catch (NumberFormatException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					if (VariableManager.DEBUG)
					{
						displayToast("JSONException: manager/managers");
					}
				}
			}
		}
	}

	/**
	 * Submit driver authentication request (login). Receives success status.
	 * 
	 * @param PIN
	 * @return
	 */
	public static String authDriver(String PIN, String driver_id, String imei_id)
	{
		String url = "http://paperlessapp.apiary.io/v1/auth/driver?imei=" + imei_id + "&mrdtoken="
				+ VariableManager.token + "&driverPIN=" + PIN + "&driverID=" + driver_id;

		String response = getInputStreamFromUrl(url, null, null);

		// System.out.println(response);

		String status = "";

		try
		{
			JSONObject jObject = new JSONObject(response);
			status = jObject.getJSONObject("response").getJSONObject("auth").getString("status");

		}
		catch (JSONException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			if (VariableManager.DEBUG)
			{
				displayToast("JSONException: auth/driver");
			}
		}

		if (VariableManager.DEBUG)
		{
			// Log.d(TAG, "token: " + token);
		}

		return status;
		// return "success"; // DEBUG!!
	}

	/**
	 * Submit manager authentication request.
	 * 
	 * @param man_id
	 * @param driver_id
	 * @param PIN
	 * @return
	 */
	public static String authManager(String man_id, String driver_id, String PIN, String imei_id)
	{
		String url = "http://paperlessapp.apiary.io/v1/auth/manager?imei=" + imei_id + "&mrdtoken="
				+ VariableManager.token + "&managerPIN=" + PIN + "&managerid=" + man_id
				+ "&driverid=" + driver_id;

		String response = getInputStreamFromUrl(url, null, null);

		// System.out.println(response);

		String status = "";

		try
		{
			JSONObject jObject = new JSONObject(response);
			status = jObject.getJSONObject("response").getJSONObject("auth").getString("status");

		}
		catch (JSONException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			if (VariableManager.DEBUG)
			{
				displayToast("JSONException: auth/manager");
			}
		}

		if (VariableManager.DEBUG)
		{
			// Log.d(TAG, "token: " + token);
		}

		return status;
	}

	/**
	 * Retrieves consignments (bags), for the specified driver ID and adds to DB
	 * 
	 * @param driver_id
	 *            of driver.
	 * @return
	 */
	public static void downloadBags(Context context, String driver_id)
	{
		String url = "http://paperlessapp.apiary.io/v1/bags/driver?id=" + driver_id + "&mrdToken="
				+ VariableManager.token;

		Log.i(TAG, "Fetching " + url);

		try
		{
			String response = getInputStreamFromUrl(url, null, null);

			JSONObject jObject = new JSONObject(response);

			JSONArray result = jObject.getJSONObject("response").getJSONArray("bags");

			if (result != null)
			{

				// Stores waybill IDs as they are loaded.
				// Used to count the number of occurences
				// For counting multiple packages.
				// Hashtable<String, Integer> waybill_IDs = new Hashtable<String, Integer>();

				for (int i = 0; i < result.length(); i++)
				{
					try
					{
						// ID
						String bag_id = result.getJSONObject(i).getString("id");

						// Download more details
						downloadBag(context, bag_id, driver_id);
					}
					catch (NumberFormatException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						if (VariableManager.DEBUG)
						{
							displayToast("JSONException: bags/driver");
						}
					}
				}
			}
		}
		catch (JSONException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			if (VariableManager.DEBUG)
			{
				displayToast("JSONException: bags/driver");
			}
		}
	}

	/**
	 * Retrieve a single bag object
	 * 
	 * @param c
	 * @param bag_id
	 */
	public static void downloadBag(Context context, String bag_id, String driver_id)
	{
		String url = "http://paperlessapp.apiary.io/v1/bag/bag?id=" + bag_id + "&mrdToken="
				+ VariableManager.token;

		Log.i(TAG, "Fetching " + url);

		try
		{
			String response = getInputStreamFromUrl(url, null, null);

			JSONObject jObject = new JSONObject(response);

			JSONObject result = jObject.getJSONObject("response").getJSONObject("bag");

			if (result != null)
			{

				// Stores waybill IDs as they are loaded.
				// Used to count the number of occurences
				// For counting multiple packages.
				Hashtable<String, Integer> waybill_IDs = new Hashtable<String, Integer>();

				try
				{
					// ID
					String id = result.getString("id");

					// Barcode
					String barcode = result.getString("barcode");

					// Status
					String bag_status = result.getString("status");

					// Waybill count
					int waybill_count = result.getInt("waybillcount");

					// scanned?
					boolean scanned = result.getBoolean("scanned");

					// ---- Destination
					// Address
					String dest_hubname = result.getJSONObject("destination").getString("hubname");
					String dest_hubcode = result.getJSONObject("destination").getString("hubcode");
					String dest_address = result.getJSONObject("destination")
							.getJSONObject("address").getString("address");
					String dest_suburb = result.getJSONObject("destination")
							.getJSONObject("address").getString("suburb");
					String dest_town = result.getJSONObject("destination").getJSONObject("address")
							.getString("town");
					String dest_contact1 = result.getJSONObject("destination")
							.getJSONObject("address").getString("contact1");
					String dest_lat = result.getJSONObject("destination").getJSONObject("address")
							.getJSONObject("coords").getString("lat");
					String dest_long = result.getJSONObject("destination").getJSONObject("address")
							.getJSONObject("coords").getString("lon");

					// Go through temp array to find number of times the
					// current waybill ID occurs.

					// Add bag to DB
					Bag bag = new Bag(id);
					bag.setDriverId(driver_id);
					bag.setBarcode(barcode);
					bag.setDestinationHubCode(dest_hubcode);
					bag.setDestinationHubName(dest_hubname);
					bag.setDestinationAddress(dest_address);
					bag.setDestinationSuburb(dest_suburb);
					bag.setDestinationTown(dest_town);
					bag.setDestinationLat(dest_lat);
					bag.setDestinationLong(dest_long);
					bag.setDestinationContact(dest_contact1);
					bag.setScanned(scanned);
					bag.setNumberItems(waybill_count);
					bag.setStatus(bag_status);

					JSONArray json_contacts = result.getJSONArray("contacts");
					ArrayList<Contact> contacts = new ArrayList<Contact>();

					bag.setContacts(contacts);

					Log.d(TAG, "Bag " + id + " added: "
							+ DbHandler.getInstance(context).addBag(bag));

					for (int i = 0; i < json_contacts.length(); i++)
					{
						String name = json_contacts.getJSONObject(i).getString("name");
						String number = json_contacts.getJSONObject(i).getString("number");
						contacts.add(new Contact(name, number));
						Log.d(TAG, "Contact " + name + " added: "
								+ DbHandler.getInstance(context).addContact(name, number, id));
					}

					// --- Waybills ---

					JSONArray waybills = result.getJSONArray("waybills");

					// Load each waybill in bag
					for (int j = 0; j < waybills.length(); j++)
					{

						// Tel
						// String tel = waybills.getJSONObject(j).getString("telephone");

						// Weight
						String weight = waybills.getJSONObject(j).getJSONObject("dimensions")
								.getString("weight")
								+ "kg";

						// Dimensions
						String dimen = waybills.getJSONObject(j).getString("dimensions");

						// Waybill ID
						String waybill_id = waybills.getJSONObject(j).getString("id");

						// barcode
						String waybill_barcode = waybills.getJSONObject(j).getString("barcode");

						// Dimensions
						String dimensions = waybills.getJSONObject(j).getJSONObject("dimensions")
								.getString("width")
								+ "mmX"
								+ waybills.getJSONObject(j).getJSONObject("dimensions")
										.getString("height")
								+ "mmX"
								+ waybills.getJSONObject(j).getJSONObject("dimensions")
										.getString("length") + "mm";

						// status
						String status = waybills.getJSONObject(j).getString("status");

						// ---- Delivery address
						// Address
						String address = waybills.getJSONObject(j).getJSONObject("deliveryaddress")
								.getString("address");
						String suburb = waybills.getJSONObject(j).getJSONObject("deliveryaddress")
								.getString("suburb");
						String town = waybills.getJSONObject(j).getJSONObject("deliveryaddress")
								.getString("town");
						String lat = waybills.getJSONObject(j).getJSONObject("deliveryaddress")
								.getJSONObject("coords").getString("lat");
						String lon = waybills.getJSONObject(j).getJSONObject("deliveryaddress")
								.getJSONObject("coords").getString("lon");

						// --- Customer
						String name = waybills.getJSONObject(j).getJSONObject("customer")
								.getString("name");
						String idnumber = waybills.getJSONObject(j).getJSONObject("customer")
								.getString("idnumber");
						String contact1 = waybills.getJSONObject(j).getJSONObject("customer")
								.getString("contact1");
						String contact2 = waybills.getJSONObject(j).getJSONObject("customer")
								.getString("contact2");
						String email = waybills.getJSONObject(j).getJSONObject("customer")
								.getString("email");

						// comlog
						// comlog is a JSONArray ***
						// String comlog = waybills.getJSONObject(j).getString("comlog");

						// parcel count
						int parcel_count = waybills.getJSONObject(j).getInt("parcelcount");

						// Create Waybill object and add values
						Waybill waybill = new Waybill(waybill_id, id);
						waybill.setEmail(email);
						waybill.setBarcode(waybill_barcode);
						waybill.setDimensions(dimensions);
						waybill.setStatus(status);
						waybill.setDeliveryTown(town);
						waybill.setDeliverySuburb(suburb);
						waybill.setDeliveryAddress(address);
						waybill.setDeliveryLat(lat);
						waybill.setDeliveryLong(lon);
						waybill.setCustomerContact1(contact1);
						waybill.setCustomerContact2(contact2);
						waybill.setCustomerID(idnumber);
						waybill.setCustomerName(name);
						// waybill.setComLog(comlog);
						waybill.setWeight(weight);
						waybill.setParcelCount(parcel_count);

						// Add ID to hashtable
						Integer current_count = waybill_IDs.get(waybill_id);

						// Calculate how many times the current waybill ID
						// has occurred already
						if (current_count != null)
						{
							// Increment occurence count of the waybill
							waybill_IDs.put(waybill_id, current_count + 1);

							// nth occurance of this waybill
							waybill.setParcelSeq(current_count + 1);
						}
						else
						{
							// First occurance of this waybill
							waybill.setParcelSeq(1);
						}

						Log.d(TAG,
								"Waybill " + waybill_id + " added: "
										+ DbHandler.getInstance(context).addWaybill(waybill));

						Log.i(TAG, "Bag list fetched.");
					}
				}
				catch (NumberFormatException e)
				{
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					Log.e(TAG, sw.toString());
				}
				catch (JSONException e)
				{
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					Log.e(TAG, sw.toString());
					if (VariableManager.DEBUG)
					{
						displayToast("JSONException: bags/bag");
					}
				}

			}
		}
		catch (JSONException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
		}
	}

	public static void downloadDelays(Context context)
	{
		String url = "http://paperlessapp.apiary.io/v1/milkruns/delays?mrdToken="
				+ VariableManager.token;

		Log.i(TAG, "Fetching " + url);

		try
		{
			String response = getInputStreamFromUrl(url, null, null);

			JSONObject jObject = new JSONObject(response);

			JSONArray result = jObject.getJSONArray("response");

			ContentValues values;

			if (result != null)
			{
				// Stores waybill IDs as they are loaded.
				// Used to count the number of occurences
				// For counting multiple packages.
				// Hashtable<String, Integer> waybill_IDs = new Hashtable<String, Integer>();

				for (int i = 0; i < result.length(); i++)
				{
					try
					{
						values = new ContentValues();

						// ID
						String delay_id = result.getJSONObject(i).getString("id");

						// Reason
						String reason = result.getJSONObject(i).getString("name");

						// Durations
						JSONArray durations = result.getJSONObject(i).getJSONArray("items");

						for (int d = 0; d < durations.length(); d++)
						{
							// Duration ID
							String duration_id = durations.getJSONObject(d).getString("id");

							// Duration
							String duration = durations.getJSONObject(d).getString("name");

							values.put(DbHandler.C_DELAYS_ID, delay_id);
							values.put(DbHandler.C_DELAYS_REASON, reason);
							values.put(DbHandler.C_DELAYS_DURATION_ID, duration_id);
							values.put(DbHandler.C_DELAYS_DURATION, duration);

							Log.d(TAG, reason + " " + duration);

							DbHandler.getInstance(context).addRow(DbHandler.TABLE_DELAYS, values);
						}
					}
					catch (NumberFormatException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						if (VariableManager.DEBUG)
						{
							displayToast("JSONException: milkruns/delays");
						}
					}
				}
			}
		}
		catch (JSONException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			if (VariableManager.DEBUG)
			{
				displayToast("JSONException: milkruns/delays");
			}
		}
	}

	public static void downloadFailedDeliveryReasons(Context context)
	{
		String url = "http://paperlessapp.apiary.io/v1/milkruns/handover?mrdToken="
				+ VariableManager.token;

		Log.i(TAG, "Fetching " + url);

		try
		{
			String response = getInputStreamFromUrl(url, null, null);

			JSONObject jObject = new JSONObject(response);

			JSONArray result = jObject.getJSONArray("response");

			ContentValues values;

			if (result != null)
			{
				// Stores waybill IDs as they are loaded.
				// Used to count the number of occurences
				// For counting multiple packages.
				// Hashtable<String, Integer> waybill_IDs = new Hashtable<String, Integer>();

				for (int i = 0; i < result.length(); i++)
				{
					try
					{
						values = new ContentValues();

						// ID
						String reason_id = result.getJSONObject(i).getString("id");

						// Reason
						String reason_name = result.getJSONObject(i).getString("name");

						values.put(DbHandler.C_FAILED_HANDOVER_REASONS_ID, reason_id);
						values.put(DbHandler.C_FAILED_HANDOVER_REASONS_NAME, reason_name);

						DbHandler.getInstance(context).addRow(
								DbHandler.TABLE_FAILED_HANDOVER_REASONS, values);
					}
					catch (NumberFormatException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						if (VariableManager.DEBUG)
						{
							displayToast("JSONException: milkruns/handover");
						}
					}
				}
			}
		}
		catch (JSONException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			if (VariableManager.DEBUG)
			{
				displayToast("JSONException: milkruns/handover");
			}
		}
	}

	public static void downloadPartialDeliveryReasons(Context context)
	{
		String url = "http://paperlessapp.apiary.io/v1/milkruns/partial?mrdToken="
				+ VariableManager.token;

		Log.i(TAG, "Fetching " + url);

		try
		{
			String response = getInputStreamFromUrl(url, null, null);

			JSONObject jObject = new JSONObject(response);

			JSONArray result = jObject.getJSONArray("response");

			ContentValues values;

			if (result != null)
			{
				// Stores waybill IDs as they are loaded.
				// Used to count the number of occurences
				// For counting multiple packages.
				// Hashtable<String, Integer> waybill_IDs = new Hashtable<String, Integer>();

				for (int i = 0; i < result.length(); i++)
				{
					try
					{
						values = new ContentValues();

						// ID
						String reason_id = result.getJSONObject(i).getString("id");

						// Reason
						String reason_name = result.getJSONObject(i).getString("name");

						values.put(DbHandler.C_PARTIAL_DELIVERY_REASONS_ID, reason_id);
						values.put(DbHandler.C_PARTIAL_DELIVERY_REASONS_NAME, reason_name);

						DbHandler.getInstance(context).addRow(
								DbHandler.TABLE_PARTIAL_DELIVERY_REASONS, values);
					}
					catch (NumberFormatException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						if (VariableManager.DEBUG)
						{
							displayToast("JSONException: milkruns/partial");
						}
					}
				}
			}
		}
		catch (JSONException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			if (VariableManager.DEBUG)
			{
				displayToast("JSONException: milkruns/partial");
			}
		}
	}

	/**
	 * Perform HTTP GET request.
	 * 
	 * @param url
	 * @return
	 */
	// http://www.androidsnippets.com/executing-a-http-get-request-with-httpclient

	public static String getInputStreamFromUrl(final String url, Activity activity, View view)
	{
		if (VariableManager.DEBUG)
		{
			Log.d(TAG, "Fetching JSON from " + url);
		}
		try
		{
			final int CONN_WAIT_TIME = 5000;
			final int CONN_DATA_WAIT_TIME = 8000;

			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, CONN_WAIT_TIME);
			HttpConnectionParams.setSoTimeout(httpParams, CONN_DATA_WAIT_TIME);

			HttpGet request = new HttpGet(url);

			// Depends on your web service
			request.setHeader("Content-type", "application/json");

			// DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
			// DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);
			DefaultHttpClient httpclient = getNewHttpClient();

			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();

			InputStream inputStream = entity.getContent();

			// json is UTF-8 by default
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"),
					8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}

			return sb.toString();
		}
		catch (UnknownHostException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			// sw.toString();
			Log.e("updateDb()", sw.toString());
			return "";
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			return "";
		}
		catch (SocketTimeoutException e)
		{
			// TODO Auto-generated catch block
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			if (VariableManager.DEBUG)
			{
				displayToast("SocketTimeoutException");
			}
			return "";
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			return "";
		}
	}

	/**
	 * Post a delay to API
	 * 
	 * @param bagid
	 * @param driverid
	 * @param delayid
	 * @return
	 */
	public static String postDelay(String bagid, String driverid, String delayid)
	{
		return postData("http://paperlessapp.apiary.io/v1/milkruns/delays?bagid=" + bagid
				+ "&driverid=" + driverid + "&mrdToken=" + VariableManager.token + "&delayid="
				+ delayid);
	}

	/**
	 * POST a communication message to server.
	 * 
	 * @param message
	 * @param bagid
	 * @param type
	 * @param result
	 * @return
	 */
	public static String postMessage(String message, String number, String bagid, String type,
			boolean result)
	{
		// Convert bool to String
		String result_string = "false";
		if (result)
		{
			result_string = "true";
		}

		// Make JSON notation
		JSONObject comExtra = new JSONObject();
		try
		{
			comExtra.put("message", message);
			comExtra.put("number", number);
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*return postData("http://paperlessapp.apiary.io/v1/waybill/communication?id=" + bagid
				+ "&comType=" + type + "&mrdToken=" + VariableManager.token + "&comResult="
				+ result_string + "&comExtra=" + comExtra.toString());*/

		String status = null;

		try
		{
			status = doJSONPOST(
					"http://paperlessapp.apiary.io/v1/waybill/communication?id=" + bagid
							+ "&comType=" + type + "&mrdToken=" + VariableManager.token
							+ "&comResult=" + result_string, comExtra, 5000)
					.getJSONObject("response").getJSONObject("waybill").getString("status");
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}
		return status;
	}

	/**
	 * Post failed handover to API.
	 * 
	 * @param bag_id
	 * @param reason_id
	 *            IDs acquired from /milkrun/handover
	 * @return
	 */
	public static String postFailedHandover(String bag_id, String reason_id)
	{
		return postData("http://paperlessapp.apiary.io/v1/milkruns/handover?bagid=" + bag_id
				+ "&handoverid=" + reason_id + "&mrdToken=" + VariableManager.token);
	}

	/**
	 * Post partial delivery to API.
	 * 
	 * @param waybill_id
	 * @param status_id
	 * @param extra
	 * @return
	 */
	public static String postPartialDelivery(String waybill_id, String status_id, String extra)
	{
		return postData("http://paperlessapp.apiary.io/v1/waybill/delivery?id=" + waybill_id
				+ "&deliveryid=" + status_id + "&mrdToken=" + VariableManager.token);
	}

	/**
	 * Perform HTTP POST request.
	 * 
	 * @param url
	 * @return
	 */
	// http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
	public static String postData(String url)
	{
		// Create a new HttpClient and Post Header
		// HttpClient httpclient = new DefaultHttpClient();
		HttpClient httpclient = getNewHttpClient();
		HttpPost httppost = new HttpPost(url);

		try
		{
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
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"),
					8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
			String result = sb.toString();

			return result;

		}
		catch (ClientProtocolException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			return "";
			// TODO Auto-generated catch block
		}
		catch (IOException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			return "";
			// TODO Auto-generated catch block
		}
	}

	public static String convertInputStreamToString(InputStream is) throws Exception
	{
		BufferedReader rd = new BufferedReader(inputStreamToReader(new BufferedInputStream(is)),
				4096);// Checks for BOM, should still work if BOM not present
		String line;
		StringBuilder sb = new StringBuilder();
		try
		{
			while ((line = rd.readLine()) != null)
			{
				sb.append(line);
			}
			rd.close();
		}
		catch (Exception e)
		{
			throw e;
		}

		return sb.toString();
	}

	// This can be used to setup a DefaultHttpClient to ignore certificate errors over https or
	// connect to non default ports
	public static DefaultHttpClient getNewHttpClient()
	{
		try
		{
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		}
		catch (Exception e)
		{
			return new DefaultHttpClient();
		}
	}

	public static JSONObject doJSONPOST(String url, JSONObject json, int timeout) throws Exception
	{
		Hashtable<String, String> temp_header_item;
		JSONObject toreturn;
		StringEntity temp_entity;
		InputStream content = null;
		int default_connection_timeout = 5000;

		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);

			// Timeouts
			HttpConnectionParams.setConnectionTimeout(httpclient.getParams(),
					default_connection_timeout);// throws java.net.SocketTimeoutException : Socket
												// is not connected
			HttpConnectionParams.setSoTimeout(httpclient.getParams(), timeout);// throws
																				// java.net.SocketTimeoutException
																				// : The operation
																				// timed out

			temp_entity = new StringEntity(json.toString());
			temp_entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

			httppost.setEntity(temp_entity);

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			content = response.getEntity().getContent();

			temp_header_item = new Hashtable<String, String>();
			for (int i = 0; i < response.getAllHeaders().length; i++)
			{
				temp_header_item = new Hashtable<String, String>();
				temp_header_item.put(response.getAllHeaders()[i].getName().toLowerCase(),
						response.getAllHeaders()[i].getValue());
			}

			toreturn = new JSONObject(convertInputStreamToString(content));

			httpclient.getConnectionManager().shutdown();
		}
		catch (Exception e)
		{
			throw e;
		}

		return toreturn;
	}

	/**
	 * Check if BOM is present and use it to determine encoding
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static Reader inputStreamToReader(BufferedInputStream in) throws IOException
	{
		in.mark(3);// Need to decorate InputStream with BufferedInputStream to enable mark and reset
					// functionality
		int byte1 = in.read();
		int byte2 = in.read();

		if (byte1 == 0xFF && byte2 == 0xFE)
		{
			Log.i("[HttpInterface]", "Stream has BOM and is encoded in UTF-16LE");
			return new InputStreamReader(in, "UTF-16LE");
		}
		else if (byte1 == 0xFF && byte2 == 0xFF)
		{
			Log.i("[HttpInterface]", "Stream has BOM and is encoded in UTF-16BE");
			return new InputStreamReader(in, "UTF-16BE");
		}
		else
		{
			int byte3 = in.read();
			if (byte1 == 0xEF && byte2 == 0xBB && byte3 == 0xBF)
			{
				Log.i("[HttpInterface]", "Stream has BOM and is encoded in UTF-8");
				return new InputStreamReader(in, "UTF-8");
			}
			else
			{
				Log.i("[HttpInterface]",
						"Stream has no BOM falling back to ISO 8859_1 (ISO-Latin-1)");
				in.reset();
				return new InputStreamReader(in);
			}
		}
	}

	/**
	 * Retrieve a single bag object
	 * 
	 * @param c
	 * @param bag_id
	 */
	public static String scanBag(Context context, String barcode)
	{
		String id = "";
		
		String url = "http://paperlessapp.apiary.io/v1/bags/scan?barcode=" + barcode + "&mrdToken="
				+ VariableManager.token;

		Log.i(TAG, "Fetching " + url);

		try
		{
			String response = getInputStreamFromUrl(url, null, null);

			JSONObject jObject = new JSONObject(response);

			JSONObject result = jObject.getJSONObject("response").getJSONObject("bag");

			if (result != null)
			{

				// Stores waybill IDs as they are loaded.
				// Used to count the number of occurences
				// For counting multiple packages.
				Hashtable<String, Integer> waybill_IDs = new Hashtable<String, Integer>();

				try
				{
					// ID
					id = result.getString("id");

				}
				catch (NumberFormatException e)
				{
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					Log.e(TAG, sw.toString());
				}
				catch (JSONException e)
				{
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					Log.e(TAG, sw.toString());
					if (VariableManager.DEBUG)
					{
						displayToast("JSONException: bags/bag");
					}
				}

			}
		}
		catch (JSONException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
		}
		
		return id;
	}

	public static String setNextDelivery(String id)
	{

		String url = "http://paperlessapp.apiary.io/v1/waybill/setnext?id=" + id + "&mrdToken="
				+ VariableManager.token;

		String response = postData(url);

		String status = "";

		try
		{
			JSONObject jObject = new JSONObject(response);
			status = jObject.getJSONObject("response").getString("status");

		}
		catch (JSONException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.e(TAG, sw.toString());
			if (VariableManager.DEBUG)
			{
				displayToast("JSONException: driver?id");
			}
			return "";
			// Oops
		}

		if (VariableManager.DEBUG)
		{
			Log.d(TAG, "token: " + status);
		}
		return status;
	}
	
}
