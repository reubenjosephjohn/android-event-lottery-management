package com.example.eventlotterysystem;

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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * DialogFragment for creating an event. Allows users to input event details, upload an image,
 * and validate the inputs before submitting the event. Supports QR code generation after event creation.
 */
public class CreateEventDialogFragment extends DialogFragment {

    private CreateEventListener listener;
    private User curUser;

    private ImageView imagePreview;
    private ImageButton removeImageButton;
    private Button uploadImageButton;
    private Uri selectedImageUri;
    private String pos;

    // ActivityResultLauncher for image selection
    private ActivityResultLauncher<String> pickImageLauncher;

    /**
     * Listener interface to handle the event creation process.
     */
    public interface CreateEventListener {
        /**
         * Called when a new event has been successfully created.
         *
         * @param newEvent The newly created event.
         */
        void onEventCreated(Event newEvent);
    }

    /**
     * Sets the listener for event creation.
     *
     * @param listener The listener to be notified when an event is created.
     */
    public void setCreateEventListener(CreateEventListener listener) {
        this.listener = listener;
    }

    /**
     * Inflates the view and initializes the UI elements. Sets up listeners for image uploading and event creation.
     *
     * @param inflater The LayoutInflater used to inflate the view.
     * @param container The parent container for the view.
     * @param savedInstanceState A Bundle containing the fragment's previously saved state.
     * @return The inflated view for the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_event_fragment, container, false);
        curUser = Control.getCurrentUser();

        // UI elements
        Switch locationSwitch = view.findViewById(R.id.location_loc);
        EditText titleEdit = view.findViewById(R.id.firstName);
        EditText descriptionEdit = view.findViewById(R.id.title_edit5);
        EditText limitChosenEdit = view.findViewById(R.id.editTextNumber2);
        EditText limitWaitingEdit = view.findViewById(R.id.editTextNumber);
        imagePreview = view.findViewById(R.id.imagePreview);
        removeImageButton = view.findViewById(R.id.removeImageButton);
        uploadImageButton = view.findViewById(R.id.uploadImage_button);
        Button finishButton = view.findViewById(R.id.finish_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        // Initialize ActivityResultLauncher for image selection
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
        uploadImageButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        // Set Remove Image Button Listener
        removeImageButton.setOnClickListener(v -> {
            selectedImageUri = null;
            imagePreview.setVisibility(View.GONE);
            removeImageButton.setVisibility(View.GONE);
            imagePreview.setImageDrawable(null);
            pos = null;
            Toast.makeText(getContext(), "Image removed", Toast.LENGTH_SHORT).show();
        });

        // Set Finish Button Listener
        finishButton.setOnClickListener(v -> createEvent(locationSwitch, titleEdit, descriptionEdit, limitChosenEdit, limitWaitingEdit));

        // Set Cancel Button Listener
        cancelButton.setOnClickListener(v -> dismiss()); // Close the dialog if canceled

        return view;
    }

    /**
     * Validates the event creation form and creates a new event if the data is valid.
     *
     * @param locationSwitch The Switch for the event's location setting.
     * @param titleEdit The EditText for the event title.
     * @param descriptionEdit The EditText for the event description.
     * @param limitChosenEdit The EditText for the chosen limit.
     * @param limitWaitingEdit The EditText for the waiting list limit.
     */
    private void createEvent(Switch locationSwitch, EditText titleEdit, EditText descriptionEdit,
                             EditText limitChosenEdit, EditText limitWaitingEdit) {
        String eventTitle = titleEdit.getText().toString().trim();
        String eventDescription = descriptionEdit.getText().toString().trim();
        String limitChosenString = limitChosenEdit.getText().toString().trim();
        String limitWaitingString = limitWaitingEdit.getText().toString().trim();

        List<String> missingFields = new ArrayList<>();
        boolean hasError = false;

        // Validate fields (simplified, can use helper methods for validation)
        if (eventTitle.isEmpty()) {
            titleEdit.setError(getString(R.string.error_event_title_required));
            missingFields.add(getString(R.string.label_event_title));
            hasError = true;
        }
        if (eventDescription.isEmpty()) {
            descriptionEdit.setError(getString(R.string.error_event_description_required));
            missingFields.add(getString(R.string.label_event_description));
            hasError = true;
        }

        int limitChosen = validateLimitField(limitChosenEdit, limitChosenString, missingFields);
        int limitWaiting = validateLimitField(limitWaitingEdit, limitWaitingString, missingFields);

        if (hasError) {
            String message = "Please fill in the following fields: " + TextUtils.join(", ", missingFields);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            return;
        }

        boolean geo = locationSwitch.isChecked();
        Event newEvent = new Event(Control.getInstance().getCurrentEventIDForEventCreation(), eventTitle, eventDescription, limitChosen, limitWaiting, geo);
        newEvent.setCreatorRef(curUser.getUserID());
        Control.getInstance().getEventList().add(newEvent);
        Control.getInstance().saveEvent(newEvent);
        newEvent.generateQR();
        newEvent.setPoster(pos);

        // Notify listener and show QR code dialog
        if (listener != null) {
            listener.onEventCreated(newEvent);
        }
        QRCodeDialogFragment qrCodeDialog = QRCodeDialogFragment.newInstance(newEvent.getHashCodeQR());
        qrCodeDialog.show(getParentFragmentManager(), "QRCodeDialogFragment");

        dismiss(); // Close the dialog
    }

    /**
     * Validates the limit fields (chosen and waiting).
     *
     * @param limitEdit The EditText for the limit field.
     * @param limitString The input string for the limit.
     * @param missingFields The list of missing fields to be populated if the validation fails.
     * @return The validated limit or a default value if the input is invalid.
     */
    private int validateLimitField(EditText limitEdit, String limitString, List<String> missingFields) {
        int limit = 0;
        if (limitString.isEmpty()) {
            limitEdit.setError(getString(R.string.error_limit_chosen_required));
            missingFields.add(limitEdit.getHint().toString());
        } else {
            try {
                limit = Integer.parseInt(limitString);
                if (limit <= 0) {
                    limitEdit.setError(getString(R.string.error_limit_positive));
                    missingFields.add(limitEdit.getHint().toString());
                }
            } catch (NumberFormatException e) {
                limitEdit.setError(getString(R.string.error_limit_chosen_invalid));
                missingFields.add(limitEdit.getHint().toString());
            }
        }
        return limit;
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

    /**
     * Retrieves a Bitmap from a URI.
     *
     * @param uri The URI of the image.
     * @return The Bitmap retrieved from the URI.
     */
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Resizes a Bitmap to a given width while maintaining aspect ratio.
     *
     * @param image The original Bitmap to be resized.
     * @param maxResolution The desired maximum resolution.
     * @return The resized Bitmap.
     */
    private Bitmap resizeBitmapToResolution(Bitmap image, int maxResolution) {
        int width = image.getWidth();
        int height = image.getHeight();
        float aspectRatio = (float) width / height;
        if (width > height) {
            width = maxResolution;
            height = (int) (width / aspectRatio);
        } else {
            height = maxResolution;
            width = (int) (height * aspectRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, false);
    }

    /**
     * Encodes a Bitmap into a Base64 string.
     *
     * @param image The Bitmap to be encoded.
     * @return The Base64 encoded string.
     */
    private String encodeBitmap(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
