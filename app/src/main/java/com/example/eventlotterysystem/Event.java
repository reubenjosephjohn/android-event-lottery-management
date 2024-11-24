package com.example.eventlotterysystem;

import android.graphics.Bitmap;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
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

    public int getLimitChosenList() {
        return limitChosenList;
    }

    public void setLimitChosenList(int limitChosenList) {
        this.limitChosenList = limitChosenList;
    }

    public int getLimitWaitingList() {
        return limitWaitingList;
    }

    public void setLimitWaitingList(int limitWaitingList) {
        this.limitWaitingList = limitWaitingList;
    }

    public int getCreatorRef() {
        return creatorRef;
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

    public String getHashCodeQR() {
        return hashCodeQR;
    }

    public void setHashCodeQR(String hashCodeQR) {
        this.hashCodeQR = hashCodeQR;
    }

    public ArrayList<Integer> getWaitingUserRefs() {
        return waitingUserRefs;
    }

    public void setWaitingUserRefs(ArrayList<Integer> waitingUserRefs) {
        this.waitingUserRefs = waitingUserRefs;
    }

    public ArrayList<Integer> getCancelledUserRefs() {
        return cancelledUserRefs;
    }

    public void setCancelledUserRefs(ArrayList<Integer> cancelledUserRefs) {
        this.cancelledUserRefs = cancelledUserRefs;
    }

    public ArrayList<Integer> getChosenUserRefs() {
        return chosenUserRefs;
    }

    public void setChosenUserRefs(ArrayList<Integer> chosenUserRefs) {
        this.chosenUserRefs = chosenUserRefs;
    }

    public ArrayList<Integer> getFinalUserRefs() {
        return finalUserRefs;
    }

    public void setFinalUserRefs(ArrayList<Integer> finalUserRefs) {
        this.finalUserRefs = finalUserRefs;
    }

    public Boolean getGeoSetting() {
        return geoSetting;
    }

    public void setGeoSetting(Boolean geoSetting) {
        this.geoSetting = geoSetting;
    }

    public ArrayList<Double> getLatitudeList() {
        return latitudeList;
    }

    public void setLatitudeList(ArrayList<Double> latitudeList) {
        this.latitudeList = latitudeList;
    }

    public ArrayList<Double> getLongitudeList() {
        return longitudeList;
    }

    public void setLongitudeList(ArrayList<Double> longitudeList) {
        this.longitudeList = longitudeList;
    }

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

    private String encodeBitmap(Bitmap bitmap) {
        // Convert bitmap to a Base64 encoded string (as an example)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}
