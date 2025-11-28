package com.example.bookon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookon.R;
import com.example.bookon.data.Book;
import com.example.bookon.data.BookDBHelper;
import com.example.bookon.data.Club;
import com.example.bookon.data.DataManager;
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
    private BookDBHelper bookDBHelper;
    private int totalBookCount = 0;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // 1. 인텐트 처리
        clubId = getIntent().getIntExtra("club_id", -1);
        if (clubId == -1) {
            String myId = getSharedPreferences("AppSettings", MODE_PRIVATE).getString("CurrentUserId", "");
            ArrayList<Club> myClubs = DataManager.getInstance(this).getMyClubList(myId);
            if (!myClubs.isEmpty()) {
                clubId = myClubs.get(0).getId();
            }
        }

        // 2. 초기화
        bookDBHelper = new BookDBHelper(this);

        // 3. 뷰 연결
        calendarView = findViewById(R.id.calendarView);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        tvDateEvent = findViewById(R.id.tv_date_event);
        rvExchangeOrder = findViewById(R.id.rv_exchange_order);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        setupBottomNav(bottomNav);

        // 4. 데이터 로드
        if (clubId != -1) {
            loadScheduleData();
        } else {
            tvDateEvent.setText("참여 중인 모임이 없습니다.");
        }

        // 5. 캘린더 클릭
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String dateText = String.format(Locale.getDefault(), "%d년 %d월 %d일", year, month + 1, dayOfMonth);
            tvSelectedDate.setText(dateText);

            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            checkEvent(c.getTimeInMillis());
        });
    }

    private void loadScheduleData() {
        // [1] 교환 순서 설정
        ArrayList<String> members = DataManager.getInstance(this).getClubMemberIds(clubId);
        ExchangeAdapter adapter = new ExchangeAdapter(members);
        rvExchangeOrder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvExchangeOrder.setAdapter(adapter);

        // [2] 일정 정보 가져오기
        String currentUserId = getSharedPreferences("AppSettings", MODE_PRIVATE).getString("CurrentUserId", "");
        currentClub = DataManager.getInstance(this).getClubById(clubId, currentUserId);

        // [3] 책 개수 가져오기
        List<Book> books = bookDBHelper.getBooksByClub(clubId);
        totalBookCount = books.size();

        // 오늘 날짜 체크
        checkEvent(calendarView.getDate());
    }

    // -------------------------------------------------------------------
    // [최종 수정] 날짜 계산 및 표시 로직 (진행 중 표시 복구)
    // -------------------------------------------------------------------
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

            // 1. 시작일인 경우
            if (diffDays == 0) {
                tvDateEvent.setText("🚀 독서 모임 시작일입니다!");
                tvDateEvent.setTextColor(getColor(R.color.brand_primary));
                return;
            }

            // 시작 전이면 빈칸
            if (diffDays < 0) {
                return;
            }

            int cycleDays = cycleWeeks * 7;

            // 2. 교환일인지 체크 (나머지가 0인 날)
            boolean isExchangeDay = (cycleDays > 0 && diffDays % cycleDays == 0);

            // 현재 몇 라운드 구간인지 계산 (나누기 몫)
            long round = diffDays / cycleDays;

            if (isExchangeDay) {
                // [교환일]
                if (round < totalBookCount) {
                    tvDateEvent.setText("📚 " + round + "차 도서 교환일입니다!");
                    tvDateEvent.setTextColor(getColor(R.color.brand_primary));
                } else if (round == totalBookCount) {
                    tvDateEvent.setText("🎉 마지막 교환일 (모임 종료)!");
                    tvDateEvent.setTextColor(getColor(R.color.brand_secondary));
                } else {
                    tvDateEvent.setText(""); // 종료 후 날짜는 빈칸
                }
            } else {
                // [교환일 아님 -> 독서 진행 중]
                // 현재 진행 중인 라운드는 (몫 + 1)
                long currentRound = round + 1;

                if (currentRound <= totalBookCount) {
                    // [복구됨] 라운드 진행 표시
                    tvDateEvent.setText("📖 현재 " + currentRound + "라운드 독서 진행 중");
                    tvDateEvent.setTextColor(getColor(R.color.text_secondary));
                } else {
                    tvDateEvent.setText(""); // 종료 후 날짜는 빈칸
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