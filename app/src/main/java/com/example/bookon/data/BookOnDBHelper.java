package com.example.bookon.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookOnDBHelper extends SQLiteOpenHelper {
    // 버전 업 (5 -> 6)
    public BookOnDBHelper(Context context) {
        super(context, "BookOn.db", null, 6);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. 모임 테이블 (일정 관련 컬럼 추가)
        db.execSQL("CREATE TABLE clubs(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "capacity INTEGER," +
                "start_date TEXT," + // 이건 모임 생성일 (기존)
                "end_date TEXT," +
                "description TEXT," +
                "status TEXT," +
                "current_book TEXT," +
                "owner_id TEXT," +
                "schedule_start TEXT," +  // [추가] 독서 시작일 (YYYY-MM-DD)
                "cycle_weeks INTEGER)"    // [추가] 교환 주기 (주 단위)
        );

        // 2. 멤버 테이블 (순서 컬럼 추가)
        db.execSQL("CREATE TABLE members(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id TEXT," +
                "club_id INTEGER," +
                "sequence INTEGER DEFAULT 0)" // [추가] 교환 순서 (1, 2, 3...)
        );

        // 3. 책 테이블 (기존 유지)
        db.execSQL("CREATE TABLE book (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "club_id INTEGER NOT NULL," +
                "title TEXT NOT NULL," +
                "author TEXT," +
                "owner_name TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS clubs");
        db.execSQL("DROP TABLE IF EXISTS members");
        db.execSQL("DROP TABLE IF EXISTS book");
        onCreate(db);
    }
}