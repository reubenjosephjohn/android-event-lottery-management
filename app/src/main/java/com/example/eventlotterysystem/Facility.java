package com.example.eventlotterysystem;

import java.io.Serializable;

public class Facility implements Serializable {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatorRef(int creatorRef) {
        this.creatorRef = creatorRef;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
