package com.example.bookon.ui;

import android.content.Intent;
import android.content.SharedPreferences; // [추가]
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookon.R;
import com.example.bookon.data.LoginHelper;

// 로그인 화면
public class LoginActivity extends BaseActivity {
    EditText etEmail;
    EditText etPw;
    Button btnLogin;
    TextView signUp;
    LoginHelper userHelper;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = findViewById(R.id.et_email);
        etPw = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        signUp = findViewById(R.id.tv_go_to_signup);
        progressBar = findViewById(R.id.pb_login_loading);
        userHelper = new LoginHelper(this);

        btnLogin.setOnClickListener(v -> loginUser());

        signUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });
    }

    private void loginUser() {
        progressBar.setVisibility(View.VISIBLE);
        String idInput = etEmail.getText().toString().trim(); // 입력한 아이디 (String)
        String pwInput = etPw.getText().toString().trim();

        if (idInput.isEmpty() || pwInput.isEmpty()) {
            Toast.makeText(this, "아이디/비밀번호 입력해주세요.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE); // 로딩 끄기
            return;
        }

        SQLiteDatabase db = userHelper.getReadableDatabase();

        // username과 password가 일치하는지 확인
        String sql = "SELECT id FROM users WHERE username=? AND password=?";
        Cursor cursor = db.rawQuery(sql, new String[]{idInput, pwInput});

        if (cursor.moveToFirst()) {
            // [로그인 성공]
            progressBar.setVisibility(View.GONE);
            long dbId = cursor.getLong(0); // DB의 고유 숫자 ID (users 테이블의 id)

            getSharedPreferences("user", MODE_PRIVATE)
                    .edit()
                    .putLong("userId", dbId)
                    .apply(); // userId 저장
            Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show();

            // 로그인한 아이디를 폰에 저장
            SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("CurrentUserId", idInput);
            editor.apply(); // 저장 실행

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish(); // 로그인 화면 종료 (뒤로가기 방지)

        } else {
            // [로그인 실패]
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }
}