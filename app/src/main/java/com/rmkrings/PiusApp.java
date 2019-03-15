package com.rmkrings;

import android.app.Application;
import android.content.Context;

public class PiusApp extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        PiusApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return PiusApp.context;
    }
}
