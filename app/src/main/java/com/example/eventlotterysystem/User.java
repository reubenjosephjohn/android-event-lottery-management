package com.example.eventlotterysystem;

import java.util.ArrayList;

public class User {
    private int userID;
    private String name;
    private String email;
    private String contact;
    private String picture;
    private boolean isAdmin;
    private Boolean notificationSetting;
    private String FID;

    // Default no-argument constructor (required for Firestore)
    public User() {}

    // For User creation after checking device
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
}
