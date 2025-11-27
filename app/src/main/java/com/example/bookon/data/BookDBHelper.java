package com.example.bookon.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

public class BookDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "bookon.db";
    public static final int DB_VERSION = 1;

    public BookDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // 테이블 한번에 깔끔하게 정의
        db.execSQL(
                "CREATE TABLE book (" +
                        "   _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "   club_id INTEGER NOT NULL," +
                        "   title TEXT NOT NULL," +
                        "   author TEXT," +
                        "   owner_name TEXT" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS book");
        onCreate(db);
    }


    // 책 INSERT
    public long insertBook(long clubId, String title, String author, String ownerName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("club_id", clubId);
        values.put("title", title);
        values.put("author", author);
        values.put("owner_name", ownerName);

        return db.insert("book", null, values);
    }

    // 특정 모임의 책 불러오기
    public List<Book> getBooksByClub(long clubId) {
        List<Book> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT _id, club_id, title, author, owner_name FROM book WHERE club_id=? ORDER BY _id DESC",
                new String[]{ String.valueOf(clubId) }
        );

        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            long cId = cursor.getLong(1);
            String title = cursor.getString(2);
            String author = cursor.getString(3);
            String ownerName = cursor.getString(4);

            result.add(new Book(id, cId, title, author, ownerName));
        }
        cursor.close();

        return result;
    }
    // 책 삭제
    // 책 한 권 삭제 (_id 기준)
    public int deleteBook(long bookId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(
                "book",           // 테이블 이름
                "_id=?",          // where 절
                new String[]{ String.valueOf(bookId) }
        );
    }
}

