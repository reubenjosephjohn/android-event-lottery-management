package com.example.eventlotterysystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminViewUserActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView contactTextView;
    private TextView emailTextView;
    private User user;
    private ImageView profileImageView;

    private ImageView deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user);

        nameTextView = findViewById(R.id.name);
        contactTextView = findViewById(R.id.contact);
        emailTextView = findViewById(R.id.email);
        deleteButton = findViewById(R.id.del_button);
        profileImageView = findViewById(R.id.imageView);

        // Get user details from Intent
        user = (User) getIntent().getSerializableExtra("user");

        // Display user details
        if (user != null) {
            nameTextView.setText("Name: " + user.getName());
            contactTextView.setText("Contact: " + user.getContact());
            emailTextView.setText("Email: " + user.getEmail());
            if (user.getPicture() != null){
                profileImageView.setImageBitmap(decodeBitmap(user.getPicture()));
            }
            else{
                profileImageView.setImageBitmap(null);
            }
        }

        // Set up back button listener
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> finish());

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AdminViewUserActivity.this);
                builder.setTitle("Delete")
                        .setMessage("Select one of the options below:")
                        .setPositiveButton("Profile", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new android.app.AlertDialog.Builder(AdminViewUserActivity.this)
                                        .setTitle("Delete Profile Information")
                                        .setMessage("Are you sure you want to delete this profile? \n \nThis will withdraw you from all events. \n \nYour facility and managed events will not be removed. ")
                                        .setPositiveButton("Delete", (dialog1, which1) -> {
                                            // Withdraw from all the events
                                            for (Event event : Control.getInstance().getEventList()) {
                                                // waiting
                                                if (event.getWaitingUserRefs().contains(Integer.valueOf(user.getUserID()))) {
                                                    event.getWaitingUserRefs().remove(Integer.valueOf(user.getUserID()));
                                                    Control.getInstance().saveEvent(event);
                                                }
                                                // chosen
                                                if (event.getChosenUserRefs().contains(Integer.valueOf(user.getUserID()))) {
                                                    event.getCancelledUserRefs().add(user.getUserID());
                                                    event.getChosenUserRefs().remove(Integer.valueOf(user.getUserID()));
                                                    Control.getInstance().saveEvent(event);
                                                }
                                                // final
                                                if (event.getFinalUserRefs().contains(Integer.valueOf(user.getUserID()))) {
                                                    event.getCancelledUserRefs().add(user.getUserID());
                                                    event.getFinalUserRefs().remove(Integer.valueOf(user.getUserID()));
                                                    Control.getInstance().saveEvent(event);
                                                }
                                            }

                                            user.setName("Default Name");
                                            user.setEmail("user@example.com");
                                            user.setContact("000-000-0000");
                                            user.setPicture(null);
                                            Control.getInstance().saveUser(user);
                                            finish();
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            }
                        })
                        .setNegativeButton("Picture", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(AdminViewUserActivity.this)
                                        .setTitle("Delete Profile Information")
                                        .setMessage("Are you sure you want to delete this Picture?")
                                        .setPositiveButton("Delete", (dialog1, which1) -> {
                                            user.setPicture(null);
                                            Control.getInstance().saveUser(user);
                                            profileImageView.setImageBitmap(null);

                                        })
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            }
                        })
                        .setCancelable(true)
                        .show();
            }
        });
    }
    private Bitmap decodeBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}