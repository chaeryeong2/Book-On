package com.example.bookon.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookon.R;

import java.util.ArrayList;

// 책 교환 순서 RecyclerView 어댑터
public class ExchangeAdapter extends RecyclerView.Adapter<ExchangeAdapter.ViewHolder> {

    private ArrayList<String> memberList;

    public ExchangeAdapter(ArrayList<String> memberList) {
        this.memberList = memberList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exchange_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String memberName = memberList.get(position);

        holder.tvMemberName.setText(memberName);

        // 마지막 순서인 사람은 다음 화살표 숨기기
        if (position == memberList.size() - 1) {
            holder.ivNextArrow.setVisibility(View.GONE);
        } else {
            holder.ivNextArrow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMemberName;
        ImageView ivNextArrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMemberName = itemView.findViewById(R.id.tv_member_name);
            ivNextArrow = itemView.findViewById(R.id.iv_next_arrow);
        }
    }
}