package com.example.bookon.data; // [중요] data -> model 로 변경

public class Club {
    private int id;
    private String name;
    private int capacity;
    private String startDate;
    private String endDate;
    private String description;

    // [추가] 실제 데이터를 저장할 변수
    private String status;
    private String currentBook;

    public Club(String name, int capacity, String startDate, String endDate, String description) {
        this.name = name;
        this.capacity = capacity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;

        // [추가] 생성 시 기본값 설정
        this.status = "모집중";
        this.currentBook = "선정 도서 없음";
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getDescription() { return description; }

    // [수정] 임시 반환값 -> 실제 변수 반환으로 변경
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCurrentBook() { return currentBook; }
    public void setCurrentBook(String currentBook) { this.currentBook = currentBook; }
}