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

/**
 * Activity that displays the details of a specific user for the admin. It provides options
 * to delete the user's profile or profile picture and withdraw the user from all events.
 */
public class AdminViewUserActivity extends AppCompatActivity {

    /**
     * TextView to display the name of the user.
     */
    private TextView nameTextView;

    /**
     * TextView to display the contact information of the user.
     */
    private TextView contactTextView;

    /**
     * TextView to display the email address of the user.
     */
    private TextView emailTextView;

    /**
     * The user object containing details about the current user being viewed.
     */
    private User user;

    /**
     * ImageView to display the user's profile picture.
     */
    private ImageView profileImageView;

    /**
     * Button to delete the user's profile or profile picture.
     */
    private ImageView deleteButton;

    /**
     * Called when the activity is created. Initializes the UI elements and displays user details.
     *
     * @param savedInstanceState A Bundle object containing the activity's previously saved state.
     */
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
        for (User u : Control.getInstance().getUserList()) {
            if (u.getUserID() == user.getUserID()) {
                user = u;
            }
        }

        // Display user details
        if (user != null) {
            nameTextView.setText("Name: " + user.getName());
            contactTextView.setText("Contact: " + user.getContact());
            emailTextView.setText("Email: " + user.getEmail());
            if (user.getPicture() != null) {
                profileImageView.setImageBitmap(decodeBitmap(user.getPicture()));
                profileImageView.setVisibility(View.VISIBLE);
            } else {
                profileImageView.setImageBitmap(null);
                profileImageView.setVisibility(View.GONE);
            }
        }

        // Set up back button listener
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> finish());

        // Set up delete button listener
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
                                            profileImageView.setVisibility(View.GONE);
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
                                            profileImageView.setVisibility(View.GONE);

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

    /**
     * Decodes a Base64 encoded image string into a Bitmap.
     *
     * @param encodedImage The Base64 encoded image string.
     * @return The decoded Bitmap.
     */
    private Bitmap decodeBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
