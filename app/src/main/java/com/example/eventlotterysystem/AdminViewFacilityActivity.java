package com.example.eventlotterysystem;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Activity that displays the details of a specific facility for the admin and allows the admin
 * to delete the facility along with all associated events and notifications.
 */
public class AdminViewFacilityActivity extends AppCompatActivity {

    /**
     * TextView to display the name of the facility.
     */
    private TextView nameTextView;

    /**
     * TextView to display the description of the facility.
     */
    private TextView descriptionTextView;

    /**
     * The facility object containing details about the current facility being viewed.
     */
    private Facility facility;

    /**
     * Button to delete the facility and all associated data.
     */
    private ImageView deleteButton;

    /**
     * Called when the activity is created. Initializes the UI elements and handles facility data display.
     *
     * @param savedInstanceState A Bundle object containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_facility);

        nameTextView = findViewById(R.id.name);
        descriptionTextView = findViewById(R.id.description);
        deleteButton = findViewById(R.id.del_button);

        // Get facility details from Intent
        facility = (Facility) getIntent().getSerializableExtra("facility");

        // Display facility details
        if (facility != null) {
            nameTextView.setText("Facility Name: " + facility.getName());
            descriptionTextView.setText("Facility Description: " + facility.getDescription());
        }

        // Set up back button listener
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> finish());

        // Set up delete button listener
        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(AdminViewFacilityActivity.this)
                    .setTitle("Delete Facility")
                    .setMessage("Are you sure you want to delete this facility?\n\nThis will delete all the events created by this user.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // Delete facility from database and Control
                        Facility realFacility = null;
                        for (Facility f : Control.getInstance().getFacilityList()) {
                            if (f.getCreatorRef() == facility.getCreatorRef()) {
                                realFacility = f;
                            }
                        }
                        Control.getInstance().getFacilityList().remove(realFacility);
                        Control.getInstance().deleteFacility(facility);

                        // Find all events created by the user
                        ArrayList<Event> eventsToDelete = new ArrayList<>();
                        for (Event event : Control.getInstance().getEventList()) {
                            if (event.getCreatorRef() == facility.getCreatorRef()) {
                                // Delete notifications related to this event
                                ArrayList<Notification> notificationsToDelete = new ArrayList<>();
                                for (Notification notification: Control.getInstance().getNotificationList()) {
                                    if (notification.getEventRef() == event.getEventID()) {
                                        Control.getInstance().deleteNotification(notification);
                                        notificationsToDelete.add(notification);
                                    }
                                }
                                Control.getInstance().getNotificationList().removeAll(notificationsToDelete);

                                // Delete event from database and Control
                                Control.getInstance().deleteEvent(event);
                                eventsToDelete.add(event);
                            }
                        }
                        Control.getInstance().getEventList().removeAll(eventsToDelete);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}
