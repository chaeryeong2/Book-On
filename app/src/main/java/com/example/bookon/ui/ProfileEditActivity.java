package com.example.bookon.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.bookon.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileEditActivity extends AppCompatActivity {

    private EditText etNickname, etBio;
    private Switch switchDarkMode;
    private Button btnSave, btnLogout;
    private TextView tvChangePhoto;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit); // XML 파일 연결

        // 1. 설정 저장소(SharedPreferences) 초기화
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // 2. 뷰 연결 (findViewById)
        etNickname = findViewById(R.id.et_nickname);
        etBio = findViewById(R.id.et_bio);
        switchDarkMode = findViewById(R.id.switch_dark_mode);
        btnSave = findViewById(R.id.btn_save_profile);
        btnLogout = findViewById(R.id.btn_logout);
        tvChangePhoto = findViewById(R.id.tv_change_photo);

        // 3. 다크 모드 초기 상태 설정
        // 저장된 값이 있거나, 현재 시스템이 이미 다크 모드라면 스위치를 켭니다.
        boolean isDarkModeSaved = sharedPreferences.getBoolean("DarkMode", false);
        if (isDarkModeSaved || AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            switchDarkMode.setChecked(true);
        }

        // ---------------------------------------------------------
        // 이벤트 리스너 설정
        // ---------------------------------------------------------

        // 저장 버튼
        btnSave.setOnClickListener(v -> {
            String nickname = etNickname.getText().toString();
            // TODO: DB에 닉네임과 정보 업데이트 로직 추가 필요
            Toast.makeText(ProfileEditActivity.this, "정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
        });

        // 로그아웃 버튼
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(ProfileEditActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            // TODO: 로그인 화면으로 이동하거나 finishAffinity() 호출
        });

        // 사진 변경 텍스트
        tvChangePhoto.setOnClickListener(v -> {
            Toast.makeText(this, "갤러리 기능 준비중입니다.", Toast.LENGTH_SHORT).show();
        });

        // 다크 모드 스위치 (앱 전체 테마 변경)
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if (isChecked) {
                // 다크 모드 켜기
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("DarkMode", true);
                Toast.makeText(this, "다크 모드 ON", Toast.LENGTH_SHORT).show();
            } else {
                // 다크 모드 끄기 (라이트 모드)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("DarkMode", false);
                Toast.makeText(this, "다크 모드 OFF", Toast.LENGTH_SHORT).show();
            }
            editor.apply(); // 설정 저장
            // 주의: 테마가 바뀌면 Activity가 자동으로 재생성(깜빡임) 됩니다. 정상입니다.
        });

        // ---------------------------------------------------------
        // 하단 네비게이션 설정
        // ---------------------------------------------------------
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // 현재 탭(내 정보) 활성화
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(ProfileEditActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_recruit) {
                startActivity(new Intent(ProfileEditActivity.this, RecruitActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_schedule) {
                startActivity(new Intent(ProfileEditActivity.this, ScheduleActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                // 이미 현재 화면
                return true;
            }
            return false;
        });
    }
}