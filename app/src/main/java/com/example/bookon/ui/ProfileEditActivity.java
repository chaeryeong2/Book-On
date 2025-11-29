package com.example.bookon.ui;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.bookon.data.DataManager; // [변경]
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

        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("user", MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences("AppSettings", MODE_PRIVATE).edit().remove("CurrentUserId").apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        boolean isDark = getSharedPreferences("AppSettings", MODE_PRIVATE).getBoolean("dark_mode", false);
        switchDark.setChecked(isDark);
        switchDark.setOnCheckedChangeListener((buttonView, checked) -> {
            if (checked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            getSharedPreferences("AppSettings", MODE_PRIVATE).edit().putBoolean("dark_mode", checked).apply();
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(0, 0); return true;
            } else if (itemId == R.id.nav_recruit) {
                startActivity(new Intent(this, RecruitActivity.class));
                overridePendingTransition(0, 0); return true;
            } else if (itemId == R.id.nav_schedule) {
                startActivity(new Intent(this, ScheduleActivity.class));
                overridePendingTransition(0, 0); return true;
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

    private void saveMyInfo() {
        String newNick = etNick.getText().toString().trim();
        String newIntro = etIntro.getText().toString().trim();

        if (newNick.isEmpty()) {
            Toast.makeText(this, "닉네임은 필수입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = userHelper.getWritableDatabase();

        String oldNick = "";
        Cursor cursor = db.rawQuery("SELECT nickname FROM users WHERE id=?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            oldNick = cursor.getString(0);
        }
        cursor.close();

        String sql = "UPDATE users SET nickname=?, intro=? WHERE id=?";
        db.execSQL(sql, new Object[]{newNick, newIntro, userId});

        // 닉네임이 변경되었을 때 책 테이블의 작성자 이름도 업데이트
        if (!oldNick.isEmpty() && !oldNick.equals(newNick)) {
            SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
            String currentLoginId = prefs.getString("CurrentUserId", "");

            if (!currentLoginId.isEmpty()) {
                // [수정] DataManager를 통해 책 주인 이름 변경
                DataManager.getInstance(this).updateBookOwnerName(currentLoginId, newNick);
            }
        }

        Toast.makeText(this, "저장 완료", Toast.LENGTH_SHORT).show();
    }
}