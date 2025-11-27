package com.example.bookon.ui;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookon.R;
import com.example.bookon.data.Club;
import com.example.bookon.data.DataManager;

import java.util.Calendar;
import java.util.Locale;

public class EditClubActivity extends AppCompatActivity {

    private int clubId;
    private EditText etName, etStatus, etBook, etCapacity, etStartDate, etEndDate, etDesc;
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

        // 2. 뷰 연결
        etName = findViewById(R.id.et_edit_name);
        etStatus = findViewById(R.id.et_edit_status);
        etBook = findViewById(R.id.et_edit_book);
        etCapacity = findViewById(R.id.et_edit_capacity);
        etStartDate = findViewById(R.id.et_edit_start_date);
        etEndDate = findViewById(R.id.et_edit_end_date);
        etDesc = findViewById(R.id.et_edit_desc);
        btnSave = findViewById(R.id.btn_save_changes);

        // 3. 기존 데이터 불러와서 화면에 채우기
        loadCurrentData();

        // 4. 날짜 선택 이벤트 연결 (CreateActivity와 동일)
        etStartDate.setOnClickListener(v -> showDatePickerDialog(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePickerDialog(etEndDate));

        // 5. 저장 버튼 클릭 이벤트
        btnSave.setOnClickListener(v -> updateClub());
    }

    private void loadCurrentData() {
        // 현재 유저 ID 가져오기 (DB 조회 시 필요)
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        String currentUserId = prefs.getString("CurrentUserId", "");

        // DB에서 해당 ID의 클럽 정보 가져오기
        currentClub = DataManager.getInstance(this).getClubById(clubId, currentUserId);

        if (currentClub != null) {
            // 가져온 데이터를 EditText에 채워넣기 (자동 입력)
            etName.setText(currentClub.getName());
            etStatus.setText(currentClub.getStatus());
            etBook.setText(currentClub.getCurrentBook());
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
        String status = etStatus.getText().toString().trim();
        String book = etBook.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();

        if (name.isEmpty() || status.isEmpty()) {
            Toast.makeText(this, "모임 이름과 상태는 필수입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(etCapacity.getText().toString().trim());
        } catch (NumberFormatException e) {
            capacity = 0; // 혹은 에러 처리
        }

        // 객체 내용 업데이트
        currentClub.setName(name);
        currentClub.setStatus(status);
        currentClub.setCurrentBook(book);
        currentClub.setCapacity(capacity);
        currentClub.setStartDate(startDate);
        currentClub.setEndDate(endDate);
        currentClub.setDescription(desc);

        // DB 업데이트 호출
        boolean isUpdated = DataManager.getInstance(this).updateClub(currentClub);

        if (isUpdated) {
            Toast.makeText(this, "수정되었습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 수정 마치고 이전 화면(상세 페이지)으로 복귀
        } else {
            Toast.makeText(this, "수정 실패", Toast.LENGTH_SHORT).show();
        }
    }

    // 날짜 선택 다이얼로그
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