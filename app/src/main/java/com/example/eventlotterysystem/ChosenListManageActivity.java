package com.example.eventlotterysystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Activity to manage the chosen list of an event.
 * This activity allows the user to view and manage the list of chosen participants for a specific event.
 * It also provides navigation to other list management activities and the ability to send notifications.
 */
public class ChosenListManageActivity extends AppCompatActivity implements NotifyFragment.NotificationListener{
    private Event event;
    private UserAdapter adapter;

    /**
     * Called when the activity is first created.
     * Initializes the activity, sets up the UI components, and handles navigation and button clicks.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chosenlist_manage);

        int eventId = getIntent().getIntExtra("eventId", -1);
        event = Control.getInstance().findEventByID(eventId);

        if (event == null) {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ListView memberList = findViewById(R.id.member_list);
        ArrayList<User> chosenList = new ArrayList<>();
        for (int userID: event.getChosenUserRefs()) {
            User user = Control.getInstance().findUserByID(userID);
            if (user != null) {
                chosenList.add(user);
            }
        }
        adapter = new UserAdapter(this, chosenList);
        memberList.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bot_nav_bar);
        bottomNavigationView.setSelectedItemId(R.id.nav_selected);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Menu menu = bottomNavigationView.getMenu();
                for (int i = 0; i < menu.size(); i++) {
                    menu.getItem(i).setChecked(false);
                }
                Intent intent;
                int itemId = item.getItemId();
                event = Control.getInstance().findEventByID(event.getEventID());
        
                if (itemId == R.id.nav_waiting) {
                    intent = new Intent(ChosenListManageActivity.this, WaitingListManageActivity.class);
                    intent.putExtra("eventId", event.getEventID());
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    return true;
                } else if (itemId == R.id.nav_selected) {
                    refreshChosenList();
                    return true;
                } else if (itemId == R.id.nav_final) {
                    intent = new Intent(ChosenListManageActivity.this, FinalListManageActivity.class);
                    intent.putExtra("eventId", event.getEventID());
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    return true;
                } else if (itemId == R.id.nav_cancelled) {
                    intent = new Intent(ChosenListManageActivity.this, CancelledListManageActivity.class);
                    intent.putExtra("eventId", event.getEventID());
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    return true;
                }
                return false;
            }
        });

        memberList.setOnItemLongClickListener((parent, view, position, id) -> {
            User user = (User) parent.getItemAtPosition(position);
            showDeleteConfirmationDialog(user);
            return true;
        });

        memberList.setOnItemClickListener((parent, view, position, id) -> {
            User user = (User) parent.getItemAtPosition(position);
            Intent intent = new Intent(this, ViewOtherUserProfileActivity.class);
            intent.putExtra("userID", user.getUserID());
            startActivity(intent);
        });


        findViewById(R.id.roll_button).setOnClickListener(v -> {
            User organizer = Control.getCurrentUser();
            if (organizer != null) {
                int remainingSpots = event.getLimitChosenList() - event.getChosenUserRefs().size() - event.getFinalUserRefs().size();
                if (remainingSpots > 0) {
                    // Reroll Operation
                    if (remainingSpots >= event.getWaitingUserRefs().size()) {
                        event.getChosenUserRefs().addAll(event.getWaitingUserRefs());
                        for (int userID: event.getWaitingUserRefs()) {
                            String automaticMessage = "[Auto] Congratulations! You have been chosen to attend " + event.getName() + "! Click 'Accept' below to accept the invitation!";
                            Notification notification = new Notification(event.getEventID(), userID, true, automaticMessage);
                            notification.setDeclined(true);
                            Control.getInstance().getNotificationList().add(notification);
                            Control.getInstance().addNotification(notification);
                        }
                        event.getWaitingUserRefs().clear();
                    } else { // not enough spots for every one
                        ArrayList<Integer> waitingListCopy = new ArrayList<>(event.getWaitingUserRefs());
                        Collections.shuffle(waitingListCopy);
                        for (int i = 0; i < remainingSpots; i++) {
                            String automaticMessage = "[Auto] Congratulations! You have been chosen to attend " + event.getName() + "! Click 'Accept' below to accept the invitation!";
                            Notification notification = new Notification(event.getEventID(), waitingListCopy.get(i), true, automaticMessage);
                            notification.setDeclined(true);
                            Control.getInstance().getNotificationList().add(notification);
                            Control.getInstance().addNotification(notification);
                        }
                        for (int i = 0; i < remainingSpots; i++) {
                            event.getChosenUserRefs().add(waitingListCopy.get(i));
                            event.getWaitingUserRefs().remove(waitingListCopy.get(i));
                        }

                    }
                    adapter.notifyDataSetChanged();
                    // Save user action
                    Control.getInstance().saveEvent(event);
                    refreshChosenList();
                    Toast.makeText(this, "Replacement applicant(s) drawn", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No entrants added as Selected list is full", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Current user is not set", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> navigateBackToViewEvent());

        Button sentNotiButton = findViewById(R.id.notify_button);
        sentNotiButton.setOnClickListener(v -> {
            // Show the notification dialog
            NotifyFragment dialog = new NotifyFragment();
            dialog.show(getSupportFragmentManager(), "NotificationDialogFragment");
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateBackToViewEvent();
            }
        });
    }

    /**
     * Called when the activity is resumed. This method sets the selected item in the bottom navigation view
     * and refreshes the list of chosen users.
     */
    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bot_nav_bar);
        bottomNavigationView.setSelectedItemId(R.id.nav_selected);
        refreshChosenList();
    }
    /**
     * Called when the activity is paused. This method overrides the default transition animation.
     */
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    /**
     * Refreshes the list of chosen users by fetching the chosen user references from the event
     * and updating the adapter with the new list.
     */
    private void refreshChosenList() {
        ListView memberList = findViewById(R.id.member_list);
        ArrayList<User> chosenList = new ArrayList<>();
        for (int userID: event.getChosenUserRefs()) {
            User user = Control.getInstance().findUserByID(userID);
            if (user != null) {
                chosenList.add(user);
            }
        }
        adapter = new UserAdapter(this, chosenList);
        memberList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * Navigates back to the ViewEventActivity.
     * This method is called when the return button is clicked or the back button is pressed.
     */
    private void navigateBackToViewEvent() {
        Intent intent = new Intent(this, ManageEventActivity.class);
        intent.putExtra("eventID", event.getEventID());
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    /**
     * Called when a notification is sent from the NotifyFragment.
     * Adds the notification to the notification list of all users in the waiting list who have notifications enabled.
     *
     * @param message The notification message to be sent.
     */
    @Override
    public void onNotify(String message) {
        for (int userID: event.getChosenUserRefs()) {
            User user = Control.getInstance().findUserByID(userID);
            // Skip Users with notifications disabled
            if (!user.getNotificationSetting()) {
                continue;
            }
            Notification noti = new Notification(event.getEventID(), userID, false, message);
            Control.getInstance().getNotificationList().add(noti);
            Control.getInstance().addNotification(noti);
        }
    }

    /**
     * Shows a confirmation dialog to delete a user from the chosen list.
     * If confirmed, the user is moved to the cancelled list.
     *
     * @param user The user to be deleted from the chosen list.
     */
    private void showDeleteConfirmationDialog(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Entrant")
                .setMessage("Are you sure you want to delete " + user.getName() + " from the Selected list?")
                .setPositiveButton(R.string.dialog_yes, (dialog, which) -> {
                    // Delete the notification
                    for (Notification notification: Control.getInstance().getNotificationList()) {
                        if (notification.getUserRef() == user.getUserID() && event.getEventID() == notification.getEventRef() && notification.getNeedAccept()) {
                            Control.getInstance().deleteNotification(notification);
                            break;
                        }
                    }

                    event.getChosenUserRefs().remove(Integer.valueOf(user.getUserID()));
                    event.getCancelledUserRefs().add(user.getUserID());
                    adapter.notifyDataSetChanged();
                    Control.getInstance().saveEvent(event);
                    refreshChosenList();
                    Toast.makeText(this, "Entrant moved to cancelled list", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }
}