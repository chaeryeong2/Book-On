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

    // ---------------------------------------------------------------
    // INSERT: 모임 생성
    // ---------------------------------------------------------------
    public long addNewClub(Club club) {
        ContentValues values = new ContentValues();
        values.put("name", club.getName());
        values.put("capacity", club.getCapacity());
        values.put("start_date", club.getStartDate());
        values.put("end_date", club.getEndDate());
        values.put("description", club.getDescription());

        // [수정 2] 상태와 현재 책 정보도 저장 (기본값 설정)
        // 만약 Club 객체에 값이 없다면 기본값으로 "모집중" 등을 넣습니다.
        values.put("status", club.getStatus() != null ? club.getStatus() : "모집중");
        values.put("current_book", club.getCurrentBook() != null ? club.getCurrentBook() : "선정 도서 없음");

        return database.insert("clubs", null, values);
    }

    // ---------------------------------------------------------------
    // SELECT 1: 모집 중인 모임 (두 번째 탭용)
    // ---------------------------------------------------------------
    public ArrayList<Club> getRecruitingClubs() {
        // status가 '모집중'인 데이터만 가져오기
        return getClubsByQuery("status = ?", new String[]{"모집중"});
    }

    // ---------------------------------------------------------------
    // SELECT 2: 내가 참여 중인 모임 (홈 화면용)
    // ---------------------------------------------------------------
    public ArrayList<Club> getMyClubList() {
        // [임시 로직] 아직 회원(Member) 테이블이 없으므로,
        // 테스트를 위해 '모집중'이 아닌('진행중' 등) 모임을 내 모임이라고 가정하고 가져옵니다.
        // 나중에는 "SELECT * FROM clubs WHERE id IN (SELECT club_id FROM members WHERE user_id = ...)" 로 바꿔야 합니다.
        return getClubsByQuery("status != ?", new String[]{"모집중"});
    }

    // ---------------------------------------------------------------
    // SELECT 공통 로직 (코드 중복 제거)
    // ---------------------------------------------------------------
    private ArrayList<Club> getClubsByQuery(String selection, String[] selectionArgs) {
        ArrayList<Club> clubList = new ArrayList<>();

        Cursor cursor = database.query(
                "clubs",
                null, // 모든 컬럼 선택
                selection, // WHERE 조건 (예: status = ?)
                selectionArgs, // 조건 값 (예: "모집중")
                null, null,
                "_id DESC" // 최신순 정렬
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // 컬럼 인덱스 찾기
                int idIndex = cursor.getColumnIndex("_id");
                int nameIndex = cursor.getColumnIndex("name");
                int capacityIndex = cursor.getColumnIndex("capacity");
                int startIndex = cursor.getColumnIndex("start_date");
                int endIndex = cursor.getColumnIndex("end_date");
                int descIndex = cursor.getColumnIndex("description");
                // [수정 3] 추가된 컬럼 읽기
                int statusIndex = cursor.getColumnIndex("status");
                int bookIndex = cursor.getColumnIndex("current_book");

                // 데이터 추출
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                int capacity = cursor.getInt(capacityIndex);
                String startDate = cursor.getString(startIndex);
                String endDate = cursor.getString(endIndex);
                String description = cursor.getString(descIndex);

                // 예외 처리: DB 버전에 따라 컬럼이 없을 수도 있으므로 확인
                String status = (statusIndex != -1) ? cursor.getString(statusIndex) : "모집중";
                String currentBook = (bookIndex != -1) ? cursor.getString(bookIndex) : "";

                // 객체 생성 (생성자 형태에 맞춰 수정 필요, 여기선 Setter 사용 권장)
                Club club = new Club(name, capacity, startDate, endDate, description);
                club.setId(id);
                club.setStatus(status);        // Club 모델에 setStatus 필요
                club.setCurrentBook(currentBook); // Club 모델에 setCurrentBook 필요

                clubList.add(club);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return clubList;
    }
}