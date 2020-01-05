package com.rmkrings.notifications;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RemoteViews;

import com.rmkrings.activities.R;
import com.rmkrings.data.vertretungsplan.GradeItem;
import com.rmkrings.data.vertretungsplan.Vertretungsplan;
import com.rmkrings.data.vertretungsplan.VertretungsplanForDate;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Cache;
import com.rmkrings.helper.FormatHelper;
import com.rmkrings.helper.StringHelper;
import com.rmkrings.widgets.DashboardWidget;

import org.json.JSONException;
import org.json.JSONObject;

// import java.util.logging.Logger;

/**
 * This service is used to update Dashboard widget. It should be triggered by app whenever
 * dashboard data is likely to change. The widget itself is not able to load substitution
 * data due to time limit restrictions. It always accesses the cache in order to update
 * itself.
 */
public class DashboardWidgetUpdateService extends Service {
    private final Cache cache = new Cache();
    private String cacheFileName(String grade) { return String.format("%s.json", grade); }

    // private final static Logger logger = Logger.getLogger(DashboardWidgetUpdateService.class.getName());

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get dashboard data from cache. Please note that this function is called
        // after cache has changed.
        try {
            String grade = AppDefaults.getGradeSetting();
            String data = cache.read(cacheFileName(grade));
            JSONObject jsonObject = new JSONObject(data);
            Vertretungsplan vertretungsplan = new Vertretungsplan(jsonObject);
            VertretungsplanForDate filteredVertretungsplan = vertretungsplan.next();

            // Get dashboard widget id and remote view.
            Context context = getApplicationContext();
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName name = new ComponentName(context, DashboardWidget.class);
            int[] ids = appWidgetManager.getAppWidgetIds(name);

            // No widget configured, exit.
            if (ids.length == 0) {
                return super.onStartCommand(intent, flags, startId);
            }

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.dashboard_widget);

            // Update widget content.
            if (filteredVertretungsplan != null) {
                final String date = filteredVertretungsplan.getDate();
                final GradeItem gradeItem = filteredVertretungsplan.getGradeItems().get(0);
                final String[] vertretungsplanItem = gradeItem.getVertretungsplanItems().get(0);
                final String lesson = vertretungsplanItem[0];
                final String course = StringHelper.replaceHtmlEntities(vertretungsplanItem[2]);

                remoteViews.setTextViewText(R.id.widgetVertretungsplanHeaderItem, date);

                if (course.isEmpty()) {
                    remoteViews.setTextViewText(R.id.widgetVertretungsplanSubHeaderItem, String.format("%s. Stunde", lesson));
                } else {
                    remoteViews.setTextViewText(R.id.widgetVertretungsplanSubHeaderItem, String.format("Fach/Kurs: %s %s. Stunde", course, lesson));
                }

                final String type = StringHelper.replaceHtmlEntities(vertretungsplanItem[1]);
                final String room = StringHelper.replaceHtmlEntities(vertretungsplanItem[3]);
                final String teacher = StringHelper.replaceHtmlEntities(vertretungsplanItem[4]);
                final String comment = StringHelper.replaceHtmlEntities(vertretungsplanItem[6]);

                remoteViews.setTextViewText(R.id.widgetSubstitutionTypeItem, type);
                remoteViews.setTextViewText(R.id.widgetRoomItem, FormatHelper.roomText(room));
                remoteViews.setTextViewText(R.id.widgetTeacherItem, teacher);

                if (comment.isEmpty()) {
                    remoteViews.setViewVisibility(R.id.widgetCommentItem, View.GONE);
                } else {
                    remoteViews.setViewVisibility(R.id.widgetCommentItem, View.VISIBLE);
                    remoteViews.setTextViewText(R.id.widgetCommentItem, comment);
                }

                if (vertretungsplanItem.length == 8 && !vertretungsplanItem[7].isEmpty()) {
                    final String eva = vertretungsplanItem[7];
                    remoteViews.setViewVisibility(R.id.widgetEvaItem, View.VISIBLE);
                    remoteViews.setTextViewText(R.id.widgetEvaItem, eva);

                } else {
                    remoteViews.setViewVisibility(R.id.widgetEvaItem, View.GONE);
                }
            }

            remoteViews.setTextViewText(R.id.widgetLastUpdate, vertretungsplan.getLastUpdate());

            appWidgetManager.updateAppWidget(ids[0], remoteViews);
        }
        catch(JSONException e) {
            e.printStackTrace();
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
