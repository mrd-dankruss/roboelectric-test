package com.mrdexpress.paperless.datatype;

import android.os.Parcel;
import android.os.Parcelable;
import com.mrdexpress.paperless.db.Device;
import com.mrdexpress.paperless.workflow.JSONObjectHelper;
import com.mrdexpress.paperless.workflow.ObservableJSONObject;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gary on 2014/04/01.
 */
public class StopItem implements Parcelable
{
    private ObservableJSONObject data;

    public StopItem( ObservableJSONObject stop)
    {
        this.data = stop;
    }

    public StopItem(Parcel in)
    {
        readFromParcel(in);
    }

    public int describeContents()
    {
        return 0;
    }

    public String getIDs()
    {
        // stops is an array, eg: "{164,163}"
        return JSONObjectHelper.getStringDef(data.get(), "id", "{}");
    }

    public void setTripOrder( int triporder)
    {
        data.get().put("triporder", triporder);
    }

    public int getTripOrder()
    {
        return JSONObjectHelper.getIntDef(data.get(), "triporder", -1);
    }

    public String getAddress()
    {
        return Device.getInstance().properCase(JSONObjectHelper.getStringDef( data.get(), "address", "!"));
    }

    public JSONObject getDestination()
    {
        return JSONObjectHelper.getJSONObjectDef(data.get(), "destination", null);
    }

    public String getDestinationDesc()
    {
        return (JSONObjectHelper.getStringDef(getDestination(), "desc", "!"));
    }

    public JSONObject getCoOrds()
    {
        return JSONObjectHelper.getJSONObjectDef(data.get(), "coords", null);
    }

    public String getReason()
    {
        JSONObject thestatus = data.getJSONObject("status");
        return JSONObjectHelper.getStringDef(thestatus, "reason", "");
        //return status;
    }



    public String getReasonDate()
    {
        JSONObject thestatus = data.getJSONObject("status");
        return JSONObjectHelper.getStringDef(thestatus, "date", "");
        //return status;
    }

    public String getStatus()
    {
        JSONObject thestatus = data.getJSONObject("status");
        return JSONObjectHelper.getStringDef(thestatus, "status", "");
        //return status;
    }

    @Override
    public String toString()
    {
        return getAddress();
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

    public static final Parcelable.Creator<StopItem> CREATOR = new Parcelable.Creator<StopItem>()
    {
        public StopItem createFromParcel(Parcel in)
        {
            return new StopItem(in);
        }

        public StopItem[] newArray(int size)
        {
            return new StopItem[size];
        }
    };
}

