package com.example.bookon.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookon.R;
import com.example.bookon.data.DataManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class ScheduleSetupActivity extends BaseActivity {

    private int clubId;
    private String selectedDateStr = "";
    private ArrayList<String> memberList; // 멤버 ID 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_setup);

        clubId = getIntent().getIntExtra("club_id", -1);

        // 멤버 불러오기
        memberList = DataManager.getInstance(this).getClubMemberIds(clubId);

        Button btnDate = findViewById(R.id.btn_pick_date);
        EditText etWeeks = findViewById(R.id.et_weeks);
        Button btnRandom = findViewById(R.id.btn_random_order);
        TextView tvPreview = findViewById(R.id.tv_order_preview);
        Button btnConfirm = findViewById(R.id.btn_confirm_schedule);

        updatePreview(tvPreview); // 초기 순서 표시

        // 1. 날짜 선택
        btnDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                selectedDateStr = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                btnDate.setText("시작일: " + selectedDateStr);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // 2. 랜덤 섞기
        btnRandom.setOnClickListener(v -> {
            Collections.shuffle(memberList); // 리스트 섞기
            updatePreview(tvPreview);
            Toast.makeText(this, "순서가 변경되었습니다.", Toast.LENGTH_SHORT).show();
        });

        // 3. 확정 버튼
        btnConfirm.setOnClickListener(v -> {
            if (selectedDateStr.isEmpty() || etWeeks.getText().toString().isEmpty()) {
                Toast.makeText(this, "정보를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            int weeks = Integer.parseInt(etWeeks.getText().toString());

            // DB에 저장
            DataManager.getInstance(this).setClubSchedule(clubId, selectedDateStr, weeks, memberList);

            Toast.makeText(this, "모임이 시작되었습니다!", Toast.LENGTH_SHORT).show();
            // ScheduleActivity로 바로 이동하거나 홈으로 이동
            finish();
        });
    }

    private void updatePreview(TextView tv) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < memberList.size(); i++) {
            sb.append(i + 1).append(". ").append(memberList.get(i)); // 닉네임 대신 ID 표시 (나중에 닉네임으로 변경 가능)
            if (i < memberList.size() - 1) sb.append(" → ");
        }
        tv.setText(sb.toString());
    }
}