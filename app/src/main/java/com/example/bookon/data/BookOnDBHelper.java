package com.example.bookon.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// 모임 및 도서 정보를 저장하는 메인 DBhelper
public class BookOnDBHelper extends SQLiteOpenHelper {

    //DB 구조 변경 시 버전을 올려야 함. 현재 ver.10
    public BookOnDBHelper(Context context) {
        super(context, "BookOn.db", null, 10);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. 모임 테이블
        db.execSQL("CREATE TABLE clubs(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "capacity INTEGER," +
                "start_date TEXT," +
                "end_date TEXT," +
                "description TEXT," +
                "status TEXT," +
                "topic TEXT," +
                "owner_id TEXT," +
                "schedule_start TEXT," +
                "cycle_weeks INTEGER)"
        );
        // 2. 멤버 테이블
        db.execSQL(
                "CREATE TABLE members (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id TEXT NOT NULL," +
                        "club_id INTEGER NOT NULL," +
                        "sequence INTEGER DEFAULT 0" +
                        ")"
        );

        // 3. 책 테이블
        db.execSQL("CREATE TABLE book (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "club_id INTEGER NOT NULL," +
                "title TEXT NOT NULL," +
                "author TEXT," +
                "owner_name TEXT," +
                "owner_id TEXT," +
                "sequence INTEGER DEFAULT 0)" // [추가] 여기에 순서 저장
        );
    }

    // DB 버전이 변경될 때 호출되어 기존 테이블을 삭제하고 새로 생성함.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS clubs");
        db.execSQL("DROP TABLE IF EXISTS members");
        db.execSQL("DROP TABLE IF EXISTS book");
        onCreate(db);
    }
}