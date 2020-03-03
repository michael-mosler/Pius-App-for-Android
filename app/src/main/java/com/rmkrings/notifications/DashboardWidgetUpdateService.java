package com.rmkrings.notifications;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.rmkrings.widgets.DashboardWidget;

/**
 * This service is used to update Dashboard widget. It should be triggered by app whenever
 * dashboard data is likely to change. The widget itself is not able to load substitution
 * data due to time limit restrictions. It always accesses the cache in order to update
 * itself.
 */
public class DashboardWidgetUpdateService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get dashboard widget id and call DashboardWidget.updateAppWidget.
        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName name = new ComponentName(context, DashboardWidget.class);
        int[] ids = appWidgetManager.getAppWidgetIds(name);

        // No widget configured, exit.
        if (ids.length == 0) {
            return super.onStartCommand(intent, flags, startId);
        }

        for (int id: ids) {
            DashboardWidget.updateAppWidget(context, appWidgetManager, id);
        }

        // Done, wait for next start of service.
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
