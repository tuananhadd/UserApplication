package com.example.userapplication;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class UserListAdapter extends BaseAdapter {
    private Context context;
    private List<User> userList;
    private DatabaseHelper dbHelper;

    public UserListAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return userList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        ImageView userImageView = convertView.findViewById(R.id.userImageView);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);
        Button updateButton = convertView.findViewById(R.id.updateButton);
        Button viewButton = convertView.findViewById(R.id.viewButton);

        final User user = userList.get(position);

        nameTextView.setText(user.getName());
        userImageView.setImageBitmap(BitmapFactory.decodeByteArray(user.getImage(), 0, user.getImage().length));

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser(user.getId(), user);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditUserActivity.class);
                intent.putExtra("USER_ID", user.getId());
                context.startActivity(intent);
            }
        });

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewUser(user);
            }
        });

        return convertView;
    }

    private void deleteUser(long userId, User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsDeleted = db.delete(
                DatabaseHelper.TABLE_USERS,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );

        db.close();

        if (rowsDeleted > 0) {
            userList.remove(user);
            notifyDataSetChanged();
            Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Error deleting user", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUser(User user) {
        Intent intent = new Intent(context, EditUserActivity.class);
        intent.putExtra("USER_ID", user.getId());
        context.startActivity(intent);
    }

    private void viewUser(User user) {
        Intent intent = new Intent(context, ViewUserActivity.class);
        intent.putExtra("USER_ID", user.getId());
        context.startActivity(intent);
    }

    public void setData(List<User> data) {
        userList = data;
    }

}
