package com.example.bookon.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
    // [C] CREATE: 모임 생성
    // ===============================================================
    public long addNewClub(Club club, String currentUserId) {
        ContentValues values = new ContentValues();
        values.put("name", club.getName());
        values.put("capacity", club.getCapacity());
        values.put("start_date", club.getStartDate());
        values.put("end_date", club.getEndDate());
        values.put("description", club.getDescription());
        values.put("status", club.getStatus() != null ? club.getStatus() : "모집중");
        values.put("topic", club.getTopic());
        values.put("owner_id", currentUserId);

        long newClubId = database.insert("clubs", null, values);

        // [수정] 모임 생성 성공 시, 방장을 'members' 테이블에도 자동 추가 (순서 0)
        if (newClubId != -1) {
            ContentValues memberValues = new ContentValues();
            memberValues.put("user_id", currentUserId);
            memberValues.put("club_id", newClubId);
            memberValues.put("sequence", 0);
            database.insert("members", null, memberValues);
        }

        return newClubId;
    }

    // ===============================================================
    // [C] JOIN: 모임 참여
    // ===============================================================
    public void joinClub(String userId, int clubId) {
        // 1. 멤버 추가
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("club_id", clubId);
        database.insert("members", null, values);

        // 2. 정원 체크 후 상태 변경 (방장이 멤버에 포함되었으므로 +1 제거)
        int currentCount = getMemberCount(clubId);

        String query = "SELECT capacity FROM clubs WHERE _id = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(clubId)});
        if (cursor.moveToFirst()) {
            int capacity = cursor.getInt(0);
            if (currentCount >= capacity) {
                ContentValues updateValues = new ContentValues();
                updateValues.put("status", "마감됨");
                database.update("clubs", updateValues, "_id = ?", new String[]{String.valueOf(clubId)});
            }
        }
        cursor.close();
    }

    // ===============================================================
    // [U] SCHEDULE: 일정 확정
    // ===============================================================
    public void setClubSchedule(int clubId, String startDate, int cycleWeeks, ArrayList<String> memberIds) {
        ContentValues clubValues = new ContentValues();
        clubValues.put("schedule_start", startDate);
        clubValues.put("cycle_weeks", cycleWeeks);
        clubValues.put("status", "진행중");

        database.update("clubs", clubValues, "_id = ?", new String[]{String.valueOf(clubId)});

        for (int i = 0; i < memberIds.size(); i++) {
            ContentValues memberValues = new ContentValues();
            memberValues.put("sequence", i + 1);
            String memWhere = "club_id = ? AND user_id = ?";
            String[] memArgs = new String[]{String.valueOf(clubId), memberIds.get(i)};
            database.update("members", memberValues, memWhere, memArgs);
        }
    }

    // ===============================================================
    // [R] READ: 조회 관련 메서드들
    // ===============================================================

    // 1. 모집 중인 모임 조회
    public ArrayList<Club> getRecruitingClubs(String currentUserId) {
        updateExpiredClubs();

        String selection = "status = ? OR status = ? OR status = ?";
        String[] selectionArgs = new String[]{"모집중", "진행중", "마감됨"};
        return getClubsByQuery(selection, selectionArgs, "_id DESC", currentUserId);
    }

    // 2. 내 모임 리스트 조회
    public ArrayList<Club> getMyClubList(String currentUserId) {
        updateExpiredClubs();

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

    // 3. ID로 특정 모임 조회
    public Club getClubById(int clubId, String currentUserId) {
        updateExpiredClubs();

        String selection = "_id = ?";
        String[] selectionArgs = new String[]{String.valueOf(clubId)};
        ArrayList<Club> result = getClubsByQuery(selection, selectionArgs, null, currentUserId);
        return !result.isEmpty() ? result.get(0) : null;
    }

    // 4. 멤버인지 확인
    public boolean checkIsMember(String userId, int clubId) {
        String query = "SELECT _id FROM members WHERE user_id = ? AND club_id = ?";
        Cursor cursor = database.rawQuery(query, new String[]{userId, String.valueOf(clubId)});
        boolean isMember = false;
        if (cursor.moveToFirst()) isMember = true;
        cursor.close();
        return isMember;
    }

    // 5. 멤버 리스트 가져오기 (방장 포함)
    public ArrayList<String> getClubMemberIds(int clubId) {
        ArrayList<String> members = new ArrayList<>();

        // 순서가 있으면(>0) 순서대로, 없으면 가입순(_id)으로 정렬
        String query = "SELECT user_id FROM members WHERE club_id = ? ORDER BY CASE WHEN sequence > 0 THEN sequence ELSE 999 END, _id ASC";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(clubId)});
        while (cursor.moveToNext()) members.add(cursor.getString(0));
        cursor.close();
        return members;
    }

    // 6. 현재 멤버 수 확인
    public int getMemberCount(int clubId) {
        String query = "SELECT COUNT(*) FROM members WHERE club_id = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(clubId)});
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    // ===============================================================
    // [U] UPDATE & [D] DELETE
    // ===============================================================
    public boolean updateClub(Club club) {
        ContentValues values = new ContentValues();
        values.put("name", club.getName());
        values.put("capacity", club.getCapacity());
        values.put("start_date", club.getStartDate());
        values.put("end_date", club.getEndDate());
        values.put("description", club.getDescription());
        values.put("status", club.getStatus());
        values.put("topic", club.getTopic());

        int rowsAffected = database.update("clubs", values, "_id = ?", new String[]{String.valueOf(club.getId())});
        return rowsAffected > 0;
    }

    public boolean deleteClub(int clubId) {
        int rowsDeleted = database.delete("clubs", "_id = ?", new String[]{String.valueOf(clubId)});
        return rowsDeleted > 0;
    }

    // ===============================================================
    // [BOOK] 책 관련 CRUD
    // ===============================================================
    public long insertBook(long clubId, String title, String author, String ownerName, String ownerId) {
        ContentValues values = new ContentValues();
        values.put("club_id", clubId);
        values.put("title", title);
        values.put("author", author);
        values.put("owner_name", ownerName);
        values.put("owner_id", ownerId);
        return database.insert("book", null, values);
    }

    public ArrayList<Book> getBooksByClub(long clubId) {
        ArrayList<Book> result = new ArrayList<>();
        Cursor cursor = database.rawQuery(
                "SELECT _id, club_id, title, author, owner_name, owner_id FROM book WHERE club_id=? ORDER BY _id DESC",
                new String[]{ String.valueOf(clubId) }
        );

        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            long cId = cursor.getLong(1);
            String title = cursor.getString(2);
            String author = cursor.getString(3);
            String ownerName = cursor.getString(4);
            String ownerId = cursor.getString(5);
            result.add(new Book(id, cId, title, author, ownerName, ownerId));
        }
        cursor.close();
        return result;
    }

    public int deleteBook(long bookId) {
        return database.delete("book", "_id=?", new String[]{ String.valueOf(bookId) });
    }

    public void updateBookOwnerName(String userId, String newName) {
        ContentValues values = new ContentValues();
        values.put("owner_name", newName);
        database.update("book", values, "owner_id=?", new String[]{userId});
    }

    // ===============================================================
    // Private Helpers
    // ===============================================================

    private void updateExpiredClubs() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        ContentValues values = new ContentValues();
        values.put("status", "마감됨");

        String whereClause = "status = ? AND end_date < ?";
        String[] whereArgs = new String[]{"모집중", today};

        database.update("clubs", values, whereClause, whereArgs);
    }

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
        int topicIndex = cursor.getColumnIndex("topic");
        int ownerIdIndex = cursor.getColumnIndex("owner_id");
        int scheduleStartIndex = cursor.getColumnIndex("schedule_start");
        int cycleWeeksIndex = cursor.getColumnIndex("cycle_weeks");

        int id = cursor.getInt(idIndex);
        String name = cursor.getString(nameIndex);
        int capacity = cursor.getInt(capacityIndex);
        String startDate = cursor.getString(startIndex);
        String endDate = cursor.getString(endIndex);
        String description = cursor.getString(descIndex);
        String status = (statusIndex != -1) ? cursor.getString(statusIndex) : "모집중";
        String topic = (topicIndex != -1) ? cursor.getString(topicIndex) : "";
        String scheduleStart = (scheduleStartIndex != -1) ? cursor.getString(scheduleStartIndex) : null;
        int cycleWeeks = (cycleWeeksIndex != -1) ? cursor.getInt(cycleWeeksIndex) : 0;

        String dbOwnerId = (ownerIdIndex != -1) ? cursor.getString(ownerIdIndex) : "";
        boolean isOwner = dbOwnerId != null && dbOwnerId.equals(currentUserId);

        // [중요] 인원이 꽉 찼으면 화면 표시용 status를 '마감됨'으로 변경
        if ("모집중".equals(status)) {
            // 방장 포함이므로 바로 비교
            int currentCount = getMemberCount(id);
            if (currentCount >= capacity) {
                status = "마감됨";
            }
        }

        Club club = new Club(name, capacity, startDate, endDate, description);
        club.setId(id);
        club.setStatus(status);
        club.setTopic(topic);
        club.setOwner(isOwner);
        club.setScheduleStart(scheduleStart);
        club.setCycleWeeks(cycleWeeks);

        return club;
    }
}