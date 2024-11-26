package com.example.eventlotterysystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * ManageEventActivity is responsible for displaying and managing event details.
 * It allows the user to view event information, edit event details, manage members,
 * view the QR code, show the event on a map, and delete the event.
 *
 * Problem: delete event has not been implemented yet.
 */
public class ManageEventActivity extends AppCompatActivity {

    private TextView eventTitle;
    private TextView eventDetail;
    private ImageView eventPoster;
    private Button buttonManage;
    private Button buttonEdit;
    private Button buttonQRCode;
    private Button buttonMap;
    private ImageView deleteButton;
    private ImageView returnButton;
    private Event curEvent;

    /**
     * Called when the activity is first created. Initializes the view elements, retrieves the
     * Event object based on the event ID passed via intent, and sets up the UI with event data.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_org_manage);  // Using the provided layout

        // Initialize views
        eventTitle = findViewById(R.id.name);
        eventDetail = findViewById(R.id.event_detail);
        eventPoster = findViewById(R.id.poster);
        buttonManage = findViewById(R.id.manage_member_button);
        buttonEdit = findViewById(R.id.event_edit_button);
        buttonQRCode = findViewById(R.id.qr_code_button);
        buttonMap = findViewById(R.id.show_map_button);
        deleteButton = findViewById(R.id.del_button);
        returnButton = findViewById(R.id.return_button);

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

        if (curEvent != null) {
            // Populate the UI with event data
            eventTitle.setText(curEvent.getName());
            eventDetail.setText("Description: " + curEvent.getDescription() + "\n"
                    + "Capacity of Event: (" + (curEvent.getChosenUserRefs().size() + curEvent.getFinalUserRefs().size()) + "/" + curEvent.getLimitChosenList() + ")\n"
                    + "Capacity of Waiting List: (" + curEvent.getWaitingUserRefs().size() + "/" + curEvent.getLimitWaitingList() + ")");
        }

        // Return button to go back
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventActivity.this, EventslistActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        // Manage members
        buttonManage.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventActivity.this, WaitingListManageActivity.class);
            intent.putExtra("eventId", curEvent.getEventID());  // Pass the eventId
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
        buttonEdit.setOnClickListener(v -> {
            EditEventDialogFragment dialog = new EditEventDialogFragment(curEvent);
            dialog.show(getSupportFragmentManager(), "EditEventDialogFragment");
        });

        buttonQRCode.setOnClickListener(v -> {
            if (curEvent.getHashCodeQR() == null){
                new AlertDialog.Builder(ManageEventActivity.this)
                        .setTitle("No QR available")
                        .setMessage("No QR code was detected. Do you want to generate a new one")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                curEvent.generateQR();
                                Control.getInstance().saveEvent(curEvent);
                                QRCodeDialogFragment dialog1 = QRCodeDialogFragment.newInstance(curEvent.getHashCodeQR());
                                dialog1.show(getSupportFragmentManager(), "QRCodeDialogFragment");
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
            else{
                QRCodeDialogFragment dialog = QRCodeDialogFragment.newInstance(curEvent.getHashCodeQR());
                dialog.show(getSupportFragmentManager(), "QRCodeDialogFragment");
            }
        });
        buttonMap.setOnClickListener(v -> {
//            curEvent.getLatitudeList().add(53.5461);
//            curEvent.getLongitudeList().add(-113.4937);
//            curEvent.getLatitudeList().add(51.0447);
//            curEvent.getLongitudeList().add(-114.0719);
            MapDialogFragment mapDialogFragment = new MapDialogFragment(curEvent);
            mapDialogFragment.show(getSupportFragmentManager(), "MapDialogFragment");
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ManageEventActivity.this);
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
                                new AlertDialog.Builder(ManageEventActivity.this)
                                    .setTitle("Delete Event")
                                    .setMessage("Are you sure you want to delete your event?")
                                    .setPositiveButton("Delete", (dialog1, which1) -> {
                                        User currentUser = Control.getCurrentUser();
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
//        deleteButton.setOnClickListener(v -> {
//            new AlertDialog.Builder(ManageEventActivity.this)
//                    .setTitle("Delete Event")
//                    .setMessage("Are you sure you want to delete your event?")
//                    .setPositiveButton("Delete", (dialog, which) -> {
//                        User currentUser = Control.getCurrentUser();
//                        // Delete related Notifications
//                        ArrayList<Notification> notificationsToDelete = new ArrayList<>();
//                        for (Notification notification : Control.getInstance().getNotificationList()) {
//                            if (notification.getEventRef() == curEvent.getEventID()) {
//                                Control.getInstance().deleteNotification(notification);
//                                notificationsToDelete.add(notification);
//                            }
//                        }
//                        Control.getInstance().getNotificationList().removeAll(notificationsToDelete);
//                        // Delete event from database and Control
//                        Control.getInstance().deleteEvent(curEvent);
//                        Control.getInstance().getEventList().remove(curEvent);
//                        finish();
//                    })
//                    .setNegativeButton("Cancel", null)
//                    .show();
//        });



    }

    public void onEventUpdated() {
        if (curEvent != null) {
            eventTitle.setText(curEvent.getName());
            eventDetail.setText("Description: " + curEvent.getDescription() + "\n"
                    + "Capacity of Event: (" + (curEvent.getChosenUserRefs().size() + curEvent.getFinalUserRefs().size()) + "/" + curEvent.getLimitChosenList() + ")\n"
                    + "Capacity of Waiting List: (" + curEvent.getWaitingUserRefs().size() + "/" + curEvent.getLimitWaitingList() + ")");
        }
        Control.getInstance().saveEvent(curEvent);
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
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    // Helper method to encode Bitmap to a String (Base64 encoding or any method you prefer)
    private String encodeBitmap(Bitmap bitmap) {
        // Convert bitmap to a Base64 encoded string (as an example)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
