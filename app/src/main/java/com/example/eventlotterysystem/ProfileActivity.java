package com.example.eventlotterysystem;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import com.bumptech.glide.Glide;

import java.util.Set;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * An Activity that displays the user's profile information and allows editing of the profile.
 *
 * Problem: User profile picture is not implemented.
 */
public class ProfileActivity extends AppCompatActivity implements EditProfileFragment.OnProfileUpdatedListener {
//public class ProfileActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView contactTextView;
    private ImageView profileImageView;
    private User curUser;
    private Button gen;
    private Button uploadImageButton;
    private Uri selectedImageUri;
    private ActivityResultLauncher<String> pickImageLauncher;
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

        curUser = Control.getCurrentUser();

        nameTextView = findViewById(R.id.name);
        emailTextView = findViewById(R.id.email);
        contactTextView = findViewById(R.id.contact);
        profileImageView = findViewById(R.id.poster);
        gen = findViewById(R.id.generate_button);
        uploadImageButton = findViewById(R.id.upload_button);

        // Initialize ActivityResultLauncher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        Bitmap bmURI = getBitmapFromUri(uri);
                        bmURI = resizeBitmapToResolution(bmURI, 300);
                        curUser.setPicture(encodeBitmap(bmURI));
                        gen.setText("Replace Image");
                        Glide.with(this)
                                .load(uri)
                                .into(profileImageView);
                        Control.getInstance().saveUser(curUser);
                    }
                }
        );

        // Set Upload Image Button Listener
        uploadImageButton.setOnClickListener(v -> {
            // Launch the image picker
            pickImageLauncher.launch("image/*");
        });

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

//         If contact is default, prompt user to edit profile
        if ("000-000-0000".equals(curUser.getContact())) {
            openEditProfileFragment("", "", "");
        }

//         Set up edit button listener
        findViewById(R.id.edit_button).setOnClickListener(v -> openEditProfileFragment(
                curUser.getName(), curUser.getEmail(), curUser.getContact()
        ));

        // Set up return button listener
        ImageButton returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(view -> finish());

        gen.setOnClickListener(v -> generateProfilePicture());

        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Delete Profile Information")
                    .setMessage("Are you sure you want to delete this profile? \n \nThis will withdraw you from all events. \n \nYour facility and managed events will not be removed. ")
                    .setPositiveButton("Delete", (dialog, which) -> {
                    // Withdraw from all the events
                        for (Event event : Control.getInstance().getEventList()) {
                            // waiting
                            if (event.getWaitingUserRefs().contains(Integer.valueOf(curUser.getUserID()))) {
                                event.getWaitingUserRefs().remove(Integer.valueOf(curUser.getUserID()));
                                Control.getInstance().saveEvent(event);
                            }

                            // chosen
                            if (event.getChosenUserRefs().contains(Integer.valueOf(curUser.getUserID()))) {
                                event.getCancelledUserRefs().add(curUser.getUserID());
                                event.getChosenUserRefs().remove(Integer.valueOf(curUser.getUserID()));
                                Control.getInstance().saveEvent(event);
                            }
                            if (event.getFinalUserRefs().contains(Integer.valueOf(curUser.getUserID()))) {
                                event.getCancelledUserRefs().add(curUser.getUserID());
                                event.getFinalUserRefs().remove(Integer.valueOf(curUser.getUserID()));
                                Control.getInstance().saveEvent(event);
                            }
                        }

                        curUser.setName("Default Name");
                        curUser.setEmail("user@example.com");
                        curUser.setContact("000-000-0000");
                        curUser.setPicture(null);

                        nameTextView.setText(curUser.getName());
                        emailTextView.setText("Email: " + curUser.getEmail());
                        contactTextView.setText("Contact: " + curUser.getContact());
                        profileImageView.setImageDrawable(null);

                        Control.getInstance().saveUser(curUser);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

    }

    /**
     * Opens the EditProfileFragment to allow the user to edit their profile information.
     *
     * @param name    The current name of the user.
     * @param email   The current email of the user.
     * @param contact The current contact information of the user.
     */
    private void openEditProfileFragment(String name, String email, String contact) {
        EditProfileFragment editProfileFragment = EditProfileFragment.newInstance(name, email, contact);
        editProfileFragment.setOnProfileUpdatedListener(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        editProfileFragment.show(fragmentManager, "editProfileFragment");
    }

    /**
     * Callback method invoked when the profile has been updated.
     *
     * @param name    The updated name of the user.
     * @param email   The updated email of the user.
     * @param contact The updated contact information of the user.
     */
    @Override
    public void onProfileUpdated(String name, String email, String contact) {
        curUser.setName(name);
        curUser.setEmail(email);
        curUser.setContact(contact);

        updateProfileUI();
        Control.getInstance().saveUser(curUser);
    }

    /**
     * Updates the UI elements to reflect the latest profile information.
     */
    private void updateProfileUI() {
        nameTextView.setText(curUser.getName());
        emailTextView.setText("Email: " + curUser.getEmail());
        contactTextView.setText("Contact: " + curUser.getContact());
    }
//
    private void generateProfilePicture() {
        // Generate picture for the user
        curUser.generate_picture();  // This calls the generate_picture method in the User class

        // After the picture is generated, update the ImageView with the new profile picture
        String generatedPicture = curUser.getPicture();
        if (generatedPicture != null) {
            Bitmap pictureBitmap = decodeBitmap(generatedPicture);  // Assuming decodeBitmap method to convert String to Bitmap
            profileImageView.setImageBitmap(pictureBitmap);  // Set the generated bitmap as the ImageView source
            gen.setText("Replace Image");
        } else {
            Log.e("ProfileActivity", "Failed to generate profile picture.");
        }
        Control.getInstance().saveUser(curUser);
    }

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
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    // Helper method to encode Bitmap to a String (Base64 encoding or any method you prefer)
    private String encodeBitmap(Bitmap bitmap) {
        // Convert bitmap to a Base64 encoded string (as an example)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    public Bitmap resizeBitmapToResolution(Bitmap bitmap, int targetResolution) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Determine scaling factor to make the longest side approximately equal to targetResolution
        float scaleFactor = (width > height)
                ? (float) targetResolution / width
                : (float) targetResolution / height;

        // Calculate new width and height
        int newWidth = Math.round(width * scaleFactor);
        int newHeight = Math.round(height * scaleFactor);

        // Resize the bitmap
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}