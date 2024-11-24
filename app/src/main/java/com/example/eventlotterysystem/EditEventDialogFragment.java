package com.example.eventlotterysystem;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class EditEventDialogFragment extends DialogFragment {

    private EditText nameEditText;
    private EditText descriptionEditText;
    private Switch geolocationSwitch;

    private Event curEvent;

    public EditEventDialogFragment(Event event) {
        this.curEvent = event;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // Inflate the custom dialog layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_event, null);

        // Initialize fields
        nameEditText = dialogView.findViewById(R.id.edit_event_name);
        descriptionEditText = dialogView.findViewById(R.id.edit_event_description);
        geolocationSwitch = dialogView.findViewById(R.id.switch1);

        // Populate fields with current event data
        nameEditText.setText(curEvent.getName());
        descriptionEditText.setText(curEvent.getDescription());
        geolocationSwitch.setChecked(curEvent.getGeoSetting());

        builder.setView(dialogView)
                .setTitle("Edit Event")
                .setPositiveButton("Save", (dialog, id) -> saveChanges())
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        return builder.create();
    }


    private void saveChanges() {
        String newName = nameEditText.getText().toString();
        String newDescription = descriptionEditText.getText().toString();
        boolean newGeoRequirement = geolocationSwitch.isChecked();


        if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newDescription)) {
            Toast.makeText(getActivity(), "Invalid input, nothing is saved", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the event details
        curEvent.setName(newName);
        curEvent.setDescription(newDescription);
        curEvent.setGeoSetting(newGeoRequirement);

        // Call the activity's method to update the event details
        if (getActivity() instanceof ManageEventActivity) {
            ((ManageEventActivity) getActivity()).onEventUpdated();
        }
    }

}
