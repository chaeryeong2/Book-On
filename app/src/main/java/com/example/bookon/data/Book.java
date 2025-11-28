package com.example.bookon.data;

public class Book {
    private long id;
    private long clubId;
    private String title;
    private String author;
    private String ownerName; // 화면 표시용 (닉네임)
    private String ownerId;   // [추가] 로직 비교용 (로그인 아이디)

    public Book(long id, long clubId, String title, String author, String ownerName, String ownerId) {
        this.id = id;
        this.clubId = clubId;
        this.title = title;
        this.author = author;
        this.ownerName = ownerName;
        this.ownerId = ownerId;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getOwnerName() { return ownerName; }
    public String getOwnerId() { return ownerId; } // Getter 추가
    public long getId() { return id; }
    public long getClubId() { return clubId; }
}