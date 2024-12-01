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
 */
public class Landing_page extends AppCompatActivity {

    private static final int DOUBLE_BACK_TIME = 2000; // Time in milliseconds
    private long lastBackPressedTime = 0;
    private Handler handler = new Handler();

    /**
     * Time threshold for double back press to exit the app, in milliseconds.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

        // request notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Control.setCurrentUser(Control.getInstance().getUserList().get(1));
        if (Control.getCurrentUser() == null){
            checkDevice(Control.getInstance());
        }

        for (Notification noti: Control.getInstance().getNotificationList()) {
            if (noti.getUserRef() == Control.getInstance().getCurrentUser().getUserID()) {
                noti.setDeclined(false);
                Control.getInstance().updateNotification(noti);
            }
        }

        if (Control.notificationToken.isEmpty()) {
            // Get and save user's Firebase Messaging Token
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w("Firebase Messaging Service", "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            String token = task.getResult();
                            // Log and toast
                            String msg = "Your Messaging Token is " + token;
                            Log.i("Messaging Token", token);
                            Log.d("Firebase Messaging Service", msg);
//                            Toast.makeText(Landing_page.this, msg, Toast.LENGTH_SHORT).show();
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


        // Set up the OnBackPressedCallback
        OnBackPressedDispatcher dispatcher = this.getOnBackPressedDispatcher();
        dispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (lastBackPressedTime + DOUBLE_BACK_TIME > System.currentTimeMillis()) {
                    finishAffinity(); // Exit the app
                } else {
                    Toast.makeText(Landing_page.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
                    lastBackPressedTime = System.currentTimeMillis();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {setEnabled(true);}
                    }, DOUBLE_BACK_TIME);
                }
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landing_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set OnClickListener on eventsIcon
        ImageView eventsIcon = findViewById(R.id.eventsIcon);
        eventsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Landing_page.this, EventslistActivity.class);
                startActivity(intent);
            }
        });

        ImageView SettingIcon = findViewById(R.id.settingsIcon);
        SettingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Landing_page.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        ImageView profileIcon = findViewById(R.id.profileIcon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Landing_page.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        profileIcon.setOnLongClickListener(v -> {
            if (Control.getCurrentUser() != null && Control.getCurrentUser().isAdmin()) {
                Intent intent = new Intent(Landing_page.this, UsersListActivity.class);
                startActivity(intent);
                return true;
            } else {
                Toast.makeText(Landing_page.this, "Only admins can view all users", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        ImageView notificationIcon = findViewById(R.id.notificationsIcon);
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Landing_page.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        ImageView facilityIcon = findViewById(R.id.facilitiesIcon);
        facilityIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Landing_page.this, facilityActivity.class);
                startActivity(intent);
            }
        });
        facilityIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (Control.getCurrentUser() != null && Control.getCurrentUser().isAdmin()) {
                    Intent intent = new Intent(Landing_page.this, FacilitiesListActivity.class);
                    startActivity(intent);
                    return true;
                } else {
                    Toast.makeText(Landing_page.this, "Only admins can view all facilities", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });

        ImageView qrIcon = findViewById(R.id.scanQRIcon);
        qrIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Landing_page.this, ScanQRActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null); // Clean up any pending callbacks
        super.onDestroy();
    }

    protected void checkDevice(Control control){
//        for (User user : Control.getInstance().getUserList()) {
//            if (user.getFID().equals(Control.getLocalFID())) {
//                Control.setCurrentUser(user);
//                return;
//            }
//        }
        if (Control.getCurrentUser() == null){
            User me = new User(Control.getInstance().getCurrentUserIDForUserCreation(), Control.getLocalFID());
            Control.getInstance().getUserList().add(me);
//            Control.setCurrentUser(me);
            Control.getInstance().saveUser(me);
        }
    }
}