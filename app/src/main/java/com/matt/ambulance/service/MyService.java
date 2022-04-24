package com.matt.ambulance.service;

import static com.matt.ambulance.util.Util.reqRef;
import static com.matt.ambulance.util.Util.user;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;
import com.matt.ambulance.R;
import com.matt.ambulance.activity.Requests;

public class MyService extends Service {
    public static Bitmap largeIcon;
    private ListenerRegistration registration;

    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (user != null)
            checkChanges();

        return START_STICKY;
    }

    private void checkChanges() {
        registration = reqRef.addSnapshotListener((value, error) -> {
            if (error == null) {
                if (!value.isEmpty()) {
                    // listen to document changes
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            sendNotification();
                        }
                    }
                }
            }
        });
    }

    private void sendNotification() {
        largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.amb3);
        //sending Intent to Navigation activity
//        SharedPrefs.setPrefs("id", "Chat", this);

        Intent intent = new Intent(this, Requests.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // generate random Request code to prevent passing same PendingIntent
//        int randomInt = (int) System.currentTimeMillis();
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0 /* Request code */, intent, 0);

        String channelId = "Requests";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_amb)
                        .setContentTitle(getString(R.string.app_name2))
                        .setContentText("Incoming Ambulance Request")
                        .setAutoCancel(true)
                        .setLargeIcon(largeIcon)
                        .setColor(getResources().getColor(R.color.red))
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    channelId,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        // generate random id to prevent duplication
        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registration.remove();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}