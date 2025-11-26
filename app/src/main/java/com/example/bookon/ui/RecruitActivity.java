package com.example.bookon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookon.R;
import com.example.bookon.data.Club;
import com.example.bookon.data.DataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton; // [추가]

import java.util.ArrayList;

public class RecruitActivity extends AppCompatActivity {

    private ListView lvRecruitList;
    private FloatingActionButton fabCreateClub; // [추가]
    private ArrayList<Club> clubList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit);

        // 1. 뷰 연결
        lvRecruitList = findViewById(R.id.lv_recruit_list);
        fabCreateClub = findViewById(R.id.fab_create_club); // [추가] XML에 이 ID가 있어야 함
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // 2. 리스트 클릭 이벤트
        lvRecruitList.setOnItemClickListener((parent, view, position, id) -> {
            Club clickedClub = clubList.get(position);
            Toast.makeText(this, clickedClub.getName() + " 구경하기", Toast.LENGTH_SHORT).show();
        });

        // 3. [추가] 모임 만들기 버튼 클릭 이벤트 (Home에서 이사옴)
        fabCreateClub.setOnClickListener(v -> {
            Intent intent = new Intent(RecruitActivity.this, CreateActivity.class);
            startActivity(intent);
        });

        // 4. 하단 네비게이션 설정
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
        clubList = DataManager.getInstance(this).getRecruitingClubs();
        ClubAdapter adapter = new ClubAdapter(this, clubList);
        lvRecruitList.setAdapter(adapter);

        if (clubList.isEmpty()) {
            Toast.makeText(this, "현재 모집 중인 모임이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}