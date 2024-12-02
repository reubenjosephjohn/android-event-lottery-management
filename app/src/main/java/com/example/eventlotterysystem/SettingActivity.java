package com.example.eventlotterysystem;

import android.os.Bundle;
import android.widget.ImageButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import androidx.appcompat.app.AppCompatActivity;

/**
 * SettingActivity manages user settings, allowing users to enable or disable notifications.
 */
public class SettingActivity extends AppCompatActivity {

    /** Switch to toggle notifications on or off */
    private MaterialSwitch notiSwitch;

    /** Currently logged-in user */
    private User curUser;

    /**
     * Called when the activity is first created. Initializes the view elements,
     * sets up the notification toggle, and saves the user's preference.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_screen);

        curUser = Control.getCurrentUser();

        // Initialize notification switch and set its initial state based on user preference
        notiSwitch = findViewById(R.id.noti_switch);
        notiSwitch.setChecked(curUser.getNotificationSetting());

        // Listener to handle changes in notification setting
        notiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            curUser.setNotificationSetting(isChecked);
            Control.getInstance().saveUser(curUser);
        });

        // Return button to go back to the previous screen
        ImageButton returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(view -> finish());
    }
    @Override
    protected void onResume() {
        super.onResume();
        notiSwitch.setChecked(curUser.getNotificationSetting());
    }

}
