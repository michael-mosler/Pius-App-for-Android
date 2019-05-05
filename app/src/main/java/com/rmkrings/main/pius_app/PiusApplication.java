package com.rmkrings.main.pius_app;

import android.app.Application;
import android.content.Context;

public class PiusApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        PiusApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return PiusApplication.context;
    }
}
