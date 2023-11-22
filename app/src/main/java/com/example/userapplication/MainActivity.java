package com.example.userapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView userListView;
    private List<User> userList;
    private UserListAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userListView = findViewById(R.id.userListView);
        userList = new ArrayList<>();
        adapter = new UserListAdapter(this, userList);
        dbHelper = new DatabaseHelper(this);

        // Load and display user data
        loadUsersFromDatabase();

        userListView.setAdapter(adapter);

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = userList.get(position);
                editUser(selectedUser);
            }
        });
    }

    private void loadUsersFromDatabase() {
        userList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_NAME,
                DatabaseHelper.COLUMN_EMAIL,
                DatabaseHelper.COLUMN_DATE,
                DatabaseHelper.COLUMN_GENDER,
                DatabaseHelper.COLUMN_IMAGE
        };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GENDER));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE));

                User user = new User();
                user.setId(id);
                user.setName(name);
                user.setEmail(email);
                user.setDate(date);
                user.setGender(gender);
                user.setImage(image);

                userList.add(user);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        adapter.notifyDataSetChanged();
    }

    public void addUser(View view) {
        Intent intent = new Intent(this, AddUserActivity.class);
        startActivity(intent);
    }

    public void editUser(User user) {
        Intent intent = new Intent(this, EditUserActivity.class);
        intent.putExtra("USER_ID", user.getId());
        startActivity(intent);
    }

    public void deleteUser(View view) {
        View parent = (View) view.getParent();
        int position = userListView.getPositionForView(parent);
        User user = userList.get(position);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsDeleted = db.delete(
                DatabaseHelper.TABLE_USERS,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(user.getId())}
        );

        if (rowsDeleted > 0) {
            Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
            userList.remove(position);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Error deleting user", Toast.LENGTH_SHORT).show();
        }
    }

    public void showAllUsers(View view) {
        loadUsersFromDatabase();
    }
}
