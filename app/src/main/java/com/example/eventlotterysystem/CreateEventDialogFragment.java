package com.example.eventlotterysystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Previous UI approach to creating an event
 */
public class CreateEventDialogFragment extends DialogFragment {

    private CreateEventListener listener;
    private User curUser;

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

        Button uploadImageButton = view.findViewById(R.id.uploadImage_button);
        Button finishButton = view.findViewById(R.id.finish_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        uploadImageButton.setOnClickListener(v -> {
                    Toast.makeText(getContext(), "Coming soon in part 4!", Toast.LENGTH_SHORT).show();
                });

        finishButton.setOnClickListener(v -> {
            // Create a new Event using user input
            String eventTitle = titleEdit.getText().toString().trim();
            String eventDescription = descriptionEdit.getText().toString().trim();
            String limitChosenString = limitChosenEdit.getText().toString().trim();
            String limitWaitingString = limitWaitingEdit.getText().toString().trim();

            if (eventTitle.isEmpty() || eventDescription.isEmpty() || limitChosenString.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int limitChosen = Integer.parseInt(limitChosenString);
            int limitWaiting = limitWaitingString.isEmpty() ? 9999 : Integer.parseInt(limitWaitingString);

            if (limitChosen <= 0 || limitWaiting <= 0) {
                Toast.makeText(getContext(), "Limits must be greater than zero.", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean geo = locationSwitch.isChecked();
            Event newEvent = new Event(Control.getInstance().getCurrentEventIDForEventCreation(), eventTitle, eventDescription, limitChosen, limitWaiting, geo);
            newEvent.setCreatorRef(curUser.getUserID());
            Control.getInstance().getEventList().add(newEvent);
            Control.getInstance().saveEvent(newEvent);
            newEvent.generateQR();

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
}
