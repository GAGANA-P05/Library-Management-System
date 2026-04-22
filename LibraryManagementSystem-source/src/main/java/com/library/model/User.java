package com.library.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDate membershipDate;
    private List<String> borrowedBookIds;
    private double pendingFineAmount;

    public User(String userId, String name, String email, String phoneNumber,
                String address, LocalDate membershipDate) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.membershipDate = membershipDate;
        this.borrowedBookIds = new ArrayList<>();
        this.pendingFineAmount = 0.0;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public LocalDate getMembershipDate() { return membershipDate; }
    public List<String> getBorrowedBookIds() { return borrowedBookIds; }
    public double getPendingFineAmount() { return pendingFineAmount; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAddress(String address) { this.address = address; }
    public void setPendingFineAmount(double amount) { this.pendingFineAmount = amount; }

    public void addBorrowedBook(String bookId) { borrowedBookIds.add(bookId); }
    public void removeBorrowedBook(String bookId) { borrowedBookIds.remove(bookId); }

    @Override
    public String toString() {
        return "User{userId='" + userId + "', name='" + name + "', email='" + email + "'}";
    }
}
