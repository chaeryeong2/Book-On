package com.example.bookon.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookon.R;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_TIME_OUT = 2000; // 2초

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. 다크 모드 설정 확인 및 적용 (기존 로직 유지)
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("DarkMode", false);
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        // UI 패딩 설정 (기존 코드 유지)
        if (findViewById(R.id.main) != null) {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // 2. [핵심 수정] 2초 후 로그인 상태 체크 후 화면 분기
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // SharedPreferences에서 CurrentUserId (로그인 상태) 확인
            String currentUserId = prefs.getString("CurrentUserId", null);

            Intent intent;

            if (currentUserId != null && !currentUserId.isEmpty()) {
                // ID가 저장되어 있으면 -> 자동 로그인 성공, 홈 화면으로 이동
                intent = new Intent(SplashActivity.this, HomeActivity.class);
            } else {
                // ID가 없으면 -> 로그인 화면으로 이동
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            // [추가] 화면 흔들림 방지 및 스택 정리
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish(); // 스플래시 화면 종료
        }, SPLASH_TIME_OUT);
    }
}