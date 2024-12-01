package com.example.eventlotterysystem;

import java.io.Serializable;

/**
 * Represents a facility in the event lottery system.
 *
 * <p>A facility contains information about its name, description, creator reference,
 * and an optional poster image. This class is designed to be compatible with Firestore
 * serialization and deserialization.</p>
 *
 */
public class Facility implements Serializable {

    /**
     * The name of the facility.
     */
    private String name;

    /**
     * A description of the facility.
     */
    private String description;

    /**
     * A reference to the creator of the facility.
     */
    private int creatorRef;

    /**
     * An optional poster image associated with the facility.
     */
    private String poster;

    /**
     * Default no-argument constructor required for Firestore.
     */
    public Facility() {}

    /**
     * Constructs a new Facility instance with specified name, description, and creator reference.
     *
     * @param name the name of the facility
     * @param description a brief description of the facility
     * @param creatorRef a reference to the creator of the facility
     */
    public Facility(String name, String description, int creatorRef) {
        this.name = name;
        this.description = description;
        this.creatorRef = creatorRef;
        this.poster = null;
    }

    /**
     * Gets the creator reference for the facility.
     *
     * @return the creator reference
     */
    public int getCreatorRef() {
        return creatorRef;
    }

    /**
     * Gets the name of the facility.
     *
     * @return the name of the facility
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the facility.
     *
     * @param name the new name of the facility
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the facility.
     *
     * @return the description of the facility
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the facility.
     *
     * @param description the new description of the facility
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the creator reference for the facility.
     *
     * @param creatorRef the new creator reference
     */
    public void setCreatorRef(int creatorRef) {
        this.creatorRef = creatorRef;
    }

    /**
     * Gets the poster image associated with the facility.
     *
     * @return the poster image URL or path
     */
    public String getPoster() {
        return poster;
    }

    /**
     * Sets the poster image for the facility.
     *
     * @param poster the URL or path to the new poster image
     */
    public void setPoster(String poster) {
        this.poster = poster;
    }
}
