package com.example.bookon.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton; // [추가]
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookon.R;
import com.example.bookon.data.BookDBHelper;

public class BookActivity extends AppCompatActivity {
    private EditText etTitle, etAuthor;
    private Button btnRegister;
    private ImageButton btnBack; // [추가]
    private BookDBHelper dbHelper;
    private long clubId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book);
        dbHelper = new BookDBHelper(this);

        clubId = getIntent().getIntExtra("club_id", -1);

        // 뷰 연결
        etTitle = findViewById(R.id.et_book_title);
        etAuthor = findViewById(R.id.et_book_author);
        btnRegister = findViewById(R.id.btn_register_book);
        btnBack = findViewById(R.id.btn_back); // [추가]

        // 리스너 연결
        btnRegister.setOnClickListener(v -> saveBook());

        // [추가] 뒤로가기 버튼 기능
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
        String ownerName = prefs.getString("CurrentUserId", "익명");

        // BookDBHelper.insertBook은 long을 반환 (ID, 실패 시 -1)
        long id = dbHelper.insertBook(clubId, title, author, ownerName);

        if (id == -1) {
            Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "책 등록 완료", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}