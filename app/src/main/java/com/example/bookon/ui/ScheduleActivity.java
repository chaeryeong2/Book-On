package com.example.bookon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookon.R;
import com.example.bookon.data.Club;
import com.example.bookon.data.DataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ScheduleActivity extends BaseActivity {

    private CalendarView calendarView;
    private TextView tvSelectedDate, tvDateEvent;
    private RecyclerView rvExchangeOrder;

    private int clubId = -1;
    private Club currentClub;
    // 날짜 계산을 위한 포맷 (년-월-일)
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // 1. 인텐트로 넘어온 club_id 받기
        clubId = getIntent().getIntExtra("club_id", -1);

        // (예외처리) ID가 없으면, 내가 참여 중인 모임 중 하나를 가져옴
        if (clubId == -1) {
            String myId = getSharedPreferences("AppSettings", MODE_PRIVATE).getString("CurrentUserId", "");
            ArrayList<Club> myClubs = DataManager.getInstance(this).getMyClubList(myId);
            if (!myClubs.isEmpty()) {
                clubId = myClubs.get(0).getId();
            }
        }

        // 2. 뷰 연결
        calendarView = findViewById(R.id.calendarView);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        tvDateEvent = findViewById(R.id.tv_date_event);
        rvExchangeOrder = findViewById(R.id.rv_exchange_order); // 리사이클러뷰
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // 3. 하단 탭 설정
        setupBottomNav(bottomNav);

        // 4. 데이터 로드 및 화면 세팅
        if (clubId != -1) {
            loadScheduleData();
        } else {
            tvDateEvent.setText("참여 중인 모임이 없습니다.");
        }

        // 5. 캘린더 클릭 이벤트
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String dateText = String.format(Locale.getDefault(), "%d년 %d월 %d일", year, month + 1, dayOfMonth);
            tvSelectedDate.setText(dateText);

            // 클릭한 날짜가 교환일인지 계산
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            checkEvent(c.getTimeInMillis());
        });
    }

    private void loadScheduleData() {
        // [1] 상단 교환 순서 (RecyclerView) 연결
        // DB에서 순서대로 정렬된 멤버 ID 리스트 가져오기
        ArrayList<String> members = DataManager.getInstance(this).getClubMemberIds(clubId);

        ExchangeAdapter adapter = new ExchangeAdapter(members);
        // 가로 스크롤 설정
        rvExchangeOrder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvExchangeOrder.setAdapter(adapter);

        // [2] 일정 정보 가져오기 (시작일, 주기)
        // SharedPreferences에서 현재 유저 ID 가져오기
        String currentUserId = getSharedPreferences("AppSettings", MODE_PRIVATE).getString("CurrentUserId", "");
        currentClub = DataManager.getInstance(this).getClubById(clubId, currentUserId);

        // 오늘 날짜 기준으로 이벤트 체크 한번 실행 (화면 켜지자마자 보이게)
        checkEvent(calendarView.getDate());
    }

    // 날짜를 받아서 교환일인지 계산하는 핵심 로직
    private void checkEvent(long clickedTimeMillis) {
        if (currentClub == null || currentClub.getScheduleStart() == null) {
            tvDateEvent.setText("일정이 설정되지 않은 모임입니다.");
            return;
        }

        try {
            Date startDate = sdf.parse(currentClub.getScheduleStart());
            int cycleWeeks = currentClub.getCycleWeeks();

            // 날짜 차이 계산 (일 단위)
            long diffMillis = clickedTimeMillis - startDate.getTime();
            long diffDays = TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);

            if (diffDays < 0) {
                tvDateEvent.setText("독서 시작 전입니다.");
                tvDateEvent.setTextColor(getColor(R.color.text_secondary));
                return;
            }

            int cycleDays = cycleWeeks * 7; // 주 -> 일 단위 변환

            // 교환일인지 체크 (시작일로부터 주기에 딱 떨어지는 날)
            if (cycleDays > 0 && diffDays > 0 && diffDays % cycleDays == 0) {
                long round = diffDays / cycleDays;
                tvDateEvent.setText("📚 " + round + "차 도서 교환일입니다!");
                tvDateEvent.setTextColor(getColor(R.color.brand_primary)); // 파란색 강조
            } else {
                long currentRound = (diffDays / cycleDays) + 1;
                tvDateEvent.setText("현재 " + currentRound + "라운드 독서 진행 중 🔥");
                tvDateEvent.setTextColor(getColor(R.color.text_secondary));
            }

        } catch (ParseException e) {
            e.printStackTrace();
            tvDateEvent.setText("날짜 계산 오류");
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