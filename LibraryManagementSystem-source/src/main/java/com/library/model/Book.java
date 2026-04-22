package com.library.model;

import com.library.enums.BookStatus;

public class Book {
    private String bookId;
    private String title;
    private String author;
    private String publisher;
    private int publicationYear;
    private String isbn;
    private String category;
    private String shelfLocation;
    private BookStatus status;

    public Book(String bookId, String title, String author, String publisher,
                int publicationYear, String isbn, String category, String shelfLocation) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.isbn = isbn;
        this.category = category;
        this.shelfLocation = shelfLocation;
        this.status = BookStatus.AVAILABLE;
    }

    public String getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public int getPublicationYear() { return publicationYear; }
    public String getIsbn() { return isbn; }
    public String getCategory() { return category; }
    public String getShelfLocation() { return shelfLocation; }
    public BookStatus getStatus() { return status; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setPublicationYear(int year) { this.publicationYear = year; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setCategory(String category) { this.category = category; }
    public void setShelfLocation(String shelfLocation) { this.shelfLocation = shelfLocation; }
    public void setStatus(BookStatus status) { this.status = status; }

    public boolean isAvailable() {
        return this.status == BookStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return "Book{id='" + bookId + "', title='" + title + "', author='" + author +
               "', status=" + status + "}";
    }
}
