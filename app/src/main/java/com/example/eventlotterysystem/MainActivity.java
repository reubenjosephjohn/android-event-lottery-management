package com.example.eventlotterysystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.check_device);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });


        // Database test
        Control control = Control.getInstance();


        // Create users
//        User tom = new User(Control.getInstance().getCurrentUserIDForUserCreation(), "Tom");
//        control.getUserList().add(tom);
//        control.saveUser(tom);
//        User jerry = new User(Control.getInstance().getCurrentUserIDForUserCreation(), "Jerry");
//        control.getUserList().add(jerry);
//        control.saveUser(jerry);

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
        }, 2500);
    }
}