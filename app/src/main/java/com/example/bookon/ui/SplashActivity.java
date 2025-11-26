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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. [핵심] 앱 실행 시 다크 모드 설정 확인 및 적용
        // 화면이 그려지기 전에 먼저 테마를 결정해야 깜빡임이 적습니다.
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("DarkMode", false);
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        // 기존 UI 패딩 설정 코드 유지
        // (XML의 최상위 레이아웃 ID가 'main'이어야 에러가 안 납니다)
        if (findViewById(R.id.main) != null) {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // 테스트 위해 그냥 홈화면으로 이동하게 해놨어요
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // 스플래시 화면을 종료하여 뒤로가기 못하게 함
        }, 2000); // 2000ms = 2초
    }
}