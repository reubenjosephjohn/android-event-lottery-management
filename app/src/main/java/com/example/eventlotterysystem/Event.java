package com.example.eventlotterysystem;

import android.graphics.Bitmap;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
/**
 * Represents an Event in the lottery system with details such as name, description, user lists, and geo-settings.
 * Provides functionality to generate a QR code for the event.
 */
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

    /**
     * Default constructor required for Firestore serialization.
     */
    public Event() {}
    /**
     * Constructor to create a new Event with necessary details such as event ID, name, description, limits, and geo settings.
     *
     * @param eventID         The unique identifier for the event.
     * @param name            The name of the event.
     * @param description     A description of the event.
     * @param limitChosenList The limit of participants chosen for the event.
     * @param limitWaitingList The limit of participants on the waiting list.
     * @param geoSetting      A boolean indicating if geolocation settings are enabled.
     */
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

    /**
     * Gets the event ID.
     *
     * @return The event ID.
     */
    public int getEventID() {
        return eventID;
    }

    /**
     * Gets the name of the event.
     *
     * @return The event name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the event.
     *
     * @param name The event name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the event.
     *
     * @return The event description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the event.
     *
     * @param description The event description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the limit for the chosen list.
     *
     * @return The limit for the chosen list.
     */
    public int getLimitChosenList() {
        return limitChosenList;
    }

    /**
     * Sets the limit for the chosen list.
     *
     * @param limitChosenList The limit for the chosen list to set.
     */
    public void setLimitChosenList(int limitChosenList) {
        this.limitChosenList = limitChosenList;
    }

    /**
     * Gets the limit for the waiting list.
     *
     * @return The limit for the waiting list.
     */
    public int getLimitWaitingList() {
        return limitWaitingList;
    }

    /**
     * Sets the limit for the waiting list.
     *
     * @param limitWaitingList The limit for the waiting list to set.
     */
    public void setLimitWaitingList(int limitWaitingList) {
        this.limitWaitingList = limitWaitingList;
    }

    /**
     * Gets the reference ID of the creator.
     *
     * @return The creator reference ID.
     */
    public int getCreatorRef() {
        return creatorRef;
    }

    /**
     * Sets the reference ID of the creator.
     *
     * @param creatorRef The creator reference ID to set.
     */
    public void setCreatorRef(int creatorRef) {
        this.creatorRef = creatorRef;
    }

    /**
     * Gets the poster of the event.
     *
     * @return The event poster.
     */
    public String getPoster() {
        return poster;
    }

    /**
     * Sets the poster of the event.
     *
     * @param poster The event poster to set.
     */
    public void setPoster(String poster) {
        this.poster = poster;
    }

    /**
     * Gets the Base64 encoded QR code for the event.
     *
     * @return The Base64 encoded QR code.
     */
    public String getHashCodeQR() {
        return hashCodeQR;
    }

    /**
     * Sets the Base64 encoded QR code for the event.
     *
     * @param hashCodeQR The Base64 encoded QR code to set.
     */
    public void setHashCodeQR(String hashCodeQR) {
        this.hashCodeQR = hashCodeQR;
    }

    /**
     * Gets the list of waiting user references.
     *
     * @return The list of waiting user references.
     */
    public ArrayList<Integer> getWaitingUserRefs() {
        return waitingUserRefs;
    }

    /**
     * Sets the list of waiting user references.
     *
     * @param waitingUserRefs The list of waiting user references to set.
     */
    public void setWaitingUserRefs(ArrayList<Integer> waitingUserRefs) {
        this.waitingUserRefs = waitingUserRefs;
    }

    /**
     * Gets the list of cancelled user references.
     *
     * @return The list of cancelled user references.
     */
    public ArrayList<Integer> getCancelledUserRefs() {
        return cancelledUserRefs;
    }

    /**
     * Sets the list of cancelled user references.
     *
     * @param cancelledUserRefs The list of cancelled user references to set.
     */
    public void setCancelledUserRefs(ArrayList<Integer> cancelledUserRefs) {
        this.cancelledUserRefs = cancelledUserRefs;
    }

    /**
     * Gets the list of chosen user references.
     *
     * @return The list of chosen user references.
     */
    public ArrayList<Integer> getChosenUserRefs() {
        return chosenUserRefs;
    }

    /**
     * Sets the list of chosen user references.
     *
     * @param chosenUserRefs The list of chosen user references to set.
     */
    public void setChosenUserRefs(ArrayList<Integer> chosenUserRefs) {
        this.chosenUserRefs = chosenUserRefs;
    }

    /**
     * Gets the list of final user references.
     *
     * @return The list of final user references.
     */
    public ArrayList<Integer> getFinalUserRefs() {
        return finalUserRefs;
    }

    /**
     * Sets the list of final user references.
     *
     * @param finalUserRefs The list of final user references to set.
     */
    public void setFinalUserRefs(ArrayList<Integer> finalUserRefs) {
        this.finalUserRefs = finalUserRefs;
    }

    /**
     * Gets the geo setting of the event.
     *
     * @return The geo setting.
     */
    public Boolean getGeoSetting() {
        return geoSetting;
    }

    /**
     * Sets the geo setting of the event.
     *
     * @param geoSetting The geo setting to set.
     */
    public void setGeoSetting(Boolean geoSetting) {
        this.geoSetting = geoSetting;
    }

    /**
     * Gets the list of latitude values.
     *
     * @return The list of latitude values.
     */
    public ArrayList<Double> getLatitudeList() {
        return latitudeList;
    }

    /**
     * Sets the list of latitude values.
     *
     * @param latitudeList The list of latitude values to set.
     */
    public void setLatitudeList(ArrayList<Double> latitudeList) {
        this.latitudeList = latitudeList;
    }

    /**
     * Gets the list of longitude values.
     *
     * @return The list of longitude values.
     */
    public ArrayList<Double> getLongitudeList() {
        return longitudeList;
    }

    /**
     * Sets the list of longitude values.
     *
     * @param longitudeList The list of longitude values to set.
     */
    public void setLongitudeList(ArrayList<Double> longitudeList) {
        this.longitudeList = longitudeList;
    }
    
    /**
     * Generates a QR code for the event using the event ID.
     * The QR code is encoded as a Base64 string and stored in the `hashCodeQR` field.
     */
    public void generateQR() {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            // Define QR code dimensions
            int width = 200;
            int height = 200;
            // Generate QR code bit matrix
            com.google.zxing.common.BitMatrix bitMatrix = writer.encode(String.valueOf(eventID), BarcodeFormat.QR_CODE, width, height);
            // Create a bitmap from the bit matrix
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            this.hashCodeQR = encodeBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Encodes a Bitmap image to a Base64 encoded string.
     *
     * @param bitmap The Bitmap image to be encoded.
     * @return A Base64 encoded string representation of the bitmap.
     */
    private String encodeBitmap(Bitmap bitmap) {
        // Convert bitmap to a Base64 encoded string (as an example)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}
