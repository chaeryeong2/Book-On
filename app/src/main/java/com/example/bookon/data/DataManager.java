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
    // [U] SCHEDULE: 일정 및 순서 확정 (ScheduleSetupActivity용)
    // [추가됨]
    // ===============================================================
    public void setClubSchedule(int clubId, String startDate, int cycleWeeks, ArrayList<String> memberIds) {
        // 1. 클럽 테이블 업데이트 (시작일, 주기, 상태 변경)
        ContentValues clubValues = new ContentValues();
        clubValues.put("schedule_start", startDate);
        clubValues.put("cycle_weeks", cycleWeeks);
        clubValues.put("status", "진행중"); // 시작하면 진행중으로 변경

        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(clubId)};
        database.update("clubs", clubValues, whereClause, whereArgs);

        // 2. 멤버 테이블 업데이트 (순서 저장)
        // memberIds 리스트에 들어있는 순서대로 1, 2, 3... 부여
        for (int i = 0; i < memberIds.size(); i++) {
            ContentValues memberValues = new ContentValues();
            memberValues.put("sequence", i + 1); // 1번부터 시작

            String memWhere = "club_id = ? AND user_id = ?";
            String[] memArgs = new String[]{String.valueOf(clubId), memberIds.get(i)};
            database.update("members", memberValues, memWhere, memArgs);
        }
    }

    // ===============================================================
    // [R] READ: 조회 관련 메서드들
    // ===============================================================

    // 1. 모집 중인 모임 조회 (RecruitActivity용)
    public ArrayList<Club> getRecruitingClubs(String currentUserId) {
        return getClubsByQuery("status = ?", new String[]{"모집중"}, "_id DESC", currentUserId);
    }

    // 2. 내 모임 리스트 조회 (HomeActivity용)
    public ArrayList<Club> getMyClubList(String currentUserId) {
        String query = "SELECT * FROM clubs WHERE owner_id = ? " +
                "OR _id IN (SELECT club_id FROM members WHERE user_id = ?) " +
                "ORDER BY owner_id DESC, _id DESC";

        ArrayList<Club> clubList = new ArrayList<>();
        Cursor cursor = database.rawQuery(query, new String[]{currentUserId, currentUserId});

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

    // 5. 멤버 리스트 가져오기 (순서가 있으면 순서대로, 없으면 가입순) [추가됨]
    public ArrayList<String> getClubMemberIds(int clubId) {
        ArrayList<String> members = new ArrayList<>();
        // sequence가 0이 아니면 sequence순, 아니면 _id순
        String query = "SELECT user_id FROM members WHERE club_id = ? ORDER BY CASE WHEN sequence > 0 THEN sequence ELSE 999 END, _id ASC";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(clubId)});

        while (cursor.moveToNext()) {
            members.add(cursor.getString(0));
        }
        cursor.close();
        return members;
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

        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(club.getId())};

        int rowsAffected = database.update("clubs", values, whereClause, whereArgs);
        return rowsAffected > 0;
    }

    // ===============================================================
    // [D] DELETE: 모임 삭제
    // ===============================================================
    public boolean deleteClub(int clubId) {
        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(clubId)};

        int rowsDeleted = database.delete("clubs", whereClause, whereArgs);
        return rowsDeleted > 0;
    }

    // ===============================================================
    // Private Helpers (중복 코드 제거)
    // ===============================================================

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

        // [추가됨] 일정 관련 컬럼 읽기
        int scheduleStartIndex = cursor.getColumnIndex("schedule_start");
        int cycleWeeksIndex = cursor.getColumnIndex("cycle_weeks");

        int id = cursor.getInt(idIndex);
        String name = cursor.getString(nameIndex);
        int capacity = cursor.getInt(capacityIndex);
        String startDate = cursor.getString(startIndex);
        String endDate = cursor.getString(endIndex);
        String description = cursor.getString(descIndex);
        String status = (statusIndex != -1) ? cursor.getString(statusIndex) : "모집중";
        String currentBook = (bookIndex != -1) ? cursor.getString(bookIndex) : "";

        // [추가됨] 일정 데이터 추출 (없으면 null 또는 0)
        String scheduleStart = (scheduleStartIndex != -1) ? cursor.getString(scheduleStartIndex) : null;
        int cycleWeeks = (cycleWeeksIndex != -1) ? cursor.getInt(cycleWeeksIndex) : 0;

        String dbOwnerId = (ownerIdIndex != -1) ? cursor.getString(ownerIdIndex) : "";
        boolean isOwner = dbOwnerId != null && dbOwnerId.equals(currentUserId);

        Club club = new Club(name, capacity, startDate, endDate, description);
        club.setId(id);
        club.setStatus(status);
        club.setCurrentBook(currentBook);
        club.setOwner(isOwner);

        // [추가됨] 일정 데이터 세팅 (Club.java에 Setter 필요)
        club.setScheduleStart(scheduleStart);
        club.setCycleWeeks(cycleWeeks);

        return club;
    }
}