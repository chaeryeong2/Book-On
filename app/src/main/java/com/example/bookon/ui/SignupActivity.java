package com.example.bookon.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookon.R;
import com.example.bookon.data.LoginHelper;

public class SignupActivity extends AppCompatActivity {
    EditText etName;
    EditText etEmail;
    EditText etPw;
    TextView userLogin;
    Button btnSignUp;
    LoginHelper userHelper;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etName = findViewById(R.id.et_signup_name);
        etEmail = findViewById(R.id.et_signup_email);
        etPw = findViewById(R.id.et_signup_password);
        userLogin = findViewById(R.id.tv_go_to_login);
        btnSignUp = findViewById(R.id.btn_signup);
        progressBar= findViewById(R.id.pb_signup_loading);
        userHelper = new LoginHelper(this);

        userLogin.setOnClickListener(v->{
            startActivity(new Intent(this, LoginActivity.class));
        });
        btnSignUp.setOnClickListener(v-> joinUser());
    }
    private void joinUser(){
        progressBar.setVisibility(View.VISIBLE);
        String nick = etName.getText().toString().trim();
        String id = etEmail.getText().toString().trim();
        String pw = etPw.getText().toString().trim();

        if(nick.isEmpty() || id.isEmpty() || pw.isEmpty()){
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "닉네임/아이디/비밀번호 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = userHelper.getWritableDatabase();

        try{
            String sql = "INSERT INTO users (nickname, username, password) VALUES(?, ?, ?)";
            db.execSQL(sql, new Object[]{nick, id, pw});
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "회원가입 완료", Toast.LENGTH_SHORT).show();
            etName.setText("");
            etEmail.setText("");
            etPw.setText("");
        } catch (Exception e){
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
        }
    }

}