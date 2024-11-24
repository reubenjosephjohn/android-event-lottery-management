package com.example.eventlotterysystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        // Manage members
        buttonManage.setOnClickListener(v -> {
//            Intent intent = new Intent(ManageEventActivity.this, WaitingListManageActivity.class);
//            intent.putExtra("eventId", curEvent.getEventID());  // Pass the eventId
//            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//            startActivity(intent);
        });
        buttonEdit.setOnClickListener(v -> {
            EditEventDialogFragment dialog = new EditEventDialogFragment(curEvent);
            dialog.show(getSupportFragmentManager(), "EditEventDialogFragment");
        });

        buttonQRCode.setOnClickListener(v -> {
            QRCodeDialogFragment dialog = QRCodeDialogFragment.newInstance(curEvent.getHashCodeQR());
            dialog.show(getSupportFragmentManager(), "QRCodeDialogFragment");
        });
        buttonMap.setOnClickListener(v -> {
            curEvent.getLatitudeList().add(53.5461);
            curEvent.getLongitudeList().add(-113.4937);
            curEvent.getLatitudeList().add(51.0447);
            curEvent.getLongitudeList().add(-114.0719);
            MapDialogFragment mapDialogFragment = new MapDialogFragment(curEvent);
            mapDialogFragment.show(getSupportFragmentManager(), "MapDialogFragment");
        });

        deleteButton.setOnClickListener(v -> {
//            new AlertDialog.Builder(ManageEventActivity.this)
//                    .setTitle("Delete Event")
//                    .setMessage("Are you sure you want to delete your event?")
//                    .setPositiveButton("Delete", (dialog, which) -> {
//                        User currentUser = Control.getCurrentUser();
//                        if (curEvent != null && currentUser != null) {
//                            FirestoreManager.getInstance().deleteEventFromDatabase(curEvent);
//                            currentUser.deleteEvent(Control.getInstance(), curEvent);
//                            finish();
//
//                        }
//                    })
//                    .setNegativeButton("Cancel", null)
//                    .show();
        });



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
}
