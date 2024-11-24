package com.example.eventlotterysystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

/**
 * Activity to manage the final list of an event.
 * This activity allows the user to view and manage the list of final participants for a specific event.
 * It also provides navigation to other list management activities and the ability to send notifications.
 */
public class FinalListManageActivity extends AppCompatActivity implements NotifyFragment.NotificationListener{
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
        setContentView(R.layout.finallist_manage);

        int eventId = getIntent().getIntExtra("eventId", -1);
        event = Control.getInstance().findEventByID(eventId);

        if (event == null) {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ListView memberList = findViewById(R.id.member_list);
        ArrayList<User> finalList = new ArrayList<>();
        for (int userID: event.getFinalUserRefs()) {
            User user = Control.getInstance().findUserByID(userID);
            if (user != null) {
                finalList.add(user);
            }
        }
        adapter = new UserAdapter(this, finalList);
        memberList.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bot_nav_bar);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                int itemId = item.getItemId();
                if (itemId == R.id.nav_waiting) {
                    intent = new Intent(FinalListManageActivity.this, WaitingListManageActivity.class);
                    intent.putExtra("eventId", event.getEventID());
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_selected) {
                    intent = new Intent(FinalListManageActivity.this, ChosenListManageActivity.class);
                    intent.putExtra("eventId", event.getEventID());
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_cancelled) {
                    intent = new Intent(FinalListManageActivity.this, CancelledListManageActivity.class);
                    intent.putExtra("eventId", event.getEventID());
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_final) {
                    // Already in FinalListManageActivity
                    return true;
                }
                return false;
            }
        });

        memberList.setOnItemClickListener((parent, view, position, id) -> {
            User user = (User) parent.getItemAtPosition(position);
            Intent intent = new Intent(this, ViewOtherUserProfileActivity.class);
            intent.putExtra("userID", user.getUserID());
            startActivity(intent);
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_final);

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

    @Override
    protected void onResume() {
        super.onResume();
        ListView memberList = findViewById(R.id.member_list);
        ArrayList<User> finalList = new ArrayList<>();
        for (int userID: event.getFinalUserRefs()) {
            User user = Control.getInstance().findUserByID(userID);
            if (user != null) {
                finalList.add(user);
            }
        }
        adapter = new UserAdapter(this, finalList);
        memberList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bot_nav_bar);
        bottomNavigationView.setSelectedItemId(R.id.nav_final);
    }

    /**
     * Called when a notification is sent from the NotifyFragment.
     * Adds the notification to the notification list of all users in the waiting list who have notifications enabled.
     *
     * @param message The notification message to be sent.
     */
    @Override
    public void onNotify(String message) {
        for (int userID: event.getFinalUserRefs()) {
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
}