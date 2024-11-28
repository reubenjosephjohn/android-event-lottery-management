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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.check_device);

        FirebaseApp.initializeApp(this);

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
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

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

        // Create users
//        User tom = new User(2, "Tom");
//        control.getUserList().add(tom);
//        control.saveUser(tom);
//        User jerry = new User(3, "Jerry");
//        control.getUserList().add(jerry);
//        control.saveUser(jerry);
//        User bob = new User(4, "Bob");
//        control.getUserList().add(bob);
//        control.saveUser(bob);
//        User macy = new User(5, "Macy");
//        control.getUserList().add(macy);
//        control.saveUser(macy);

        // Create notification
//        Notification n1 = new Notification(16, 6, false, "Test message 2");
//        control.getNotificationList().add(n1);
//        control.addNotification(n1);


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
        }, 10000);
    }
}