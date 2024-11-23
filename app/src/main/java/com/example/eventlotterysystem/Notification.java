package com.example.eventlotterysystem;

public class Notification {
    private int eventRef;
    private int userRef;
    private Boolean needAccept;
    private Boolean isAccepted;
    private Boolean isDeclined;
    private String customMessage;

    // Default no-argument constructor (required for Firestore)
    public Notification() {}

    // For notification creation
    public Notification(int eventRef, int userRef, Boolean needAccept, String customMessage) {
        this.eventRef = eventRef;
        this.userRef = userRef;
        this.needAccept = needAccept;
        this.isAccepted = false;
        this.isDeclined = false;
        this.customMessage = customMessage;
    }

    // Getters and Setters
    public int getEventRef() {
        return eventRef;
    }
    public int getUserRef() {
        return userRef;
    }

}
