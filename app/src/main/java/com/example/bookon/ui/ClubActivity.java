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

// 모집중 모임 상세 및 참여 화면
public class ClubActivity extends BaseActivity {

    private int clubId;
    private Club currentClub;
    private String currentUserId;
    private LinearLayout layoutOwnerActions;
    private Button btnEdit, btnDelete, btnJoin, btnStart;
    private ImageButton btnBack;
    private TextView tvName, tvStatus, tvTopic, tvCapacity, tvDate, tvDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        clubId = getIntent().getIntExtra("club_id", -1);
        if (clubId == -1) { finish(); return; }

        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        currentUserId = prefs.getString("CurrentUserId", "");

        tvName = findViewById(R.id.tv_detail_name);
        tvStatus = findViewById(R.id.tv_detail_status);
        tvTopic = findViewById(R.id.tv_topic);
        tvCapacity = findViewById(R.id.tv_detail_capacity);
        tvDate = findViewById(R.id.tv_detail_date);
        tvDesc = findViewById(R.id.tv_detail_desc);

        layoutOwnerActions = findViewById(R.id.layout_owner_actions);
        btnEdit = findViewById(R.id.btn_edit_club);
        btnDelete = findViewById(R.id.btn_delete_club);
        btnJoin = findViewById(R.id.btn_join_club);
        btnBack = findViewById(R.id.btn_back);
        btnStart = findViewById(R.id.btn_start_club);

        // 버튼 리스너 설정
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

        // 참여하기 버튼 클릭 시
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
            int currentCount = DataManager.getInstance(this).getMemberCount(clubId);

            tvName.setText(currentClub.getName());
            tvTopic.setText(currentClub.getTopic());
            tvCapacity.setText(currentCount + " / " + currentClub.getCapacity() + "명");
            tvDate.setText(currentClub.getStartDate() + " ~ " + currentClub.getEndDate());
            tvDesc.setText(currentClub.getDescription());

            String displayStatus = currentClub.getStatus(); // 기본은 DB 값

            // 1. 상태 결정 로직
            if ("진행중".equals(displayStatus)) {
                // 그대로 진행중
            } else if (currentCount >= currentClub.getCapacity()) {
                // 인원이 꽉 찼으면 강제로 '마감됨'으로 표시
                displayStatus = "마감됨";
            }

            tvStatus.setText(displayStatus);

            // 2. 색상 변경 로직
            if ("모집중".equals(displayStatus)) {
                // 모집중 -> 파란색
                tvStatus.setBackgroundTintList(getColorStateList(R.color.brand_primary));
            } else if ("진행중".equals(displayStatus)) {
                // 진행중 -> 민트색
                tvStatus.setBackgroundTintList(getColorStateList(R.color.brand_secondary));
            } else {
                // 마감 -> 회색
                tvStatus.setBackgroundTintList(getColorStateList(R.color.text_secondary));
            }

            // 버튼 표시 제어
            if (currentClub.isOwner()) {
                // [CASE 1] 방장
                layoutOwnerActions.setVisibility(View.VISIBLE);
                btnJoin.setVisibility(View.GONE);

                // '모집중' 또는 '마감됨' 상태일 때만 시작 버튼 보임
                if ("모집중".equals(displayStatus) || "마감됨".equals(displayStatus)) {
                    btnStart.setVisibility(View.VISIBLE);
                } else {
                    btnStart.setVisibility(View.GONE);
                }

            } else {
                // [CASE 2] 일반인
                layoutOwnerActions.setVisibility(View.GONE);
                btnStart.setVisibility(View.GONE);

                boolean isMember = DataManager.getInstance(this).checkIsMember(currentUserId, clubId);

                if (isMember) {
                    btnJoin.setText("이미 참여 중인 모임입니다");
                    btnJoin.setEnabled(false);
                    btnJoin.setVisibility(View.VISIBLE);
                    btnJoin.setBackgroundColor(getColor(R.color.text_secondary));
                } else {
                    // 미가입자 조건 체크
                    if ("진행중".equals(displayStatus)) {
                        btnJoin.setText("이미 시작된 모임입니다");
                        btnJoin.setEnabled(false);
                        btnJoin.setBackgroundColor(getColor(R.color.text_secondary));
                    }
                    else if ("마감됨".equals(displayStatus)) {
                        btnJoin.setText("모집 인원이 마감되었습니다");
                        btnJoin.setEnabled(false);
                        btnJoin.setBackgroundColor(getColor(R.color.text_secondary));
                    }
                    else {
                        btnJoin.setText("이 모임 참여하기");
                        btnJoin.setEnabled(true);
                        btnJoin.setVisibility(View.VISIBLE);
                        btnJoin.setBackgroundTintList(getColorStateList(R.color.brand_secondary));
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