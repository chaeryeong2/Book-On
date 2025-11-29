package com.example.bookon.data;

// 독서 모임 정보 객체
public class Club {
    private int id;
    private String name;
    private int capacity;
    private String startDate;
    private String endDate;
    private String description;
    private String status; // 모임 상태 (모집중, 진행중, 마감됨)
    private String topic; // 모임 주제
    private boolean isOwner; // 현재 사용자가 방장인지 여부
    private String scheduleStart; // 독서 시작일
    private int cycleWeeks; // 교환 주기

    // 모임 객체 생성자
    public Club(String name, int capacity, String startDate, String endDate, String description) {
        this.name = name;
        this.capacity = capacity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;

        // 생성 시 기본값 설정
        this.status = "모집중";
        this.topic = "자유 주제";
        this.isOwner = false;

        this.scheduleStart = null;
        this.cycleWeeks = 0;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public boolean isOwner() { return isOwner; }
    public void setOwner(boolean owner) { isOwner = owner; }

    public String getScheduleStart() { return scheduleStart; }
    public void setScheduleStart(String scheduleStart) { this.scheduleStart = scheduleStart; }

    public int getCycleWeeks() { return cycleWeeks; }
    public void setCycleWeeks(int cycleWeeks) { this.cycleWeeks = cycleWeeks; }
}