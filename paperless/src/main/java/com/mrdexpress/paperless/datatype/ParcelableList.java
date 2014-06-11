package com.mrdexpress.paperless.datatype;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Created by gary on 2014-05-06.
 */
public class ParcelableList implements Parcelable {
    private List _list;

    public ParcelableList( List list) {
        this._list = list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        try {
            dest.writeString( JsonUtil2.getJsonRepresentation(_list));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void readFromParcel(Parcel in)
    {
        try {
            _list = (List) JsonUtil2.getObjectFromJson(in.readString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static final Parcelable.Creator<ParcelableList> CREATOR = new Parcelable.Creator<ParcelableList>()
    {
        public ParcelableList createFromParcel(Parcel in)
        {
            try {
                return new ParcelableList((List) JsonUtil2.getObjectFromJson(in.readString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public ParcelableList[] newArray(int size)
        {
            return new ParcelableList[size];
        }
    };
}
