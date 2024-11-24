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

public class QRCodeDialogFragment extends DialogFragment {

    private static final String ARG_HASH_CODE = "hashCodeQR";

    /**
     * Factory method to create a new instance of QRCodeDialogFragment with the event's hash code.
     * @param hashCodeQR the QR code hash string of the event
     * @return an instance of QRCodeDialogFragment
     */
    public static QRCodeDialogFragment newInstance(String hashCodeQR) {
        QRCodeDialogFragment fragment = new QRCodeDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HASH_CODE, hashCodeQR);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_qrcode, container, false);

        // Retrieve the QR code hash from arguments
        String hashCodeQR = getArguments().getString(ARG_HASH_CODE);

        // Find UI components
        ImageView imageViewQRCode = view.findViewById(R.id.imageViewQRCode);
        TextView textViewHash = view.findViewById(R.id.textViewHash);

        // Display hash code in the text view
        textViewHash.setVisibility(view.GONE);

        // Generate and display QR code based on hashCodeQR
        Bitmap qrCodeBitmap = decodeBitmap(hashCodeQR);
        if (qrCodeBitmap != null) {
            imageViewQRCode.setImageBitmap(qrCodeBitmap);
        }

        return view;
    }
    private Bitmap decodeBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
