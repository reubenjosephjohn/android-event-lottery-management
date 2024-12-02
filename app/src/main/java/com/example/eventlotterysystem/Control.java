package com.example.eventlotterysystem;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
/**
 * Singleton control class for managing users, facilities, events, and notifications in the Event Lottery System.
 */
public class Control {
    private int currentUserID;
    private int currentEventID;
    private ArrayList<User> userList;
    private ArrayList<Facility> facilityList;
    private ArrayList<Event> eventList;
    private ArrayList<Notification> notificationList;
    // runtime attributes
    private static Control instance;
    private static String localFID = "";
    // Notification token
    public static String notificationToken = "";
    // Database
    private FirebaseFirestore db;


    /**
     * Private constructor for the singleton Control instance.
     */
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

    /**
     * Retrieves the singleton instance of the Control class.
     *
     * @return the single instance of Control.
     */
    public static synchronized Control getInstance() {
        if (instance == null) {
            instance = new Control();
        }
        return instance;
    }
    /**
     * Sets up Firestore listeners to track changes in the database.
     */
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
//                                        notification.setDeclined(false);
//                                        DocumentReference notificationRef = doc.getReference();

//                                        notificationRef.update("declined", false)
//                                                .addOnSuccessListener(aVoid -> Log.i("Firestore", "Notification declined field successfully updated to false"))
//                                                .addOnFailureListener(ee -> Log.e("Firestore", "Failed to update declined field", ee));

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
    /**
     * Saves a user to the Firestore database.
     *
     * @param user the User object to save.
     */
    public void saveUser(User user) {
        db.collection("users").document(String.valueOf(user.getUserID())).set(user)
                .addOnSuccessListener(aVoid -> Log.i("Firestore", "User saved"))
                .addOnFailureListener(e -> Log.e("Firestore", "User save failed", e));
    }
    /**
     * Saves a facility to the Firestore database.
     *
     * @param facility the Facility object to save.
     */
    public void saveFacility(Facility facility) {
        int facilityID = findUserByID(facility.getCreatorRef()).getUserID();
        db.collection("facilities").document(String.valueOf(facilityID)).set(facility)
                .addOnSuccessListener(aVoid -> Log.i("Firestore", "Facility saved"))
                .addOnFailureListener(e -> Log.e("Firestore", "Facility save failed", e));
    }
    /**
     * Deletes the specified facility from the Firestore database.
     *
     * @param facility The Facility object to be deleted.
     */
    public void deleteFacility(Facility facility) {
        db.collection("facilities").document(String.valueOf(facility.getCreatorRef())).delete()
                .addOnSuccessListener(aVoid -> Log.i("Firestore", "Facility deleted"))
                .addOnFailureListener(e -> Log.e("Firestore", "Facility delete failed", e));
    }
    /**
     * Saves the specified event to the Firestore database.
     *
     * @param event The Event object to be saved.
     */
    public void saveEvent(Event event) {
        db.collection("events").document(String.valueOf(event.getEventID())).set(event)
                .addOnSuccessListener(aVoid -> Log.i("Firestore", "Event saved"))
                .addOnFailureListener(e -> Log.e("Firestore", "Event save failed", e));
    }
    /**
     * Deletes the specified event from the Firestore database.
     *
     * @param event The Event object to be deleted.
     */
    public void deleteEvent(Event event) {
        db.collection("events").document(String.valueOf(event.getEventID())).delete()
                .addOnSuccessListener(aVoid -> Log.i("Firestore", "Event deleted"))
                .addOnFailureListener(e -> Log.e("Firestore", "Event delete failed", e));
    }
    /**
     * Adds a new notification to the Firestore database.
     *
     * @param notification The Notification object to be added.
     */
    public void addNotification(Notification notification) {
        db.collection("notifications").add(notification)
                .addOnSuccessListener(documentReference -> {
                    notification.setDocumentID(documentReference.getId());
                    updateNotification(notification);
                    Log.i("Firestore", "Notification saved");
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Notification save failed", e));
    }
    /**
     * Updates the specified notification in the Firestore database.
     *
     * @param notification The Notification object to be updated.
     */
    public void updateNotification(Notification notification) {
        db.collection("notifications").document(notification.getDocumentID()).set(notification)
                .addOnSuccessListener(aVoid -> Log.i("Firestore", "Notification updated"))
                .addOnFailureListener(e -> Log.e("Firestore", "Notification update failed", e));
    }
    /**
     * Deletes the specified notification from the Firestore database.
     *
     * @param notification The Notification object to be deleted.
     */
    public void deleteNotification(Notification notification) {
        db.collection("notifications").document(notification.getDocumentID()).delete()
                .addOnSuccessListener(aVoid -> Log.i("Firestore", "Notification deleted"))
                .addOnFailureListener(e -> Log.e("Firestore", "Notification delete failed", e));
    }
    /**
     * Retrieves the current user ID.
     *
     * @return The current user ID.
     */
    // Getters and Setters
    public int getCurrentUserID() {
        return currentUserID;
    }

    /**
     * Retrieves the current user ID for user creation, increments the ID, and updates Firestore.
     *
     * @return The user ID to be used for user creation.
     */
    public int getCurrentUserIDForUserCreation() {
        int result = currentUserID;
        currentUserID++;
        db.collection("control").document("ControlData").update("currentUserID", currentUserID);
        return result;
    }
    /**
     * Retrieves the current event ID.
     *
     * @return The current event ID.
     */
    public int getCurrentEventID() {
        return currentEventID;
    }
    /**
     * Retrieves the current event ID for event creation, increments the ID, and updates Firestore.
     *
     * @return The event ID to be used for event creation.
     */
    public int getCurrentEventIDForEventCreation() {
        int result = currentEventID;
        currentEventID++;
        db.collection("control").document("ControlData").update("currentEventID", currentEventID);
        return result;
    }
    /**
     * Retrieves the current user based on the local FID.
     *
     * @return The User object of the current user, or null if not found.
     */
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
    /**
     * Retrieves the list of users.
     *
     * @return The list of User objects.
     */
    public ArrayList<User> getUserList() {
        return userList;
    }

    /**
     * Retrieves the list of events.
     *
     * @return The list of Event objects.
     */
    public ArrayList<Event> getEventList() {
        return eventList;
    }

    /**
     * Retrieves the list of facilities.
     *
     * @return The list of Facility objects.
     */
    public ArrayList<Facility> getFacilityList() {
        return facilityList;
    }

    /**
     * Retrieves the list of notifications.
     *
     * @return The list of Notification objects.
     */
    public ArrayList<Notification> getNotificationList() {
        return notificationList;
    }

    /**
     * Retrieves the local FID.
     *
     * @return The local FID as a string.
     */
    public static String getLocalFID() {
        return localFID;
    }

    /**
     * Sets the local FID.
     *
     * @param localFID The local FID to set.
     */
    public static void setLocalFID(String localFID) {
        Control.localFID = localFID;
    }

    /**
     * Finds a user by their ID.
     *
     * @param userID The ID of the user to find.
     * @return The User object if found, or null if not found.
     */
    public User findUserByID(int userID) {
        for (User user : userList) {
            if (user.getUserID() == userID) {
                return user;
            }
        }
        return null;
    }

    /**
     * Finds an event by its ID.
     *
     * @param eventID The ID of the event to find.
     * @return The Event object if found, or null if not found.
     */
    public Event findEventByID(int eventID) {
        for (Event event : eventList) {
            if (event.getEventID() == eventID) {
                return event;
            }
        }
        return null;
    }

    /**
     * Sends a notification with the specified event name and message.
     *
     * @param context   The application context.
     * @param eventName The name of the event.
     * @param message   The notification message.
     */
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


    /**
     * Get a test instance of the Control class (without database integration).
     */
    // For backend test only
    public static Control getTestInstance() {
        if (instance == null) {
            instance = new Control(1);
        }
        return instance;
    }
    /**
     * Constructor for the getTestInstance method.
     */
    private Control(int random) {
        this.currentUserID = 0;
        this.currentEventID = 0;
        this.userList = new ArrayList<>();
        this.facilityList = new ArrayList<>();
        this.eventList = new ArrayList<>();
        this.notificationList = new ArrayList<>();
    }


}
