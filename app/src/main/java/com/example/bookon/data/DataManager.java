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

    // ===============================================================
    // [C] CREATE: 모임 생성 (INSERT)
    // ===============================================================
    public long addNewClub(Club club, String currentUserId) {
        ContentValues values = new ContentValues();
        values.put("name", club.getName());
        values.put("capacity", club.getCapacity());
        values.put("start_date", club.getStartDate());
        values.put("end_date", club.getEndDate());
        values.put("description", club.getDescription());

        // 상태값 설정 (없으면 기본값)
        values.put("status", club.getStatus() != null ? club.getStatus() : "모집중");
        values.put("current_book", club.getCurrentBook() != null ? club.getCurrentBook() : "선정 도서 없음");

        // 만든 사람 아이디 저장 (방장)
        values.put("owner_id", currentUserId);

        return database.insert("clubs", null, values);
    }

    // ===============================================================
    // [C] JOIN: 모임 참여하기 (멤버 테이블에 추가)
    // ===============================================================
    public void joinClub(String userId, int clubId) {
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("club_id", clubId);
        database.insert("members", null, values);
    }

    // ===============================================================
    // [R] READ: 조회 관련 메서드들
    // ===============================================================

    // 1. 모집 중인 모임 조회 (RecruitActivity용)
    public ArrayList<Club> getRecruitingClubs(String currentUserId) {
        // 조건: 상태가 '모집중'인 것
        return getClubsByQuery("status = ?", new String[]{"모집중"}, "_id DESC", currentUserId);
    }

    // 2. 내 모임 리스트 조회 (HomeActivity용)
    // 조건: (내가 만든 모임) OR (내가 멤버로 가입한 모임)
    public ArrayList<Club> getMyClubList(String currentUserId) {
        // 복잡한 OR 조건과 서브쿼리가 필요하므로 rawQuery용 SQL 작성
        String query = "SELECT * FROM clubs WHERE owner_id = ? " +
                "OR _id IN (SELECT club_id FROM members WHERE user_id = ?) " +
                "ORDER BY owner_id DESC, _id DESC"; // 내가 만든 것을 상단으로 정렬

        ArrayList<Club> clubList = new ArrayList<>();
        Cursor cursor = database.rawQuery(query, new String[]{currentUserId, currentUserId});

        // 커서에서 데이터 추출 (아래 공통 로직과 비슷하지만 rawQuery용으로 별도 작성)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                clubList.add(cursorToClub(cursor, currentUserId));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return clubList;
    }

    // 3. ID로 특정 모임 하나만 조회 (상세 페이지용)
    public Club getClubById(int clubId, String currentUserId) {
        String selection = "_id = ?";
        String[] selectionArgs = new String[]{String.valueOf(clubId)};

        ArrayList<Club> result = getClubsByQuery(selection, selectionArgs, null, currentUserId);

        if (!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    // 4. 이미 참여한 멤버인지 확인 (중복 참여 방지)
    public boolean checkIsMember(String userId, int clubId) {
        String query = "SELECT _id FROM members WHERE user_id = ? AND club_id = ?";
        Cursor cursor = database.rawQuery(query, new String[]{userId, String.valueOf(clubId)});
        boolean isMember = false;
        if (cursor.moveToFirst()) {
            isMember = true;
        }
        cursor.close();
        return isMember;
    }

    // ===============================================================
    // [U] UPDATE: 모임 수정
    // ===============================================================
    public boolean updateClub(Club club) {
        ContentValues values = new ContentValues();
        values.put("name", club.getName());
        values.put("capacity", club.getCapacity());
        values.put("start_date", club.getStartDate());
        values.put("end_date", club.getEndDate());
        values.put("description", club.getDescription());
        values.put("status", club.getStatus());
        values.put("current_book", club.getCurrentBook());

        // owner_id는 변경하지 않음

        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(club.getId())};

        // 업데이트된 행의 개수가 0보다 크면 true
        int rowsAffected = database.update("clubs", values, whereClause, whereArgs);
        return rowsAffected > 0;
    }

    // ===============================================================
    // [D] DELETE: 모임 삭제
    // ===============================================================
    public boolean deleteClub(int clubId) {
        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(clubId)};

        // 삭제된 행의 개수가 0보다 크면 true
        int rowsDeleted = database.delete("clubs", whereClause, whereArgs);
        return rowsDeleted > 0;
    }

    // ===============================================================
    // Private Helpers (중복 코드 제거)
    // ===============================================================

    // 공통 쿼리 실행기 (단순 WHERE 조건용)
    private ArrayList<Club> getClubsByQuery(String selection, String[] selectionArgs, String sortOrder, String currentUserId) {
        ArrayList<Club> clubList = new ArrayList<>();
        Cursor cursor = database.query("clubs", null, selection, selectionArgs, null, null, sortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                clubList.add(cursorToClub(cursor, currentUserId));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return clubList;
    }

    // 커서에서 데이터를 꺼내 Club 객체로 변환하는 메서드
    private Club cursorToClub(Cursor cursor, String currentUserId) {
        int idIndex = cursor.getColumnIndex("_id");
        int nameIndex = cursor.getColumnIndex("name");
        int capacityIndex = cursor.getColumnIndex("capacity");
        int startIndex = cursor.getColumnIndex("start_date");
        int endIndex = cursor.getColumnIndex("end_date");
        int descIndex = cursor.getColumnIndex("description");
        int statusIndex = cursor.getColumnIndex("status");
        int bookIndex = cursor.getColumnIndex("current_book");
        int ownerIdIndex = cursor.getColumnIndex("owner_id");

        int id = cursor.getInt(idIndex);
        String name = cursor.getString(nameIndex);
        int capacity = cursor.getInt(capacityIndex);
        String startDate = cursor.getString(startIndex);
        String endDate = cursor.getString(endIndex);
        String description = cursor.getString(descIndex);
        String status = (statusIndex != -1) ? cursor.getString(statusIndex) : "모집중";
        String currentBook = (bookIndex != -1) ? cursor.getString(bookIndex) : "";

        // 방장 여부 확인 (DB의 owner_id와 현재 로그인한 ID 비교)
        String dbOwnerId = (ownerIdIndex != -1) ? cursor.getString(ownerIdIndex) : "";
        boolean isOwner = dbOwnerId != null && dbOwnerId.equals(currentUserId);

        Club club = new Club(name, capacity, startDate, endDate, description);
        club.setId(id);
        club.setStatus(status);
        club.setCurrentBook(currentBook);
        club.setOwner(isOwner);

        return club;
    }
}