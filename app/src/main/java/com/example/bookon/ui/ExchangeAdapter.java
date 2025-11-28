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

public class ExchangeAdapter extends RecyclerView.Adapter<ExchangeAdapter.ViewHolder> {

    private ArrayList<String> memberList;

    public ExchangeAdapter(ArrayList<String> memberList) {
        this.memberList = memberList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // [주의] 레이아웃 파일 이름이 item_exchange_order.xml 이라고 가정합니다.
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
            // 보내주신 XML의 ID와 연결
            tvMemberName = itemView.findViewById(R.id.tv_member_name);
            ivNextArrow = itemView.findViewById(R.id.iv_next_arrow);
        }
    }
}