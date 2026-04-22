package com.library.service;

import com.library.enums.TransactionStatus;
import com.library.model.*;
import com.library.repository.*;

import java.util.List;
import java.util.stream.Collectors;

public class ReportService {

    private final TransactionRepository transactionRepository;
    private final FineRepository fineRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final NotificationRepository notificationRepository;

    public ReportService(TransactionRepository transactionRepository,
                         FineRepository fineRepository,
                         UserRepository userRepository,
                         BookRepository bookRepository,
                         NotificationRepository notificationRepository) {
        this.transactionRepository = transactionRepository;
        this.fineRepository = fineRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<Transaction> getAllTransactions() { return transactionRepository.findAll(); }

    public List<Transaction> getOverdueTransactions() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getStatus() == TransactionStatus.OVERDUE)
                .collect(Collectors.toList());
    }

    public List<Fine> getAllFines()             { return fineRepository.findAll(); }
    public double getTotalFinesCollected()      { return fineRepository.totalCollected(); }

    public List<User> getAllUsers()             { return userRepository.findAll(); }
    public List<Book> getAllBooks()             { return bookRepository.findAll(); }

    public List<Notification> getAllNotifications() { return notificationRepository.findAll(); }

    public List<Notification> getNotificationsForUser(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    public long getTotalBooksIssued() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getStatus() != TransactionStatus.CLOSED ||
                             t.getReturnDate() != null)
                .count();
    }

    public long getActiveIssuedCount() {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getStatus() == TransactionStatus.ISSUED ||
                             t.getStatus() == TransactionStatus.OVERDUE)
                .count();
    }

    public long getAvailableBooksCount() {
        return bookRepository.findAll().stream()
                .filter(Book::isAvailable)
                .count();
    }
}
