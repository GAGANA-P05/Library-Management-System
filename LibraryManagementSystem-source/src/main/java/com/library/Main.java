package com.library;

import com.library.controller.*;
import com.library.observer.MaintenanceStaffObserver;
import com.library.observer.UserNotificationObserver;
import com.library.repository.*;
import com.library.service.*;
import com.library.view.LoginFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        // ── Repositories ─────────────────────────────────────────
        BookRepository bookRepo = new BookRepository();
        UserRepository userRepo = new UserRepository();
        LibrarianRepository libRepo = new LibrarianRepository();
        TransactionRepository txRepo = new TransactionRepository();
        FineRepository fineRepo = new FineRepository();
        NotificationRepository notifRepo = new NotificationRepository();
        MaintenanceStaffRepository maintRepo = new MaintenanceStaffRepository();

        // ── Services ─────────────────────────────────────────────
        BookService bookService = new BookService(bookRepo);
        UserService userService = new UserService(userRepo);
        LibrarianService libService = new LibrarianService(libRepo);
        TransactionService txService = new TransactionService(txRepo, fineRepo, bookRepo, userRepo);
        AdminService adminService = new AdminService();
        MaintenanceService maintService = new MaintenanceService(maintRepo);
        ReportService reportService = new ReportService(txRepo, fineRepo, userRepo, bookRepo, notifRepo);

        // ── Observers ────────────────────────────────────────────
        UserNotificationObserver userNotifObserver = new UserNotificationObserver(notifRepo);
        MaintenanceStaffObserver maintObserver = new MaintenanceStaffObserver();

        bookService.addObserver(maintObserver);
        txService.addObserver(userNotifObserver);

        // ── Controllers ──────────────────────────────────────────
        BookController bookCtrl = new BookController(bookService);
        UserController userCtrl = new UserController(userService);
        LibrarianController libCtrl = new LibrarianController(libService);
        TransactionController txCtrl = new TransactionController(txService);
        AdminController adminCtrl = new AdminController(adminService, reportService);
        MaintenanceController maintCtrl = new MaintenanceController(maintService);

        StaffDutyRepository dutyRepo = new StaffDutyRepository();

        // ── Launch UI ────────────────────────────────────────────
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            new LoginFrame(
                    bookCtrl, userCtrl, libCtrl, txCtrl,
                    adminCtrl, maintCtrl, dutyRepo
            ).setVisible(true);
        });
    }
}