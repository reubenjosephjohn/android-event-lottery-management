package com.example.eventlotterysystem;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Represents the landing page of the event lottery system.
 *
 * <p>This activity serves as the main entry point after login, providing navigation
 * to various sections such as events, settings, profiles, notifications, facilities,
 * and a QR code scanner.</p>
 *
 * @author Dingjingmu (Steven) Yang
 * @version 1.0
 * @since 2024-11-29
 */
public class Landing_page extends AppCompatActivity {

    /**
     * Time threshold for double back press to exit the app, in milliseconds.
     */
    private static final int DOUBLE_BACK_TIME = 2000;

    /**
     * Stores the timestamp of the last back press.
     */
    private long lastBackPressedTime = 0;

    /**
     * Handler to manage delayed tasks, such as resetting back press flags.
     */
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

        // Request notification permission for Android TIRAMISU and later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Check or initialize the current user
        if (Control.getCurrentUser() == null) {
            checkDevice(Control.getInstance());
        }

        // Update notifications for the current user
        for (Notification noti : Control.getInstance().getNotificationList()) {
            if (noti.getUserRef() == Control.getInstance().getCurrentUser().getUserID()) {
                noti.setDeclined(false);
                Control.getInstance().updateNotification(noti);
            }
        }

        // Get and save Firebase Messaging Token if not already retrieved
        if (Control.notificationToken.isEmpty()) {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w("Firebase Messaging Service", "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            String token = task.getResult();
                            Log.i("Messaging Token", token);
                            for (User user : Control.getInstance().getUserList()) {
                                if (user.getFID().equals(Control.getLocalFID())) {
                                    user.setNotificationToken(token);
                                    Control.getInstance().saveUser(user);
                                    break;
                                }
                            }
                        }
                    });
        }

        // Configure back press handling
        OnBackPressedDispatcher dispatcher = this.getOnBackPressedDispatcher();
        dispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (lastBackPressedTime + DOUBLE_BACK_TIME > System.currentTimeMillis()) {
                    finishAffinity();
                } else {
                    Toast.makeText(Landing_page.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                    lastBackPressedTime = System.currentTimeMillis();
                    handler.postDelayed(() -> setEnabled(true), DOUBLE_BACK_TIME);
                }
            }
        });

        // Adjust layout padding to account for system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landing_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up click listeners for various icons
        setupIconClickListeners();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null); // Clean up any pending callbacks
        super.onDestroy();
    }

    /**
     * Checks the current device for a user and initializes one if necessary.
     *
     * @param control the control instance managing the application state
     */
    protected void checkDevice(Control control) {
        if (Control.getCurrentUser() == null) {
            User me = new User(Control.getInstance().getCurrentUserIDForUserCreation(), Control.getLocalFID());
            Control.getInstance().getUserList().add(me);
            Control.getInstance().saveUser(me);
        }
    }

    /**
     * Sets up click and long-click listeners for various icons on the landing page.
     */
    private void setupIconClickListeners() {
        ImageView eventsIcon = findViewById(R.id.eventsIcon);
        eventsIcon.setOnClickListener(v -> startActivity(new Intent(Landing_page.this, EventslistActivity.class)));

        ImageView settingsIcon = findViewById(R.id.settingsIcon);
        settingsIcon.setOnClickListener(v -> startActivity(new Intent(Landing_page.this, SettingActivity.class)));

        ImageView profileIcon = findViewById(R.id.profileIcon);
        profileIcon.setOnClickListener(v -> startActivity(new Intent(Landing_page.this, ProfileActivity.class)));
        profileIcon.setOnLongClickListener(v -> {
            if (Control.getCurrentUser() != null && Control.getCurrentUser().isAdmin()) {
                startActivity(new Intent(Landing_page.this, UsersListActivity.class));
                return true;
            } else {
                Toast.makeText(Landing_page.this, "Only admins can view all users", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        ImageView notificationIcon = findViewById(R.id.notificationsIcon);
        notificationIcon.setOnClickListener(v -> startActivity(new Intent(Landing_page.this, NotificationActivity.class)));

        ImageView facilityIcon = findViewById(R.id.facilitiesIcon);
        facilityIcon.setOnClickListener(v -> startActivity(new Intent(Landing_page.this, facilityActivity.class)));
        facilityIcon.setOnLongClickListener(v -> {
            if (Control.getCurrentUser() != null && Control.getCurrentUser().isAdmin()) {
                startActivity(new Intent(Landing_page.this, FacilitiesListActivity.class));
                return true;
            } else {
                Toast.makeText(Landing_page.this, "Only admins can view all facilities", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        ImageView qrIcon = findViewById(R.id.scanQRIcon);
        qrIcon.setOnClickListener(v -> startActivity(new Intent(Landing_page.this, ScanQRActivity.class)));
    }
}
