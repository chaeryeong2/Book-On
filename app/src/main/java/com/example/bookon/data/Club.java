package com.example.bookon.data;

public class Club {
    private int id;

    private String name;
    private int capacity;
    private String startDate;
    private String endDate;
    private String description;

    public Club(String name, int capacity, String startDate, String endDate, String description) {
        this.name = name;
        this.capacity = capacity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getDescription() { return description; }

    // 임시 메소드
    public String getStatus() { return "모집중"; }
    public String getCurrentBook() { return "아직 없음"; }
}