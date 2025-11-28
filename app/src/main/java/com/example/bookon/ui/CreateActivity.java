package com.example.bookon.ui;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookon.R;
import com.example.bookon.data.Club;
import com.example.bookon.data.DataManager;

import java.util.Calendar;
import java.util.Locale;

public class CreateActivity extends BaseActivity { // BaseActivity 상속 유지

    // [추가] etTopic 변수 추가
    private EditText etClubName, etTopic, etCapacity, etStartDate, etEndDate, etDescription;
    private Button btnCreateClubSubmit;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // 1. 뷰 연결
        etClubName = findViewById(R.id.et_club_name);
        etTopic = findViewById(R.id.et_club_topic); // [추가] XML ID와 연결
        etCapacity = findViewById(R.id.et_capacity);
        etStartDate = findViewById(R.id.et_start_date);
        etEndDate = findViewById(R.id.et_end_date);
        etDescription = findViewById(R.id.et_description);
        btnCreateClubSubmit = findViewById(R.id.btn_create_club_submit);

        // 2. 날짜 선택 리스너 연결
        etStartDate.setOnClickListener(v -> showDatePickerDialog(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePickerDialog(etEndDate));

        // 3. 모임 만들기 버튼 클릭 리스너
        btnCreateClubSubmit.setOnClickListener(v -> {
            String name = etClubName.getText().toString().trim();
            String topic = etTopic.getText().toString().trim(); // [추가] 주제 가져오기
            String capacityStr = etCapacity.getText().toString().trim();
            String startDate = etStartDate.getText().toString().trim();
            String endDate = etEndDate.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            // 유효성 검사 (주제 포함)
            if (name.isEmpty() || topic.isEmpty() || capacityStr.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(CreateActivity.this, "필수 정보를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            int capacity;
            try {
                capacity = Integer.parseInt(capacityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(CreateActivity.this, "인원은 숫자만 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Club 객체 생성
            Club newClub = new Club(name, capacity, startDate, endDate, description);
            newClub.setTopic(topic); // [추가] 주제 설정

            // 현재 로그인한 사용자 ID 가져오기
            SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
            String currentUserId = prefs.getString("CurrentUserId", "testUser");

            // DB에 저장
            long result = DataManager.getInstance(CreateActivity.this).addNewClub(newClub, currentUserId);

            if (result != -1) {
                Toast.makeText(CreateActivity.this, "모임이 생성되었습니다!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(CreateActivity.this, "모임 생성 실패 (DB 오류)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog(final EditText editText) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                CreateActivity.this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth);
                    editText.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }
}