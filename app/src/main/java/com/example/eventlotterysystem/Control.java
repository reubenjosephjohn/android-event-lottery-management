package com.example.eventlotterysystem;

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




    public Control() {
        this.currentUserID = 0;
        this.currentEventID = 0;
        this.userList = new ArrayList<>();
        this.facilityList = new ArrayList<>();
        this.eventList = new ArrayList<>();
        this.notificationList = new ArrayList<>();
    }


    public static Control getInstance() {
        if (instance == null) {
            instance = new Control();
        }
        return instance;
    }

    // finder methods
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
