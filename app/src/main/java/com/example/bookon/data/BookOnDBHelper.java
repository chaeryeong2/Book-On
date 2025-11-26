package com.example.bookon.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookOnDBHelper extends SQLiteOpenHelper {

    // [수정] DB 구조가 바뀌었으므로 버전을 2로 올림 (1 -> 2)
    public BookOnDBHelper(Context context) {
        super(context, "BookOn.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // [수정] status와 current_book 컬럼 추가
        db.execSQL("CREATE TABLE clubs(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "capacity INTEGER," +
                "start_date TEXT," +
                "end_date TEXT," +
                "description TEXT," +
                "status TEXT," +       // 추가됨
                "current_book TEXT)"   // 추가됨
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS clubs");
        onCreate(db);
    }
}