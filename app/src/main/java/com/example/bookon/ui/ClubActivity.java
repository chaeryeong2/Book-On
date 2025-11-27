package com.example.bookon.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookon.R;
import com.example.bookon.data.Club;
import com.example.bookon.data.DataManager;

public class ClubActivity extends AppCompatActivity {

    private int clubId;
    private Club currentClub;
    private String currentUserId;

    // UI 요소
    private LinearLayout layoutOwnerActions;
    private Button btnEdit, btnDelete, btnJoin; // btnJoin 추가
    private ImageButton btnBack; // [추가]
    private TextView tvName, tvStatus, tvBook, tvCapacity, tvDate, tvDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

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

        layoutOwnerActions = findViewById(R.id.layout_owner_actions);
        btnEdit = findViewById(R.id.btn_edit_club);
        btnDelete = findViewById(R.id.btn_delete_club);
        btnJoin = findViewById(R.id.btn_join_club); // [추가]
        btnBack = findViewById(R.id.btn_back); // [추가]

        // 버튼 리스너
        btnDelete.setOnClickListener(v -> deleteClub());
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ClubActivity.this, EditClubActivity.class);
            intent.putExtra("club_id", clubId);
            startActivity(intent);
        });

        // 뒤로가기 버튼 연결
        btnBack.setOnClickListener(v -> {
            finish(); // 현재 액티비티 종료 -> 이전 화면으로 복귀
        });

        // [추가] 참여하기 버튼 클릭 시
        btnJoin.setOnClickListener(v -> {
            // DB에 멤버로 추가
            DataManager.getInstance(this).joinClub(currentUserId, clubId);
            Toast.makeText(this, "참여 완료! 홈 화면에 추가되었습니다.", Toast.LENGTH_SHORT).show();
            // 화면 새로고침
            loadClubData();
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

            // ----------------------------------------------------
            // [핵심 로직] 방장이냐 아니냐에 따라 버튼 교체
            // ----------------------------------------------------
            if (currentClub.isOwner()) {
                // 1. 방장인 경우 -> 수정/삭제 보이기, 참여 버튼 숨기기
                layoutOwnerActions.setVisibility(View.VISIBLE);
                btnJoin.setVisibility(View.GONE);
            } else {
                // 2. 방장이 아닌 경우 -> 수정/삭제 숨기기
                layoutOwnerActions.setVisibility(View.GONE);

                // 이미 참여했는지 확인
                boolean isMember = DataManager.getInstance(this).checkIsMember(currentUserId, clubId);

                if (isMember) {
                    // 이미 멤버라면 버튼 숨기기 (또는 '참여중' 텍스트 표시)
                    btnJoin.setText("이미 참여 중인 모임입니다");
                    btnJoin.setEnabled(false);
                    btnJoin.setVisibility(View.VISIBLE);
                    btnJoin.setBackgroundColor(getColor(R.color.text_secondary)); // 회색 처리
                } else {
                    // 참여 안 했으면 버튼 보이기
                    btnJoin.setText("이 모임 참여하기");
                    btnJoin.setEnabled(true);
                    btnJoin.setVisibility(View.VISIBLE);
                    btnJoin.setBackgroundTintList(getColorStateList(R.color.brand_secondary));
                }
            }
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