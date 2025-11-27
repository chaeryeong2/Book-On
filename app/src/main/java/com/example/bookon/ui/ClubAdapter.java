package com.example.bookon.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookon.R;
import com.example.bookon.data.Club; // 패키지명 확인

import java.util.ArrayList;

public class ClubAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Club> clubList;

    public ClubAdapter(Context context, ArrayList<Club> clubList) {
        this.context = context;
        this.clubList = clubList;
    }

    @Override
    public int getCount() { return clubList.size(); }

    @Override
    public Object getItem(int position) { return clubList.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_club_card, parent, false);
        }

        TextView tvClubName = convertView.findViewById(R.id.tv_club_name);
        TextView tvCurrentBook = convertView.findViewById(R.id.tv_current_book);
        TextView tvMemberCount = convertView.findViewById(R.id.tv_member_count);
        TextView tvClubStatus = convertView.findViewById(R.id.tv_club_status);

        // [추가] 방장 배지 뷰 연결
        TextView tvOwnerBadge = convertView.findViewById(R.id.tv_owner_badge);

        ImageView ivBookIcon = convertView.findViewById(R.id.iv_book_icon);
        ImageView ivMemberIcon = convertView.findViewById(R.id.iv_member_icon);

        Club club = clubList.get(position);

        tvClubName.setText(club.getName());
        tvCurrentBook.setText("현재 책: " + club.getCurrentBook());
        tvMemberCount.setText("정원 " + club.getCapacity() + "명");
        String statusStr = club.getStatus();
        tvClubStatus.setText(statusStr);

        // ---------------------------------------------------
        // [추가] 내가 만든 모임이면 배지 보이기
        // ---------------------------------------------------
        if (club.isOwner()) {
            tvOwnerBadge.setVisibility(View.VISIBLE);
        } else {
            tvOwnerBadge.setVisibility(View.GONE);
        }

        // 상태 칩 색상 변경
        if (statusStr.equals("모집중")) {
            tvClubStatus.setBackgroundTintList(context.getColorStateList(R.color.brand_primary));
        } else {
            tvClubStatus.setBackgroundTintList(context.getColorStateList(R.color.brand_secondary));
        }

        ivBookIcon.setColorFilter(context.getColor(R.color.brand_primary));
        ivMemberIcon.setColorFilter(context.getColor(R.color.text_secondary));

        return convertView;
    }
}