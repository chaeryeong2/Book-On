package com.example.bookon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ClubAdapter extends BaseAdapter {

    private Context context; // 화면 제어권 (색상 변경 등에 필요)
    private ArrayList<Club> clubList; // 데이터 리스트

    // 생성자
    public ClubAdapter(Context context, ArrayList<Club> clubList) {
        this.context = context;
        this.clubList = clubList;
    }

    // 1. 데이터 개수 반환
    @Override
    public int getCount() {
        return clubList.size();
    }

    // 2. 특정 위치의 데이터 반환
    @Override
    public Object getItem(int position) {
        return clubList.get(position);
    }

    // 3. 특정 위치의 아이템 ID 반환 (보통 position 그대로 사용)
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 4. ★핵심★ 각 아이템(카드 한 장)의 화면을 그려주는 메서드
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 4-1. 만약 재사용할 뷰(convertView)가 없다면 새로 XML을 inflate 합니다.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // 아까 만들어둔 item_club_card.xml 을 불러옵니다.
            convertView = inflater.inflate(R.layout.item_club_card, parent, false);
        }

        // 4-2. 뷰 안의 위젯들을 찾아옵니다 (findViewById 사용)
        TextView tvClubName = convertView.findViewById(R.id.tv_club_name);
        TextView tvCurrentBook = convertView.findViewById(R.id.tv_current_book);
        TextView tvMemberCount = convertView.findViewById(R.id.tv_member_count);
        TextView tvClubStatus = convertView.findViewById(R.id.tv_club_status);
        ImageView ivBookIcon = convertView.findViewById(R.id.iv_book_icon);
        ImageView ivMemberIcon = convertView.findViewById(R.id.iv_member_icon);


        // 4-3. 현재 위치(position)에 맞는 데이터를 가져와서 뷰에 넣어줍니다.
        Club club = clubList.get(position);

        tvClubName.setText(club.getName());
        tvCurrentBook.setText("현재 책: " + club.getCurrentBook());
        tvMemberCount.setText("멤버 " + club.getMemberCount() + "명");
        tvClubStatus.setText(club.getStatus());

        // 상태에 따른 칩 색상 변경 (여기서 context가 필요함)
        if (club.getStatus().equals("모집중")) {
            tvClubStatus.setBackgroundTintList(context.getColorStateList(R.color.brand_primary));
        } else {
            tvClubStatus.setBackgroundTintList(context.getColorStateList(R.color.brand_secondary));
        }

        // (아이콘 색상도 context로 바꿔주면 더 좋습니다)
        ivBookIcon.setColorFilter(context.getColor(R.color.brand_primary));
        ivMemberIcon.setColorFilter(context.getColor(R.color.text_secondary));


        // 4-4. 완성된 뷰를 리스트뷰에게 반환합니다.
        return convertView;
    }
}