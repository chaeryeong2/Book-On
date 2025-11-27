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
    // [수정] 누가 만들었는지 알기 위해 currentUserId를 받습니다.
    // ---------------------------------------------------------------
    public long addNewClub(Club club, String currentUserId) {
        ContentValues values = new ContentValues();
        values.put("name", club.getName());
        values.put("capacity", club.getCapacity());
        values.put("start_date", club.getStartDate());
        values.put("end_date", club.getEndDate());
        values.put("description", club.getDescription());

        // 상태값 설정
        values.put("status", club.getStatus() != null ? club.getStatus() : "모집중");
        values.put("current_book", club.getCurrentBook() != null ? club.getCurrentBook() : "선정 도서 없음");

        // [핵심 수정] 1이 아니라 '만든 사람의 아이디'를 저장합니다.
        // DB 테이블에 'owner_id' 컬럼이 있어야 합니다!
        values.put("owner_id", currentUserId);

        return database.insert("clubs", null, values);
    }

    // ---------------------------------------------------------------
    // SELECT 1: 모집 중인 모임 (RecruitActivity용)
    // ---------------------------------------------------------------
    public ArrayList<Club> getRecruitingClubs(String currentUserId) {
        // 모집중인 것만 가져오기
        // currentUserId를 넘기는 이유는 리스트 중에서 혹시 내가 만든게 있다면 표시하기 위함
        return getClubsByQuery("status = ?", new String[]{"모집중"}, "_id DESC", currentUserId);
    }

    // ---------------------------------------------------------------
    // SELECT 2: 내가 참여 중인 모임 (HomeActivity용)
    // ---------------------------------------------------------------
    public ArrayList<Club> getMyClubList(String currentUserId) {
        // [핵심 수정]
        // 조건: (상태가 모집중이 아님 = 참여중) OR (만든 사람이 '나'인 경우)
        // SQL: status != '모집중' OR owner_id = 'currentUserId'

        String selection = "status != ? OR owner_id = ?";
        String[] selectionArgs = new String[]{"모집중", currentUserId};

        // 정렬: 최신순 (_id DESC)
        // (참고: 문자열 ID로는 '내꺼 위로 정렬'이 SQL만으론 복잡하므로 일단 최신순으로 둡니다.
        //  필요하면 자바 코드에서 정렬해야 합니다.)
        String sortOrder = "_id DESC";

        return getClubsByQuery(selection, selectionArgs, sortOrder, currentUserId);
    }

    // ---------------------------------------------------------------
    // SELECT 공통 로직
    // ---------------------------------------------------------------
    private ArrayList<Club> getClubsByQuery(String selection, String[] selectionArgs, String sortOrder, String currentUserId) {
        ArrayList<Club> clubList = new ArrayList<>();

        Cursor cursor = database.query(
                "clubs",
                null,
                selection,
                selectionArgs,
                null, null,
                sortOrder
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("_id");
                int nameIndex = cursor.getColumnIndex("name");
                int capacityIndex = cursor.getColumnIndex("capacity");
                int startIndex = cursor.getColumnIndex("start_date");
                int endIndex = cursor.getColumnIndex("end_date");
                int descIndex = cursor.getColumnIndex("description");
                int statusIndex = cursor.getColumnIndex("status");
                int bookIndex = cursor.getColumnIndex("current_book");

                // [수정] is_owner 대신 owner_id 컬럼을 읽습니다.
                int ownerIdIndex = cursor.getColumnIndex("owner_id");

                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                int capacity = cursor.getInt(capacityIndex);
                String startDate = cursor.getString(startIndex);
                String endDate = cursor.getString(endIndex);
                String description = cursor.getString(descIndex);

                String status = (statusIndex != -1) ? cursor.getString(statusIndex) : "모집중";
                String currentBook = (bookIndex != -1) ? cursor.getString(bookIndex) : "";

                // [핵심 로직] DB에 저장된 아이디와 현재 로그인한 아이디가 같은지 비교!
                // DB에 저장된 owner_id가 없으면(null) 남의 것으로 처리
                String dbOwnerId = (ownerIdIndex != -1) ? cursor.getString(ownerIdIndex) : "";
                boolean isOwner = dbOwnerId != null && dbOwnerId.equals(currentUserId);

                Club club = new Club(name, capacity, startDate, endDate, description);
                club.setId(id);
                club.setStatus(status);
                club.setCurrentBook(currentBook);

                // 계산된 결과(true/false)를 객체에 세팅
                club.setOwner(isOwner);

                clubList.add(club);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return clubList;
    }
}