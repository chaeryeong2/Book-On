package com.example.bookon.ui;

import android.content.Intent;
import android.content.SharedPreferences; // [추가]
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bookon.R;
import com.example.bookon.data.Club;
import com.example.bookon.data.DataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

// 모집중인 모임 목록 화면
public class RecruitActivity extends BaseActivity {

    private ListView lvRecruitList;
    private FloatingActionButton fabCreateClub;
    private ArrayList<Club> clubList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit);

        lvRecruitList = findViewById(R.id.lv_recruit_list);
        fabCreateClub = findViewById(R.id.fab_create_club);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        lvRecruitList.setOnItemClickListener((parent, view, position, id) -> {
            Club clickedClub = clubList.get(position);
            Intent intent = new Intent(RecruitActivity.this, ClubActivity.class);
            intent.putExtra("club_id", clickedClub.getId());
            startActivity(intent);
        });

        fabCreateClub.setOnClickListener(v -> {
            Intent intent = new Intent(RecruitActivity.this, CreateActivity.class);
            startActivity(intent);
        });

        bottomNav.setSelectedItemId(R.id.nav_recruit);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(RecruitActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_recruit) {
                return true;
            } else if (itemId == R.id.nav_schedule) {
                startActivity(new Intent(RecruitActivity.this, ScheduleActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(RecruitActivity.this, ProfileEditActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecruitingData();
    }

    private void loadRecruitingData() {
        // 현재 로그인한 ID 가져오기
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        String currentUserId = prefs.getString("CurrentUserId", "testUser");

        // ID를 넘겨줌
        clubList = DataManager.getInstance(this).getRecruitingClubs(currentUserId);

        ClubAdapter adapter = new ClubAdapter(this, clubList);
        lvRecruitList.setAdapter(adapter);

        if (clubList.isEmpty()) {
            Toast.makeText(this, "현재 모집 중인 모임이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}