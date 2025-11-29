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

// 메인 화면 (내 모임 탭)
public class HomeActivity extends BaseActivity {

    private ListView lvClubList;
    private ArrayList<Club> clubList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        long userIdFromIntent = getIntent().getLongExtra("userId", -1);

        lvClubList = findViewById(R.id.lv_club_list);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        lvClubList.setOnItemClickListener((parent, view, position, id) -> {
            Club clickedClub = clubList.get(position);
            Intent intent = new Intent(HomeActivity.this, ClubDetailActivity.class);
            intent.putExtra("club_id", clickedClub.getId());
            startActivity(intent);
        });

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
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        String currentUserId = prefs.getString("CurrentUserId", "");

        clubList = DataManager.getInstance(this).getMyClubList(currentUserId);

        ClubAdapter clubAdapter = new ClubAdapter(this, clubList);
        lvClubList.setAdapter(clubAdapter);

        if (clubList.isEmpty()) {
            Toast.makeText(this, "참여 중인 모임이 없습니다. '모집중' 탭에서 모임을 찾아보세요!", Toast.LENGTH_LONG).show();
        }
    }
}