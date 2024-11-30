package com.example.eventlotterysystem;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * An Activity that displays a list of notifications for the current user.
 */
public class NotificationActivity extends AppCompatActivity {
    private User curUser;
    private LinearLayout list;

    /**
     * Called when the activity is first created. Initializes the UI and populates notifications.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied.
     */
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_screen);

        curUser = Control.getCurrentUser();

        // Set up return button listener
        ImageButton returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(view -> finish());

        list = findViewById(R.id.notificationList);

        // Populate notifications
        for (Notification noti: Control.getInstance().getNotificationList()) {
            if (noti.getUserRef() == curUser.getUserID()) {
                addNotiToSection(noti, list);
            }
        }
    }

    /**
     * Adds a single notification view to the specified section.
     *
     * @param noti    The notification to display.
     * @param section The LinearLayout section where the notification view will be added.
     */
    private void addNotiToSection(Notification noti, LinearLayout section) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View NotifiView = inflater.inflate(R.layout.notification_content, section, false);

        TextView titleTextView = NotifiView.findViewById(R.id.notification_title);
        TextView messageTextView = NotifiView.findViewById(R.id.notification_message);
        Event relatedEvent = Control.getInstance().findEventByID(noti.getEventRef());
        String message = noti.getCustomMessage();
        titleTextView.setText(relatedEvent.getName());
        messageTextView.setText(message);

        Button AcceptButton = NotifiView.findViewById(R.id.btnAccept);
        Button DeclineButton = NotifiView.findViewById(R.id.btnDecline);
        Button DeleteButton = NotifiView.findViewById(R.id.btnRemove);

        // if no need to accept/decline, disable buttons
        if (!noti.getNeedAccept()){
            AcceptButton.setVisibility(View.GONE);
            DeclineButton.setVisibility(View.GONE);
        }

        if (noti.getAccepted()) {
            AcceptButton.setEnabled(false);
            DeclineButton.setEnabled(false);
        }

        // handle accept button
        AcceptButton.setOnClickListener(v -> {
            AcceptButton.setEnabled(false);
            DeclineButton.setEnabled(false);
            relatedEvent.getFinalUserRefs().add(curUser.getUserID());
            relatedEvent.getChosenUserRefs().remove(Integer.valueOf(curUser.getUserID()));
            Control.getInstance().saveEvent(relatedEvent);
            noti.setAccepted(true);
            Control.getInstance().updateNotification(noti);
        });

        // handle decline button
        DeclineButton.setOnClickListener(v -> {
            AcceptButton.setEnabled(false);
            DeclineButton.setEnabled(false);
            relatedEvent.getCancelledUserRefs().add(curUser.getUserID());
            relatedEvent.getChosenUserRefs().remove(Integer.valueOf(curUser.getUserID()));
            Control.getInstance().saveEvent(relatedEvent);
            noti.setAccepted(true);
            Control.getInstance().updateNotification(noti);
        });

        // handle delete button
        DeleteButton.setOnClickListener(v -> {
            section.removeView(NotifiView);
            Control.getInstance().deleteNotification(noti);
            Control.getInstance().getNotificationList().remove(noti);
        });

        section.addView(NotifiView);
    }
    private boolean inList(ArrayList<User> l, User u) {
        for (User user : l) {
            if (user.getUserID() == u.getUserID()) {
                return true;
            }
        }
        return false;
    }
}