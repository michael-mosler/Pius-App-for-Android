package com.rmkrings;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.rmkrings.helper.Reachability;
import com.rmkrings.notifications.PiusAppMessageService;

public class pius_app_for_android extends Application {
    private static pius_app_for_android instance;

    public void onCreate() {
        instance = this;

        super.onCreate();

        registerReceiver(Reachability.getInstance(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        PiusAppMessageService piusAppMessageService = new PiusAppMessageService();
        piusAppMessageService.updateDeviceToken();
    }

    public static Context getAppContext() {
        return instance;
    }

    public static String getAppPackageName() { return instance.getPackageName(); }
}
