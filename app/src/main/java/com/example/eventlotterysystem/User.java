package com.example.eventlotterysystem;

import java.util.ArrayList;

public class User {
    private int userID;
    private String name;
    private String email;
    private String contact;
    private String picture;
    private int facilityRef;
    private boolean isAdmin;
    private ArrayList<Integer> notificationRefs;
    private ArrayList<Integer> enrolledEventRefs;
    private ArrayList<Integer> organizedEventRefs;
    private Boolean notificationSetting;
    private String FID;

    public int getUserID() {
        return userID;
    }
}
