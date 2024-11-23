package com.example.eventlotterysystem;

public class Facility {
    private String name;
    private String description;
    private int creatorRef;
    private String poster;

    // Default no-argument constructor (required for Firestore)
    public Facility() {}

    // For facility creation
    public Facility(String name, String description, int creatorRef) {
        this.name = name;
        this.description = description;
        this.creatorRef = creatorRef;
        this.poster = null;
    }

    public int getCreatorRef() {
        return creatorRef;
    }
}
