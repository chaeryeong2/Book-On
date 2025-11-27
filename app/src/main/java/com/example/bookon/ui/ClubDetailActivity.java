package com.example.bookon.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.bookon.R;
import com.example.bookon.data.Club;
import com.example.bookon.data.DataManager;

public class ClubDetailActivity extends BaseActivity {

    private int clubId;
    private Club currentClub;
    private String currentUserId;

    // UI 요소
    private Button btnEdit, btnDelete, btnBook;
    private ImageButton btnBack; // [추가]
    private TextView tvName, tvStatus, tvBook, tvCapacity, tvDate, tvDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_detail);

        clubId = getIntent().getIntExtra("club_id", -1);
        if (clubId == -1) { finish(); return; }

        // 현재 유저 ID 가져오기
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        currentUserId = prefs.getString("CurrentUserId", "");

        // 뷰 연결
        tvName = findViewById(R.id.tv_detail_name);
        tvStatus = findViewById(R.id.tv_detail_status);
        tvBook = findViewById(R.id.tv_detail_book);
        tvCapacity = findViewById(R.id.tv_detail_capacity);
        tvDate = findViewById(R.id.tv_detail_date);
        tvDesc = findViewById(R.id.tv_detail_desc);

        btnEdit = findViewById(R.id.btn_edit_club);
        btnDelete = findViewById(R.id.btn_delete_club);
        btnBook = findViewById(R.id.btn_book_list); // [추가]
        btnBack = findViewById(R.id.btn_back); // [추가]

        // 버튼 리스너
        btnDelete.setOnClickListener(v -> deleteClub());
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ClubDetailActivity.this, EditClubActivity.class);
            intent.putExtra("club_id", clubId);
            startActivity(intent);
        });

        // 책 목록 보기
        btnBook.setOnClickListener(v->{
            Intent intent = new Intent(ClubDetailActivity.this, BookListActivity.class);
            intent.putExtra("club_id", clubId);
            intent.putExtra("club_name", currentClub.getName());
            startActivity(intent);
        });

        // 뒤로가기 버튼 연결
        btnBack.setOnClickListener(v -> {
            finish(); // 현재 액티비티 종료 -> 이전 화면으로 복귀
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClubData();
    }

    private void loadClubData() {
        currentClub = DataManager.getInstance(this).getClubById(clubId, currentUserId);

        if (currentClub != null) {
            tvName.setText(currentClub.getName());
            tvStatus.setText(currentClub.getStatus());
            tvBook.setText(currentClub.getCurrentBook());
            tvCapacity.setText(currentClub.getCapacity() + "명");
            tvDate.setText(currentClub.getStartDate() + " ~ " + currentClub.getEndDate());
            tvDesc.setText(currentClub.getDescription());
        }
    }

    private void deleteClub() {
        new AlertDialog.Builder(this)
                .setTitle("모임 삭제")
                .setMessage("정말로 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> {
                    DataManager.getInstance(this).deleteClub(clubId);
                    Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("취소", null)
                .show();
    }
}