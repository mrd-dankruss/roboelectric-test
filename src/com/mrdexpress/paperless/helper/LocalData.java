package com.mrdexpress.paperless.helper;

import java.util.Observable;
import android.content.SharedPreferences;

/**
 * Created by hannobean on 2014/03/17.
 */
public class LocalData extends Observable{
    private static final LocalData INSTANCE = new LocalData();

    private LocalData() {}

    //public static void SaveKey(Context context){
    //    SharedPreferences sp = getSharedPreferences(prefName, Context.MODE_PRIVATE);
    //    SharedPreferences.Editor editor = sp.edit();
    //}

    public static LocalData getInstance() {
        return INSTANCE;
    }
}
