package com.mrdexpress.paperless.net;

import java.io.File;

import android.util.Log;

public class PostPart
{
	public static final String MIME_TYPE_PNG = "image/png";
	public static final String MIME_TYPE_JPG = "image/jpg";
	public static final String MIME_TYPE_ZIP = "application/zip";
	
	public String key;
	public Object content_body;
	public String mime_type;
	
	public PostPart(String key, Object content_body) throws Exception
	{
		if (content_body instanceof File)
		{
			throw new Exception("Mime type needs to be set for a post part containing a File");
		}
		this.key = key;
		this.content_body = content_body;
	}
	
	public PostPart(String key, Object content_body, String mime_type) throws Exception
	{
		if ( (content_body instanceof File) && (mime_type.compareTo("") == 0) )
		{
			throw new Exception("Mime type needs to be set for a post part containing a File");
		}
		Log.e("[PostPart]", "Image");
		this.key = key;
		this.content_body = content_body;
		this.mime_type = mime_type;
	}
}