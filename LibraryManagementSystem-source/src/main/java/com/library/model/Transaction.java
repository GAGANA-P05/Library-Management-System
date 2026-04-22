package com.library.model;

import com.library.enums.TransactionStatus;
import java.time.LocalDate;

public class Transaction {
    private String transactionId;
    private String userId;
    private String bookId;
    private String librarianId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private TransactionStatus status;

    public Transaction(String transactionId, String userId, String bookId,
                       String librarianId, LocalDate issueDate, LocalDate dueDate) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.bookId = bookId;
        this.librarianId = librarianId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = TransactionStatus.ISSUED;
    }

    public String getTransactionId() { return transactionId; }
    public String getUserId() { return userId; }
    public String getBookId() { return bookId; }
    public String getLibrarianId() { return librarianId; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public TransactionStatus getStatus() { return status; }

    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public boolean isOverdue() {
        if (returnDate != null) return returnDate.isAfter(dueDate);
        return LocalDate.now().isAfter(dueDate);
    }

    public long getDaysLate() {
        if (!isOverdue()) return 0;
        LocalDate compareDate = (returnDate != null) ? returnDate : LocalDate.now();
        return dueDate.until(compareDate).getDays();
    }

    @Override
    public String toString() {
        return "Transaction{id='" + transactionId + "', userId='" + userId +
               "', bookId='" + bookId + "', status=" + status + "}";
    }
}
