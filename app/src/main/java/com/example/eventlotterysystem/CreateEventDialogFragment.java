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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * Previous UI approach to creating an event
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

    public interface CreateEventListener {
        void onEventCreated(Event newEvent);
    }

    public void setCreateEventListener(CreateEventListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_event_fragment, container, false);
        curUser = Control.getCurrentUser();


        Switch locationSwitch = view.findViewById(R.id.location_loc);
        EditText titleEdit = view.findViewById(R.id.firstName);
        EditText descriptionEdit = view.findViewById(R.id.title_edit5);
        EditText limitChosenEdit = view.findViewById(R.id.editTextNumber2);
        EditText limitWaitingEdit = view.findViewById(R.id.editTextNumber);
        ImageView imagePreview = view.findViewById(R.id.imagePreview);
        ImageButton removeImageButton = view.findViewById(R.id.removeImageButton);
        Button uploadImageButton = view.findViewById(R.id.uploadImage_button);
        Button finishButton = view.findViewById(R.id.finish_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

//        EditText registrationStartEdit = view.findViewById(R.id.registration_start);
//        EditText registrationEndEdit = view.findViewById(R.id.registration_end);
//        EditText eventStartEdit = view.findViewById(R.id.event_start);
//        EditText eventEndEdit = view.findViewById(R.id.event_end);

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
            imagePreview.setVisibility(View.GONE);
            removeImageButton.setVisibility(View.GONE);
            imagePreview.setImageDrawable(null);
            pos = null;
            Toast.makeText(getContext(), "Image removed", Toast.LENGTH_SHORT).show();
        });

        finishButton.setOnClickListener(v -> {
            // Retrieve and trim input values
            String eventTitle = titleEdit.getText().toString().trim();
            String eventDescription = descriptionEdit.getText().toString().trim();
            String limitChosenString = limitChosenEdit.getText().toString().trim();
            String limitWaitingString = limitWaitingEdit.getText().toString().trim();

//            String regStart = registrationStartEdit.getText().toString().trim();
//            String regEnd = registrationEndEdit.getText().toString().trim();
//            String eventStart = eventStartEdit.getText().toString().trim();
//            String eventEnd = eventEndEdit.getText().toString().trim();

            boolean hasError = false;
            List<String> missingFields = new ArrayList<>();

            // Validate Event Title
            if (eventTitle.isEmpty()) {
                titleEdit.setError(getString(R.string.error_event_title_required));
                missingFields.add(getString(R.string.label_event_title));
                hasError = true;
            } else {
                titleEdit.setError(null);
            }

            // Validate Event Description
            if (eventDescription.isEmpty()) {
                descriptionEdit.setError(getString(R.string.error_event_description_required));
                missingFields.add(getString(R.string.label_event_description));
                hasError = true;
            } else {
                descriptionEdit.setError(null);
            }

            // Validate Limit Chosen
            int limitChosen = 0;
            if (limitChosenString.isEmpty()) {
                limitChosenEdit.setError(getString(R.string.error_limit_chosen_required));
                missingFields.add(getString(R.string.label_limit_chosen));
                hasError = true;
            } else {
                try {
                    limitChosen = Integer.parseInt(limitChosenString);
                    if (limitChosen <= 0) {
                        limitChosenEdit.setError(getString(R.string.error_limit_positive));
                        missingFields.add(getString(R.string.label_limit_chosen));
                        hasError = true;
                    } else {
                        limitChosenEdit.setError(null);
                    }
                } catch (NumberFormatException e) {
                    limitChosenEdit.setError(getString(R.string.error_limit_chosen_invalid));
                    missingFields.add(getString(R.string.label_limit_chosen));
                    hasError = true;
                }
            }

            // Validate Limit Waiting (Optional Field)
            int limitWaiting = 9999; // Default value
            if (!limitWaitingString.isEmpty()) {
                try {
                    limitWaiting = Integer.parseInt(limitWaitingString);
                    if (limitWaiting <= 0) {
                        limitWaitingEdit.setError(getString(R.string.error_limit_positive));
                        missingFields.add(getString(R.string.label_limit_waiting));
                        hasError = true;
                    } else {
                        limitWaitingEdit.setError(null);
                    }
                } catch (NumberFormatException e) {
                    limitWaitingEdit.setError(getString(R.string.error_limit_waiting_invalid));
                    missingFields.add(getString(R.string.label_limit_waiting));
                    hasError = true;
                }
            } else {
                // If the waiting limit is optional and left empty, remove any previous errors
                limitWaitingEdit.setError(null);
            }

//            if(!validDates(regStart, regEnd, eventStart, eventEnd)){
//                return;
//            }

            // If there are any validation errors, show a Toast and halt the process
            if (hasError) {
                String message;
                if (missingFields.size() == 1) {
                    message = "Please fill in the " + missingFields.get(0) + " field.";
                } else if (missingFields.size() > 1) {
                    String fields = TextUtils.join(", ", missingFields);
                    message = "Please fill in the following fields: " + fields + ".";
                } else {
                    // Fallback message if no specific fields are missing
                    message = getString(R.string.toast_fill_required_fields);
                }
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

            // Pass the event to the listener
            if (listener != null) {
                listener.onEventCreated(newEvent);
            }
            // Display the QR code for the created event
            String qrCodeHash = newEvent.getHashCodeQR(); // Replace with your QR code generation method
            QRCodeDialogFragment qrCodeDialog = QRCodeDialogFragment.newInstance(qrCodeHash);
            qrCodeDialog.show(getParentFragmentManager(), "QRCodeDialogFragment");

            dismiss(); // Close the dialog
        });

        cancelButton.setOnClickListener(v -> dismiss()); // Close the dialog if canceled

        return view;
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
            return MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
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

    protected boolean validDate(String date) {
        String regex = "^\\d{0,4}(-\\d{0,2})?(-\\d{0,2})?$";
        return date.length() == 10 && date.matches(regex);
    }

    protected boolean validPeriod(String start, String end) {
        LocalDate date1 = LocalDate.parse(start);
        LocalDate date2 = LocalDate.parse(end);
        return date1.isBefore(date2);
    }

//    protected boolean validDates(String regStart, String regEnd, String eventStart, String eventEnd) {
//        if (!validDate(regStart) || !validDate(regEnd) || !validDate(eventStart) || !validDate(eventEnd)) {
//            Toast.makeText(getContext(), "Use date format: YYYY-MM-DD", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (!validPeriod(regStart, regEnd)) {
//            Toast.makeText(getContext(), "Registration Start must precede Registration End", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if ( !validPeriod(eventStart, eventEnd)) {
//            Toast.makeText(getContext(), "Event Start must precede Event End", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if (!validPeriod(regEnd, eventStart)) {
//            Toast.makeText(getContext(), "Registration End must precede Event Start", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }

}