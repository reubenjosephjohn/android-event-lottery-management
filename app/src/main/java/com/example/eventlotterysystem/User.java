package com.example.eventlotterysystem;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a user in the lottery system with details such as name, email, contact, and picture.
 */
public class User implements Serializable {
    private int userID;
    private String name;
    private String email;
    private String contact;
    private String picture;
    private boolean isAdmin;
    private Boolean notificationSetting;
    private String FID;
    private String notificationToken;

    /**
     * Default no-argument constructor (required for Firestore).
     */
    public User() {}

    
    /**
     * Constructs a new User with the specified user ID and Firebase installation ID (FID).
     *
     * @param UserID The user ID.
     * @param FID The Firebase installation ID.
     */
    public User(int UserID, String FID) {
        this.userID = UserID;
        this.name = "Default Name";
        this.email = "user@example.com";
        this.contact = "000-000-0000";
        this.picture = null;
        this.isAdmin = false;
        this.notificationSetting = true;
        this.FID = FID;
    }

    /**
     * Checks if the user is valid.
     *
     * @return True if the user is valid, false otherwise.
     */
    public Boolean isValid (){
        if (name.equals("Default Name") || email.equals("user@example.com")) {
            return false;
        }
        return true;
    }

    // Getters and Setters

    /**
     * Gets the user ID.
     *
     * @return The user ID.
     */    
    public int getUserID() {
        return userID;
    }

    /**
     * Gets the name of the user.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param name The user's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the email of the user.
     *
     * @return The user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     *
     * @param email The user's email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the contact information of the user.
     *
     * @return The user's contact information.
     */
    public String getContact() {
        return contact;
    }

    /**
     * Sets the contact information of the user.
     *
     * @param contact The user's contact information.
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * Gets the picture of the user.
     *
     * @return The user's picture.
     */
    public String getPicture() {
        return picture;
    }

    /**
     * Sets the picture of the user.
     *
     * @param picture The user's picture.
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     * Checks if the user is an admin.
     *
     * @return True if the user is an admin, false otherwise.
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Sets whether the user is an admin.
     *
     * @param isAdmin True if the user is an admin, false otherwise.
     */
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    /**
     * Gets the notification setting of the user.
     *
     * @return The notification setting.
     */
    public Boolean getNotificationSetting() {
        return notificationSetting;
    }

    /**
     * Sets the notification setting of the user.
     *
     * @param notificationSetting The notification setting.
     */
    public void setNotificationSetting(Boolean notificationSetting) {
        this.notificationSetting = notificationSetting;
    }

    /**
     * Gets the Firebase installation ID (FID) of the user.
     *
     * @return The Firebase installation ID.
     */
    public String getFID() {
        return FID;
    }

    /**
     * Sets the Firebase installation ID (FID) of the user.
     *
     * @param FID The Firebase installation ID.
     */
    public void setFID(String FID) {
        this.FID = FID;
    }

    /**
     * Gets the notification token of the user.
     *
     * @return The notification token.
     */
    public String getNotificationToken() {return notificationToken;}

    /**
     * Sets the notification token of the user.
     *
     * @param notificationToken The notification token.
     */
    public void setNotificationToken(String notificationToken) {this.notificationToken = notificationToken; }

    /**
     * Generates a profile picture for the user based on their initials.
     */
    public void generate_picture() {
        if (name != null && !name.isEmpty()) {
            // Extract initials from the user's name
            String initials = getInitials(name);

            // Create the picture (Bitmap) for the user
            Bitmap bitmap = createImageWithInitials(initials);

            // Create a Picture object with the generated Bitmap (assumes the current user is the uploader)
            this.picture = encodeBitmap(bitmap); // You may need a way to encode the bitmap as a String
        }
    }

    /**
     * Helper method to extract initials from the name.
     *
     * @param name The name to extract initials from.
     * @return The initials of the name.
     */
    private String getInitials(String name) {
        String[] nameParts = name.split(" ");
        StringBuilder initials = new StringBuilder();
        for (String part : nameParts) {
            if (!part.isEmpty()) {
                initials.append(part.charAt(0));  // Take the first letter of each name part
            }
        }
        return initials.toString().toUpperCase();  // Convert initials to uppercase
    }

    /**
     * Helper method to create a Bitmap with initials.
     *
     * @param initials The initials to display on the Bitmap.
     * @return The generated Bitmap.
     */
    private Bitmap createImageWithInitials(String initials) {
        int width = 200;  // Image width
        int height = 200; // Image height
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Set background color
        canvas.drawColor(Color.parseColor("#4CAF50")); // Green background (you can change the color)

        // Set up paint object for text
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);  // Text color
        paint.setTextSize(100);  // Text size
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextAlign(Paint.Align.CENTER);

        // Draw initials in the center of the canvas
        float xPos = width / 2;
        float yPos = (height / 2) - ((paint.descent() + paint.ascent()) / 2);
        canvas.drawText(initials, xPos, yPos, paint);

        return bitmap;  // Return the generated bitmap
    }

    /**
     * Helper method to encode a Bitmap to a Base64 encoded string.
     *
     * @param bitmap The Bitmap to encode.
     * @return The Base64 encoded string representation of the bitmap.
     */
    private String encodeBitmap(Bitmap bitmap) {
        // Convert bitmap to a Base64 encoded string (as an example)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
