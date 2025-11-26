package com.example.bookon.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookon.R;
import com.example.bookon.data.Club;

import java.util.ArrayList;

public class ClubAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Club> clubList;

    public ClubAdapter(Context context, ArrayList<Club> clubList) {
        this.context = context;
        this.clubList = clubList;
    }

    @Override
    public int getCount() {
        return clubList.size();
    }

    @Override
    public Object getItem(int position) {
        return clubList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

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
        ImageView ivBookIcon = convertView.findViewById(R.id.iv_book_icon);
        ImageView ivMemberIcon = convertView.findViewById(R.id.iv_member_icon);


        Club club = clubList.get(position);

        tvClubName.setText(club.getName());
        tvCurrentBook.setText("현재 책: " + club.getCurrentBook());
        tvMemberCount.setText("정원 " + club.getCapacity() + "명");
        String statusStr = club.getStatus();
        tvClubStatus.setText(statusStr);

        // 상태에 따른 칩 색상 변경
        if (statusStr.equals("모집중")) {
            // 모집중 -> 파란색(Primary)
            tvClubStatus.setBackgroundTintList(context.getColorStateList(R.color.brand_primary));
        } else {
            // 진행중 -> 민트색(Secondary)
            tvClubStatus.setBackgroundTintList(context.getColorStateList(R.color.brand_secondary));
        }

        // 아이콘 색상 설정
        ivBookIcon.setColorFilter(context.getColor(R.color.brand_primary));
        ivMemberIcon.setColorFilter(context.getColor(R.color.text_secondary));

        return convertView;
    }
}