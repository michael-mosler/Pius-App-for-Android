package com.rmkrings.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.rmkrings.interfaces.ReachabilityChangeCallback;
import com.rmkrings.pius_app_for_android;

public class Reachability extends BroadcastReceiver {
    private static Reachability self;
    private ReachabilityChangeCallback reachabilityChangeCallback;

    public static Reachability getInstance() {
        if (self == null) {
            self = new Reachability();
        }

        return self;
    }

    public static boolean isReachable() {
        ConnectivityManager cm = (ConnectivityManager) pius_app_for_android.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (reachabilityChangeCallback != null) {
            reachabilityChangeCallback.execute(isReachable());
        }
    }

    public void setReachabilityChangeCallback(ReachabilityChangeCallback reachabilityChangeCallback) {
        this.reachabilityChangeCallback = reachabilityChangeCallback;
    }
}
