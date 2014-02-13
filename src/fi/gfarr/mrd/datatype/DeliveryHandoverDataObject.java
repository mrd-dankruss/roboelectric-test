package fi.gfarr.mrd.datatype;

import android.os.Parcel;
import android.os.Parcelable;

public class DeliveryHandoverDataObject implements Parcelable
{

	private String parcelID;
	private boolean parcelScanned;
	private String barcode;

	public DeliveryHandoverDataObject(String parcelID, boolean parcelScanned)
	{
		this.parcelID = parcelID;
		this.parcelScanned = parcelScanned;
		setBarcode("No Barcode");
	}

	public DeliveryHandoverDataObject(Parcel in)
	{
		readFromParcel(in);
	}

	public String getParcelID()
	{
		return parcelID;
	}

	public boolean isParcelScanned()
	{
		return parcelScanned;
	}

	public void setParcelScanned(boolean newScannedStatus)
	{
		parcelScanned = newScannedStatus;
	}

	/**
	 * @return the barcode
	 */
	public String getBarcode()
	{
		return barcode;
	}

	/**
	 * @param barcode
	 *            the barcode to set
	 */
	public void setBarcode(String barcode)
	{
		this.barcode = barcode;
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
		dest.writeString(getParcelID());
		dest.writeByte((byte) (isParcelScanned() ? 1 : 0));
		dest.writeString(getBarcode());

	}

	public void readFromParcel(Parcel in)
	{
		parcelID = in.readString();
		parcelScanned = in.readByte() != 0; // myBoolean == true if byte != 0
		setBarcode(in.readString());
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
