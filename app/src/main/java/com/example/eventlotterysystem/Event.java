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
    private Boolean GeoSetting;
    private ArrayList<Double> latitudeList;
    private ArrayList<Double> longitudeList;

    public int getEventID() {
        return eventID;
    }

}
