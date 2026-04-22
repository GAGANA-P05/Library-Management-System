package com.library.controller;

import com.library.model.*;
import com.library.service.AdminService;
import com.library.service.ReportService;

import java.util.List;
import java.util.Optional;

public class AdminController {

    private final AdminService adminService;
    private final ReportService reportService;

    public AdminController(AdminService adminService, ReportService reportService) {
        this.adminService = adminService;
        this.reportService = reportService;
    }

    public Optional<Admin> login(String username, String password) {
        return adminService.authenticate(username, password);
    }

    public List<Transaction> getTransactionHistory() {
        return reportService.getAllTransactions();
    }

    public List<Transaction> getOverdueTransactions() {
        return reportService.getOverdueTransactions();
    }

    public List<Fine> getFineReport() {
        return reportService.getAllFines();
    }

    public double getTotalFinesCollected() {
        return reportService.getTotalFinesCollected();
    }

    public List<User> getUserActivity() {
        return reportService.getAllUsers();
    }

    public List<Book> getBookUsageReport() {
        return reportService.getAllBooks();
    }

    public List<Notification> getAllNotifications() {
        return reportService.getAllNotifications();
    }

    public long getActiveIssuedCount()   { return reportService.getActiveIssuedCount(); }
    public long getAvailableBooksCount() { return reportService.getAvailableBooksCount(); }
    public long getTotalBooksIssued()    { return reportService.getTotalBooksIssued(); }
}
