package com.example.eventlotterysystem;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * NotifyFragment is a dialog fragment that prompts the user to input a notification message.
 * The message is sent to an implementing listener when the user confirms the action.
 */
public class NotifyFragment extends DialogFragment {

    /**
     * Listener interface for notifying when the user submits a message.
     */
    public interface NotificationListener {
        /**
         * Called when the user submits a notification message.
         *
         * @param message The notification message entered by the user.
         */
        void onNotify(String message);
    }

    /** Listener for handling the notification action */
    private NotificationListener listener;

    /**
     * Called when the fragment is attached to the context.
     * Ensures the hosting activity or fragment implements NotificationListener.
     *
     * @param context The context to which the fragment is attached.
     * @throws ClassCastException if the context does not implement NotificationListener.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NotificationListener) {
            listener = (NotificationListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement NotificationListener");
        }
    }

    /**
     * Creates the dialog view and handles user input for the notification message.
     * Sets up the "Notify" button to send the message to the listener and the "Cancel" button to dismiss the dialog.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     * @return A new AlertDialog instance with a custom view for message input.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View dialogView = requireActivity().getLayoutInflater().inflate(R.layout.dialog_list_manage_notify, null);

        // Reference the correct EditText within TextInputLayout
        EditText messageEditText = dialogView.findViewById(R.id.textInputEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(dialogView)
                .setTitle("Notify List?")
                .setPositiveButton("Notify", (dialog, id) -> {
                    String message = messageEditText.getText().toString().trim();
                    if (listener != null) {
                        listener.onNotify(message);
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        return builder.create();
    }
}