package com.rmkrings;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Reachability;
import com.rmkrings.notifications.PiusAppMessageService;

import java.util.HashMap;

public class pius_app_for_android extends Application {
    private static pius_app_for_android instance;

    public void onCreate() {
        instance = this;

        super.onCreate();

        // Activate Push Notifications.
        registerReceiver(Reachability.getInstance(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        PiusAppMessageService piusAppMessageService = new PiusAppMessageService();
        piusAppMessageService.updateDeviceToken();

        // Get backend address from Firebase Remote Config.
        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        final HashMap<String, Object> map = new HashMap<>();
        map.put("gatewayAddress", AppDefaults.getBaseUrl());
        firebaseRemoteConfig.setDefaultsAsync(map);
        firebaseRemoteConfig.fetchAndActivate();
    }

    public static Context getAppContext() {
        return instance;
    }

    public static String getAppPackageName() { return instance.getPackageName(); }
}
