package com.example.bookon.ui;

import android.content.Intent;
import android.database.Cursor; // [추가]
import android.database.sqlite.SQLiteDatabase; // [추가]
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookon.R;
import com.example.bookon.data.Book;
import com.example.bookon.data.Club;
import com.example.bookon.data.DataManager;
import com.example.bookon.data.LoginHelper; // [추가]
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ScheduleActivity extends BaseActivity {

    private CalendarView calendarView;
    private TextView tvSelectedDate, tvDateEvent;
    private RecyclerView rvExchangeOrder;

    private int clubId = -1;
    private Club currentClub;
    private int totalBookCount = 0;
    private LoginHelper loginHelper; // [추가] 닉네임 조회용

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // 1. 초기화
        loginHelper = new LoginHelper(this); // [추가]

        clubId = getIntent().getIntExtra("club_id", -1);
        if (clubId == -1) {
            String myId = getSharedPreferences("AppSettings", MODE_PRIVATE).getString("CurrentUserId", "");
            ArrayList<Club> myClubs = DataManager.getInstance(this).getMyClubList(myId);
            if (!myClubs.isEmpty()) {
                clubId = myClubs.get(0).getId();
            }
        }

        calendarView = findViewById(R.id.calendarView);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        tvDateEvent = findViewById(R.id.tv_date_event);
        rvExchangeOrder = findViewById(R.id.rv_exchange_order);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        setupBottomNav(bottomNav);

        if (clubId != -1) {
            loadScheduleData();
        } else {
            tvDateEvent.setText("참여 중인 모임이 없습니다.");
        }

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String dateText = String.format(Locale.getDefault(), "%d년 %d월 %d일", year, month + 1, dayOfMonth);
            tvSelectedDate.setText(dateText);

            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            checkEvent(c.getTimeInMillis());
        });
    }

    private void loadScheduleData() {
        // [수정] 1. 멤버 ID 리스트 가져오기
        ArrayList<String> memberIds = DataManager.getInstance(this).getClubMemberIds(clubId);

        // [수정] 2. ID를 닉네임으로 변환
        ArrayList<String> memberNicknames = new ArrayList<>();
        for (String id : memberIds) {
            memberNicknames.add(getNickname(id)); // ID -> 닉네임 변환
        }

        // [수정] 3. 닉네임 리스트를 어댑터에 전달
        ExchangeAdapter adapter = new ExchangeAdapter(memberNicknames);
        rvExchangeOrder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvExchangeOrder.setAdapter(adapter);

        String currentUserId = getSharedPreferences("AppSettings", MODE_PRIVATE).getString("CurrentUserId", "");
        currentClub = DataManager.getInstance(this).getClubById(clubId, currentUserId);

        // 책 개수 세기
        List<Book> books = DataManager.getInstance(this).getBooksByClub(clubId);
        totalBookCount = books.size();

        checkEvent(calendarView.getDate());
    }

    // [추가] ID로 닉네임 조회하는 헬퍼 메서드
    private String getNickname(String userId) {
        SQLiteDatabase db = loginHelper.getReadableDatabase();
        String nickname = userId; // 기본값은 아이디

        Cursor cursor = db.rawQuery("SELECT nickname FROM users WHERE username = ?", new String[]{userId});

        if (cursor.moveToFirst()) {
            nickname = cursor.getString(0);
        }
        cursor.close();
        return nickname;
    }

    private void checkEvent(long clickedTimeMillis) {
        tvDateEvent.setText("");

        if (currentClub == null || currentClub.getScheduleStart() == null) {
            tvDateEvent.setText("일정이 설정되지 않은 모임입니다.");
            return;
        }

        if (totalBookCount == 0) {
            tvDateEvent.setText("등록된 책이 없어 일정을 계산할 수 없습니다.");
            return;
        }

        try {
            Date startDate = sdf.parse(currentClub.getScheduleStart());
            int cycleWeeks = currentClub.getCycleWeeks();

            long diffMillis = clickedTimeMillis - startDate.getTime();
            long diffDays = TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);

            if (diffDays == 0) {
                tvDateEvent.setText("🚀 독서 모임 시작일입니다!");
                tvDateEvent.setTextColor(getColor(R.color.brand_secondary));
                return;
            }

            if (diffDays < 0) {
                return;
            }

            int cycleDays = cycleWeeks * 7;
            boolean isExchangeDay = (cycleDays > 0 && diffDays % cycleDays == 0);
            long round = diffDays / cycleDays;

            if (isExchangeDay) {
                if (round < totalBookCount) {
                    tvDateEvent.setText("📚 " + round + "차 도서 교환일입니다!");
                    tvDateEvent.setTextColor(getColor(R.color.brand_secondary));
                } else if (round == totalBookCount) {
                    tvDateEvent.setText("🎉 마지막 교환일 (모임 종료)!");
                    tvDateEvent.setTextColor(getColor(R.color.brand_secondary));
                } else {
                    tvDateEvent.setText("");
                }
            } else {
                long currentRound = round + 1;
                if (currentRound <= totalBookCount) {
                    tvDateEvent.setText("📖 현재 " + currentRound + "라운드 독서 진행 중");
                    tvDateEvent.setTextColor(getColor(R.color.text_secondary));
                } else {
                    tvDateEvent.setText("");
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setupBottomNav(BottomNavigationView bottomNav) {
        bottomNav.setSelectedItemId(R.id.nav_schedule);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0); return true;
            } else if (id == R.id.nav_recruit) {
                startActivity(new Intent(this, RecruitActivity.class));
                overridePendingTransition(0, 0); return true;
            } else if (id == R.id.nav_schedule) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileEditActivity.class));
                overridePendingTransition(0, 0); return true;
            }
            return false;
        });
    }
}