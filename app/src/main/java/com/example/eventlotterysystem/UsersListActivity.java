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

public class UsersListActivity extends AppCompatActivity {

    private ListView usersListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> usersList;
    private ArrayList<User> users;
    private ArrayList<String> filteredUsersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        usersListView = findViewById(R.id.users_list_view);
        usersList = new ArrayList<>();
        filteredUsersList = new ArrayList<>();
        users = Control.getInstance().getUserList();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredUsersList);
        usersListView.setAdapter(adapter);


        usersList.clear();
        for (User u : users) {
            if (u.isValid()){
                usersList.add(u.getName());
            }
        }
        Collections.sort(usersList); // Sort facilities list alphabetically
        filterUsers(""); // Initialize the filtered list with all facilities


        usersListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUserName = filteredUsersList.get(position);
            User selectedUser = null;
            for (User user : users) {
                if (user.getName().equals(selectedUserName)) {
                    selectedUser = user;
                    break;
                }
            }
            if (selectedUser != null) {
                Intent intent = new Intent(UsersListActivity.this, AdminViewUserActivity.class);
                intent.putExtra("user", selectedUser);
                startActivity(intent);
            }
        });

//        usersListView.setOnItemLongClickListener((parent, view, position, id) -> {
//            String selectedUserName = filteredUsersList.get(position);
//            User selectedUser = users.stream().filter(user -> user.getName()
//                    .equals(selectedUserName)).findFirst().orElse(null);
//
//            if (selectedUser != null) {
//                new AlertDialog.Builder(UsersListActivity.this)
//                        .setTitle("Delete Profile")
//                        .setMessage("Are you sure you want to delete this profile?")
//                        .setPositiveButton("Delete", (dialog, which) -> {
//                            User currentUser = Control.getCurrentUser();
//                            if (currentUser != null) {
//                                //currentUser.adminDeleteUser(Control.getInstance(), selectedUser);
//                                FirestoreManager.getInstance().saveControl(Control.getInstance());
//                                filteredUsersList.remove(position);
//                                adapter.notifyDataSetChanged();
//                                Toast.makeText(UsersListActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
//                            }
//                        })
//                        .setNegativeButton("Cancel", null)
//                        .show();
//            }
//            return true;
//        });

        // Set up back button listener
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> finish());

        // Set up search functionality
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsers(newText);
                return false;
            }
        });
    }

    private void filterUsers(String query) {
        filteredUsersList.clear();
        for (String userName : usersList) {
            if (userName.toLowerCase().contains(query.toLowerCase())) {
                filteredUsersList.add(userName);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        users = Control.getInstance().getUserList(); // Get the latest list of users
        usersList.clear();

        // Add valid users to the main list
        for (User u : users) {
            if (u.isValid()) {
                usersList.add(u.getName());
            }
        }

        // Sort the users alphabetically
        Collections.sort(usersList);

        // Reapply the filter (if there's an active query)
        SearchView searchView = findViewById(R.id.search_view);
        String currentQuery = searchView.getQuery().toString();
        filterUsers(currentQuery);
    }
}