package com.example.userapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ViewUserActivity extends AppCompatActivity {
    private ImageView userImageView;
    private TextView nameTextView, emailTextView, dateTextView,genderTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        userImageView = findViewById(R.id.userImageView);
        nameTextView = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        dateTextView = findViewById(R.id.dateTextView);
        genderTextView = findViewById(R.id.genderTextView);

        long userId = getIntent().getLongExtra("USER_ID", -1);

        User user = getUserData(userId);

        if (user != null) {
            // Display user details
            nameTextView.setText("Name: " + user.getName());
            emailTextView.setText("Email: " + user.getEmail());
            dateTextView.setText("Birthday: " + user.getDate());
            genderTextView.setText("Gender: " + user.getGender());

            byte[] imageBytes = user.getImage();
            if (imageBytes != null) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                userImageView.setImageBitmap(imageBitmap);
            }
            Button backButton = findViewById(R.id.backButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Return to the main activity
                    Intent intent = new Intent(ViewUserActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private User getUserData(long userId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;

        String[] projection = {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_NAME,
                DatabaseHelper.COLUMN_EMAIL,
                DatabaseHelper.COLUMN_DATE,
                DatabaseHelper.COLUMN_GENDER,
                DatabaseHelper.COLUMN_IMAGE
        };

        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
            String gender = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GENDER));
            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE));

            // Create a User object with the retrieved data
            user = new User(id, name, email, date, gender, imageBytes);

            cursor.close();


        }

        db.close();

        return user;
    }
}
