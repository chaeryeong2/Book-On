package com.example.bookon.ui;

import android.content.SharedPreferences;
import android.database.Cursor; // [추가]
import android.database.sqlite.SQLiteDatabase; // [추가]
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookon.R;
import com.example.bookon.data.BookDBHelper;
import com.example.bookon.data.LoginHelper; // [추가]

public class BookActivity extends AppCompatActivity {
    private EditText etTitle, etAuthor;
    private Button btnRegister;
    private ImageButton btnBack;
    private BookDBHelper dbHelper;
    private LoginHelper userHelper; // [추가] 유저 DB 접근용
    private long clubId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book);

        dbHelper = new BookDBHelper(this);
        userHelper = new LoginHelper(this); // [추가] 초기화

        clubId = getIntent().getIntExtra("club_id", -1);
        etTitle = findViewById(R.id.et_book_title);
        etAuthor = findViewById(R.id.et_book_author);
        btnRegister = findViewById(R.id.btn_register_book);
        btnBack = findViewById(R.id.btn_back);

        btnRegister.setOnClickListener(v -> saveBook());
        btnBack.setOnClickListener(v -> finish());
    }

    private void saveBook() {
        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "책 제목을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. 아이디 가져오기
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        String currentUserId = prefs.getString("CurrentUserId", "");

        // 2. 닉네임 가져오기
        String nickname = "익명";
        if (!currentUserId.isEmpty()) {
            SQLiteDatabase userDb = userHelper.getReadableDatabase();
            Cursor cursor = userDb.rawQuery("SELECT nickname FROM users WHERE username = ?", new String[]{currentUserId});
            if (cursor.moveToFirst()) {
                nickname = cursor.getString(0);
            }
            cursor.close();
        }

        // 3. [수정] 아이디와 닉네임 둘 다 저장
        long id = dbHelper.insertBook(clubId, title, author, nickname, currentUserId);

        if (id == -1) {
            Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "책 등록 완료", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}