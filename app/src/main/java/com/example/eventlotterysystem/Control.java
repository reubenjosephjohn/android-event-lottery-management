package com.example.eventlotterysystem;

import android.util.Log;

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
    private static User currentUser = null; // current logged in user
    private static String localFID = "";
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
                    if (queryDocumentSnapshots != null) {
                        notificationList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Notification notification = doc.toObject(Notification.class);
                            notificationList.add(notification);
                        }
                        Log.i("Firestore", "Notification list updated");
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

    public void saveEvent(Event event) {
        db.collection("events").document(String.valueOf(event.getEventID())).set(event)
                .addOnSuccessListener(aVoid -> Log.i("Firestore", "Event saved"))
                .addOnFailureListener(e -> Log.e("Firestore", "Event save failed", e));
    }

    public void saveNotification(Notification notification) {
        db.collection("notifications").add(notification)
                .addOnSuccessListener(aVoid -> Log.i("Firestore", "Notification saved"))
                .addOnFailureListener(e -> Log.e("Firestore", "Notification save failed", e));
    }

    // Getters and Setters
    public int getCurrentUserID() {
        return currentUserID;
    }


    public int getCurrentUserIDForUserCreation() {
        int result = currentEventID;
        currentEventID++;
        db.collection("control").document("ControlData").update("currentEventID", currentEventID);
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
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        Control.currentUser = currentUser;
    }

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

    // finder methods (use try-catch to handle errors)
    public User findUserByID(int userID) {
        for (User user : userList) {
            if (user.getUserID() == userID) {
                return user;
            }
        }
        throw new IllegalArgumentException("User with ID " + userID + " not found.");
    }

    public Event findEventByID(int eventID) {
        for (Event event : eventList) {
            if (event.getEventID() == eventID) {
                return event;
            }
        }
        throw new IllegalArgumentException("Event with ID " + eventID + " not found.");
    }

}
