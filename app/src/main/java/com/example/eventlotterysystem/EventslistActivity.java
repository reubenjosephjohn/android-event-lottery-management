package com.example.eventlotterysystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * EventslistActivity displays a categorized list of events, including organized, waiting, canceled,
 * chosen, final, and other events. Users can create events and view detailed information about each event.
 */
public class EventslistActivity extends AppCompatActivity {

    /** Layout for displaying events */
    private LinearLayout orglist;
    private LinearLayout waitlist;
    private LinearLayout cancellist;
    private LinearLayout chosenlist;
    private LinearLayout finallist;
    private LinearLayout otherlist;
    private TextView ownedEvents;
    private TextView pendingEvents;
    private TextView selectedEvents;
    private TextView approvedEvents;
    private TextView rejectedEvents;
    private TextView otherEvents;
    private LinearLayout ownedEventsTitle;
    private LinearLayout pendingEventsTitle;
    private LinearLayout selectedEventsTitle;
    private LinearLayout approvedEventsTitle;
    private LinearLayout rejectedEventsTitle;
    private LinearLayout otherEventsTitle;

    /** Currently logged-in user */
    private User curUser;
    private Facility curFac;

    /**
     * Called when the activity is first created. Initializes the view elements,
     * populates the categorized event lists, and sets up event creation functionality.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_lists_screen);

        curUser = Control.getCurrentUser();

        ImageButton returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventslistActivity.this, Landing_page.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        orglist = findViewById(R.id.org);
        waitlist = findViewById(R.id.wait);
        chosenlist = findViewById(R.id.chosen);
        finallist = findViewById(R.id.finall);
        cancellist = findViewById(R.id.cancel);
        otherlist = findViewById(R.id.other);
        ownedEvents = findViewById(R.id.ownedEvents);
        pendingEvents = findViewById(R.id.pendingEvents);
        selectedEvents = findViewById(R.id.selectedEvents);
        approvedEvents = findViewById(R.id.approvedEvents);
        rejectedEvents = findViewById(R.id.rejectedEvents);
        otherEvents = findViewById(R.id.otherEvents);
        ownedEventsTitle = findViewById(R.id.ownedEventsTitle);
        pendingEventsTitle = findViewById(R.id.pendingEventsTitle);
        selectedEventsTitle = findViewById(R.id.selectedEventsTitle);
        approvedEventsTitle = findViewById(R.id.approvedEventsTitle);
        rejectedEventsTitle = findViewById(R.id.rejectedEventsTitle);
        otherEventsTitle = findViewById(R.id.otherEventsTitle);


        // Set all lists and titles to GONE initially
        ownedEvents.setVisibility(View.GONE);
        orglist.setVisibility(View.GONE);
        ownedEventsTitle.setVisibility(View.GONE);
        pendingEvents.setVisibility(View.GONE);
        waitlist.setVisibility(View.GONE);
        pendingEventsTitle.setVisibility(View.GONE);
        selectedEvents.setVisibility(View.GONE);
        chosenlist.setVisibility(View.GONE);
        selectedEventsTitle.setVisibility(View.GONE);
        approvedEvents.setVisibility(View.GONE);
        finallist.setVisibility(View.GONE);
        approvedEventsTitle.setVisibility(View.GONE);
        rejectedEvents.setVisibility(View.GONE);
        cancellist.setVisibility(View.GONE);
        rejectedEventsTitle.setVisibility(View.GONE);
        otherEvents.setVisibility(View.GONE);
        otherlist.setVisibility(View.GONE);
        otherEventsTitle.setVisibility(View.GONE);

        // Set click listeners
        ownedEvents.setOnClickListener(v -> toggleSection(orglist, findViewById(R.id.ownedExpandIcon)));
        pendingEvents.setOnClickListener(v -> toggleSection(waitlist, findViewById(R.id.pendingExpandIcon)));
        selectedEvents.setOnClickListener(v -> toggleSection(chosenlist, findViewById(R.id.selectedExpandIcon)));
        approvedEvents.setOnClickListener(v -> toggleSection(finallist, findViewById(R.id.approvedExpandIcon)));
        rejectedEvents.setOnClickListener(v -> toggleSection(cancellist, findViewById(R.id.rejectedExpandIcon)));
        otherEvents.setOnClickListener(v -> toggleSection(otherlist, findViewById(R.id.otherExpandIcon)));

// Repeat for additional lists

        // Repopulate each section with the updated event list
        for (Event event : Control.getInstance().getEventList()) {
            if (event.getCreatorRef() == curUser.getUserID()) {
                addEventToSection(event, orglist);
                ownedEvents.setVisibility(View.VISIBLE);
                orglist.setVisibility(View.VISIBLE);
                ownedEventsTitle.setVisibility(View.VISIBLE);
            } else if (inList(event.getWaitingUserRefs(), curUser.getUserID())) {
                addEventToSection(event, waitlist);
                pendingEvents.setVisibility(View.VISIBLE);
                waitlist.setVisibility(View.VISIBLE);
                pendingEventsTitle.setVisibility(View.VISIBLE);
            } else if (inList(event.getCancelledUserRefs(), curUser.getUserID())) {
                addEventToSection(event, cancellist);
                rejectedEvents.setVisibility(View.VISIBLE);
                cancellist.setVisibility(View.VISIBLE);
                rejectedEventsTitle.setVisibility(View.VISIBLE);
            } else if (inList(event.getChosenUserRefs(), curUser.getUserID())) {
                addEventToSection(event, chosenlist);
                selectedEvents.setVisibility(View.VISIBLE);
                chosenlist.setVisibility(View.VISIBLE);
                selectedEventsTitle.setVisibility(View.VISIBLE);
            } else if (inList(event.getFinalUserRefs(), curUser.getUserID())) {
                addEventToSection(event, finallist);
                approvedEvents.setVisibility(View.VISIBLE);
                finallist.setVisibility(View.VISIBLE);
                approvedEventsTitle.setVisibility(View.VISIBLE);
            } else {
                addEventToSection(event, otherlist);
                otherEvents.setVisibility(View.VISIBLE);
                otherlist.setVisibility(View.VISIBLE);
                otherEventsTitle.setVisibility(View.VISIBLE);
            }
        }


        curFac = null;
        for (Facility facility : Control.getInstance().getFacilityList()) {
            if (facility.getCreatorRef()==curUser.getUserID()) {
                curFac = facility;
                break;
            }
        }
        Button createEventButton = findViewById(R.id.create_button);
        createEventButton.setOnClickListener(v -> {
            if (curFac == null) {
                // Display an AlertDialog to inform the user they need a facility
                new AlertDialog.Builder(this)
                        .setTitle("Facility Required")
                        .setMessage("You need to have a facility first.")
                        .setPositiveButton("Confirm", (dialog, which) -> {})
                        .show();
            } else {
                // Show event creation dialog if user has a facility
                CreateEventDialogFragment dialog = new CreateEventDialogFragment();
                dialog.setCreateEventListener(newEvent -> {
                    Control.getInstance().getEventList().add(newEvent);
                    addEventToSection(newEvent, orglist);
                    ownedEvents.setVisibility(View.VISIBLE);
                    orglist.setVisibility(View.VISIBLE);
                    ownedEventsTitle.setVisibility(View.VISIBLE);
                    Control.getInstance().saveEvent(newEvent);
                });
                dialog.show(getSupportFragmentManager(), "CreateEventDialogFragment");
            }
        });
    }

    /**
     * Checks if the specified user is in the given list.
     *
     * @param l The list of users to check
     * @param u The user to find
     * @return True if the user is in the list, false otherwise
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
     * Adds an event to the specified section layout and sets up the click listener
     * for viewing the event details in ViewEventActivity.
     *
     * @param event   The event to display
     * @param section The section (organized, waiting, canceled, etc.) to add the event to
     */
    private void addEventToSection(Event event, LinearLayout section) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View eventView = inflater.inflate(R.layout.event_content, section, false);

        TextView nameTextView = eventView.findViewById(R.id.name);
        TextView statusTextView = eventView.findViewById(R.id.user_status);
        nameTextView.setText(event.getName());
        statusTextView.setText(event.getDescription());

        // Set click listener to open the appropriate activity with event details
        eventView.setOnClickListener(v -> {
            boolean manage = false;
            if (event.getCreatorRef() == curUser.getUserID()) {
                manage = true;
            }
            Intent intent = manage
                    ? new Intent(this, ManageEventActivity.class)
                    : new Intent(this, ViewEventActivity.class);
            intent.putExtra("eventID", event.getEventID());
            startActivity(intent);
        });

        // Set up edit and delete buttons (initially invisible)
        ImageButton editButton = eventView.findViewById(R.id.button1);
        ImageButton deleteButton = eventView.findViewById(R.id.button2);
        editButton.setVisibility(View.INVISIBLE);
        deleteButton.setVisibility(View.INVISIBLE);

        section.addView(eventView);
    }
    @Override
    protected void onResume() {
        super.onResume();
        orglist.removeAllViews();
        waitlist.removeAllViews();
        cancellist.removeAllViews();
        chosenlist.removeAllViews();
        finallist.removeAllViews();
        otherlist.removeAllViews();
        ownedEvents.setVisibility(View.GONE);
        orglist.setVisibility(View.GONE);
        ownedEventsTitle.setVisibility(View.GONE);
        pendingEvents.setVisibility(View.GONE);
        waitlist.setVisibility(View.GONE);
        pendingEventsTitle.setVisibility(View.GONE);
        selectedEvents.setVisibility(View.GONE);
        chosenlist.setVisibility(View.GONE);
        selectedEventsTitle.setVisibility(View.GONE);
        approvedEvents.setVisibility(View.GONE);
        finallist.setVisibility(View.GONE);
        approvedEventsTitle.setVisibility(View.GONE);
        rejectedEvents.setVisibility(View.GONE);
        cancellist.setVisibility(View.GONE);
        rejectedEventsTitle.setVisibility(View.GONE);
        otherEvents.setVisibility(View.GONE);
        otherlist.setVisibility(View.GONE);
        otherEventsTitle.setVisibility(View.GONE);

        // Repopulate each section with the updated event list
        for (Event event : Control.getInstance().getEventList()) {
            if (event.getCreatorRef() == curUser.getUserID()) {
                addEventToSection(event, orglist);
                ownedEvents.setVisibility(View.VISIBLE);
                orglist.setVisibility(View.VISIBLE);
                ownedEventsTitle.setVisibility(View.VISIBLE);
            } else if (inList(event.getWaitingUserRefs(), curUser.getUserID())) {
                addEventToSection(event, waitlist);
                pendingEvents.setVisibility(View.VISIBLE);
                waitlist.setVisibility(View.VISIBLE);
                pendingEventsTitle.setVisibility(View.VISIBLE);
            } else if (inList(event.getCancelledUserRefs(), curUser.getUserID())) {
                addEventToSection(event, cancellist);
                rejectedEvents.setVisibility(View.VISIBLE);
                cancellist.setVisibility(View.VISIBLE);
                rejectedEventsTitle.setVisibility(View.VISIBLE);
            } else if (inList(event.getChosenUserRefs(), curUser.getUserID())) {
                addEventToSection(event, chosenlist);
                selectedEvents.setVisibility(View.VISIBLE);
                chosenlist.setVisibility(View.VISIBLE);
                selectedEventsTitle.setVisibility(View.VISIBLE);
            } else if (inList(event.getFinalUserRefs(), curUser.getUserID())) {
                addEventToSection(event, finallist);
                approvedEvents.setVisibility(View.VISIBLE);
                finallist.setVisibility(View.VISIBLE);
                approvedEventsTitle.setVisibility(View.VISIBLE);
            } else {
                addEventToSection(event, otherlist);
                otherEvents.setVisibility(View.VISIBLE);
                otherlist.setVisibility(View.VISIBLE);
                otherEventsTitle.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Toggles the visibility of a section layout and updates the corresponding indicator.
     * @param layout
     * @param indicator
     */
    private void toggleSection(LinearLayout layout, ImageView indicator) {
        if (layout.getVisibility() == View.GONE) {
            layout.setVisibility(View.VISIBLE);
            indicator.setImageResource(R.drawable.arrow_drop_down_24px);
        } else {
            layout.setVisibility(View.GONE);
            indicator.setImageResource(R.drawable.arrow_right_24px);
        }
    }
}
