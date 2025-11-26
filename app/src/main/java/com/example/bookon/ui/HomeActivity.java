package com.example.bookon.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.bookon.R;
import com.example.bookon.data.Club;
import com.example.bookon.data.DataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ListView lvClubList;
    private ArrayList<Club> clubList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다크 모드 설정 적용
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("DarkMode", false);
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_home);

        // 1. 뷰 연결 (플로팅 버튼 제거됨)
        lvClubList = findViewById(R.id.lv_club_list);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // 2. 리스트 아이템 클릭 이벤트
        lvClubList.setOnItemClickListener((parent, view, position, id) -> {
            Club clickedClub = clubList.get(position);
            Toast.makeText(HomeActivity.this, clickedClub.getName() + " 선택됨", Toast.LENGTH_SHORT).show();
        });

        // 3. 하단 네비게이션 설정
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_recruit) {
                startActivity(new Intent(HomeActivity.this, RecruitActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_schedule) {
                startActivity(new Intent(HomeActivity.this, ScheduleActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(HomeActivity.this, ProfileEditActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClubData();
    }

    private void loadClubData() {
        clubList = DataManager.getInstance(this).getMyClubList();
        ClubAdapter clubAdapter = new ClubAdapter(this, clubList);
        lvClubList.setAdapter(clubAdapter);

        if (clubList.isEmpty()) {
            Toast.makeText(this, "참여 중인 모임이 없습니다. '모집중' 탭에서 모임을 찾아보세요!", Toast.LENGTH_LONG).show();
        }
    }
}