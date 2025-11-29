package com.example.bookon.data;

// 책 정보 객체
public class Book {
    private long id;
    private long clubId;
    private String title;
    private String author;
    private String ownerName;
    private String ownerId;

    // 책 객체 생성자
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
    public String getOwnerId() { return ownerId; }
    public long getId() { return id; }
    public long getClubId() { return clubId; }
}