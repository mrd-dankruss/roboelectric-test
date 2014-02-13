package fi.gfarr.mrd.datatype;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class DeliveryHandoverDataObjectArray implements Parcelable
{

	private List<DeliveryHandoverDataObject> mObjList; // MyClass should implement Parcelable
														// properly

	// ==================== Parcelable ====================
	public int describeContents()
	{
		return 0;
	}

	public void writeToParcel(Parcel out, int flags)
	{
		out.writeList(mObjList);
	}

	private DeliveryHandoverDataObjectArray(Parcel in)
	{
		mObjList = new ArrayList<DeliveryHandoverDataObject>();
		in.readList(mObjList, getClass().getClassLoader());
	}

	public static final Parcelable.Creator<DeliveryHandoverDataObjectArray> CREATOR = new Parcelable.Creator<DeliveryHandoverDataObjectArray>()
	{
		public DeliveryHandoverDataObjectArray createFromParcel(Parcel in)
		{
			return new DeliveryHandoverDataObjectArray(in);
		}

		public DeliveryHandoverDataObjectArray[] newArray(int size)
		{
			return new DeliveryHandoverDataObjectArray[size];
		}
	};

}
