package com.example.bookon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookon.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// [변경] extends AppCompatActivity -> extends BaseActivity
public class ScheduleActivity extends BaseActivity {

    private CalendarView calendarView;
    private TextView tvSelectedDate, tvDateEvent;
    private RecyclerView rvExchangeOrder;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // BaseActivity의 onCreate에서 다크 모드 설정이 먼저 수행됩니다.

        setContentView(R.layout.activity_schedule);

        // 1. 뷰 연결
        calendarView = findViewById(R.id.calendarView);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        tvDateEvent = findViewById(R.id.tv_date_event);
        rvExchangeOrder = findViewById(R.id.rv_exchange_order);
        bottomNav = findViewById(R.id.bottom_navigation);

        // 2. 캘린더 날짜 선택 이벤트 설정
        setupCalendar();

        // 3. 교환 순서 리스트 설정
        setupExchangeList();

        // 4. 하단 네비게이션 바 설정
        setupBottomNavigation();
    }

    // ---------------------------------------------------------------
    // 캘린더 관련 로직
    // ---------------------------------------------------------------
    private void setupCalendar() {
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // month는 0부터 시작하므로 +1
            String dateStr = String.format("%d년 %d월 %d일", year, month + 1, dayOfMonth);
            tvSelectedDate.setText(dateStr);

            // [임시 로직] 예시 데이터
            if (dayOfMonth == 15) {
                tvDateEvent.setText("📚 '철학은 어떻게 삶의 무기가 되는가' 교환일");
                tvDateEvent.setTextColor(getColor(R.color.brand_secondary));
            } else if (dayOfMonth == 20) {
                tvDateEvent.setText("📢 독서 모임 정기 회의");
                tvDateEvent.setTextColor(getColor(R.color.brand_primary)); // 색상 예시 변경
            } else {
                tvDateEvent.setText("일정이 없는 날입니다.");
                tvDateEvent.setTextColor(getColor(R.color.text_secondary));
            }
        });
    }

    // ---------------------------------------------------------------
    // 교환 순서 리스트 설정
    // ---------------------------------------------------------------
    private void setupExchangeList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvExchangeOrder.setLayoutManager(layoutManager);

        // TODO: 추후 Adapter 연결 필요
        // 현재는 빈 상태입니다.
    }

    // ---------------------------------------------------------------
    // 하단 네비게이션 바 설정
    // ---------------------------------------------------------------
    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_schedule);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(ScheduleActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_recruit) {
                startActivity(new Intent(ScheduleActivity.this, RecruitActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_schedule) {
                // 현재 화면
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(ScheduleActivity.this, ProfileEditActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}