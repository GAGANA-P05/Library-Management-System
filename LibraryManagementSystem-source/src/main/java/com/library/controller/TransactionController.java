package com.library.controller;

import com.library.model.Fine;
import com.library.model.Transaction;
import com.library.service.TransactionService;

import java.util.List;
import java.util.Optional;

public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public Transaction issueBook(String userId, String bookId, String librarianId) {
        return transactionService.issueBook(userId, bookId, librarianId);
    }

    public Optional<Fine> returnBook(String transactionId, String librarianId) {
        return transactionService.returnBook(transactionId, librarianId);
    }

    public boolean payFine(String fineId) {
        return transactionService.payFine(fineId);
    }

    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    public List<Transaction> getTransactionsByUser(String userId) {
        return transactionService.getTransactionsByUser(userId);
    }

    public List<Fine> getAllFines() {
        return transactionService.getAllFines();
    }

    public List<Fine> getFinesByUser(String userId) {
        return transactionService.getFinesByUser(userId);
    }

    public List<Fine> getUnpaidFinesByUser(String userId) {
        return transactionService.getUnpaidFinesByUser(userId);
    }

    public double getTotalFinesCollected() {
        return transactionService.totalFinesCollected();
    }

    public Optional<Transaction> getTransaction(String txId) {
        return transactionService.getTransaction(txId);
    }

    public Optional<Fine> getFine(String fineId) {
        return transactionService.getFine(fineId);
    }
}
