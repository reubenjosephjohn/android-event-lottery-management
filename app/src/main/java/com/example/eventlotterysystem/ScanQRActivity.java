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
        integrator.setCameraId(0);  // Use front camera
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
                Toast.makeText(this, "Scan canceled", Toast.LENGTH_SHORT).show();
            } else {
                boolean eventFound = false;
                try {
                    // Try to parse the event ID from the QR code contents
                    // Launch the event view activity
                    int eventID = Integer.parseInt(result.getContents());
                    for (Event event : Control.getInstance().getEventList()) {
                        if (event.getEventID() == eventID) {
                            eventFound = true;
                            Intent intent = new Intent(this, ViewEventActivity.class);
                            intent.putExtra("eventID", eventID);
                            startActivity(intent);
                            break;
                        }
                    }
                    if (!eventFound) {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                    finish();

                } catch (NumberFormatException e) {
                    // Handle invalid format if the QR code does not contain a valid event ID
                    Toast.makeText(this, "Invalid QR code format", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}