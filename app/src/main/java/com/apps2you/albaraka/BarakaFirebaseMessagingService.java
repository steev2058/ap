package com.apps2you.albaraka;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.ArrayMap;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.apps2you.albaraka.data.model.NotificationContent;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.ui.registration.LoginActivity;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.viewmodels.HomeViewModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.GsonBuilder;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import dagger.android.AndroidInjection;

import static com.apps2you.albaraka.utils.cryptography.ConstantsKt.NOTIFICATION_EXTRA;

public class BarakaFirebaseMessagingService extends FirebaseMessagingService {

    private static final AtomicInteger ati = new AtomicInteger(0);

    @Override
    public void onCreate() {
        super.onCreate();

        AndroidInjection.inject(this);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        UserUtils.getInstance(getApplicationContext()).saveFCMToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            ArrayMap<String, Object> body = new GsonBuilder().create().fromJson(remoteMessage.getData().get("ntf_body"), ArrayMap.class);
            Integer ntf_type = null, ntf_transaction_id = null;
            String branch_code = null;

            if (body != null) {
                ntf_type = ((Double) Objects.requireNonNull(body.get("type"))).intValue();
                ntf_transaction_id = Integer.parseInt((String) Objects.requireNonNull(body.get("od_trans_no")));
                branch_code = (String) body.get("branch_code");
            }
            if (ntf_transaction_id == null) {
                ntf_type = Constants.NOTIFICATION_NORMAL;
                ntf_transaction_id = -1;
            }
            String ntf_title = remoteMessage.getData().get("ntf_title");
            String ntf_text = remoteMessage.getData().get("ntf_text");
            sendNotification(ntf_title, ntf_text, ntf_type, ntf_transaction_id, branch_code);
            HomeViewModel.notificationCount.set(HomeViewModel.notificationCount.get() + 1);
        }
    }

    private void sendNotification(String ntf_title, String ntf_text, int ntf_type, int ntf_transaction_id, String branch_code) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(NOTIFICATION_EXTRA,
                new NotificationContent(-1, ntf_type, ntf_title, ntf_text, ntf_transaction_id, "", branch_code));

        int pendingIntentFlag =
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT : PendingIntent.FLAG_ONE_SHOT;

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                new SecureRandom().nextInt()/* Request code */,
                intent,
                pendingIntentFlag);

        String channelId = getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_app_notification)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentTitle(ntf_title)
                        .setContentText(ntf_text)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(ntf_text));
        notificationManager.notify(new SecureRandom().nextInt(), notificationBuilder.build());
    }
}
