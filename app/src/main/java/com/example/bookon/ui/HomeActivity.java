package com.example.bookon.ui;

import android.content.Intent;
import android.content.SharedPreferences; // [필수]
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
        // BaseActivity에서 다크모드 등 기본 설정 처리됨

        setContentView(R.layout.activity_home);

        long userIdFromIntent = getIntent().getLongExtra("userId", -1);
        // 필요하다면 userIdFromIntent를 사용 (프로필 등)

        // 1. 뷰 연결
        lvClubList = findViewById(R.id.lv_club_list);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // 2. 리스트 아이템 클릭 이벤트
        lvClubList.setOnItemClickListener((parent, view, position, id) -> {
            Club clickedClub = clubList.get(position);
            Intent intent = new Intent(HomeActivity.this, ClubDetailActivity.class);
            intent.putExtra("club_id", clickedClub.getId());
            startActivity(intent);
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
                Intent intent = new Intent(HomeActivity.this, ProfileEditActivity.class);
                intent.putExtra("userId", userIdFromIntent); // 필요 시 넘김
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
        // [수정] LoginActivity에서 저장한 아이디("CurrentUserId")를 꺼내옵니다.
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        String currentUserId = prefs.getString("CurrentUserId", ""); // 없으면 빈 문자열

        // [수정] 아이디를 DataManager에 전달 -> 내 모임만 필터링해서 가져옴
        clubList = DataManager.getInstance(this).getMyClubList(currentUserId);

        ClubAdapter clubAdapter = new ClubAdapter(this, clubList);
        lvClubList.setAdapter(clubAdapter);

        if (clubList.isEmpty()) {
            Toast.makeText(this, "참여 중인 모임이 없습니다. '모집중' 탭에서 모임을 찾아보세요!", Toast.LENGTH_LONG).show();
        }
    }
}