package com.example.bookon.ui;

import android.content.Intent;
import android.content.SharedPreferences; // [필수]
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
import com.example.bookon.data.BookDBHelper;
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

        userId = getSharedPreferences("user", MODE_PRIVATE)
                .getLong("userId", -1);

        loadMyInfo();

        btnSave.setOnClickListener(v -> saveMyInfo());

        // 로그아웃
        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("user", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            // 자동 로그인 키 삭제
            getSharedPreferences("AppSettings", MODE_PRIVATE)
                    .edit()
                    .remove("CurrentUserId")
                    .apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // 다크 모드 설정
        boolean isDark = getSharedPreferences("settings", MODE_PRIVATE)
                .getBoolean("dark_mode", false);
        switchDark.setChecked(isDark);
        switchDark.setOnCheckedChangeListener((buttonView, checked) -> {
            if (checked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit()
                    .putBoolean("dark_mode", checked)
                    .apply();
        });

        // 하단 네비게이션
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_recruit) {
                startActivity(new Intent(this, RecruitActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_schedule) {
                startActivity(new Intent(this, ScheduleActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
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
            etIntro.setText(cursor.getString(1));
        }
        cursor.close();
    }

    // [수정됨] 닉네임 변경 시 로직 수정
    private void saveMyInfo() {
        String newNick = etNick.getText().toString().trim();
        String newIntro = etIntro.getText().toString().trim();

        if (newNick.isEmpty()) {
            Toast.makeText(this, "닉네임은 필수입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = userHelper.getWritableDatabase();

        // 1. 프로필(users) 테이블 업데이트
        String sql = "UPDATE users SET nickname=?, intro=? WHERE id=?";
        db.execSQL(sql, new Object[]{newNick, newIntro, userId});

        // 2. 책(book) 테이블 업데이트
        // (내 아이디를 가진 책을 찾아서 닉네임을 새 것으로 변경)

        // 현재 로그인 아이디 가져오기
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        String currentLoginId = prefs.getString("CurrentUserId", "");

        if (!currentLoginId.isEmpty()) {
            BookDBHelper bookHelper = new BookDBHelper(this);
            // "내 아이디(currentLoginId)"인 책들의 주인 이름을 "newNick"으로 바꿔라
            bookHelper.updateOwnerName(currentLoginId, newNick);
        }

        Toast.makeText(this, "저장 완료", Toast.LENGTH_SHORT).show();
    }
}