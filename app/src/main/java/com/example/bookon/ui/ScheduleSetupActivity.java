package com.example.bookon.ui;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookon.R;
import com.example.bookon.data.DataManager;
import com.example.bookon.data.LoginHelper; // [추가]

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

// 모임 시작을 위한 일정 및 순서 설정 화면
public class ScheduleSetupActivity extends BaseActivity {

    private int clubId;
    private String selectedDateStr = "";
    private ArrayList<String> memberList;
    private LoginHelper loginHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_setup);

        loginHelper = new LoginHelper(this);

        clubId = getIntent().getIntExtra("club_id", -1);

        memberList = DataManager.getInstance(this).getClubMemberIds(clubId);

        Button btnDate = findViewById(R.id.btn_pick_date);
        EditText etWeeks = findViewById(R.id.et_weeks);
        Button btnRandom = findViewById(R.id.btn_random_order);
        TextView tvPreview = findViewById(R.id.tv_order_preview);
        Button btnConfirm = findViewById(R.id.btn_confirm_schedule);

        updatePreview(tvPreview);

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
            updatePreview(tvPreview);        // 화면 갱신
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
            finish();
        });
    }

    // ID 리스트를 돌면서 닉네임으로 변환하여 표시
    private void updatePreview(TextView tv) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < memberList.size(); i++) {
            String memberId = memberList.get(i);
            String nickname = getNickname(memberId); // ID -> 닉네임 변환

            sb.append(i + 1).append(". ").append(nickname);

            if (i < memberList.size() - 1) sb.append(" → ");
        }
        tv.setText(sb.toString());
    }

    // ID(이메일)로 닉네임을 조회하는 헬퍼 메서드
    private String getNickname(String userId) {
        SQLiteDatabase db = loginHelper.getReadableDatabase();
        String nickname = userId; // 기본값은 아이디(못 찾을 경우 대비)

        // users 테이블에서 username(아이디)이 일치하는 행의 nickname을 가져옴
        Cursor cursor = db.rawQuery("SELECT nickname FROM users WHERE username = ?", new String[]{userId});

        if (cursor.moveToFirst()) {
            nickname = cursor.getString(0);
        }
        cursor.close();
        return nickname;
    }
}