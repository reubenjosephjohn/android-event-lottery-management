package com.example.eventlotterysystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * An activity that initiates a QR code scan and handles the scanned results.
 * The QR code is expected to contain an event ID, which is used to navigate to the corresponding event details.
 */
public class ScanQRActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created. Initiates a QR code scan using the ZXing library.
     *
     * @param savedInstanceState If the activity is being reinitialized after previously being shut down, this contains the data most recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        // Start QR code scan
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setCameraId(0);  // Use the default camera (0 for back, 1 for front)
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    /**
     * Handles the result of the QR code scan. Parses the QR code contents to find an event ID,
     * and navigates to the event details if the event is found.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of the QR code scan
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // QR code scan was canceled
                Toast.makeText(this, "Scan canceled", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    // Parse the event ID from the QR code contents
                    int eventID = Integer.parseInt(result.getContents());
                    boolean eventFound = false;

                    // Check if the event ID matches any event in the event list
                    for (Event event : Control.getInstance().getEventList()) {
                        if (event.getEventID() == eventID) {
                            // Launch the ViewEventActivity with the event ID
                            Intent intent = new Intent(this, ViewEventActivity.class);
                            intent.putExtra("eventID", eventID);
                            startActivity(intent);
                            eventFound = true;
                            break;
                        }
                    }

                    if (!eventFound) {
                        // Display message if event is not found
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    // Handle invalid QR code format
                    Toast.makeText(this, "Invalid QR code format", Toast.LENGTH_SHORT).show();
                }

                // Finish the activity after handling the result
                finish();
            }
        } else {
            // Call the super method for unhandled results
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
