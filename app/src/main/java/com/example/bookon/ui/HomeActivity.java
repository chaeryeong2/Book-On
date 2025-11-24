package com.example.bookon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookon.R;
import com.example.bookon.data.Club;
import com.example.bookon.data.ClubAdapter;
import com.example.bookon.data.DataManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ListView lvClubList;
    private FloatingActionButton fabCreateClub;
    private ArrayList<Club> clubList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        lvClubList = findViewById(R.id.lv_club_list);
        fabCreateClub = findViewById(R.id.fab_create_club);

        lvClubList.setOnItemClickListener((parent, view, position, id) -> {
            Club clickedClub = clubList.get(position);

            Toast.makeText(HomeActivity.this, clickedClub.getName() + " 선택됨 (DB ID: " + clickedClub.getId() + ")", Toast.LENGTH_SHORT).show();
        });

        fabCreateClub.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CreateActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClubData();
    }

    // DB에서 데이터를 가져와 리스트뷰에 연결하는 함수
    private void loadClubData() {
        clubList = DataManager.getInstance(this).getClubList();

        ClubAdapter clubAdapter = new ClubAdapter(this, clubList);

        lvClubList.setAdapter(clubAdapter);

        if (clubList.isEmpty()) {
            Toast.makeText(this, "아직 모임이 없습니다. + 버튼을 눌러 만들어보세요!", Toast.LENGTH_SHORT).show();
        }
    }
}