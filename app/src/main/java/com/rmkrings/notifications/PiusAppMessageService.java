package com.rmkrings.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rmkrings.activities.R;
import com.rmkrings.activities.ScheduleChangedActivity;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.loader.HttpDeviceTokenSetter;
import com.rmkrings.pius_app_for_android;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Cares about all push notification related stuff. It receives push messages and registers
 * device token in backend.
 */
public class PiusAppMessageService extends FirebaseMessagingService implements HttpResponseCallback {

    /**
     * New token has been created by Android for Pius App. We must register this token in
     * backend for being able to receive push messages.
     * @param token - The token that Android has created.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        sendToken(token);
    }

    /**
     * This method is being called whenever a push message is received.
     * @param remoteMessage - The push message that has been received.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Intent intent = new Intent(this, ScheduleChangedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("deltaList", remoteMessage.getData().get("deltaList"));
        intent.putExtra("timestamp", remoteMessage.getData().get("timestamp"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Show notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "PIUS-APP")
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPiusBlue))
                .setContentTitle(Objects.requireNonNull(remoteMessage.getNotification()).getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);

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
    }

    /**
     * Gets device token of Pius App that is bound to this device and triggers
     * registration in backend. Please note that getting device token other than in iOS
     * is an asynchronous operation and thus a callback is needed which then
     * starts actual registration.
     */
    public void updateDeviceToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }

                    String token = task.getResult();
                    sendToken(token);
                });
    }

    /**
     * Compute SHA1 hash of login credentials. These information is needed in backend for being able
     * to stop pushing when credentials get revoked by Pius Gymnasium.
     * @return - SHA1 of login credentials.
     */
    private String credential() {
        // Guard: When not authenticated return null.
        if (!AppDefaults.isAuthenticated()) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            String hashInput = AppDefaults.getUsername();
            hashInput = hashInput.concat(AppDefaults.getPassword());

            // Hash input is concatenation of username and password.
            final byte[] b = hashInput.getBytes();

            // Compute digest as byte array. This is signed and cannot be converted into hex
            // string without conversion into BigInteger. Who has designed these crazy Java
            // interfaces? In iOS 13 it's a one shot.
            final byte[] digest = md.digest(b);
            final BigInteger no = new BigInteger(1, digest);
            return no.toString(16);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Registers device token in backend along with some other information that is needed to
     * create push message for substitution schedule changes.
     * @param token - The token that uniquely identifies this app instance.
     */
    private void sendToken(String token) {
        final String grade = AppDefaults.getGradeSetting();

        String versionName;

        // Get version name. If this throws simply use empty string. An error here should
        // not break app.
        try {
            final Context context = pius_app_for_android.getAppContext();
            final PackageManager pm = context.getPackageManager();

            versionName = pm.getPackageInfo(pius_app_for_android.getAppPackageName(), 0).versionName;
        }
        catch (Exception e) {
            versionName = "";
        }

        final ArrayList<String> courseList = AppDefaults.getCourseList();
        final String credential = this.credential();

        final HttpDeviceTokenSetter httpDeviceTokenSetter = new HttpDeviceTokenSetter(token, grade, courseList, versionName, credential);
        httpDeviceTokenSetter.load(this);
    }

    /**
     * Void, we do not care about any outcome of token registration.
     * @param data - Response data, ignored.
     */
    @Override
    public void execute(HttpResponseData data) { }

    @Override
    public void onInternalError(Exception e) { }

}
