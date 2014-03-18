package com.mrdexpress.paperless.datatype;

import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.internal.JsonReader;
import com.mrdexpress.paperless.workflow.JSONObjectHelper;
import com.mrdexpress.paperless.workflow.ObservableJSONObject;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONUtil;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.codehaus.jackson.impl.JsonParserBase;

import java.util.Observable;
import java.util.Observer;

public class DeliveryHandoverDataObject implements Parcelable
{
    public ObservableJSONObject data;

    //JSONObject data;
	/*private String parcelID;
	private boolean parcelScanned;
	private String barcode;*/

	//public DeliveryHandoverDataObject(String parcelID, boolean parcelScanned)
    public DeliveryHandoverDataObject( ObservableJSONObject datain)
	{
		/*this.parcelID = parcelID;
		this.parcelScanned = parcelScanned;
		setBarcode("No Barcode");*/
        this.data = datain;
	}

	public DeliveryHandoverDataObject(Parcel in)
	{
		readFromParcel(in);
	}

	public int getParcelID()
	{
		//return parcelID;
        return JSONObjectHelper.getIntDef(data.get(), "id", -1);
	}

	public boolean isParcelScanned()
	{
        if( data.get().containsKey("scannedtime"))
            return true;
        else
            return false;
	}

	public void setParcelScanned( int newScannedTime)
	{
        data.setInt("scannedtime", newScannedTime);
		//parcelScanned = newScannedStatus;
	}

	/**
	 * @return the barcode
	 */
	public String getBarcode()
	{
        return JSONObjectHelper.getStringDef(data.get(), "barcode", "");
	}

    public String getMDX()
    {
        return JSONObjectHelper.getStringDef( data.get(), "mdx", "");
    }

    public String getXof()
    {
        return JSONObjectHelper.getStringDef( data.get(), "xof", "");     }

	/**
	 * @param barcode
	 *            the barcode to set
	 */
	public void setBarcode(String barcode)
	{
        data.setString("barcode", barcode);
	}

	@Override
	public int describeContents()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		// TODO Auto-generated method stub
		/*dest.writeString(getParcelID());
		dest.writeByte((byte) (isParcelScanned() ? 1 : 0));
		dest.writeString(getBarcode());*/
        dest.writeString( data.getJSON());

	}

	public void readFromParcel(Parcel in)
	{
		/*parcelID = in.readString();
		parcelScanned = in.readByte() != 0; // myBoolean == true if byte != 0
		setBarcode(in.readString());*/
        data.setJSON( in.readString());
    }

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
	{
		public DeliveryHandoverDataObject createFromParcel(Parcel in)
		{
			return new DeliveryHandoverDataObject(in);
		}

		public DeliveryHandoverDataObject[] newArray(int size)
		{
			return new DeliveryHandoverDataObject[size];
		}
	};

}
