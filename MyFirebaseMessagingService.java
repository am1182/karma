package com.politics.karma;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Firebase_MSG";
    private LocalBroadcastManager broadcaster;

    private FirebaseDatabase db;
    private DatabaseReference notificationsRef;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userId;


    // ===== ON CREATE
    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);

    }


    // ===== ON MESSAGE RECEIVED
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        sendNotification(remoteMessage.getNotification().getBody());
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message: " + remoteMessage.getNotification().getBody());


        // ===== BROADCAST
        Intent intent = new Intent("data");
        intent.putExtra("from", remoteMessage.getFrom());
        intent.putExtra("body", remoteMessage.getNotification().getBody());
        broadcaster.sendBroadcast(intent);


        // ===== SAVE TO DB
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        db = FirebaseDatabase.getInstance();
        notificationsRef = db.getInstance().getReference().child("notifications");

        String key = notificationsRef.child(userId).push().getKey();
        notificationsRef.child(userId).child(key).child("body").setValue(remoteMessage.getNotification().getBody());
    }


    // ===== SEND NOTIFICATION
    public void sendNotification(String messageBody) {
        Intent intent = new Intent(this, ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_button_like_red)
                //.setContentTitle("Notification")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
