package com.example.eventlotterysystem;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.core.content.ContextCompat;

/**
 * The main activity of the application. This activity initializes the application, retrieves the Firebase installation ID,
 * and navigates to the landing page after a delay.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created. This method sets up the UI, retrieves the Firebase installation ID,
     * and navigates to the landing page after a delay.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.check_device);


        Control control = Control.getInstance();
        // get Firebase installation ID
        FirebaseInstallations.getInstance().getId().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    Control.setLocalFID(task.getResult());
                } else {
                    Log.e("Firebase Error", "Error getting Firebase Installation ID", task.getException());
                }
            }
        });


        Toast.makeText(MainActivity.this, "Synchronizing data...", Toast.LENGTH_SHORT).show();
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Set the button visible after 3 seconds
                // checkDeviceButton.setVisibility(View.VISIBLE);
                // Automatically go to the next page
                Control.getInstance();
                Intent intent = new Intent(MainActivity.this, Landing_page.class);
                startActivity(intent);
            }
        }, 11000);
    }
}