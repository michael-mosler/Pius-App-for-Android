package com.rmkrings.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rmkrings.activities.R;
import com.rmkrings.activities.ScheduleChangedActivity;
import com.rmkrings.data.vertretungsplan.Vertretungsplan;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Cache;
import com.rmkrings.helper.Config;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.loader.HttpDeviceTokenSetter;
import com.rmkrings.pius_app_for_android;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class PiusAppMessageService extends FirebaseMessagingService implements HttpResponseCallback {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        sendToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Intent intent = new Intent(this, ScheduleChangedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("deltaList", remoteMessage.getData().get("deltaList"));
        intent.putExtra("timestamp", remoteMessage.getData().get("timestamp"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Show notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "PIUS-APP")
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(getResources().getColor(R.color.colorPiusBlue))
                .setContentTitle(Objects.requireNonNull(remoteMessage.getNotification()).getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            String id = "PIUS-APP";
            CharSequence name = remoteMessage.getNotification().getTitle();
            String description = getString(R.string.title_activity_schedule_changed);
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(mChannel);
            notificationManager.notify(0, builder.build());
        } else {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0, builder.build());
        }

        // If schedule data is attached update dashboard data cache and then
        // reload widget.
        if (remoteMessage.getData().containsKey("substitutionSchedule")) {
            try {
                final String data = remoteMessage.getData().get("substitutionSchedule");
                final Vertretungsplan vertretungsplan = new Vertretungsplan(new JSONObject(data));
                final String grade = AppDefaults.getGradeSetting();
                final Cache cache = new Cache();
                cache.store(Config.digestFilename(grade), vertretungsplan.getDigest());
                cache.store(Config.cacheFilename(grade), data);

                // Update widget when new data has been loaded.
                Context context = pius_app_for_android.getAppContext();
                Intent widgetIntent = new Intent(context, DashboardWidgetUpdateService.class);
                context.startService(widgetIntent);
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateDeviceToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new Instance ID token
                        if (task.getResult() != null) {
                            String token = task.getResult().getToken();
                            sendToken(token);
                        }
                    }
                });
    }

    private void sendToken(String token) {
        String grade = AppDefaults.getGradeSetting();

        // Send token only if grade is set and authenticated.
        if (grade.length() > 0 && AppDefaults.isAuthenticated()) {
            ArrayList<String> courseList = AppDefaults.getCourseList();
            HttpDeviceTokenSetter httpDeviceTokenSetter = new HttpDeviceTokenSetter(token, grade, courseList);
            httpDeviceTokenSetter.load(this);
        }
    }

    @Override
    public void execute(HttpResponseData data) { /* void */ }

}
