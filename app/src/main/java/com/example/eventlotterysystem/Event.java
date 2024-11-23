package com.example.eventlotterysystem;

import java.util.ArrayList;

public class Event {
    private int eventID;
    private String name;
    private String description;
    private int limitChosenList; // This is the limit for the total events
    private int limitWaitingList;
    private int creatorRef;
    private String poster;
    private String hashCodeQR;
    private ArrayList<Integer> waitingUserRefs;
    private ArrayList<Integer> cancelledUserRefs;
    private ArrayList<Integer> chosenUserRefs;
    private ArrayList<Integer> finalUserRefs;
    private Boolean geoSetting;
    private ArrayList<Double> latitudeList;
    private ArrayList<Double> longitudeList;

    // Default no-argument constructor (required for Firestore)
    public Event() {}

    // For event creation
    public Event(int eventID, String name, String description, int limitChosenList, int limitWaitingList, boolean geoSetting) {
        this.eventID = eventID;
        this.name = name;
        this.description = description;
        this.limitChosenList = limitChosenList;
        this.limitWaitingList = limitWaitingList;
        this.geoSetting = geoSetting;
        this.waitingUserRefs = new ArrayList<>();
        this.cancelledUserRefs = new ArrayList<>();
        this.chosenUserRefs = new ArrayList<>();
        this.finalUserRefs = new ArrayList<>();
        this.latitudeList = new ArrayList<>();
        this.longitudeList = new ArrayList<>();
    }

    public int getEventID() {
        return eventID;
    }

}
