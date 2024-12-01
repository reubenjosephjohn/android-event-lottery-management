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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for editing an existing event. Provides a user interface to update event details,
 * such as the event's name, description, geolocation setting, and poster image.
 */
public class EditEventDialogFragment extends DialogFragment {

    private EditEventListener listener;

    private TextView eventTitle;
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
//    private TextView chosenLimitText;
//    private TextView waitingLimitText;
    private Switch geolocationSwitch;

    private Event curEvent;

    private EditText registrationStartEdit, registrationEndEdit, eventStartEdit, eventEndEdit;

    /**
     * Listener interface to notify when the event is edited.
     */

    public interface EditEventListener {
        /**
         * Callback when an event is successfully edited.
         *
         * @param updatedEvent The updated event object.
         */
        void onEventEdited(Event updatedEvent);
    }

    /**
     * Sets the listener for event editing callback.
     *
     * @param listener The listener to be notified when the event is edited.
     */
    public void setEditEventListener(EditEventListener listener) {
        this.listener = listener;
    }

    /**
     * Constructor for creating the dialog fragment with an existing event to edit.
     *
     * @param event The event to be edited.
     */
    public EditEventDialogFragment(Event event) {
        this.curEvent = event;
    }

    /**
     * Creates and returns the view for the dialog fragment.
     *
     * @param inflater           The LayoutInflater used to inflate the view.
     * @param container          The parent view group.
     * @param savedInstanceState The saved instance state bundle.
     * @return The view for the dialog fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_event_fragment, container, false);

        // Initialize fields
        eventTitle = view.findViewById(R.id.textView4);
        nameEditText = view.findViewById(R.id.firstName);
        descriptionEditText = view.findViewById(R.id.title_edit5);
        limitChosenEdit = view.findViewById(R.id.editTextNumber2);
        limitWaitingEdit = view.findViewById(R.id.editTextNumber);
//        chosenLimitText = view.findViewById(R.id.textView6);
//        waitingLimitText = view.findViewById(R.id.textView7);
        geolocationSwitch = view.findViewById(R.id.location_loc);
        imagePreview = view.findViewById(R.id.imagePreview);
        removeImageButton = view.findViewById(R.id.removeImageButton);
        uploadImageButton = view.findViewById(R.id.uploadImage_button);
        Button finishButton = view.findViewById(R.id.finish_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        registrationStartEdit= view.findViewById(R.id.registration_start);
        registrationEndEdit = view.findViewById(R.id.registration_end);
        eventStartEdit = view.findViewById(R.id.event_start);
        eventEndEdit = view.findViewById(R.id.event_end);

        // Hide limit fields
//        chosenLimitText.setVisibility(View.GONE);
//        waitingLimitText.setVisibility(View.GONE);
        limitChosenEdit.setVisibility(View.GONE);
        limitWaitingEdit.setVisibility(View.GONE);

        eventTitle.setText(curEvent.getName());

        if (curEvent.getPoster() != null) {
            uploadImageButton.setText("Replace Poster");
        }

        String eventDesc = curEvent.getDescription();
        String regStart = extractDate(eventDesc, 0);
        String regEnd = extractDate(eventDesc, 1);
        String eventStart = extractDate(eventDesc, 2);
        String eventEnd = extractDate(eventDesc, 3);

        String shortEventDesc = shorten(eventDesc);

        // Populate fields with current event data
        nameEditText.setText(curEvent.getName());
        descriptionEditText.setText(shortEventDesc);
        geolocationSwitch.setChecked(curEvent.getGeoSetting());

        registrationStartEdit.setText(regStart);
        registrationEndEdit.setText(regEnd);
        eventStartEdit.setText(eventStart);
        eventEndEdit.setText(eventEnd);

        // Initialize ActivityResultLauncher for image picking
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

        // Set Remove Image Button Listener
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

            String newRegStart = registrationStartEdit.getText().toString().trim();
            String newRegEnd = registrationEndEdit.getText().toString().trim();
            String newEventStart = eventStartEdit.getText().toString().trim();
            String newEventEnd = eventEndEdit.getText().toString().trim();

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

            // Validate Registration and Event Dates
            if (!validDates(newRegStart, newRegEnd, newEventStart, newEventEnd)) {
                return;
            }
            else {
                eventDescription = eventDescription + "\n"
                        + "Registration Period: " + newRegStart + " to " + newRegEnd + "\n"
                        + "Event Period: " + newEventStart + " to " + newEventEnd;
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
     * @param bitmap   The Bitmap to resize.
     * @param targetResolution The target resolution.
     * @return The resized Bitmap.
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

    public static String shorten(String input) {
        if (input == null) {
            return null;
        }

        int newlineIndex = input.indexOf('\n');
        if (newlineIndex == -1) {
            return input;
        }

        return input.substring(0, newlineIndex);
    }
    protected boolean validDate(String date) {
        String regex = "^\\d{0,4}(-\\d{0,2})?(-\\d{0,2})?$";
        return date.length() == 10 && date.matches(regex);
    }

    protected boolean validPeriod(String start, String end) {
        LocalDate date1 = LocalDate.parse(start);
        LocalDate date2 = LocalDate.parse(end);
        return date1.isBefore(date2);
    }

    protected boolean validDates(String regStart, String regEnd, String eventStart, String eventEnd) {
        if (!validDate(regStart) || !validDate(regEnd) || !validDate(eventStart) || !validDate(eventEnd)) {
            Toast.makeText(getContext(), "Use date format: YYYY-MM-DD", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!validPeriod(regStart, regEnd)) {
            Toast.makeText(getContext(), "Registration Start must precede Registration End", Toast.LENGTH_SHORT).show();
            return false;
        }
        if ( !validPeriod(eventStart, eventEnd)) {
            Toast.makeText(getContext(), "Event Start must precede Event End", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!validPeriod(regEnd, eventStart)) {
            Toast.makeText(getContext(), "Registration End must precede Event Start", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String extractDate(String eventDesc, int dateType) {
        String regStart = "";
        String regEnd = "";
        String eventStart = "";
        String eventEnd = "";

        String[] lines = eventDesc.split("\n");
        for (String line : lines) {
            if (line.contains("Registration Period:")) {
                String regPeriod = line.replace("Registration Period: ", "").trim();
                String[] regDates = regPeriod.split(" to ");
                regStart = regDates[0];
                regEnd = regDates[1];
            }
            if (line.contains("Event Period: ")) {
                String regPeriod = line.replace("Event Period: ", "").trim();
                String[] eventDates = regPeriod.split(" to ");
                eventStart = eventDates[0];
                eventEnd = eventDates[1];
            }
        }

        switch (dateType) {
            case 0:
                return regStart;
            case 1:
                return regEnd;
            case 2:
                return eventStart;
            case 3:
                return eventEnd;
            default:
                return "";
        }
    }
}
