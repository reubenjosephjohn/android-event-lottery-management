package com.example.eventlotterysystem;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * A service that extends FirebaseMessagingService to handle incoming FCM messages.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "eventlotterysystem_notifications";

    /**
     * Called when a message is received.
     *
     * @param remoteMessage The message received from FCM.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Get notification data from the RemoteMessage
        String title = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getTitle() : "Default Title";
        String message = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : "Default Message";
        Log.i("Messaging", "Message Received!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        // Show the notification
        sendNotification(title, message);
    }

    /**
     * Sends a notification to the user.
     *
     * @param title   The title of the notification.
     * @param message The message body of the notification.
     */
    private void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Log.i("Messaging", "Called Send Notification!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Log.d("Messaging", "Notification Title: " + title);
        Log.d("Messaging", "Notification Message: " + message);
        // Create a notification channel (required for Android 8.0 and higher)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Event Lottery Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for the Event Lottery System app");
            notificationManager.createNotificationChannel(channel);
        }

        // Create an intent to open the app when the notification is clicked
        Intent intent = new Intent(this, MainActivity.class); // Replace MainActivity with your main activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_letter_icon)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);


        // Show the notification
        Log.i("Messaging", "NotificationManager.notify called!");
        notificationManager.notify(0, builder.build());
    }

    /**
     * Called when a new token for the default Firebase project is generated.
     *
     * @param token The new token.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Handle the new token (optional, if needed for FCM token updates)
    }
}
