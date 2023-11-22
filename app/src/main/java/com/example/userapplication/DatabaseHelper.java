package com.example.userapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "UserDatabase";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_IMAGE = "image";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_GENDER + " TEXT," +
                COLUMN_IMAGE + " BLOB" +
                ");";

        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_EMAIL,
                COLUMN_DATE,
                COLUMN_GENDER,
                COLUMN_IMAGE
        };

        Cursor cursor = db.query(
                TABLE_USERS,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));

                User user = new User(id, name, email, date, gender, image);
                userList.add(user);
            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return userList;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_DATE, user.getDate());
        values.put(COLUMN_GENDER, user.getGender());
        values.put(COLUMN_IMAGE, user.getImage());

        int rowsUpdated = db.update(
                TABLE_USERS,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(user.getId())}
        );

        db.close();
        return rowsUpdated;
    }

    public void deleteUser(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, COLUMN_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public User getUserById(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS, // Table name
                new String[] { COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_GENDER, COLUMN_IMAGE }, // Columns to retrieve
                COLUMN_ID + "=?", // Selection criteria
                new String[] { String.valueOf(userId) }, // Selection arguments
                null, // Group by
                null, // Having
                null, // Order by
                null  // Limit
        );

        User user = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                user = new User();
                user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
                user.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                user.setGender(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER)));
                byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));
                user.setImage(imageBytes);
            }
            cursor.close();
        }

        db.close();

        return user;
    }
}

