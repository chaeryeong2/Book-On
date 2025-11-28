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

import com.example.bookon.R;
import com.example.bookon.data.Club;
import com.example.bookon.data.DataManager;

public class ClubActivity extends BaseActivity { // BaseActivity 상속 유지

    private int clubId;
    private Club currentClub;
    private String currentUserId;

    // UI 요소
    private LinearLayout layoutOwnerActions;
    private Button btnEdit, btnDelete, btnJoin, btnStart;
    private ImageButton btnBack;
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
        btnJoin = findViewById(R.id.btn_join_club);
        btnBack = findViewById(R.id.btn_back);
        btnStart = findViewById(R.id.btn_start_club);

        // ---------------------------------------------------------------
        // 버튼 리스너 설정
        // ---------------------------------------------------------------

        btnBack.setOnClickListener(v -> finish());

        btnDelete.setOnClickListener(v -> deleteClub());

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ClubActivity.this, EditClubActivity.class);
            intent.putExtra("club_id", clubId);
            startActivity(intent);
        });

        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(ClubActivity.this, ScheduleSetupActivity.class);
            intent.putExtra("club_id", clubId);
            startActivity(intent);
        });

        // [참여하기] 버튼 클릭 시 (더블 체크)
        btnJoin.setOnClickListener(v -> {
            if (currentClub == null) return;

            // 1. 상태 체크 (이미 시작된 모임인지)
            if ("진행중".equals(currentClub.getStatus())) {
                Toast.makeText(this, "이미 시작된 모임입니다.", Toast.LENGTH_SHORT).show();
                loadClubData();
                return;
            }

            // 2. 정원 체크
            int currentCount = DataManager.getInstance(this).getMemberCount(clubId);
            if (currentCount >= currentClub.getCapacity()) {
                Toast.makeText(this, "정원이 초과되어 가입할 수 없습니다.", Toast.LENGTH_SHORT).show();
                loadClubData(); // 화면 새로고침하여 버튼 상태 업데이트
                return;
            }

            // 3. 가입 진행
            DataManager.getInstance(this).joinClub(currentUserId, clubId);
            Toast.makeText(this, "참여 완료! 홈 화면에 추가되었습니다.", Toast.LENGTH_SHORT).show();
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
            // 현재 멤버 수 조회
            int currentCount = DataManager.getInstance(this).getMemberCount(clubId);

            tvName.setText(currentClub.getName());
            tvStatus.setText(currentClub.getStatus());
            tvBook.setText(currentClub.getCurrentBook());
            // 인원 표시 (현재 / 최대)
            tvCapacity.setText(currentCount + " / " + currentClub.getCapacity() + "명");
            tvDate.setText(currentClub.getStartDate() + " ~ " + currentClub.getEndDate());
            tvDesc.setText(currentClub.getDescription());

            // ----------------------------------------------------
            // [핵심 로직] 방장 여부 및 상태/정원에 따른 버튼 표시
            // ----------------------------------------------------
            if (currentClub.isOwner()) {
                // 1. 방장인 경우
                layoutOwnerActions.setVisibility(View.VISIBLE); // 수정/삭제 보임
                btnJoin.setVisibility(View.GONE); // 참여 버튼 숨김

                // '모집중'일 때만 시작 버튼 보임
                if ("모집중".equals(currentClub.getStatus())) {
                    btnStart.setVisibility(View.VISIBLE);
                } else {
                    btnStart.setVisibility(View.GONE);
                }

            } else {
                // 2. 방장이 아닌 경우
                layoutOwnerActions.setVisibility(View.GONE);
                btnStart.setVisibility(View.GONE);

                // 이미 참여했는지 확인
                boolean isMember = DataManager.getInstance(this).checkIsMember(currentUserId, clubId);

                if (isMember) {
                    // [CASE A] 이미 가입함
                    btnJoin.setText("이미 참여 중인 모임입니다");
                    btnJoin.setEnabled(false);
                    btnJoin.setVisibility(View.VISIBLE);
                    btnJoin.setBackgroundColor(getColor(R.color.text_secondary)); // 회색
                } else {
                    // 미가입 상태 -> 조건 체크

                    if ("진행중".equals(currentClub.getStatus())) {
                        // [CASE B] 이미 시작됨
                        btnJoin.setText("이미 시작된 모임입니다");
                        btnJoin.setEnabled(false);
                        btnJoin.setVisibility(View.VISIBLE);
                        btnJoin.setBackgroundColor(getColor(R.color.text_secondary));
                    }
                    else if (currentCount >= currentClub.getCapacity()) {
                        // [CASE C] 정원 초과
                        btnJoin.setText("모집 인원이 마감되었습니다");
                        btnJoin.setEnabled(false);
                        btnJoin.setVisibility(View.VISIBLE);
                        btnJoin.setBackgroundColor(getColor(R.color.text_secondary));
                    }
                    else {
                        // [CASE D] 가입 가능
                        btnJoin.setText("이 모임 참여하기");
                        btnJoin.setEnabled(true);
                        btnJoin.setVisibility(View.VISIBLE);
                        btnJoin.setBackgroundTintList(getColorStateList(R.color.brand_secondary)); // 민트색
                    }
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