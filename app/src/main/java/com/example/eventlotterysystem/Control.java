package com.example.eventlotterysystem;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;

public class Control {
    private int currentUserID;
    private int currentEventID;
    private ArrayList<User> userList;
    private ArrayList<Facility> facilityList;
    private ArrayList<Event> eventList;
    private ArrayList<Notification> notificationList;
    // runtime attributes
    private static Control instance;
//    private static User currentUser = null; // current logged in user
    private static String localFID = "";
    // Notification token
    public static String notificationToken = "";

    // Database
    private FirebaseFirestore db;



    private Control() {
        this.currentUserID = 0;
        this.currentEventID = 0;
        this.userList = new ArrayList<>();
        this.facilityList = new ArrayList<>();
        this.eventList = new ArrayList<>();
        this.notificationList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        setUpListeners();
    }


    public static synchronized Control getInstance() {
        if (instance == null) {
            instance = new Control();
        }
        return instance;
    }

    // Database operations
    private void setUpListeners() {
        // Listener for Control Data
        db.collection("control").document("ControlData")
                .addSnapshotListener((documentSnapshot , e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Control listener failed", e);
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        currentUserID = documentSnapshot.getLong("currentUserID").intValue();
                        currentEventID = documentSnapshot.getLong("currentEventID").intValue();
                    }
                });

        // Listener for User List
        db.collection("users")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "User listener failed", e);
                        return;
                    }
                    if (queryDocumentSnapshots != null) {
                        userList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            // Now sure how this works but its super cool
                            User user = doc.toObject(User.class);
                            userList.add(user);
                        }
                        Log.i("Firestore", "User list updated");
                    }
                });
        // Listener for Facility List
        db.collection("facilities")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Facility listener failed", e);
                        return;
                    }
                    if (queryDocumentSnapshots != null) {
                        facilityList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Facility facility = doc.toObject(Facility.class);
                            facilityList.add(facility);
                        }
                        Log.i("Firestore", "Facility list updated");
                    }
                });
        // Listener for Event List
        db.collection("events")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Event listener failed", e);
                        return;
                    }
                    if (queryDocumentSnapshots != null) {
                        eventList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Event event = doc.toObject(Event.class);
                            eventList.add(event);
                        }
                        Log.i("Firestore", "Event list updated");
                    }
                });
        // Listener for Notification List
        db.collection("notifications")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Notification listener failed", e);
                        return;
                    }
                    // Read data from data base
                    if (queryDocumentSnapshots != null) {
                        notificationList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Notification notification = doc.toObject(Notification.class);
                            notificationList.add(notification);
                        }
                        Log.i("Firestore", "Notification list updated");

                        // Don't push notifications if user's notification setting is off
                        Boolean userNotificationSetting = false;
                        for (User user : userList) {
                            if (user.getFID().equals(localFID)){
                                userNotificationSetting = user.getNotificationSetting();
                                break;
                            }
                        }
                        if (userNotificationSetting){
                            // Document add listener
                            for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
                                if (change.getType() == DocumentChange.Type.ADDED) {
                                    DocumentSnapshot doc = change.getDocument();
                                    Notification notification = doc.toObject(Notification.class);
                                    if (notification.getDeclined()){
                                        notification.setDeclined(false);
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        DocumentReference notificationRef = doc.getReference();
                                        notificationRef.update("declined", false)
                                                .addOnSuccessListener(aVoid -> Log.i("Firestore", "Notification declined field updated to false"))
                                                .addOnFailureListener(ee -> Log.e("Firestore", "Failed to update declined field", e));
                                        ;
                                        for (User user : Control.getInstance().getUserList()) {
                                            if (user.getFID().equals(localFID)){ // find myself
                                                if (user.getUserID() == notification.getUserRef()) {
                                                    String eventName = Control.getInstance().findEventByID(notification.getEventRef()).getName();
                                                    Control.getInstance().sendNotification(
                                                            MyApplication.getAppContext(), // Retrieve context from a custom Application class
                                                            eventName,
                                                            notification.getCustomMessage()
                                                    );
                                                    Log.i("Notification", "Notification: Event:" + eventName +", Message: " + notification.getCustomMessage() + "; it will be sent to android notification system");
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                });
    }

    public void saveUser(User user) {
        db.collection("users").document(String.valueOf(user.getUserID())).set(user)
                .addOnSuccessListener(aVoid -> Log.i("Firestore", "User saved"))
                .addOnFailureListener(e -> Log.e("Firestore", "User save failed", e));
    }

    public void saveFacility(Facility facility) {
        int facilityID = findUserByID(facility.getCreatorRef()).getUserID();
        db.collection("facilities").document(String.valueOf(facilityID)).set(facility)
                .addOnSuccessListener(aVoid -> Log.i("Firestore", "Facility saved"))
                .addOnFailureListener(e -> Log.e("Firestore", "Facility save failed", e));
    }

    public void deleteFacility(Facility facility) {
        db.collection("facilities").document(String.valueOf(facility.getCreatorRef())).delete()
                .addOnSuccessListener(aVoid -> Log.i("Firestore", "Facility deleted"))
                .addOnFailureListener(e -> Log.e("Firestore", "Facility delete failed", e));
    }

    public void saveEvent(Event event) {
        db.collection("events").document(String.valueOf(event.getEventID())).set(event)
                .addOnSuccessListener(aVoid -> Log.i("Firestore", "Event saved"))
                .addOnFailureListener(e -> Log.e("Firestore", "Event save failed", e));
    }

    public void deleteEvent(Event event) {
        db.collection("events").document(String.valueOf(event.getEventID())).delete()
                .addOnSuccessListener(aVoid -> Log.i("Firestore", "Event deleted"))
                .addOnFailureListener(e -> Log.e("Firestore", "Event delete failed", e));
    }

    public void addNotification(Notification notification) {
        db.collection("notifications").add(notification)
                .addOnSuccessListener(documentReference -> {
                    notification.setDocumentID(documentReference.getId());
                    updateNotification(notification);
                    Log.i("Firestore", "Notification saved");
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Notification save failed", e));
    }

    public void updateNotification(Notification notification) {
        db.collection("notifications").document(notification.getDocumentID()).set(notification)
                .addOnSuccessListener(aVoid -> Log.i("Firestore", "Notification updated"))
                .addOnFailureListener(e -> Log.e("Firestore", "Notification update failed", e));
    }

    public void deleteNotification(Notification notification) {
        db.collection("notifications").document(notification.getDocumentID()).delete()
                .addOnSuccessListener(aVoid -> Log.i("Firestore", "Notification deleted"))
                .addOnFailureListener(e -> Log.e("Firestore", "Notification delete failed", e));
    }

    // Getters and Setters
    public int getCurrentUserID() {
        return currentUserID;
    }


    public int getCurrentUserIDForUserCreation() {
        int result = currentUserID;
        currentUserID++;
        db.collection("control").document("ControlData").update("currentUserID", currentUserID);
        return result;
    }

    public int getCurrentEventID() {
        return currentEventID;
    }

    public int getCurrentEventIDForEventCreation() {
        int result = currentEventID;
        currentEventID++;
        db.collection("control").document("ControlData").update("currentEventID", currentEventID);
        return result;
    }

    public static User getCurrentUser() {
        User u;
        for (User user : Control.getInstance().getUserList()) {
            if (user.getFID().equals(Control.getLocalFID())){
                u = user;
                return u;
            }
        }
        return null;
    }

//    public static void setCurrentUser(User currentUser) {
//        Control.currentUser = currentUser;
//    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public ArrayList<Event> getEventList() {
        return eventList;
    }

    public ArrayList<Facility> getFacilityList() {
        return facilityList;
    }

    public ArrayList<Notification> getNotificationList() {
        return notificationList;
    }

    public static String getLocalFID() {
        return localFID;
    }

    public static void setLocalFID(String localFID) {
        Control.localFID = localFID;
    }

    // finder methods (use try-catch to handle errors)
    public User findUserByID(int userID) {
        for (User user : userList) {
            if (user.getUserID() == userID) {
                return user;
            }
        }
        return null;
    }

    public Event findEventByID(int eventID) {
        for (Event event : eventList) {
            if (event.getEventID() == eventID) {
                return event;
            }
        }
        return null;
    }

    public void sendNotification(Context context, String eventName, String message) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNEL_ID = "eventlotterysystem_notifications";

        // Create a notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Event Lottery Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for the Event Lottery System app");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Intent to open the app when notification is clicked
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Event: " + eventName)
                .setSmallIcon(R.drawable.ic_letter_icon)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Show the notification
        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }


}
