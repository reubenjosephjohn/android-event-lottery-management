package com.example.eventlotterysystem;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A DialogFragment that allows users to edit facility details such as name and description.
 */
public class EditFacilityFragment extends DialogFragment {

    private EditText nameEditText, descriptionEditText;
    private OnFacilityUpdatedListener facilityUpdatedListener;

    /**
     * Interface definition for a callback to be invoked when the facility is updated.
     */
    public interface OnFacilityUpdatedListener {
        /**
         * Called when the facility has been updated.
         *
         * @param name        The updated name of the facility.
         * @param description The updated description of the facility.
         */
        void onFacilityUpdated(String name, String description);
    }

    /**
     * Sets the listener that will be notified when the facility is updated.
     *
     * @param listener The listener to notify.
     */
    public void setOnFacilityUpdatedListener(OnFacilityUpdatedListener listener) {
        this.facilityUpdatedListener = listener;
    }

    /**
     * Creates a new instance of EditFacilityFragment with the provided name and description.
     *
     * @param name        The current name of the facility.
     * @param description The current description of the facility.
     * @return A new instance of EditFacilityFragment.
     */
    public static EditFacilityFragment newInstance(String name, String description) {
        EditFacilityFragment fragment = new EditFacilityFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("description", description);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Inflates the layout for the fragment, initializes the UI elements, and sets up click listeners.
     * Handles the form validation and triggers the facility updated listener.
     *
     * @param inflater           The LayoutInflater used to inflate the fragment's view.
     * @param container          The parent container to attach the fragment's view.
     * @param savedInstanceState A saved instance state bundle (optional).
     * @return The view for the fragment.
     */
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_facility_fragment, container, false);

        nameEditText = view.findViewById(R.id.facility_name_edit);
        descriptionEditText = view.findViewById(R.id.facility_description_edit);

        if (getArguments() != null) {
            nameEditText.setText(getArguments().getString("name"));
            descriptionEditText.setText(getArguments().getString("description"));
        }

        Button finishButton = view.findViewById(R.id.finish_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        finishButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            List<String> missingFields = new ArrayList<>();
            boolean hasError = false;

            // Validate facility name
            if (name.isEmpty()) {
                nameEditText.setError("Facility Name is required");
                missingFields.add("Facility Name");
                hasError = true;
            } else {
                nameEditText.setError(null);
            }

            // Validate facility description
            if (description.isEmpty()) {
                descriptionEditText.setError("Facility Description is required");
                missingFields.add("Facility Description");
                hasError = true;
            } else {
                descriptionEditText.setError(null);
            }

            // Show a Toast message if validation failed
            if (hasError) {
                String message;
                if (missingFields.size() == 1) {
                    message = "Please fill in the " + missingFields.get(0) + " field.";
                } else {
                    String fields = TextUtils.join(", ", missingFields);
                    message = "Please fill in the following fields: " + fields + ".";
                }
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                return;
            }

            // Notify the listener with the updated facility details
            if (facilityUpdatedListener != null) {
                facilityUpdatedListener.onFacilityUpdated(name, description);
            }

            // Dismiss the dialog
            dismiss();
        });

        // Cancel button action
        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    /**
     * Adjusts the size of the dialog window to 99% of the screen width and wrap content for height.
     */
    @Override
    public void onResume() {
        super.onResume();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.99);
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setLayout(width, height);
    }
}
