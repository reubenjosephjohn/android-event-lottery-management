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
