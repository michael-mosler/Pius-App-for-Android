package com.rmkrings.widgets;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;
import android.widget.RemoteViews;

import com.rmkrings.activities.R;
import com.rmkrings.data.vertretungsplan.GradeItem;
import com.rmkrings.data.vertretungsplan.Vertretungsplan;
import com.rmkrings.data.vertretungsplan.VertretungsplanForDate;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Cache;
import com.rmkrings.helper.Config;
import com.rmkrings.helper.FormatHelper;
import com.rmkrings.helper.StringHelper;
import com.rmkrings.notifications.DashboardWidgetUpdateService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementation of App Widget functionality.
 */
public class DashboardWidget extends AppWidgetProvider {
    private PendingIntent pendingIntent;

    /**
     * Checks if dashboard can be used. If it can than widget also can show data.
     * @return - Returns true if dashboard can be used.
     */
    private static boolean canUseDashboard(){
        return (AppDefaults.isAuthenticated() && (AppDefaults.hasLowerGrade() || (AppDefaults.hasUpperGrade() && AppDefaults.getCourseList().size() > 0)));
    }

    /**
     * Show a given message in comment view of Pius App widget. All other views get
     * hidden.
     * @param remoteViews - Remote views to use to control visibilty and to set text
     * @param message - Message to show
     */
    private static void showMessage(RemoteViews remoteViews, String message) {
        remoteViews.setViewVisibility(R.id.widgetVertretungsplanHeaderItem, View.GONE);
        remoteViews.setViewVisibility(R.id.widgetVertretungsplanSubHeaderItem, View.GONE);
        remoteViews.setViewVisibility(R.id.widgetSubstitutionTypeItem, View.GONE);
        remoteViews.setViewVisibility(R.id.widgetRoomItem, View.GONE);
        remoteViews.setViewVisibility(R.id.widgetTeacherItem, View.GONE);
        remoteViews.setViewVisibility(R.id.widgetEvaItem, View.GONE);
        remoteViews.setViewVisibility(R.id.widgetDetailsLayout, View.GONE);
        remoteViews.setViewVisibility(R.id.widgetCommentItem, View.VISIBLE);
        remoteViews.setTextViewText(R.id.widgetCommentItem, message);
        remoteViews.setTextViewText(R.id.widgetLastUpdate, null);

    }

    /**
     * Updates content of Pius-App widget from current cache content.
     * @param context - Context to get resources from
     * @param appWidgetManager - App widget manager that finally will update the widget
     * @param appWidgetId - The widget id of widget to update
     */
    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        final Cache cache = new Cache();

        try {
            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.dashboard_widget);
            final String grade = AppDefaults.getGradeSetting();
            final String cacheFileName = Config.cacheFilename(grade);

            if (!canUseDashboard()) {
                showMessage(remoteViews, context.getResources().getString(R.string.error_cannot_use_dashboard_widget));
            } else if (!cache.fileExists(cacheFileName)) {
                showMessage(remoteViews, context.getResources().getString(R.string.error_no_data));
            } else {
                // Ok,
                final String data = cache.read(Config.cacheFilename(grade));
                final JSONObject jsonObject = new JSONObject(data);
                final Vertretungsplan vertretungsplan = new Vertretungsplan(jsonObject);
                final VertretungsplanForDate filteredVertretungsplan = vertretungsplan.next();

                // Update widget content.
                if (filteredVertretungsplan != null) {
                    final String date = filteredVertretungsplan.getDate();
                    final GradeItem gradeItem = filteredVertretungsplan.getGradeItems().get(0);
                    final String[] vertretungsplanItem = gradeItem.getVertretungsplanItems().get(0);
                    final String lesson = vertretungsplanItem[0];
                    final String course = StringHelper.replaceHtmlEntities(vertretungsplanItem[2]);

                    remoteViews.setViewVisibility(R.id.widgetVertretungsplanHeaderItem, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.widgetVertretungsplanSubHeaderItem, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.widgetDetailsLayout, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.widgetSubstitutionTypeItem, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.widgetRoomItem, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.widgetTeacherItem, View.VISIBLE);

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
                } else {
                    showMessage(remoteViews, context.getResources().getString(R.string.text_empty_future_schedule));
                }

                remoteViews.setTextViewText(R.id.widgetLastUpdate, vertretungsplan.getLastUpdate());
            }

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called whenver widget gets updated by system.
     * @param context - Widget context
     * @param appWidgetManager - Systems app widget manager
     * @param appWidgetIds - Widget IDs of existing Pius App widgets
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, DashboardWidgetUpdateService.class);

        if (pendingIntent == null) {
            pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 60000, pendingIntent);

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    /**
     * Not implemented.
     * @param context - Widget context
     */
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    /**
     * Not implemented.
     * @param context - Widget context
     */
    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

