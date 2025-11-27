package com.example.bookon.data;

public class Book {
    private long id;
    private long clubId;
    private String title;
    private String author;
    // 여기 추가
    private String ownerName; // 책 등록한 사람 닉네임

    public Book(long id, long clubId, String title, String author, String ownerName) {
        this.id = id;
        this.clubId = clubId;
        this.title = title;
        this.author = author;
        this.ownerName = ownerName;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getOwnerName() { return ownerName; }
    public long getId() { return id; }
    public long getClubId() { return clubId; }

}
