package com.example.bookon.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookOnDBHelper extends SQLiteOpenHelper {

    // [수정] 버전 4로 변경 (앱 재설치 필수!)
    public BookOnDBHelper(Context context) {
        super(context, "BookOn.db", null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE clubs(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "capacity INTEGER," +
                "start_date TEXT," +
                "end_date TEXT," +
                "description TEXT," +
                "status TEXT," +
                "current_book TEXT," +
                "owner_id TEXT)" // [수정] 0/1이 아니라 '아이디'를 저장
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS clubs");
        onCreate(db);
    }
}