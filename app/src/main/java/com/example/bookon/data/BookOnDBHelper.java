package com.example.bookon.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookOnDBHelper extends SQLiteOpenHelper {

    // [수정] 버전 5로 변경
    public BookOnDBHelper(Context context) {
        super(context, "BookOn.db", null, 5);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. 모임 테이블 (기존)
        db.execSQL("CREATE TABLE clubs(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "capacity INTEGER," +
                "start_date TEXT," +
                "end_date TEXT," +
                "description TEXT," +
                "status TEXT," +
                "current_book TEXT," +
                "owner_id TEXT)"
        );

        // 2. [추가] 멤버 테이블 (누가 어느 모임에 가입했는지 저장)
        db.execSQL("CREATE TABLE members(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id TEXT," +
                "club_id INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS clubs");
        onCreate(db);
    }
}