package com.example.bookon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookon.R;
import com.example.bookon.data.Book;
import com.example.bookon.data.BookDBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity {
    private FloatingActionButton fabCreateClub;
    TextView clubName;
    private ListView lvBooks;

    private BookDBHelper dbHelper;
    private BookAdapter adapter;
    private List<Book> bookList;
    private int club_id;
    private long lastClickTime = 0;
    private int lastClickPosition = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_list);
        // 독서 모임 이름
        clubName = findViewById(R.id.tv_club_name);
        String name = getIntent().getStringExtra("club_name");
        club_id = getIntent().getIntExtra("club_id", -1);
        clubName.setText("\uD83D\uDCDA " + name);

        // LIST
        dbHelper = new BookDBHelper(this);
        lvBooks = findViewById(R.id.lv_book_list);

        bookList = new ArrayList<>();
        adapter = new BookAdapter(this, bookList);
        lvBooks.setAdapter(adapter);
        lvBooks.setOnItemClickListener((parent, view, position, id) -> {
            long now = System.currentTimeMillis();

            // 같은 아이템을 짧은 시간 안에 두 번 클릭한 경우 = 삭제
            if (position == lastClickPosition && (now - lastClickTime) < 400) { // 400ms 안에 두 번
                Book book = bookList.get(position);

                int deleted = dbHelper.deleteBook(book.getId());
                if (deleted > 0) {
                    bookList.remove(position);
                    adapter.notifyDataSetChanged();
                    // 삭제 완료 안내
                    Toast.makeText(BookListActivity.this, "책을 삭제했습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BookListActivity.this, "삭제 실패했습니다.", android.widget.Toast.LENGTH_SHORT).show();
                }

                // 상태 초기화
                lastClickTime = 0;
                lastClickPosition = -1;

            } else {
                // 첫 번째 클릭일 때: “한 번 더 누르면 삭제” 안내
                lastClickTime = now;
                lastClickPosition = position;
                Toast.makeText(BookListActivity.this, "한 번 더 탭하면 삭제됩니다.", Toast.LENGTH_SHORT).show();
            }
        });


        // 책 추가 버튼
        fabCreateClub = findViewById(R.id.fab_create_book);
        fabCreateClub.setOnClickListener(v -> {
            Intent intent = new Intent(BookListActivity.this, BookActivity.class);
            intent.putExtra("club_id", club_id);
            startActivity(intent);
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        loadBooks();
    }

    private void loadBooks() {
        if (club_id == -1) return;

        bookList.clear();
        bookList.addAll(dbHelper.getBooksByClub(club_id));
        adapter.notifyDataSetChanged();
    }
}