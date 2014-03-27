package com.mrdexpress.paperless.db;

import android.content.Context;
import android.util.Log;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

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


    private Integer activeDriverIndex = 0;
    public ArrayList<UserData> driversList;

    public ArrayList<UserData> managersList;
    private int activeManagerIndex = 0;

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

    public UserData getActiveManager(){
        if (managersList.size() > 0)
        {
            return this.managersList.get(activeManagerIndex);
        }
        else
        {
            return null;
        }
    }

    public UserData getActiveDriver(){
        if (driversList.size() > 0)
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
                    driversList.add(new UserData(obj , Type.DRIVER));
                }
                if ( obj.get("role").toString().contains("MANAGER") ){
                    //Create Driver UserData and Add it to the Driver List
                    managersList.add(new UserData(obj , Type.DRIVER));
                }
            }
        }
        catch (Exception e)
        {
            Log.e("MRD-EX" , "Must be fixed ASAP: " + e.getMessage());
        }
    }

    private enum Type
    {
        MANAGER,DRIVER;
    }

    private class UserData
    {
        public net.minidev.json.JSONObject json;
        private String firstName;
        private String lastName;
        private int id;
        private String pin;
        private Type usertype;

        public UserData(net.minidev.json.JSONObject obj, Type type){
            json = obj;
            try{
                firstName = obj.get("firstName").toString();
                lastName = obj.get("lastName").toString();
                pin = obj.get("driverPin").toString();
                id = Integer.parseInt(obj.get("id").toString());
                usertype = type;
            }
            catch(Exception e)
            {
                Log.e("MRD-EX" , "Parsing variable exception , this should be fixed when it happens!! " + e.getMessage());
            }
        }

        public int getid() {
            return id;
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

    }
}
