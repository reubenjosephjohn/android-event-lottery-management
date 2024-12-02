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
 * Represents a user in the event lottery system. The user will have a unique userID, name, email,
 * contact information, picture, Firebase installation ID (FID). It also saves the user's admin status
 * and notification settings.
 */

public class User implements Serializable {
    /**
     * A unique user ID, which is automatically assigned by the system during user creation.
     */
    private int userID;
    /**
     * User's details.
     */
    private String name;
    private String email;
    private String contact;
    /**
     * User's encoded profile picture, which can be generated or uploaded.
     */
    private String picture;
    /**
     * Indicates whether the user is an admin.
     */
    private boolean isAdmin;
    /**
     * Indicates whether the user has notifications enabled or disabled.
     */
    private Boolean notificationSetting;
    /**
     * Firebase installation ID.
     */
    private String FID;
    /**
     * Firebase notification token.
     */
    private String notificationToken;

    /**
     * Default no-argument constructor required for Firestore.
     */
    public User() {}

    /**
     * Constructor for creating a new user.
     * @param UserID
     * @param FID
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
     * Determine if a user's profile is valid or not. Users with invalid profiles may be rejected to
     * perform some operations.
     * @return true if the user has a valid name and email, false otherwise.
     */
    public boolean isValid (){
        if (name.equals("Default Name") || email.equals("user@example.com")) {
            return false;
        }
        return true;
    }


    // Getters and Setters
    public int getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Boolean getNotificationSetting() {
        return notificationSetting;
    }

    public void setNotificationSetting(Boolean notificationSetting) {
        this.notificationSetting = notificationSetting;
    }

    public String getFID() {
        return FID;
    }

    public void setFID(String FID) {
        this.FID = FID;
    }

    public String getNotificationToken() {return notificationToken;}

    public void setNotificationToken(String notificationToken) {this.notificationToken = notificationToken; }

    /**
     * Generates a picture for the user based on their name.
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
     * Helper method to extract initials from the name
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
     * Helper method to create a Bitmap with initials
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
     * Helper method to encode Bitmap to a String (Base64 encoding)
     */
    private String encodeBitmap(Bitmap bitmap) {
        // Convert bitmap to a Base64 encoded string (as an example)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
