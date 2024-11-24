package com.example.eventlotterysystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanQRActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        // Start QR code scan
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setCameraId(0);  // Use front camera
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of the QR code scan
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan canceled", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    // Try to parse the event ID from the QR code contents
                    // Launch the event view activity
                    int eventID = Integer.parseInt(result.getContents());
                    Intent intent = new Intent(this, ViewEventActivity.class);
                    intent.putExtra("eventID", eventID);
                    startActivity(intent);
                } catch (NumberFormatException e) {
                    // Handle invalid format if the QR code does not contain a valid event ID
                    Toast.makeText(this, "Invalid QR code format", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
