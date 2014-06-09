/**
 * 
 */
package com.mrdexpress.paperless.net;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author greg
 * 
 */
public class CallQueueObject
{
	private String url;
	private String json;

	public CallQueueObject(String url, String json)
	{
		setUrl(url);
		setJSON(json);
	}

	/**
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}

	/**
	 * @return the json
	 */
	public JSONObject getJSON()
	{
		JSONObject obj = null;
		try
		{
			obj = new JSONObject(json);
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * @param json
	 *            the json to set
	 */
	public void setJSON(String json)
	{
		this.json = json;
	}

}
