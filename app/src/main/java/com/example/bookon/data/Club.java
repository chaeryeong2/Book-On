package com.example.bookon.data;

public class Club {
    private int id;
    private String name;
    private int capacity;
    private String startDate;
    private String endDate;
    private String description;

    // 실제 데이터를 저장할 변수
    private String status;
    private String currentBook;

    // [추가] 방장 여부 (true면 내가 만든 모임)
    private boolean isOwner;

    public Club(String name, int capacity, String startDate, String endDate, String description) {
        this.name = name;
        this.capacity = capacity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;

        // 생성 시 기본값 설정
        this.status = "모집중";
        this.currentBook = "선정 도서 없음";
        this.isOwner = false; // 기본값은 false (DB에서 읽어올 때 true로 바꿈)
    }

    // --------------------------------------------------------
    // Getters & Setters
    // --------------------------------------------------------

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getDescription() { return description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCurrentBook() { return currentBook; }
    public void setCurrentBook(String currentBook) { this.currentBook = currentBook; }

    // [추가] 방장 여부 확인 메서드
    public boolean isOwner() { return isOwner; }
    public void setOwner(boolean owner) { isOwner = owner; }
}