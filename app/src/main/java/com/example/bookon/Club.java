package com.example.bookon;

public class Club {
    String name;        // 모임 이름
    String currentBook; // 현재 읽고 있는 책
    int memberCount;    // 멤버 수
    String status;      // 상태 (예: 진행중, 모집중)

    // 생성자 (Constructor) - 데이터를 한 번에 넣기 위함
    public Club(String name, String currentBook, int memberCount, String status) {
        this.name = name;
        this.currentBook = currentBook;
        this.memberCount = memberCount;
        this.status = status;
    }

    // Getter 메소드들 (데이터를 꺼내올 때 사용)
    public String getName() { return name; }
    public String getCurrentBook() { return currentBook; }
    public int getMemberCount() { return memberCount; }
    public String getStatus() { return status; }
}