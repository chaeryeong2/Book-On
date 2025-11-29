package com.example.bookon.ui;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookon.R;
import com.example.bookon.data.DataManager; // [변경]
import com.example.bookon.data.LoginHelper;

// 모임에 책 등록 화면
public class BookActivity extends AppCompatActivity {
    private EditText etTitle, etAuthor;
    private Button btnRegister;
    private ImageButton btnBack;
    private LoginHelper userHelper;
    private long clubId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book);

        userHelper = new LoginHelper(this);

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

        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        String currentUserId = prefs.getString("CurrentUserId", "");

        String nickname = "익명";
        if (!currentUserId.isEmpty()) {
            SQLiteDatabase userDb = userHelper.getReadableDatabase();
            Cursor cursor = userDb.rawQuery("SELECT nickname FROM users WHERE username = ?", new String[]{currentUserId});
            if (cursor.moveToFirst()) {
                nickname = cursor.getString(0);
            }
            cursor.close();
        }

        long id = DataManager.getInstance(this).insertBook(clubId, title, author, nickname, currentUserId);

        if (id == -1) {
            Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "책 등록 완료", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}