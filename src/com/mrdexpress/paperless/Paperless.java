package com.mrdexpress.paperless;

import android.app.Application;
import android.content.Context;

/**
 * Created by hannobean on 2014/03/27.
 */

public class Paperless extends Application {
    private static Paperless instance;

    public static Paperless getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}