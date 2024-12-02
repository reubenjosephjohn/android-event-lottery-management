package com.example.eventlotterysystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * UserAdapter is a custom ArrayAdapter for displaying User objects in a ListView.
 */
public class UserAdapter extends ArrayAdapter<User> {
    private Context context;
    private List<User> users;

    /**
     * Constructs a new UserAdapter.
     *
     * @param context The current context.
     * @param users The list of User objects to display.
     */
    public UserAdapter(Context context, List<User> users) {
        super(context, 0, users);
        this.context = context;
        this.users = users;
    }


    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.).
     *
     * @param position The position of the item within the adapter's data set of the item whose view we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        }

        TextView userName = convertView.findViewById(R.id.user_name);
        userName.setText(user.getName());

        return convertView;
    }
}