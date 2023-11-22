package com.example.userapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class EditUserActivity extends AppCompatActivity {
    private EditText editName, editEmail;
    private DatePicker editDate;
    private ImageView editImageView;
    private long userId;
    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton, femaleRadioButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        editName = findViewById(R.id.editTextEditName);
        editEmail = findViewById(R.id.editTextEditEmail);
        editDate = findViewById(R.id.editDatePicker);
        genderRadioGroup = findViewById(R.id.editGenderRadioGroup);
        maleRadioButton = findViewById(R.id.maleRadioButton);
        femaleRadioButton = findViewById(R.id.femaleRadioButton);
        editImageView = findViewById(R.id.imageViewEdit);

        userId = getIntent().getLongExtra("USER_ID", -1);
        dbHelper = new DatabaseHelper(this);

        if (userId != -1) {
            // Load user data from the database and populate the UI
            User user = getUserData(userId);
            editName.setText(user.getName());
            editEmail.setText(user.getEmail());
            setDatePickerDate(editDate, user.getDate());
            String gender = user.getGender();

            if ("Male".equals(gender)) {
                maleRadioButton.setChecked(true);
            } else if ("Female".equals(gender)) {
                femaleRadioButton.setChecked(true);
            }

            editImageView.setImageBitmap(BitmapFactory.decodeByteArray(user.getImage(), 0, user.getImage().length));
        }

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Return to the main activity
                Intent intent = new Intent(EditUserActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void saveEditedData(View view) {
        String name = editName.getText().toString();
        String email = editEmail.getText().toString();
        String date = getSelectedDate(editDate);
        String gender = getSelectedGender();
        byte[] image = imageViewToByteArray(editImageView);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, name);
        values.put(DatabaseHelper.COLUMN_EMAIL, email);
        values.put(DatabaseHelper.COLUMN_DATE, date);
        values.put(DatabaseHelper.COLUMN_GENDER, gender);
        values.put(DatabaseHelper.COLUMN_IMAGE, image);

        int rowsUpdated = db.update(
                DatabaseHelper.TABLE_USERS,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(userId)}
        );

        db.close();

        if (rowsUpdated > 0) {
            Toast.makeText(this, "Data updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error updating data", Toast.LENGTH_SHORT).show();
        }
    }

    private String getSelectedDate(DatePicker datePicker) {
        int year = datePicker.getYear();
        int month = datePicker.getMonth() + 1;
        int day = datePicker.getDayOfMonth();
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    private User getUserData(long userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseHelper.COLUMN_NAME,
                DatabaseHelper.COLUMN_EMAIL,
                DatabaseHelper.COLUMN_DATE,
                DatabaseHelper.COLUMN_GENDER,
                DatabaseHelper.COLUMN_IMAGE
        };

        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

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
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
            String gender = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GENDER));
            byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE));

            cursor.close();

            User user = new User();
            user.setId(userId);
            user.setName(name);
            user.setEmail(email);
            user.setDate(date);
            user.setGender(gender);
            user.setImage(image);

            return user;
        }

        return null;
    }

    private void setDatePickerDate(DatePicker datePicker, String date) {
        String[] dateParts = date.split("-");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1; // Month is 0-based
        int day = Integer.parseInt(dateParts[2]);
        datePicker.init(year, month, day, null);
    }

    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                editImageView.setImageBitmap(imageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] imageViewToByteArray(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    private String getSelectedGender() {
        int selectedRadioButtonId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);

        if (selectedRadioButton != null) {
            return selectedRadioButton.getText().toString();
        } else {
            return ""; // Default value when no gender is selected
        }
    }
}
