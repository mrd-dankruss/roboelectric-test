package com.mrdexpress.paperless.datatype;

import android.os.Parcel;
import android.os.Parcelable;
import com.mrdexpress.paperless.workflow.JSONObjectHelper;
import com.mrdexpress.paperless.workflow.ObservableJSONObject;

public class UserItem implements Parcelable
{
    private ObservableJSONObject data;

	public enum UserType
	{
		DRIVER, MANAGER
	}

    public UserItem( ObservableJSONObject person)
	{
        this.data = person;
	}

    public UserItem(Parcel in)
    {
        readFromParcel(in);
    }

    public int describeContents()
    {
        return 0;
    }

	public int getUserID()
	{
		return JSONObjectHelper.getIntDef(data.get(), "id", -1);
	}

	public String getUserName()
	{
        return JSONObjectHelper.getStringDef( data.get(), "firstName", "!") + " " + JSONObjectHelper.getStringDef( data.get(), "lastName", "!");
	}

    public String getFirstName()
    {
        return JSONObjectHelper.getStringDef( data.get(), "firstName", "!");
    }

    public String getLastName()
    {
        return JSONObjectHelper.getStringDef( data.get(), "lastName", "!");
    }

	public UserType getUserType()
	{
        String role = JSONObjectHelper.getStringDef( data.get(), "role", "!");
        if( role.contains("MANAGER"))
            return UserType.MANAGER;
        return UserType.DRIVER;
	}

    public boolean isPinSet()
    {
        return !JSONObjectHelper.getStringDef( data.get(), "driverPin", "").trim().equals("");
    }

    public String getPin()
    {
        return JSONObjectHelper.getStringDef( data.get(), "driverPin", null);
    }


	@Override
	public String toString()
	{
		return getUserName();
	}

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString( data.getJSON());
    }

    public void readFromParcel(Parcel in)
    {
        data.setJSON( in.readString());
    }

    public static final Parcelable.Creator<UserItem> CREATOR = new Parcelable.Creator<UserItem>()
    {
        public UserItem createFromParcel(Parcel in)
        {
            return new UserItem(in);
        }

        public UserItem[] newArray(int size)
        {
            return new UserItem[size];
        }
    };
}
