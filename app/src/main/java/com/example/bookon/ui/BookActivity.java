package com.example.bookon.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookon.R;
import com.example.bookon.data.BookDBHelper;

public class BookActivity extends AppCompatActivity {
    private EditText etTitle, etAuthor;
    private Button btnRegister;
    private BookDBHelper dbHelper;
    private long clubId;  // 어느 모임의 책인지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book);
        dbHelper = new BookDBHelper(this);

        // 이전 화면(BookListActivity)에서 넘긴 club_id 받기
        clubId = getIntent().getIntExtra("club_id", -1);
        etTitle = findViewById(R.id.et_book_title);
        etAuthor = findViewById(R.id.et_book_author);
        btnRegister = findViewById(R.id.btn_register_book);
        btnRegister.setOnClickListener(v -> saveBook());
    }
    private void saveBook() {
        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "책 제목을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        String ownerName = prefs.getString("CurrentUserId", "익명");

        long id = dbHelper.insertBook(clubId, title, author, ownerName);

        if (id == -1) {
            Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "책 등록 완료", Toast.LENGTH_SHORT).show();
            finish(); // 이 액티비티 닫고 BookListActivity로 돌아감
        }
    }
}