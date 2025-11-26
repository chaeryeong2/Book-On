package com.example.bookon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bookon.R;
import com.example.bookon.data.Club;
import com.example.bookon.data.DataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity {

    private ListView lvClubList;
    private ArrayList<Club> clubList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long userId = getIntent().getLongExtra("userId", -1);
        // userId를 받아야만 내 정보에 닉네임, 한 줄 소개 로드 가능

        // [삭제됨] 기존에 있던 SharedPreferences 및 다크 모드 설정 코드는
        // BaseActivity의 super.onCreate()에서 자동으로 처리하므로 삭제했습니다.

        setContentView(R.layout.activity_home);

        // 1. 뷰 연결
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
                // 이미 홈 화면
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
                Intent intent = new Intent(HomeActivity.this, ProfileEditActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
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