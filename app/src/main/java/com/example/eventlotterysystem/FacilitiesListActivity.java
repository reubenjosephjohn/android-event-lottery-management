package com.example.eventlotterysystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Activity to display the list of facilities in the system.
 * Allows users to search, view details, and manage facilities.
 */
public class FacilitiesListActivity extends AppCompatActivity {

    private ListView facilitiesListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> facilitiesList;
    private ArrayList<Facility> facilities;
    private ArrayList<String> filteredFacilitiesList;

    /**
     * Initializes the activity and sets up the views, adapters, and event listeners.
     * Loads the facilities list, applies filtering, and handles item clicks.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facilities_list);

        facilitiesListView = findViewById(R.id.facilities_list_view);
        facilitiesList = new ArrayList<>();
        filteredFacilitiesList = new ArrayList<>();
        facilities = Control.getInstance().getFacilityList();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredFacilitiesList);
        facilitiesListView.setAdapter(adapter);

        // Populate the list with facility names and sort them alphabetically
        facilitiesList.clear();
        for (Facility facility : facilities) {
            facilitiesList.add(facility.getName());
        }
        Collections.sort(facilitiesList); // Sort facilities list alphabetically
        filterFacilities(""); // Initialize the filtered list with all facilities

        // Handle item click to view details of the selected facility
        facilitiesListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFacilityName = filteredFacilitiesList.get(position);
            Facility selectedFacility = null;
            for (Facility facility : facilities) {
                if (facility.getName().equals(selectedFacilityName)) {
                    selectedFacility = facility;
                    break;
                }
            }
            if (selectedFacility != null) {
                Intent intent = new Intent(FacilitiesListActivity.this, AdminViewFacilityActivity.class);
                intent.putExtra("facility", selectedFacility); // Pass facility object to next activity
                startActivity(intent);
            }
        });

        // Set up back button listener to finish the activity
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> finish());

        // Set up search functionality to filter facilities by name
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterFacilities(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterFacilities(newText);
                return false;
            }
        });
    }

    /**
     * Filters the facilities list based on the query.
     * Updates the displayed list with matching facility names.
     *
     * @param query The search query.
     */
    private void filterFacilities(String query) {
        filteredFacilitiesList.clear();
        for (String facilityName : facilitiesList) {
            if (facilityName.toLowerCase().contains(query.toLowerCase())) {
                filteredFacilitiesList.add(facilityName);
            }
        }
        adapter.notifyDataSetChanged(); // Notify adapter to refresh the list
    }

    /**
     * Resumes the activity and updates the facilities list.
     * Re-applies the filter based on the current search query.
     */
    @Override
    protected void onResume() {
        super.onResume();
        facilities = Control.getInstance().getFacilityList(); // Get the latest list of facilities
        facilitiesList.clear();

        // Add facility names to the main list
        for (Facility u : facilities) {
            facilitiesList.add(u.getName());
        }

        // Sort the facilities alphabetically
        Collections.sort(facilitiesList);

        // Reapply the filter (if there's an active query)
        SearchView searchView = findViewById(R.id.search_view);
        String currentQuery = searchView.getQuery().toString();
        filterFacilities(currentQuery);
    }
}
