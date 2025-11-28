package com.example.bookon.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookon.R;
import com.example.bookon.data.Book;
import com.example.bookon.data.Club;
import com.example.bookon.data.DataManager; // [필수]
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity {
    private FloatingActionButton fabCreateBook;
    TextView clubName;
    private ListView lvBooks;
    private ImageButton btnBack;

    // [수정] BookDBHelper 변수 삭제
    // private BookDBHelper dbHelper;

    private BookAdapter adapter;
    private List<Book> bookList;
    private int clubId;
    private long lastClickTime = 0;
    private int lastClickPosition = -1;

    private String currentUserId;
    private String currentClubStatus = "모집중";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_list);

        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        currentUserId = prefs.getString("CurrentUserId", "");

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        clubName = findViewById(R.id.tv_club_name);
        String name = getIntent().getStringExtra("club_name");
        clubId = getIntent().getIntExtra("club_id", -1);
        clubName.setText("\uD83D\uDCDA " + name);

        // [수정] dbHelper 생성 코드 삭제
        // dbHelper = new BookDBHelper(this);

        lvBooks = findViewById(R.id.lv_book_list);

        bookList = new ArrayList<>();
        adapter = new BookAdapter(this, bookList);
        lvBooks.setAdapter(adapter);

        lvBooks.setOnItemClickListener((parent, view, position, id) -> {
            Book book = bookList.get(position);

            if ("진행중".equals(currentClubStatus)) {
                Toast.makeText(this, "모임이 이미 시작되어 책을 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!book.getOwnerId().equals(currentUserId)) {
                Toast.makeText(this, "본인이 등록한 책만 삭제할 수 있습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            long now = System.currentTimeMillis();

            if (position == lastClickPosition && (now - lastClickTime) < 400) {
                // [수정] DataManager를 통해 삭제
                int deleted = DataManager.getInstance(this).deleteBook(book.getId());

                if (deleted > 0) {
                    bookList.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(BookListActivity.this, "책을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BookListActivity.this, "삭제 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
                lastClickTime = 0;
                lastClickPosition = -1;

            } else {
                lastClickTime = now;
                lastClickPosition = position;
                Toast.makeText(BookListActivity.this, "한 번 더 탭하면 삭제됩니다.", Toast.LENGTH_SHORT).show();
            }
        });

        fabCreateBook = findViewById(R.id.fab_create_book);
        fabCreateBook.setOnClickListener(v -> {
            if ("진행중".equals(currentClubStatus)) {
                Toast.makeText(this, "모임이 시작되어 책을 추가할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(BookListActivity.this, BookActivity.class);
            intent.putExtra("club_id", clubId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClubInfo();
        loadBooks();
    }

    private void loadClubInfo() {
        if (clubId == -1) return;
        Club club = DataManager.getInstance(this).getClubById(clubId, "");
        if (club != null) {
            currentClubStatus = club.getStatus();
        }
    }

    private void loadBooks() {
        if (clubId == -1) return;

        bookList.clear();
        // [수정] DataManager를 통해 책 목록 조회
        bookList.addAll(DataManager.getInstance(this).getBooksByClub(clubId));
        adapter.notifyDataSetChanged();
    }
}