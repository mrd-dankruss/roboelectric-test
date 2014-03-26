package com.mrdexpress.paperless.db;

import android.content.Context;
import android.util.Log;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import com.mrdexpress.paperless.datatype.UserItem;
import net.minidev.json.JSONArray;
import org.json.JSONObject;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by hannobean on 2014/03/25.
 */
public class Drivers {
    private static Drivers _instance = null;
    private Context context;
    private ReadContext drivers;
    private Boolean loaded = false;
    private ArrayList<DriversObject> driverlistarray;
    private Integer activeindex = 0;

    public static Drivers getInstance() {
        if (_instance == null) {
            _instance = new Drivers();
        }
        return _instance;
    }

    public ReadContext getDrivers() {
        return drivers;
    }

    public void setDrivers(JSONObject json) {
        drivers = JsonPath.parse(json.toString());
        loaded = true;
    }

    public void setDrivers(String json) {
        drivers = JsonPath.parse(json);
        loaded = true;
    }

    public JSONArray getDriversData() {
        net.minidev.json.JSONArray driverlist = null;
        try {
            driverlist = drivers.read("$.response.drivers[*]");
            driverlistarray = getDriversDataList(driverlist);
        } catch (PathNotFoundException e) {
            Log.e("JSON Exception", e.toString());
        } catch (Exception e) {
            Log.e("JSON Exception", e.toString());
        }
        return driverlist;
    }

    public ArrayList<DriversObject> getDriversDataList(){
        net.minidev.json.JSONArray json = this.getDriversData();
        ArrayList<DriversObject> list = new ArrayList<DriversObject>();
        for (int i=0; i<json.size(); i++) {
            list.add(new DriversObject((net.minidev.json.JSONObject)json.get(i)));
        }
        return list;
    }

    public ArrayList<DriversObject> getDriversDataList(net.minidev.json.JSONArray json){
        ArrayList<DriversObject> list = new ArrayList<DriversObject>();
        for (int i=0; i<json.size(); i++) {
            list.add(new DriversObject((net.minidev.json.JSONObject)json.get(i)));
        }
        return list;
    }

    public void setActiveIndex(Integer index){
        activeindex = index;
    }

    public DriversObject getActiveDriver(){
        return driverlistarray.get(activeindex);
    }


    public class DriversObject implements Serializable {
        private Integer id;
        private String firstName;
        private String lastName;
        private String driverPin;

        public net.minidev.json.JSONObject jsondata;

        public DriversObject(net.minidev.json.JSONObject json) {
            this.jsondata = json;
            Field[] allFields = DriversObject.class.getDeclaredFields();
            for (Field field : allFields) {
                if (Modifier.isPrivate(field.getModifiers()) && json.containsKey(field.getName())) {
                    if (field.getType() == java.lang.String.class){
                        try{
                            java.lang.reflect.Method method = this.getClass().getDeclaredMethod("set" + field.getName().toString(), java.lang.String.class);
                            method.invoke(this , (String)json.get(field.getName()));
                        }catch(Exception e){
                            Log.e("MRD-Exception" , e.getMessage());
                        }
                    }
                    else if (field.getType() == java.lang.Integer.class){
                        try{
                            java.lang.reflect.Method method = this.getClass().getDeclaredMethod("set" + field.getName().toString(), java.lang.Integer.class);
                            method.invoke(this , (Integer)json.get(field.getName()));
                        }catch(Exception e){
                            Log.e("MRD-Exception" , e.getMessage());
                        }
                    }
                }
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
            return driverPin;
        }

        public void setdriverPin(String pin) {
            this.driverPin = pin;
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

        public UserItem.UserType getUserType(){
            return UserItem.UserType.DRIVER;
        }
    }

}
