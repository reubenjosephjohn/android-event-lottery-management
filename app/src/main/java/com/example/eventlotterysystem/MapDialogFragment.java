package com.example.eventlotterysystem;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;

/**
 * A DialogFragment that displays a Google Map with markers representing event locations.
 * This fragment allows users to view a map with the locations associated with a given event.
 */
public class MapDialogFragment extends DialogFragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private Event curEvent;

    /**
     * Constructs a MapDialogFragment with the specified event.
     *
     * @param event The event object containing location data to display on the map.
     */
    public MapDialogFragment(Event event) {
        this.curEvent = event;
    }

    /**
     * Inflates the layout for the fragment and initializes the MapView.
     *
     * @param inflater  The LayoutInflater object used to inflate the layout.
     * @param container The parent view the fragment UI is attached to, or null.
     * @param savedInstanceState A bundle containing the saved state, or null.
     * @return The root view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_map, container, false);

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        MapsInitializer.initialize(requireActivity().getApplicationContext());
        mapView.getMapAsync(this);

        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    /**
     * Called when the Google Map is ready to use.
     *
     * @param map The GoogleMap object.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Center the map on a default global view.
        LatLng defaultLocation = new LatLng(20.0, 0.0); // Centered at a global view.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 2));

        // Customize marker appearance with red color and larger icon.
        BitmapDescriptor redMarkerIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);

        // Get lists of latitude and longitude from the event object.
        List<Double> latitudeList = curEvent.getLatitudeList();
        List<Double> longitudeList = curEvent.getLongitudeList();

        // Loop through the lists and add markers for each pair of coordinates.
        for (int i = 0; i < latitudeList.size() && i < longitudeList.size(); i++) {
            double latitude = latitudeList.get(i);
            double longitude = longitudeList.get(i);
            LatLng location = new LatLng(latitude, longitude);

            // Add a marker for each location with red color.
            googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title("Entrant Location")
                    .icon(redMarkerIcon));
        }
    }

    /**
     * Sets the size of the dialog to 90% of the screen width and height.
     */
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.9);
            params.height = (int) (requireContext().getResources().getDisplayMetrics().heightPixels * 0.9);
            dialog.getWindow().setAttributes(params);
        }
    }

    /**
     * Resumes the MapView when the fragment resumes.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    /**
     * Pauses the MapView when the fragment is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }

    /**
     * Destroys the MapView when the fragment is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.onDestroy();
    }

    /**
     * Handles low memory situations by notifying the MapView.
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }
}
