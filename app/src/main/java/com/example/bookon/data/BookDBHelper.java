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
    public static final int DB_VERSION = 2; // [수정] 버전 올림 (1 -> 2)

    public BookDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE book (" +
                        "   _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "   club_id INTEGER NOT NULL," +
                        "   title TEXT NOT NULL," +
                        "   author TEXT," +
                        "   owner_name TEXT," +
                        "   owner_id TEXT" + // [추가] 아이디 저장 컬럼
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS book");
        onCreate(db);
    }

    // [수정] 인자에 ownerId 추가
    public long insertBook(long clubId, String title, String author, String ownerName, String ownerId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("club_id", clubId);
        values.put("title", title);
        values.put("author", author);
        values.put("owner_name", ownerName);
        values.put("owner_id", ownerId); // [추가]

        return db.insert("book", null, values);
    }

    public List<Book> getBooksByClub(long clubId) {
        List<Book> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        // [수정] owner_id도 같이 조회
        Cursor cursor = db.rawQuery(
                "SELECT _id, club_id, title, author, owner_name, owner_id FROM book WHERE club_id=? ORDER BY _id DESC",
                new String[]{ String.valueOf(clubId) }
        );

        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            long cId = cursor.getLong(1);
            String title = cursor.getString(2);
            String author = cursor.getString(3);
            String ownerName = cursor.getString(4);
            String ownerId = cursor.getString(5); // [추가]

            result.add(new Book(id, cId, title, author, ownerName, ownerId));
        }
        cursor.close();
        return result;
    }

    public int deleteBook(long bookId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("book", "_id=?", new String[]{ String.valueOf(bookId) });
    }

    // [수정] 닉네임 변경 시: owner_id가 내 것인 책들의 이름을 바꾼다 (더 안전함)
    public void updateOwnerName(String userId, String newName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("owner_name", newName);

        // owner_id가 userId(로그인한 사람)인 행만 업데이트
        db.update("book", values, "owner_id=?", new String[]{userId});
    }
}