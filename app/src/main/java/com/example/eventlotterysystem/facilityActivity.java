package com.example.eventlotterysystem;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import java.util.ArrayList;

/**
 * An Activity that displays and allows editing of the user's facility information.
 */
public class facilityActivity extends AppCompatActivity implements EditFacilityFragment.OnFacilityUpdatedListener {
    private TextView nameTextView;
    private TextView descriptionTextView;
    private User curUser;
    private Facility curFac;

    /**
     * Called when the activity is first created. Initializes the UI and sets up event listeners.
     * Image button not implemented in this version.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facility_profile);

        curUser = Control.getCurrentUser();

        // Initialize UI elements
        nameTextView = findViewById(R.id.name);
        descriptionTextView = findViewById(R.id.email); // Assuming 'email' is a typo and should be 'description'


        curFac = null;
        for (Facility facility : Control.getInstance().getFacilityList()) {
            if (facility.getCreatorRef()==curUser.getUserID()) {
                curFac = facility;
                break;
            }
        }
        // If the user has no facility, prompt to create one
        if (curFac == null) {
            openEditFacilityFragment();
        } else {
            updateFacilityUI();
        }

        // Set up edit button listener
        findViewById(R.id.edit_button).setOnClickListener(v -> openEditFacilityFragment());

        // Set up return button listener
        ImageButton returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(view -> finish());

        ImageButton deleteButton = findViewById(R.id.del_button);
        deleteButton.setOnClickListener(v -> {
            for (Facility facility : Control.getInstance().getFacilityList()) {
                // If the user's facility is found
                if (facility.getCreatorRef() == curUser.getUserID()) {
                    new AlertDialog.Builder(facilityActivity.this)
                            .setTitle("Delete Facility")
                            .setMessage("Are you sure you want to delete your facility?\n\nThis will delete all your managed events. " )
                            .setPositiveButton("Delete", (dialog, which) -> {
                                // Delete facility from database and Control
                                Control.getInstance().deleteFacility(facility);
                                Control.getInstance().getFacilityList().remove(facility);
                                // Find all events created by the user
                                ArrayList<Event> eventsToDelete = new ArrayList<>();
                                for (Event event : Control.getInstance().getEventList()) {
                                    if (event.getCreatorRef() == curUser.getUserID()) {
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
                    break;
                }
            }

        });
    }

    /**
     * Opens the EditFacilityFragment to allow the user to create or edit their facility information.
     */
    private void openEditFacilityFragment() {
        String facilityName = curFac != null ? curFac.getName() : "";
        String facilityDescription = curFac != null ? curFac.getDescription() : "";

        EditFacilityFragment editFacilityFragment = EditFacilityFragment.newInstance(
                facilityName,
                facilityDescription
        );
        editFacilityFragment.setOnFacilityUpdatedListener(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        editFacilityFragment.show(fragmentManager, "editFacilityFragment");
    }

    /**
     * Callback method invoked when the facility has been updated.
     *
     * @param name        The updated name of the facility.
     * @param description The updated description of the facility.
     */
    @Override
    public void onFacilityUpdated(String name, String description) {
        if (curFac == null) {
            Facility newFacility = new Facility(name, description, curUser.getUserID()); // Assuming curUser is the creator
            curFac = newFacility;
            Control.getInstance().getFacilityList().add(newFacility);
        } else {
            curFac.setName(name);
            curFac.setDescription(description);
        }

        updateFacilityUI();
    }

    /**
     * Updates the UI elements to reflect the latest facility information.
     */
    private void updateFacilityUI() {
        if (curFac != null) {
            nameTextView.setText(curFac.getName());
            descriptionTextView.setText(curFac.getDescription());
            Control.getInstance().saveFacility(curFac);
        }
    }

}