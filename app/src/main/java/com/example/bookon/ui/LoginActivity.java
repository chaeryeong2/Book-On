package com.example.bookon.ui;

import android.content.Intent;
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
        progressBar= findViewById(R.id.pb_login_loading);
        userHelper = new LoginHelper(this);
        btnLogin.setOnClickListener(v->loginUser());

        signUp.setOnClickListener(v->{
            startActivity(new Intent(this, SignupActivity.class));
        });

    }
    private void loginUser(){
        progressBar.setVisibility(View.VISIBLE);
        String id = etEmail.getText().toString().trim();
        String pw = etPw.getText().toString().trim();

        if(id.isEmpty() || pw.isEmpty()){
            Toast.makeText(this, "아이디/비밀번호 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = userHelper.getReadableDatabase();

        String sql = "SELECT id FROM users WHERE username=? AND password=?";
        Cursor cursor = db.rawQuery(sql, new String[]{id, pw});

        if(cursor.moveToFirst()){
            // 로그인 성공
            progressBar.setVisibility(View.GONE);
            long userId = cursor.getLong(0);
            Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }
}