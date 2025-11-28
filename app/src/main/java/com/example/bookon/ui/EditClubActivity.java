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

public class EditClubActivity extends BaseActivity {

    private int clubId;
    // [수정] etStatus 삭제, etBook -> etTopic 변경
    private EditText etName, etTopic, etCapacity, etStartDate, etEndDate, etDesc;
    private Button btnSave;
    private Club currentClub;
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_club);

        // 1. Intent로 넘겨받은 club_id 확인
        clubId = getIntent().getIntExtra("club_id", -1);
        if (clubId == -1) {
            Toast.makeText(this, "오류: 모임 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. 뷰 연결 (etStatus 제거됨)
        etName = findViewById(R.id.et_edit_name);
        etTopic = findViewById(R.id.et_edit_topic); // [수정] XML ID와 일치시켜야 함
        etCapacity = findViewById(R.id.et_edit_capacity);
        etStartDate = findViewById(R.id.et_edit_start_date);
        etEndDate = findViewById(R.id.et_edit_end_date);
        etDesc = findViewById(R.id.et_edit_desc);
        btnSave = findViewById(R.id.btn_save_changes);

        // 3. 기존 데이터 불러와서 화면에 채우기
        loadCurrentData();

        // 4. 날짜 선택 이벤트 연결
        etStartDate.setOnClickListener(v -> showDatePickerDialog(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePickerDialog(etEndDate));

        // 5. 저장 버튼 클릭 이벤트
        btnSave.setOnClickListener(v -> updateClub());
    }

    private void loadCurrentData() {
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        String currentUserId = prefs.getString("CurrentUserId", "");

        currentClub = DataManager.getInstance(this).getClubById(clubId, currentUserId);

        if (currentClub != null) {
            etName.setText(currentClub.getName());

            // [수정] 주제(Topic) 불러오기
            etTopic.setText(currentClub.getTopic());

            // [참고] status는 화면에 표시하지 않고, 객체 내부에만 유지함

            etCapacity.setText(String.valueOf(currentClub.getCapacity()));
            etStartDate.setText(currentClub.getStartDate());
            etEndDate.setText(currentClub.getEndDate());
            etDesc.setText(currentClub.getDescription());
        } else {
            Toast.makeText(this, "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateClub() {
        // 입력된 값 가져오기
        String name = etName.getText().toString().trim();
        String topic = etTopic.getText().toString().trim(); // [수정] 주제 가져오기
        String desc = etDesc.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();

        // [수정] 유효성 검사 (Status 제거, Topic 추가)
        if (name.isEmpty() || topic.isEmpty()) {
            Toast.makeText(this, "모임 이름과 주제는 필수입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(etCapacity.getText().toString().trim());
        } catch (NumberFormatException e) {
            capacity = 0;
        }

        // 객체 내용 업데이트
        currentClub.setName(name);
        currentClub.setTopic(topic); // [수정] 주제 업데이트
        // currentClub.setStatus(...) -> 기존 상태 유지 (수정 안 함)

        currentClub.setCapacity(capacity);
        currentClub.setStartDate(startDate);
        currentClub.setEndDate(endDate);
        currentClub.setDescription(desc);

        // DB 업데이트 호출
        boolean isUpdated = DataManager.getInstance(this).updateClub(currentClub);

        if (isUpdated) {
            Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 상세 화면으로 복귀
        } else {
            Toast.makeText(this, "수정 실패", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog(final EditText editText) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EditClubActivity.this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth);
                    editText.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }
}