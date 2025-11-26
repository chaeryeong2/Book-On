package com.example.bookon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookon.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ScheduleActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView tvSelectedDate, tvDateEvent;
    private RecyclerView rvExchangeOrder;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // 1. 뷰 연결 (findViewById)
        calendarView = findViewById(R.id.calendarView);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        tvDateEvent = findViewById(R.id.tv_date_event);
        rvExchangeOrder = findViewById(R.id.rv_exchange_order);
        bottomNav = findViewById(R.id.bottom_navigation);

        // 2. 캘린더 날짜 선택 이벤트 설정
        setupCalendar();

        // 3. 교환 순서 리스트 설정 (RecyclerView)
        setupExchangeList();

        // 4. 하단 네비게이션 바 설정 (핵심!)
        setupBottomNavigation();
    }
    // ---------------------------------------------------------------
    // 캘린더 관련 로직
    // ---------------------------------------------------------------
    private void setupCalendar() {
        // 날짜가 변경될 때마다 실행되는 리스너
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // month는 0부터 시작하므로 +1 해줘야 함
                String dateStr = String.format("%d년 %d월 %d일", year, month + 1, dayOfMonth);
                tvSelectedDate.setText(dateStr);

                // [임시 로직] 특정 날짜에 일정이 있다고 가정하고 보여주기
                // 실제로는 DB에서 해당 날짜의 일정을 조회해야 합니다.
                if (dayOfMonth == 15) {
                    tvDateEvent.setText("📚 '철학은 어떻게 삶의 무기가 되는가' 교환일");
                    tvDateEvent.setTextColor(getColor(R.color.brand_secondary)); // 강조색
                } else if (dayOfMonth == 20) {
                    tvDateEvent.setText("📢 독서 모임 정기 회의");
                } else {
                    tvDateEvent.setText("일정이 없는 날입니다.");
                    tvDateEvent.setTextColor(getColor(R.color.text_secondary)); // 기본색
                }
            }
        });
    }

    // ---------------------------------------------------------------
    // 교환 순서 리스트 (RecyclerView) 설정
    // ---------------------------------------------------------------
    private void setupExchangeList() {
        // 가로 스크롤 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvExchangeOrder.setLayoutManager(layoutManager);

        // TODO: 나중에 'ExchangeAdapter'를 만들어서 연결해야 리스트가 보입니다.
        // 현재는 데이터가 없으므로 비어있는 상태로 둡니다.
        // 예시:
        // ArrayList<String> names = new ArrayList<>();
        // names.add("김철수"); names.add("이영희"); names.add("박민수");
        // ExchangeAdapter adapter = new ExchangeAdapter(names);
        // rvExchangeOrder.setAdapter(adapter);
    }

    // ---------------------------------------------------------------
    // 하단 네비게이션 바 설정
    // ---------------------------------------------------------------
    private void setupBottomNavigation() {
        // 현재 탭(일정)을 활성화 상태로 표시
        bottomNav.setSelectedItemId(R.id.nav_schedule);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // 내 모임(Home)으로 이동
                startActivity(new Intent(ScheduleActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0); // 애니메이션 제거
                return true;
            } else if (itemId == R.id.nav_recruit) {
                // 모임 찾기(Recruit)로 이동
                startActivity(new Intent(ScheduleActivity.this, RecruitActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_schedule) {
                // 이미 현재 화면이므로 아무것도 안 함
                return true;
            } else if (itemId == R.id.nav_profile) {
                // 내 정보(Profile)로 이동
                startActivity(new Intent(ScheduleActivity.this, ProfileEditActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}
