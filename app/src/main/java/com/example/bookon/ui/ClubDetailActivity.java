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

// 참여중 모임 상세 화면 (책 목록 진입)
public class ClubDetailActivity extends BaseActivity {

    private int clubId;
    private Club currentClub;
    private String currentUserId;
    private LinearLayout layoutOwnerActions;
    private Button btnEdit, btnDelete, btnBook;
    private ImageButton btnBack;
    private TextView tvName, tvStatus, tvTopic, tvCapacity, tvDate, tvDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_detail);

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
        btnBook = findViewById(R.id.btn_book_list);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        btnDelete.setOnClickListener(v -> deleteClub());

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ClubDetailActivity.this, EditClubActivity.class);
            intent.putExtra("club_id", clubId);
            startActivity(intent);
        });

        // 책 목록 보기
        btnBook.setOnClickListener(v -> {
            if (currentClub != null) {
                Intent intent = new Intent(ClubDetailActivity.this, BookListActivity.class);
                intent.putExtra("club_id", clubId);
                intent.putExtra("club_name", currentClub.getName());
                startActivity(intent);
            }
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
            // 현재 인원 계산
            int currentCount = DataManager.getInstance(this).getMemberCount(clubId);

            tvName.setText(currentClub.getName());
            tvTopic.setText(currentClub.getTopic());
            tvCapacity.setText(currentCount + " / " + currentClub.getCapacity() + "명");
            tvDate.setText(currentClub.getStartDate() + " ~ " + currentClub.getEndDate());
            tvDesc.setText(currentClub.getDescription());

            // 상태 뱃지 텍스트 및 색상 동적 변경
            String displayStatus = currentClub.getStatus();

            // 인원이 꽉 찼으면 '마감됨'으로 표시 (진행중이 아닐 때만)
            if (!"진행중".equals(displayStatus) && currentCount >= currentClub.getCapacity()) {
                displayStatus = "마감됨";
            }

            tvStatus.setText(displayStatus);

            // 색상 변경 로직
            if ("모집중".equals(displayStatus)) {
                tvStatus.setBackgroundTintList(getColorStateList(R.color.brand_primary));
            } else if ("진행중".equals(displayStatus)) {
                tvStatus.setBackgroundTintList(getColorStateList(R.color.brand_secondary));
            } else { // 마감됨
                tvStatus.setBackgroundTintList(getColorStateList(R.color.text_secondary));
            }

            // 방장 여부에 따른 버튼 표시
            if (currentClub.isOwner()) {
                // 방장 -> 수정/삭제 버튼 보임
                layoutOwnerActions.setVisibility(View.VISIBLE);
            } else {
                // 일반 멤버 -> 수정/삭제 버튼 숨김
                layoutOwnerActions.setVisibility(View.GONE);
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