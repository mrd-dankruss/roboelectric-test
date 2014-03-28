package com.mrdexpress.paperless.db;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.internal.JsonReader;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hannobean on 2014/03/27.
 */
public class Users implements Serializable
{
    private static Users _instance = null;
    private Context context;
    private ReadContext users;


    private Integer activeDriverIndex = -1;
    public ArrayList<UserData> driversList;

    public ArrayList<UserData> managersList;
    private int activeManagerIndex = -1;

    public static Users getInstance() {
        if (_instance == null) {
            _instance = new Users();
        }
        return _instance;
    }

    public void setUsers(String json) {
        users = JsonPath.parse(json);
        this.UserParser();
    }

    public void setActiveDriverIndex(int i){
        activeDriverIndex = i;
    }
    
    public void setActiveManagerIndex(int i){
        activeManagerIndex = i;
    }

    public void setActiveManager( UserData user){
        activeManagerIndex = managersList.indexOf( user);
    }

    public UserData getActiveManager(){
        if (managersList.size() > 0 && activeManagerIndex > -1)
        {
            return this.managersList.get(activeManagerIndex);
        }
        else
        {
            return null;
        }
    }

    public UserData getActiveDriver(){
        if (driversList.size() > 0  && activeDriverIndex > -1)
        {
            return this.driversList.get(activeDriverIndex);
        }
        else
        {
            return null;
        }
    }

    public void UserParser(){
        net.minidev.json.JSONArray userlist = null;
        try
        {
            userlist = users.read("$.response.drivers[*]");
            driversList = new ArrayList<UserData>();
            managersList = new ArrayList<UserData>();

            for (int i=0; i<userlist.size(); i++) {
                net.minidev.json.JSONObject obj = (net.minidev.json.JSONObject)userlist.get(i);
                if ( obj.get("role").toString().contains("DRIVER") ){
                    //Create Driver UserData and Add it to the Driver List
                    driversList.add(new UserData(obj));
                }
                if ( obj.get("role").toString().contains("MANAGER") ){
                    //Create Driver UserData and Add it to the Driver List
                    managersList.add(new UserData(obj));
                }
            }
        }
        catch (Exception e)
        {
            Log.e("MRD-EX" , "Must be fixed ASAP: " + e.getMessage());
        }
    }

    public enum Type
    {
        MANAGER,DRIVER;
    }

    // this need to be Parcelable to be able to serialise into a Bundle
    public class UserData implements Parcelable
    {
        public net.minidev.json.JSONObject json;
        private String firstName;
        private String lastName;
        private int id;
        private String pin;
        private Type usertype;

        public UserData(net.minidev.json.JSONObject obj){
            json = obj;
            if( json != null) {
                setUserDataFromJSON();
            }
        }

        private void setUserDataFromJSON(){
            if( json != null) {
                try {
                    firstName = json.get("firstName").toString();
                    lastName = json.get("lastName").toString();
                    pin = json.get("driverPin").toString();
                    id = Integer.parseInt(json.get("id").toString());
                    if (json.get("role").toString().contains("MANAGER"))
                        usertype = Type.MANAGER;
                    else
                        usertype = Type.DRIVER;
                } catch (Exception e) {
                    Log.e("MRD-EX", "Parsing variable exception , this should be fixed when it happens!! " + e.getMessage());
                }
            }
        }

        public Integer getid() {
            return id;
        }

        public String getStringid(){
            return Integer.toString(id);
        }

        public void setid(Integer id) {
            this.id = id;
        }

        public String getfirstName() {
            return firstName;
        }

        public void setfirstName(String firstName) {
            this.firstName = firstName;
        }
        public String getdriverPin() {
            return pin;
        }

        public void setdriverPin(String pin) {
            this.pin = pin;
        }

        public String getlastName() {
            return lastName;
        }

        public String getFullName() {
            return this.firstName + " " + this.lastName;
        }

        public void setlastName(String lastName) {
            this.lastName = lastName;
        }

        public Object getUndefined(String key){
            return json.get(key);
        }

        public Type getUsertype(){
            return usertype;
        }

        @Override
        public String toString()
        {
            return getFullName();
        }

        public int describeContents()
        {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeString(json.toJSONString());
        }

        public void readFromParcel(Parcel in)
        {
            json = (JSONObject) new JsonReader().parse( in.readString());
            setUserDataFromJSON();
        }

        public final Parcelable.Creator<UserData> CREATOR = new Parcelable.Creator<UserData>()
        {
            public UserData createFromParcel(Parcel in)
            {
                UserData ud = new UserData(null);
                ud.readFromParcel( in);
                return ud;
            }

            public UserData[] newArray(int size)
            {
                return new UserData[size];
            }
        };

    }
}
