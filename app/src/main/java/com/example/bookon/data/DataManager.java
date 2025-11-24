package com.example.bookon.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DataManager {
    private static DataManager instance;
    private BookOnDBHelper dbHelper;
    private SQLiteDatabase database;

    private DataManager(Context context) {
        dbHelper = new BookOnDBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context.getApplicationContext());
        }
        return instance;
    }

    // insert
    public long addNewClub(Club club) {
        ContentValues values = new ContentValues();
        values.put("name", club.getName());
        values.put("capacity", club.getCapacity());
        values.put("start_date", club.getStartDate());
        values.put("end_date", club.getEndDate());
        values.put("description", club.getDescription());

        return database.insert("clubs", null, values);
    }

    // select
    public ArrayList<Club> getClubList() {
        ArrayList<Club> clubList = new ArrayList<>();

        Cursor cursor = database.query(
                "clubs",
                null,
                null, null, null, null,
                "_id DESC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("_id");
                int nameIndex = cursor.getColumnIndex("name");
                int capacityIndex = cursor.getColumnIndex("capacity");
                int startIndex = cursor.getColumnIndex("start_date");
                int endIndex = cursor.getColumnIndex("end_date");
                int descIndex = cursor.getColumnIndex("description");

                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                int capacity = cursor.getInt(capacityIndex);
                String startDate = cursor.getString(startIndex);
                String endDate = cursor.getString(endIndex);
                String description = cursor.getString(descIndex);

                Club club = new Club(name, capacity, startDate, endDate, description);
                club.setId(id);

                clubList.add(club);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return clubList;
    }
}