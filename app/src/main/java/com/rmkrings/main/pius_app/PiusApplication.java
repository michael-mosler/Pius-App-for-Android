package com.rmkrings.main.pius_app;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.rmkrings.helper.Reachability;

public class PiusApplication extends Application {
    private static PiusApplication self;

    public PiusApplication() {
        self = this;
    }

    public void onCreate() {
        super.onCreate();
        registerReceiver(Reachability.getInstance(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public static Context getAppContext() {
        return self;
    }
}
