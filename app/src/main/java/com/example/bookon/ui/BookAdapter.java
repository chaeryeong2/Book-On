package com.example.bookon.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.bookon.R;
import com.example.bookon.data.Book;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {
    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_book, parent, false);
        }

        Book book = getItem(position);

        TextView tvTitle = convertView.findViewById(R.id.tv_book_title);
        TextView tvAuthor = convertView.findViewById(R.id.tv_book_author);
        TextView tvMember = convertView.findViewById(R.id.tv_member);

        if (book != null) {
            tvTitle.setText(book.getTitle());
            tvAuthor.setText(book.getAuthor());
            tvMember.setText(book.getOwnerName());
        }

        return convertView;
    }
}
