package com.mrdexpress.paperless.datatype;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.mrdexpress.paperless.db.Bag;
import com.mrdexpress.paperless.workflow.JSONObjectHelper;
import com.mrdexpress.paperless.workflow.ObservableJSONObject;
import com.mrdexpress.paperless.workflow.Workflow;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeliveryHandoverDataObject implements Parcelable
{
    public ObservableJSONObject data;

    public DeliveryHandoverDataObject( ObservableJSONObject datain )
	{
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

    public Bag getFlowDataFromParcel(ArrayList<Bag> bags){
        int Pid = JSONObjectHelper.getIntDef(data.get(), "id", -1);
        if (Pid > 0){
            for (int i = 0; i < bags.size(); i++){
                Bag bg = bags.get(i);
                JSONObject jso = Workflow.getInstance().getTripStopByBag(bg.getBagID());
                List<JSONArray> jsb = Workflow.getInstance().getBagParcelsOnly(bg.getBagID());
                if (jsb.size() > 0){
                    JSONArray jsr = jsb.get(0);
                    for(int j = 0; j < jsr.size(); j++){
                        JSONObject obj = (JSONObject)jsr.get(j);
                        if (null != obj && obj.containsKey("id")){
                            String Pid2 = obj.get("id").toString();
                            if (Integer.toString(Pid).equals(Pid2)){
                                Log.e("MRD" , "Huzzah");
                                return bg;
                            }
                        }
                    }
                }
                Log.e("MRD-EX", "aaaa");
            }
        }
        return null;
    }

    public void unscanParcel(){
        if( data.get().containsKey("scannedtime"))
            data.get().remove("scannedtime");
    }

	/**
	 * @return the barcode
	 */
	public String getBarcode()
	{
        return JSONObjectHelper.getStringDef(data.get(), "barcode", "");
	}

    public String getHubName(){
        return JSONObjectHelper.getStringDef(data.get(), "hubname", "");
    }

    public String getHubCode(){
        return JSONObjectHelper.getStringDef(data.get(), "hubcode", "");
    }

    public String getVolumetrics()
    {
        JSONObject jsa = JSONObjectHelper.getJSONObjectDef(data.get(), "dimensions", null);
        if( jsa != null)
        {
            return  String.valueOf(JSONObjectHelper.getIntDef(jsa, "width", 0)) + " x " + String.valueOf(JSONObjectHelper.getIntDef(jsa, "length", 0)) + " x " + String.valueOf(JSONObjectHelper.getIntDef(jsa, "height", 0));
        }
        return "";
    }

    public int getID()
    {
        return JSONObjectHelper.getIntDef(data.get(), "id", -1);     }

    public String getMDX()
    {
        return JSONObjectHelper.getStringDef( data.get(), "mdx", "");
    }

    public String getXof()
    {
        return JSONObjectHelper.getStringDef( data.get(), "xof", "");
    }

    public String getLarge()
    {
        return JSONObjectHelper.getStringDef( data.get(), "parcel", "");
    }

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
