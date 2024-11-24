package com.example.eventlotterysystem;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

/**
 * An Activity that displays the user's profile information and allows editing of the profile.
 *
 * Problem: User profile picture is not implemented.
 */
//public class ProfileActivity extends AppCompatActivity implements EditProfileFragment.OnProfileUpdatedListener {
public class ProfileActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView contactTextView;
    private ImageView profileImageView;
    private User curUser;
    private Button gen;
    private ImageView deleteButton;

    /**
     * Called when the activity is first created. Initializes the UI and sets up event listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        // Test control data
//        Log.i("checkControlData", "Profile Activity Control Data Test");
//        Utils.checkControlData(Control.getInstance());
//        curUser = Control.getCurrentUser();
        curUser = Control.getInstance().getUserList().get(0);
        nameTextView = findViewById(R.id.name);
        emailTextView = findViewById(R.id.email);
        contactTextView = findViewById(R.id.contact);
        profileImageView = findViewById(R.id.poster);
        gen = findViewById(R.id.generate_button);
        deleteButton = findViewById(R.id.del_button);

        String picture = curUser.getPicture();  // Get the current picture from the user object
        if (picture != null) {
            // If a picture exists, decode the Base64 content and set it to the ImageView
            Bitmap pictureBitmap = decodeBitmap(picture);  // Assuming decodeBitmap method to convert String to Bitmap
            profileImageView.setImageBitmap(pictureBitmap);  // Set the generated bitmap as the ImageView source
            gen.setText("Replace Image");
        }

        // Set initial profile information
        nameTextView.setText(curUser.getName());
        emailTextView.setText("Email: " +curUser.getEmail());
        contactTextView.setText("Contact: " +curUser.getContact());

        // If contact is default, prompt user to edit profile
//        if ("000-000-0000".equals(curUser.getContact())) {
//            openEditProfileFragment("", "", "");
//        } else {
            // Just display the profile unless the user clicks edit
            // openEditProfileFragment(curUser.getName(), curUser.getEmail(), curUser.getContact());
//        }

        // Set up edit button listener
//        findViewById(R.id.edit_button).setOnClickListener(v -> openEditProfileFragment(
//                curUser.getName(), curUser.getEmail(), curUser.getContact()
//        ));

        // Set up return button listener
        ImageButton returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(view -> finish());

//        gen.setOnClickListener(v -> generateProfilePicture());

//        deleteButton.setOnClickListener(v -> {
//            new AlertDialog.Builder(ProfileActivity.this)
//                    .setTitle("Delete Profile Information")
//                    .setMessage("Are you sure you want to delete this profile?")
//                    .setPositiveButton("Delete", (dialog, which) -> {
//                    // Withdraw from all the events
//                        for (Event event : curUser.getEnrolledList()) {
//                            event.getWaitingList().remove(curUser);
//                            if (event.getChosenList().contains(curUser)){
//                                event.getChosenList().remove(curUser);
//                                event.getCancelledList().add(curUser);
//                            }
//                            if (event.getFinalList().contains(curUser)) {
//                                event.getFinalList().remove(curUser);
//                                event.getCancelledList().add(curUser);
//                            }
//                        }
//                        curUser.getEnrolledList().clear();
//                        curUser.setName("Default Name");
//                        curUser.setEmail("user@example.com");
//                        curUser.setContact("000-000-0000");
//
//                        nameTextView.setText(curUser.getName());
//                        emailTextView.setText("Email: " + curUser.getEmail());
//                        contactTextView.setText("Contact: " + curUser.getContact());
//
//                        FirestoreManager.getInstance().saveControl(Control.getInstance());
//                    })
//                    .setNegativeButton("Cancel", null)
//                    .show();
//        });

    }

    /**
     * Opens the EditProfileFragment to allow the user to edit their profile information.
     *
     * @param name    The current name of the user.
     * @param email   The current email of the user.
     * @param contact The current contact information of the user.
     */
//    private void openEditProfileFragment(String name, String email, String contact) {
//        EditProfileFragment editProfileFragment = EditProfileFragment.newInstance(name, email, contact);
//        editProfileFragment.setOnProfileUpdatedListener(this);
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        editProfileFragment.show(fragmentManager, "editProfileFragment");
//    }

    /**
     * Callback method invoked when the profile has been updated.
     *
     * @param name    The updated name of the user.
     * @param email   The updated email of the user.
     * @param contact The updated contact information of the user.
     */
//    @Override
//    public void onProfileUpdated(String name, String email, String contact) {
//        curUser.setName(name);
//        curUser.setEmail(email);
//        curUser.setContact(contact);

//        updateProfileUI();
//    }

    /**
     * Updates the UI elements to reflect the latest profile information.
     */
//    private void updateProfileUI() {
//        nameTextView.setText(curUser.getName());
//        emailTextView.setText("Email: " + curUser.getEmail());
//        contactTextView.setText("Contact: " + curUser.getContact());
//        FirestoreManager.getInstance().saveControl(Control.getInstance());
//    }
//
//    private void generateProfilePicture() {
//        // Generate picture for the user
//        curUser.generate_picture();  // This calls the generate_picture method in the User class
//
//        // After the picture is generated, update the ImageView with the new profile picture
//        String generatedPicture = curUser.getPicture();
//        if (generatedPicture != null) {
//            Bitmap pictureBitmap = decodeBitmap(generatedPicture);  // Assuming decodeBitmap method to convert String to Bitmap
//            profileImageView.setImageBitmap(pictureBitmap);  // Set the generated bitmap as the ImageView source
//            gen.setText("Replace Image");
//        } else {
//            Log.e("ProfileActivity", "Failed to generate profile picture.");
//        }
//        FirestoreManager.getInstance().saveControl(Control.getInstance());
//    }

    /**
     * Decodes a Base64 encoded string back to a Bitmap.
     *
     * @param encodedImage The Base64 encoded image content.
     * @return The decoded Bitmap.
     */
    private Bitmap decodeBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}