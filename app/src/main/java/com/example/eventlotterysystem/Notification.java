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
     * Constructs a new Notification with the specified details.
     *
     * @param eventRef      The reference ID of the event.
     * @param userRef       The reference ID of the user.
     * @param needAccept    Whether the notification requires acceptance.
     * @param customMessage A custom message for the notification.

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

    /**
     * Gets the reference ID of the event.
     *
     * @return The event reference ID.
     */
    public int getEventRef() {
        return eventRef;
    }

    /**
     * Gets the reference ID of the user.
     *
     * @return The user reference ID.
     */
    public int getUserRef() {
        return userRef;
    }

    /**
     * Gets the document ID of the notification.
     *
     * @return The document ID.
     */
    public String getDocumentID() {
        return documentID;
    }

    /**
     * Sets the document ID of the notification.
     *
     * @param documentID The document ID to set.
     */
    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    /**
     * Gets the custom message of the notification.
     *
     * @return The custom message.
     */
    public String getCustomMessage() {
        return customMessage;
    }

    /**
     * Sets the custom message of the notification.
     *
     * @param customMessage The custom message to set.
     */
    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    /**
     * Gets whether the notification requires acceptance.
     *
     * @return True if the notification requires acceptance, false otherwise.
     */
    public Boolean getNeedAccept() {
        return needAccept;
    }

    /**
     * Sets whether the notification requires acceptance.
     *
     * @param needAccept True if the notification requires acceptance, false otherwise.
     */
    public void setNeedAccept(Boolean needAccept) {
        this.needAccept = needAccept;
    }

    
    /**
     * Gets whether the notification is accepted.
     *
     * @return True if the notification is accepted, false otherwise.
     */
    public Boolean getAccepted() {
        return isAccepted;
    }

    /**
     * Sets whether the notification is accepted.
     *
     * @param isAccepted True if the notification is accepted, false otherwise.
     */
    public void setAccepted(Boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    /**
     * Gets whether the notification is declined.
     *
     * @return True if the notification is declined, false otherwise.
     */
    public Boolean getDeclined() {
        return isDeclined;
    }

    /**
     * Sets whether the notification is declined.
     *
     * @param declined True if the notification is declined, false otherwise.
     */
    public void setDeclined(Boolean declined) {
        isDeclined = declined;
    }
}
