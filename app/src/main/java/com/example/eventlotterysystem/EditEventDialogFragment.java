package com.example.eventlotterysystem;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditEventDialogFragment extends DialogFragment {

    private EditEventListener listener;

    private ImageView imagePreview;
    private ImageButton removeImageButton;
    private Button uploadImageButton;
    private Uri selectedImageUri;
    private String pos; // Base64 encoded poster

    // ActivityResultLauncher for image selection
    private ActivityResultLauncher<String> pickImageLauncher;

    private EditText nameEditText;
    private EditText descriptionEditText;
    private EditText limitChosenEdit;
    private EditText limitWaitingEdit;
    private TextView chosenLimitText;
    private TextView waitingLimitText;
    private Switch geolocationSwitch;

    private Event curEvent;

    public interface EditEventListener {
        void onEventEdited(Event updatedEvent);
    }

    public void setEditEventListener(EditEventListener listener) {
        this.listener = listener;
    }

    public EditEventDialogFragment(Event event) {
        this.curEvent = event;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_event_fragment, container, false);

        // Initialize fields
        nameEditText = view.findViewById(R.id.firstName);
        descriptionEditText = view.findViewById(R.id.title_edit5);
        limitChosenEdit = view.findViewById(R.id.editTextNumber2);
        limitWaitingEdit = view.findViewById(R.id.editTextNumber);
        chosenLimitText = view.findViewById(R.id.textView6);
        waitingLimitText = view.findViewById(R.id.textView7);
        geolocationSwitch = view.findViewById(R.id.location_loc);
        imagePreview = view.findViewById(R.id.imagePreview);
        removeImageButton = view.findViewById(R.id.removeImageButton);
        uploadImageButton = view.findViewById(R.id.uploadImage_button);
        Button finishButton = view.findViewById(R.id.finish_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        // Hide limit fields
        chosenLimitText.setVisibility(View.GONE);
        waitingLimitText.setVisibility(View.GONE);
        limitChosenEdit.setVisibility(View.GONE);
        limitWaitingEdit.setVisibility(View.GONE);

        // Populate fields with current event data
        nameEditText.setText(curEvent.getName());
        descriptionEditText.setText(curEvent.getDescription());
        geolocationSwitch.setChecked(curEvent.getGeoSetting());

        // Initialize ActivityResultLauncher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        Bitmap bmURI = getBitmapFromUri(uri);
                        bmURI = resizeBitmapToResolution(bmURI, 400);
                        pos = encodeBitmap(bmURI);
                        imagePreview.setVisibility(View.VISIBLE);
                        removeImageButton.setVisibility(View.VISIBLE);
                        Glide.with(this)
                                .load(uri)
                                .into(imagePreview);
                    }
                }
        );

        // Set Upload Image Button Listener
        uploadImageButton.setOnClickListener(v -> {
            pickImageLauncher.launch("image/*");
        });

        removeImageButton.setOnClickListener(v -> {
            selectedImageUri = null;
            pos = null;
            imagePreview.setVisibility(View.GONE);
            removeImageButton.setVisibility(View.GONE);
            imagePreview.setImageDrawable(null);
            Toast.makeText(getContext(), "Poster removed", Toast.LENGTH_SHORT).show();
        });

        // Set Finish Button Listener
        finishButton.setOnClickListener(v -> {
            // Retrieve and trim input values
            String eventTitle = nameEditText.getText().toString().trim();
            String eventDescription = descriptionEditText.getText().toString().trim();

            boolean hasError = false;
            List<String> missingFields = new ArrayList<>();

            // Validate Event Title
            if (eventTitle.isEmpty()) {
                nameEditText.setError(getString(R.string.error_event_title_required));
                missingFields.add(getString(R.string.label_event_title));
                hasError = true;
            } else {
                nameEditText.setError(null);
            }

            // Validate Event Description
            if (eventDescription.isEmpty()) {
                descriptionEditText.setError(getString(R.string.error_event_description_required));
                missingFields.add(getString(R.string.label_event_description));
                hasError = true;
            } else {
                descriptionEditText.setError(null);
            }

            // If there are validation errors, show a Toast message and halt the process
            if (hasError) {
                String message;
                if (missingFields.size() == 1) {
                    message = "Please fill in the " + missingFields.get(0) + " field.";
                } else if (missingFields.size() > 1) {
                    String fields = TextUtils.join(", ", missingFields);
                    message = "Please fill in the following fields: " + fields + ".";
                } else {
                    message = "Please correct the errors above.";
                }
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                return;
            }

            boolean geo = geolocationSwitch.isChecked();
            // Update the event details
            curEvent.setName(eventTitle);
            curEvent.setDescription(eventDescription);
            curEvent.setGeoSetting(geo);

            // Update the poster if changed
            if (pos != null) {
                curEvent.setPoster(pos);
            }

            // Save changes to the event list
            Control.getInstance().saveEvent(curEvent);

            // Notify the listener about the edited event
            if (listener != null) {
                listener.onEventEdited(curEvent);
            }

            dismiss();
        });

        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    /**
     * Decodes a Base64 encoded string back to a Bitmap.
     *
     * @param encodedImage The Base64 encoded image content.
     * @return The decoded Bitmap.
     */
    private Bitmap decodeBitmap(String encodedImage) {
        try {
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decodes a Uri to a Bitmap.
     *
     * @param uri The Uri of the image to decode.
     * @return The decoded Bitmap, or null if decoding fails.
     */
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Encodes a Bitmap to a Base64 string.
     *
     * @param bitmap The Bitmap to encode.
     * @return The Base64 encoded string.
     */
    private String encodeBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Resizes a Bitmap to a target resolution while maintaining aspect ratio.
     *
     * @param bitmap           The original bitmap to resize.
     * @param targetResolution The desired resolution for the longest side.
     * @return The resized bitmap.
     */
    public Bitmap resizeBitmapToResolution(Bitmap bitmap, int targetResolution) {
        if (bitmap == null || targetResolution <= 0) {
            throw new IllegalArgumentException("Bitmap must not be null and target resolution must be positive.");
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleFactor = (width > height)
                ? (float) targetResolution / width
                : (float) targetResolution / height;

        // Avoid upscaling
        if (scaleFactor >= 1.0f) {
            return bitmap;
        }

        int newWidth = Math.round(width * scaleFactor);
        int newHeight = Math.round(height * scaleFactor);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}
