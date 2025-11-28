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
import com.example.bookon.data.BookDBHelper;
import com.example.bookon.data.Club; // [추가]
import com.example.bookon.data.DataManager; // [추가]
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity {
    private FloatingActionButton fabCreateBook;
    TextView clubName;
    private ListView lvBooks;
    private ImageButton btnBack;

    private BookDBHelper dbHelper;
    private BookAdapter adapter;
    private List<Book> bookList;
    private int clubId;
    private long lastClickTime = 0;
    private int lastClickPosition = -1;

    private String currentUserId;
    private String currentClubStatus = "모집중"; // [추가] 모임 상태 저장 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_list);

        // 1. 현재 로그인한 사용자 ID 가져오기
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        currentUserId = prefs.getString("CurrentUserId", "");

        // 2. 뷰 연결
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        clubName = findViewById(R.id.tv_club_name);
        String name = getIntent().getStringExtra("club_name");
        clubId = getIntent().getIntExtra("club_id", -1);
        clubName.setText("\uD83D\uDCDA " + name);

        dbHelper = new BookDBHelper(this);
        lvBooks = findViewById(R.id.lv_book_list);

        bookList = new ArrayList<>();
        adapter = new BookAdapter(this, bookList);
        lvBooks.setAdapter(adapter);

        // 3. 리스트 클릭 이벤트 (삭제 로직)
        lvBooks.setOnItemClickListener((parent, view, position, id) -> {
            Book book = bookList.get(position);

            // -----------------------------------------------------------
            // [추가] 1. 모임이 이미 시작되었는지 확인 (진행중이면 삭제 불가)
            // -----------------------------------------------------------
            if ("진행중".equals(currentClubStatus)) {
                Toast.makeText(this, "모임이 이미 시작되어 책을 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return; // 함수 종료
            }

            // [핵심 수정] 닉네임이 아니라 '아이디(ownerId)'로 본인 확인!
            // 닉네임을 바꿔도 ownerId는 안 바뀌므로 안전함.
            if (!book.getOwnerId().equals(currentUserId)) {
                Toast.makeText(this, "본인이 등록한 책만 삭제할 수 있습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- 삭제 진행 ---
            long now = System.currentTimeMillis();

            if (position == lastClickPosition && (now - lastClickTime) < 400) {
                int deleted = dbHelper.deleteBook(book.getId());
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
            // [추가] 모임 시작됐으면 책 추가도 막고 싶다면 여기에 조건 추가
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
        loadClubInfo(); // [추가] 모임 상태 불러오기
        loadBooks();
    }

    // [추가] DB에서 모임 상태(진행중/모집중)를 확인하는 함수
    private void loadClubInfo() {
        if (clubId == -1) return;
        // DataManager를 이용해 클럽 정보 가져오기
        Club club = DataManager.getInstance(this).getClubById(clubId, "");
        if (club != null) {
            currentClubStatus = club.getStatus(); // 상태 업데이트
        }
    }

    private void loadBooks() {
        if (clubId == -1) return;

        bookList.clear();
        bookList.addAll(dbHelper.getBooksByClub(clubId));
        adapter.notifyDataSetChanged();
    }
}