package com.example.bookon.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.bookon.R;
import com.example.bookon.data.LoginHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileEditActivity extends BaseActivity {
    EditText etNick, etIntro;
    Button btnSave;
    Button btnLogout;
    LoginHelper userHelper;
    Switch switchDark;
    long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_edit);

        etNick = findViewById(R.id.et_nickname);
        etIntro = findViewById(R.id.et_bio);
        btnSave = findViewById(R.id.btn_save_profile);
        btnLogout = findViewById(R.id.btn_logout);
        switchDark = findViewById(R.id.switch_dark_mode);
        userHelper = new LoginHelper(this);

        userId = getIntent().getLongExtra("userId", -1);
        loadMyInfo();

        btnSave.setOnClickListener(v -> saveMyInfo());

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            // [추가] 로그아웃 시 뒤로 가기 막기 (선택 사항)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // 기존 다크 모드 로직 유지
        boolean isDark = getSharedPreferences("settings", MODE_PRIVATE)
                .getBoolean("dark_mode", false);
        switchDark.setChecked(isDark);
        switchDark.setOnCheckedChangeListener((buttonView, checked) -> {
            if (checked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            // 설정 저장
            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit()
                    .putBoolean("dark_mode", checked)
                    .apply();
        });

        // ---------------------------------------------------------
        // [추가된 부분] 하단 네비게이션 바 설정
        // ---------------------------------------------------------
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // 현재 탭(내 정보) 활성화
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // 홈(내 서재)으로 이동
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0); // 애니메이션 제거
                return true;
            } else if (itemId == R.id.nav_recruit) {
                // 모집중으로 이동
                startActivity(new Intent(this, RecruitActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_schedule) {
                // 일정으로 이동
                startActivity(new Intent(this, ScheduleActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                // 이미 현재 화면임
                return true;
            }
            return false;
        });
    }

    private void loadMyInfo() {
        SQLiteDatabase db = userHelper.getReadableDatabase();
        String sql = "SELECT nickname, intro FROM users WHERE id=?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            etNick.setText(cursor.getString(0));
            etIntro.setText(cursor.getString(1)); // intro 없으면 null일 수 있음
        }
        cursor.close();
    }

    private void saveMyInfo() {
        String nick = etNick.getText().toString().trim();
        String intro = etIntro.getText().toString().trim();

        if (nick.isEmpty()) {
            Toast.makeText(this, "닉네임은 필수입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = userHelper.getWritableDatabase();
        String sql = "UPDATE users SET nickname=?, intro=? WHERE id=?";
        db.execSQL(sql, new Object[]{nick, intro, userId});

        Toast.makeText(this, "저장 완료", Toast.LENGTH_SHORT).show();
    }
}