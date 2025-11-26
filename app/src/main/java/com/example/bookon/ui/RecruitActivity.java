package com.example.bookon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bookon.R;
import com.example.bookon.data.Club;
import com.example.bookon.data.DataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

// [변경] AppCompatActivity -> BaseActivity 상속 변경
public class RecruitActivity extends BaseActivity {

    private ListView lvRecruitList;
    private FloatingActionButton fabCreateClub;
    private ArrayList<Club> clubList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // BaseActivity의 super.onCreate()에서 다크 모드 설정을 미리 처리합니다.

        setContentView(R.layout.activity_recruit);

        // 1. 뷰 연결
        lvRecruitList = findViewById(R.id.lv_recruit_list);
        fabCreateClub = findViewById(R.id.fab_create_club); // 주의: activity_recruit.xml에 이 ID가 있어야 앱이 죽지 않습니다.
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // 2. 리스트 클릭 이벤트
        lvRecruitList.setOnItemClickListener((parent, view, position, id) -> {
            Club clickedClub = clubList.get(position);
            Toast.makeText(this, clickedClub.getName() + " 구경하기", Toast.LENGTH_SHORT).show();
            // 상세 페이지 이동 로직은 추후 구현
        });

        // 3. 모임 만들기 버튼 클릭 이벤트
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
                // 이미 현재 화면
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