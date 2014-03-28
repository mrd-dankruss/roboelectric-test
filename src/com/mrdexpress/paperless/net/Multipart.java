package com.mrdexpress.paperless.net;

import java.io.ByteArrayOutputStream;

public class Multipart 
{
	public static final String BOUNDRY = "----------V2ymHFg03ehbqgZCaKO6jy";
	
	public static final String MIME_TYPE_JSON = "text/json";
	public static final String MIME_TYPE_JPEG = "image/jpeg";
	
	private byte[] bytes;
	
	public Multipart(String key, byte[] data, String content_type) throws Exception
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(("--"+BOUNDRY+"\r\n").getBytes());
		
		baos.write(("Content-Disposition: form-data; name=\""+key+"\"; filename=\""+key+"\"\r\n" +
				"Content-Type: " + content_type + "\r\n" +
				"\r\n").getBytes());
		baos.write(data);
		baos.write(("\r\n").getBytes());
		
		bytes = baos.toByteArray();
		baos.close();
	}
	
	public byte[] getBytes()
	{
		return bytes;
	}
}
