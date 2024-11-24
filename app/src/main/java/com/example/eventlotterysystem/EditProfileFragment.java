package com.example.eventlotterysystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * A DialogFragment that allows users to edit their profile details such as name, email, and contact.
 */
public class EditProfileFragment extends DialogFragment {

    private EditText nameEditText, emailEditText, contactEditText;
    private OnProfileUpdatedListener profileUpdatedListener;

    /**
     * Interface definition for a callback to be invoked when the profile is updated.
     */
    public interface OnProfileUpdatedListener {
        /**
         * Called when the profile has been updated.
         *
         * @param name    The updated name of the user.
         * @param email   The updated email of the user.
         * @param contact The updated contact information of the user.
         */
        void onProfileUpdated(String name, String email, String contact);
    }

    /**
     * Sets the listener that will be notified when the profile is updated.
     *
     * @param listener The listener to notify.
     */
    public void setOnProfileUpdatedListener(OnProfileUpdatedListener listener) {
        this.profileUpdatedListener = listener;
    }

    /**
     * Creates a new instance of EditProfileFragment with the provided user details.
     *
     * @param name    The current name of the user.
     * @param email   The current email of the user.
     * @param contact The current contact information of the user.
     * @return A new instance of EditProfileFragment.
     */
    public static EditProfileFragment newInstance(String name, String email, String contact) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("email", email);
        args.putString("contact", contact);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_user_fragment, container, false);

        nameEditText = view.findViewById(R.id.name);
        emailEditText = view.findViewById(R.id.email);
        contactEditText = view.findViewById(R.id.user_contact);

        if (getArguments() != null) {
            nameEditText.setText(getArguments().getString("name"));
            emailEditText.setText(getArguments().getString("email"));
            contactEditText.setText(getArguments().getString("contact"));
        }
        if ("000-000-0000".equals(getArguments().getString("contact"))){
            nameEditText.setText(getArguments().getString(""));
            emailEditText.setText(getArguments().getString(""));
            contactEditText.setText(getArguments().getString(""));
        }

        Button finishButton = view.findViewById(R.id.finish_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        finishButton.setOnClickListener(v -> {
            if (profileUpdatedListener != null) {
                profileUpdatedListener.onProfileUpdated(
                        nameEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        contactEditText.getText().toString()
                );
            }
            dismiss();
        });

        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Set dialog size to 99% of screen width and wrap content for height
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.99);
        int height = WindowManager.LayoutParams.WRAP_CONTENT; // Adjust height as needed
        getDialog().getWindow().setLayout(width, height);
    }
}