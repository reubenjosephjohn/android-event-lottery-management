package com.example.eventlotterysystem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * A DialogFragment that displays a QR code generated from an encoded hash string.
 * The QR code represents the event's unique identifier.
 */
public class QRCodeDialogFragment extends DialogFragment {

    private static final String ARG_HASH_CODE = "hashCodeQR";

    /**
     * Factory method to create a new instance of QRCodeDialogFragment with the event's hash code.
     *
     * @param hashCodeQR The QR code hash string of the event.
     * @return A new instance of QRCodeDialogFragment.
     */
    public static QRCodeDialogFragment newInstance(String hashCodeQR) {
        QRCodeDialogFragment fragment = new QRCodeDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HASH_CODE, hashCodeQR);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Inflates the layout for the fragment and initializes the QR code display.
     *
     * @param inflater  The LayoutInflater object used to inflate the layout.
     * @param container The parent view the fragment UI is attached to, or null.
     * @param savedInstanceState A bundle containing the saved state, or null.
     * @return The root view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_qrcode, container, false);

        // Retrieve the QR code hash from arguments
        String hashCodeQR = getArguments() != null ? getArguments().getString(ARG_HASH_CODE) : null;

        // Find UI components
        ImageView imageViewQRCode = view.findViewById(R.id.imageViewQRCode);
        TextView textViewHash = view.findViewById(R.id.textViewHash);

        // Optionally display the hash code in the TextView (hidden by default)
        textViewHash.setVisibility(View.GONE);

        // Generate and display the QR code based on hashCodeQR
        Bitmap qrCodeBitmap = decodeBitmap(hashCodeQR);
        if (qrCodeBitmap != null) {
            imageViewQRCode.setImageBitmap(qrCodeBitmap);
        }

        return view;
    }

    /**
     * Decodes a Base64-encoded string into a Bitmap image.
     *
     * @param encodedImage The Base64-encoded string representing the QR code image.
     * @return A Bitmap representation of the QR code, or null if decoding fails.
     */
    private Bitmap decodeBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
