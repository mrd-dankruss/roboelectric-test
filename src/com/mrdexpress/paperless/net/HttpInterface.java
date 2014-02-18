package com.mrdexpress.paperless.net;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import android.util.Log;

public class HttpInterface 
{
	private static int CONNECTION_TIMEOUT = 10000; //10 seconds
	private static int READ_TIMEOUT = 300000; //20 seconds, now 5min
	
	public static byte[] doGet(String url, Hashtable<String, String> params) throws Exception
	{
		byte[] bytes = null;
		
		String key;
		String value;
		
		if (params != null)
		{
			Enumeration<String> keys = params.keys();
			url += "?";
			
			while (keys.hasMoreElements())
			{
				key = keys.nextElement();
				value = params.get(key);
				
				url += key + "=" + value + "&";
			}
			
			url = url.substring(0, url.length() - 1);
		}
		
		Log.i("[HttpInterface]", "Connecting to: " + url);
		HttpURLConnection con = (HttpURLConnection)(new URL(url)).openConnection();
		con.setConnectTimeout(CONNECTION_TIMEOUT);
		con.setReadTimeout(READ_TIMEOUT);
		InputStream is = con.getInputStream();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i;
		
		while ((i = is.read()) != -1)
		{
			baos.write(i);
		}
        
		bytes = baos.toByteArray();
		
		return bytes;
	}
	
	public static byte[] sendJSON(String url, JSONObject json) throws Exception
	{
		byte[] result = null;
		
		URL u = new URL(url);
		HttpURLConnection con = (HttpURLConnection)u.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-type", "text/json");
		con.setConnectTimeout(CONNECTION_TIMEOUT);
		con.setReadTimeout(READ_TIMEOUT);
		OutputStream os = con.getOutputStream();
		os.write(json.toString().getBytes());
		InputStream is = con.getInputStream();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i;
		
		while ((i = is.read()) != -1)
		{
			baos.write(i);
		}
        
		result = baos.toByteArray();
		
		return result;
	}
	
	public static byte[] sendMultipart(String url, byte[] parts) throws Exception
	{

		
		Log.i("[HttpInterface]", new String(parts));
		
		byte[] result = null;
		
		URL u = new URL(url);
		HttpURLConnection con = (HttpURLConnection)u.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-type", "multipart/form-data; boundary="+Multipart.BOUNDRY);
		con.setConnectTimeout(CONNECTION_TIMEOUT);
		con.setReadTimeout(READ_TIMEOUT);
		OutputStream os = con.getOutputStream();
		os.write(parts);
		InputStream is = con.getInputStream();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i;
		
		while ((i = is.read()) != -1)
		{
			baos.write(i);
		}
        
		result = baos.toByteArray();
		
		return result;
	}
	
	public static JSONObject getInputStreamFromPOSTMultipart(String url, ArrayList<PostPart> values) throws Exception
	{
		int i;
		int num_values;
		MultipartEntity temp_entity;
		JSONObject toreturn;
		InputStream content = null;
		Hashtable<String, String> temp_header_item;
		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			//httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1); Not sure if needed
			HttpPost httppost = new HttpPost(url);
Log.e("Posting to ", url);
			// Timeouts
			HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), CONNECTION_TIMEOUT);// Throws java.net.SocketTimeoutException : Socket is not connected
			HttpConnectionParams.setSoTimeout(httpclient.getParams(), CONNECTION_TIMEOUT);// Throws java.net.SocketTimeoutException : The operation timed out
			temp_entity = new MultipartEntity();
Log.e("MultipartInstance", "created");
			num_values = values.size();
Log.e("HttpInterface", "num values "+num_values);
			for (i = 0;i < num_values;i++)
			{
				if (values.get(i).content_body instanceof File)
				{
Log.e("HttpInterface", "file "+values.get(i).key);
					temp_entity.addPart(values.get(i).key, new FileBody((( File ) values.get(i).content_body ), values.get(i).mime_type ));
Log.e("HttpInterface", "Added value to temp_entiy "+values.get(i).key);
				}
				else// String
				{
Log.e("HttpInterface", "String Key"+values.get(i).key);
					temp_entity.addPart(values.get(i).key, new StringBody( values.get(i).content_body.toString() , "text/plain", Charset.forName( "UTF-8" )));
				}
			}
Log.e("HttpInterface", "Tempy Entity ..."+temp_entity.toString());			
			httppost.setEntity(temp_entity);
Log.e("HttpInterface", "HttpClient to executed");
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
Log.e("HttpInterface", "HttpClient executed");
			content = response.getEntity().getContent();
Log.e("HttpInterface", "HttpContent "+content.toString());
			temp_header_item = new Hashtable<String, String>();
			for (i = 0;i < response.getAllHeaders().length;i++)
			{
Log.i("[HTTP HEADER LOOP]", "");
			temp_header_item = new Hashtable<String, String>();
			temp_header_item.put(response.getAllHeaders()[i].getName().toLowerCase(), response.getAllHeaders()[i].getValue());
			}
			String s = convertInputStreamToString(content);
			toreturn = new JSONObject(s);//new HTTPReturnType(temp_header_item, convertInputStreamToString(content));
			httpclient.getConnectionManager().shutdown();
		}
		catch (Exception e)
		{
			throw e;
		}
		
		return toreturn;
	}
	
	public static String convertInputStreamToString(InputStream is) throws Exception
	{
		BufferedReader rd = new BufferedReader(inputStreamToReader(new BufferedInputStream(is)), 4096);// Checks for BOM, should still work if BOM not present
		String line;
		StringBuilder sb =  new StringBuilder();
		try
		{
			while ( (line = rd.readLine()) != null )
			{
				sb.append(line);
			}
			rd.close();
		}
		catch(Exception e)
		{
			throw e;
		}
		
		Log.i("[HTTP CONTENT]", ""+sb.toString());
		return sb.toString();
	}
	
	public static Reader inputStreamToReader(BufferedInputStream in) throws IOException// Check if BOM is present and use it to determine encoding
	{
		in.mark(3);// Need to decorate InputStream with BufferedInputStream to enable mark and reset functionality
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
				Log.i("[HttpInterface]", "Stream has no BOM falling back to ISO 8859_1 (ISO-Latin-1)");
				in.reset();
				return new InputStreamReader(in);
			}
		}
	}
}
