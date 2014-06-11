package com.mrdexpress.paperless.datatype;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

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
		in.readList(mObjList, mObjList.getClass().getClassLoader());
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
