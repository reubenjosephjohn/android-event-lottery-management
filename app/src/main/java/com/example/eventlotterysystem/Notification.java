package com.example.eventlotterysystem;

/**
 * Represents a notification in the event lottery system.
 *
 * <p>A notification contains information about the event reference, user reference, customMessage,
 * documentID in the database and the status of this notification. </p>
 *
 */
public class Notification {
    /**
     * A reference to the event associated with the notification.
     */
    private int eventRef;
    /**
     * A reference to the recipient of the notification.
     */
    private int userRef;
    /**
     * Notification status flags.
     */
    private Boolean needAccept;
    private Boolean isAccepted;
    private Boolean isDeclined;
    /**
     * A custom message associated with the notification.
     */
    private String customMessage;
    /**
     * A unique identifier for the notification in the database.
     */
    private String documentID;

    /**
     * Default no-argument constructor required for Firestore.
     */
    public Notification() {}

    /**
     * Constructs a new Notification instance with specified event reference, user reference,
     * @param eventRef
     * @param userRef
     * @param needAccept
     * @param customMessage
     */
    public Notification(int eventRef, int userRef, Boolean needAccept, String customMessage) {
        this.eventRef = eventRef;
        this.userRef = userRef;
        this.needAccept = needAccept;
        this.isAccepted = false;
        this.isDeclined = true;
        this.customMessage = customMessage;
        this.documentID = "";
    }

    // Getters and Setters
    public int getEventRef() {
        return eventRef;
    }

    public int getUserRef() {
        return userRef;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    public Boolean getNeedAccept() {
        return needAccept;
    }

    public void setNeedAccept(Boolean needAccept) {
        this.needAccept = needAccept;
    }

    public Boolean getAccepted() {
        return isAccepted;
    }

    public void setAccepted(Boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    public Boolean getDeclined() {
        return isDeclined;
    }

    public void setDeclined(Boolean declined) {
        isDeclined = declined;
    }
}
