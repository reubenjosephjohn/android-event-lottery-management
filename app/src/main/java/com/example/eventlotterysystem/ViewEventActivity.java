package com.example.eventlotterysystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.location.Location;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * ViewEventActivity displays the details of a selected event and allows the user to join or cancel their participation.
 * Admin users can also delete the event.
 */
public class ViewEventActivity extends AppCompatActivity {

    /** TextView for displaying the event title */
    private TextView eventTitle;

    /** TextView for displaying the event details */
    private TextView eventDetail;

    /** ImageView for displaying the event poster */
    private ImageView eventPoster;

    /** Button for joining or canceling participation in the event */
    private Button joinbutton, declinebutton;

    /** ImageView for deleting the event, visible only to admin users */
    private ImageView deleteButton;

    /** ImageView for returning to the previous activity */
    private ImageView returnButton;

    /** The current event being viewed */
    private Event curEvent;

    /** The currently logged-in user */
    private User curUser;
    private TextView textView10;

    private FusedLocationProviderClient fusedLocationClient;

    /**
     * Called when the activity is first created. Initializes the view elements, retrieves the
     * Event object and user details, and sets up the UI with event data.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState.
     */
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_entrant_join);

        curUser = Control.getCurrentUser();

        // Initialize views
        eventTitle = findViewById(R.id.name);
        eventDetail = findViewById(R.id.Event_detail);
        eventPoster = findViewById(R.id.poster);
        deleteButton = findViewById(R.id.del_button);
        returnButton = findViewById(R.id.return_button);
        joinbutton = findViewById(R.id.Entrant_join_button);
        declinebutton = findViewById(R.id.decline);
        textView10 = findViewById(R.id.textView10);

        curEvent = null;
        // Retrieve the Event object passed via intent
        int id = (int) getIntent().getSerializableExtra("eventID");
        for (Event event : Control.getInstance().getEventList()) {
            if (event.getEventID() == id) {
                curEvent = event;
                break;
            }
        }

        String picture = curEvent.getPoster();  // Get the current picture from the user object
        if (picture != null) {
            // If a picture exists, decode the Base64 content and set it to the ImageView
            Bitmap pictureBitmap = decodeBitmap(picture);  // Assuming decodeBitmap method to convert String to Bitmap
            eventPoster.setImageBitmap(pictureBitmap);  // Set the generated bitmap as the ImageView source
        }


        if(inList(curEvent.getWaitingUserRefs(), curUser.getUserID())){
            joinbutton.setText("Cancel Event");
            declinebutton.setVisibility(View.GONE);
        }else if(inList(curEvent.getChosenUserRefs(), curUser.getUserID())){
            joinbutton.setVisibility(View.GONE);
            declinebutton.setVisibility(View.GONE);
            textView10.setVisibility(View.VISIBLE);
        }else if(inList(curEvent.getFinalUserRefs(), curUser.getUserID())){
            joinbutton.setVisibility(View.GONE);
            declinebutton.setVisibility(View.GONE);
        }else if(inList(curEvent.getCancelledUserRefs(), curUser.getUserID())){
            joinbutton.setVisibility(View.GONE);
            declinebutton.setVisibility(View.GONE);
        }

        else {
            joinbutton.setText("Join Event");
            declinebutton.setVisibility(View.GONE);
        }


        // Hide delete button if user is not an admin
        if (!curUser.isAdmin()) {
            deleteButton.setVisibility(View.GONE);
        }
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewEventActivity.this);
                builder.setTitle("Delete")
                        .setMessage("Choose one of the following actions:")
                        .setPositiveButton("QR code", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                curEvent.setHashCodeQR(null);
                                Control.getInstance().saveEvent(curEvent);
                            }
                        })
                        .setNegativeButton("Poster", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                curEvent.setPoster(null);
                                Control.getInstance().saveEvent(curEvent);
                                eventPoster.setImageBitmap(null);
                            }
                        })
                        .setNeutralButton("Event", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(ViewEventActivity.this)
                                        .setTitle("Delete Event")
                                        .setMessage("Are you sure you want to delete your event?")
                                        .setPositiveButton("Delete", (dialog1, which1) -> {
                                            // Delete related Notifications
                                            ArrayList<Notification> notificationsToDelete = new ArrayList<>();
                                            for (Notification notification : Control.getInstance().getNotificationList()) {
                                                if (notification.getEventRef() == curEvent.getEventID()) {
                                                    Control.getInstance().deleteNotification(notification);
                                                    notificationsToDelete.add(notification);
                                                }
                                            }
                                            Control.getInstance().getNotificationList().removeAll(notificationsToDelete);
                                            // Delete event from database and Control
                                            Control.getInstance().deleteEvent(curEvent);
                                            Control.getInstance().getEventList().remove(curEvent);
                                            finish();
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            }
                        })
                        .setCancelable(true)
                        .show();
            }
        });



        // Populate the UI with event data
        if (curEvent != null) {
            eventTitle.setText(curEvent.getName());
            eventDetail.setText("Description: " + curEvent.getDescription() + "\n"
                    + "Capacity of Event: (" + (curEvent.getChosenUserRefs().size() + curEvent.getFinalUserRefs().size()) + "/" + curEvent.getLimitChosenList() + ")\n"
                    + "Capacity of Waiting List: (" + curEvent.getWaitingUserRefs().size() + "/" + curEvent.getLimitWaitingList() + ")");
        }

        // Set up the return button to go back to the Events list
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(ViewEventActivity.this, EventslistActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (joinbutton.getText().equals("Join Event")) {
            if ((curEvent.getChosenUserRefs().size() + curEvent.getFinalUserRefs().size())>=curEvent.getLimitChosenList() || curEvent.getWaitingUserRefs().size() >= curEvent.getLimitWaitingList()){
                joinbutton.setEnabled(false);
            }
        }
        // Join or cancel participation in the event based on current status
        joinbutton.setOnClickListener(v -> {
            if (joinbutton.getText().equals("Join Event")) {
                if (!(curUser.isValid())) {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("Cannot Join")
                            .setMessage("You need a valid profile to join")
                            .setPositiveButton("Confirm", (dialogInterface, which) -> {
                                dialogInterface.dismiss();
                            })
                            .create();
                    dialog.show();
                }

                else{
                    if (curEvent.getGeoSetting()) {
                        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {

                            // Request location permissions from the user
                            ActivityCompat.requestPermissions(this,
                                    new String[]{
                                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                                    },
                                    1001); // Request code (unique identifier)
                            return; // Exit the current method to wait for the user's response
                        }
                        // Create a dialog if geolocation is required
                        new android.app.AlertDialog.Builder(ViewEventActivity.this)
                                .setMessage("This event requires geo information. Do you want to join?")
                                .setPositiveButton("Confirm", (dialog, which) -> {
                                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                    // Permission is already granted; proceed with location access
                                    fusedLocationClient.getLastLocation()
                                            .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Location> task) {
                                                    if (task.isSuccessful() && task.getResult() != null) {
                                                        // Location found, now get the latitude and longitude
                                                        Location location = task.getResult();
                                                        double latitude = location.getLatitude();
                                                        double longitude = location.getLongitude();
                                                        // Store geo-location in Firestore
                                                        curEvent.getLatitudeList().add(latitude);
                                                        curEvent.getLongitudeList().add(longitude);
                                                        Toast.makeText(getApplicationContext(),
                                                                "Latitude: " + latitude + ", Longitude: " + longitude,
                                                                Toast.LENGTH_SHORT).show();
                                                        // Save user action to Firestore
                                                        Control.getInstance().saveEvent(curEvent);
                                                    }
                                                }
                                            });
                                    // User confirmed, proceed with joining the event
                                    curEvent.getWaitingUserRefs().add(curUser.getUserID());
                                    joinbutton.setText("Cancel Event");
                                    // Update event details
                                    eventDetail.setText("Description: " + curEvent.getDescription() + "\n"
                                            + "Capacity of Event: (" + (curEvent.getChosenUserRefs().size() + curEvent.getFinalUserRefs().size()) + "/" + curEvent.getLimitChosenList() + ")\n"
                                            + "Capacity of Waiting List: (" + curEvent.getWaitingUserRefs().size() + "/" + curEvent.getLimitWaitingList() + ")");
                                    // Save user action to Firestore
                                    Control.getInstance().saveEvent(curEvent);
                                })
                                .setNegativeButton("Cancel", (dialog, which) -> {
                                    // User canceled, don't join the event
                                    dialog.dismiss();
                                })
                                .create()
                                .show();
                    } else {
                        // No geo requirement, join the event directly
                        curEvent.getWaitingUserRefs().add(curUser.getUserID());
                        joinbutton.setText("Cancel Event");

                        // Update event details
                        eventDetail.setText("Description: " + curEvent.getDescription() + "\n"
                                + "Capacity of Event: (" + (curEvent.getChosenUserRefs().size() + curEvent.getFinalUserRefs().size()) + "/" + curEvent.getLimitChosenList() + ")\n"
                                + "Capacity of Waiting List: (" + curEvent.getWaitingUserRefs().size() + "/" + curEvent.getLimitWaitingList() + ")");

                        // Save user action to Firestore
                        Control.getInstance().saveEvent(curEvent);
                        }
                }
            } else if (joinbutton.getText().equals("Accept Invitation")) {
                curEvent.getChosenUserRefs().remove(Integer.valueOf(curUser.getUserID()));
                curEvent.getFinalUserRefs().add(curUser.getUserID());
                joinbutton.setVisibility(View.GONE);
                declinebutton.setVisibility(View.GONE);
                // Save user action to Firestore
                Control.getInstance().saveEvent(curEvent);
            } else {
                // User clicked to cancel event
                curEvent.getWaitingUserRefs().remove(Integer.valueOf(curUser.getUserID()));
                joinbutton.setText("Join Event");
                // Update event details
                eventDetail.setText("Description: " + curEvent.getDescription() + "\n"
                        + "Capacity of Event: (" + (curEvent.getChosenUserRefs().size() + curEvent.getFinalUserRefs().size()) + "/" + curEvent.getLimitChosenList() + ")\n"
                        + "Capacity of Waiting List: (" + curEvent.getWaitingUserRefs().size() + "/" + curEvent.getLimitWaitingList() + ")");
                // Save user action to Firestore
                Control.getInstance().saveEvent(curEvent);
            }
        });
        declinebutton.setOnClickListener(v -> {
            // User clicked to decline event
            curEvent.getChosenUserRefs().remove(Integer.valueOf(curUser.getUserID()));
            curEvent.getCancelledUserRefs().add(curUser.getUserID());
            joinbutton.setVisibility(View.GONE);
            declinebutton.setVisibility(View.GONE);
            // Save user action to Firestore
            Control.getInstance().saveEvent(curEvent);
        });

    }

    /**
     * Checks if a list contains a specific integer.
     * @param l  ArrayList of integers
     * @param u integer to check for
     * @return
     */
    private boolean inList(ArrayList<Integer> l, int u) {
        for (int user : l) {
            if (user == u) {
                return true;
            }
        }
        return false;
    }

    /**
     * Decodes a Base64 encoded string back to a Bitmap.
     *
     * @param encodedImage The Base64 encoded image content.
     * @return The decoded Bitmap.
     */
    private Bitmap decodeBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    /**
     * Decodes a Uri to a Bitmap.
     * @param uri The Uri from database of the image to decode
     * @return
     */
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Helper method to encode Bitmap to a String (Base64 encoding or any method you prefer)
     * @param bitmap bitmap that needs to be encoded into a String for storage into database
     * @return
     */
    private String encodeBitmap(Bitmap bitmap) {
        // Convert bitmap to a Base64 encoded string (as an example)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Extracts the date from the event description string
     * @param eventDesc eventDescription from database
     * @param dateType which date to be extracted 0 for registration start, 1 for registration end, 2 for event start, 3 for event end
     * @return the extracted date as a String
     */
    private String extractDate(String eventDesc, int dateType) {
        String regStart = "";
        String regEnd = "";
        String eventStart = "";
        String eventEnd = "";

        String[] lines = eventDesc.split("\n");
        for (String line : lines) {
            if (line.contains("Registration Period:")) {
                String regPeriod = line.replace("Registration Period: ", "").trim();
                String[] regDates = regPeriod.split(" to ");
                regStart = regDates[0];
                regEnd = regDates[1];
            }
            if (line.contains("Event Period: ")) {
                String regPeriod = line.replace("Registration Period:", "").trim();
                String[] eventDates = regPeriod.split(" to ");
                eventStart = eventDates[0];
                eventEnd = eventDates[1];
            }
        }

        switch(dateType) {
            case 0:
                return regStart;
            case 1:
                return regEnd;
            case 2:
                return eventStart;
            case 3:
                return eventEnd;
            default:
                return "";
        }

    }

    /**
     * Checks if the start date is before the end date
     * @param start date that should be first in the period
     * @param end date that should be second in the period
     * @return true if the start date is before the end date, false otherwise
     */
    protected boolean validPeriod(String start, String end) {
        LocalDate date1 = LocalDate.parse(start);
        LocalDate date2 = LocalDate.parse(end);
        return date1.isBefore(date2) || date1.isEqual(date2);
    }

}
