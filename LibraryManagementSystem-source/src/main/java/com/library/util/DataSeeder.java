package com.library.util;

import com.library.controller.*;
import com.library.enums.StaffRole;
import com.library.model.Admin;
import com.library.service.AdminService;

public class DataSeeder {

    public static void seed(BookController bookCtrl,
                            UserController userCtrl,
                            LibrarianController libCtrl,
                            MaintenanceController maintCtrl,
                            AdminService adminService) {

        // ── Admin ─────────────────────────────────────────────────────────
        adminService.addAdmin(new Admin("ADMIN-001", "admin", "admin123", "System Admin", "admin@library.com"));

        // ── Librarians ────────────────────────────────────────────────────
        libCtrl.addLibrarian("EMP-101", "Priya Sharma",   "priya@library.com",   "9876543210", "9AM-5PM");
        libCtrl.addLibrarian("EMP-102", "Arjun Mehta",    "arjun@library.com",   "9876543211", "1PM-9PM");

        // ── Maintenance Staff ─────────────────────────────────────────────
        maintCtrl.addStaff("Ravi Kumar",    StaffRole.CLEANER,      "ravi@library.com",    "9000000001");
        maintCtrl.addStaff("Suresh Babu",   StaffRole.SECURITY,     "suresh@library.com",  "9000000002");
        maintCtrl.addStaff("Mohan Das",     StaffRole.REPAIR_STAFF, "mohan@library.com",   "9000000003");

        // ── Books ─────────────────────────────────────────────────────────
        bookCtrl.addBook("BK-001", "Clean Code",               "Robert C. Martin",  "Prentice Hall",      2008, "978-0132350884", "Technology",  "A1-S1");
        bookCtrl.addBook("BK-002", "Design Patterns",          "Gang of Four",       "Addison-Wesley",     1994, "978-0201633610", "Technology",  "A1-S2");
        bookCtrl.addBook("BK-003", "The Pragmatic Programmer", "Andrew Hunt",        "Addison-Wesley",     1999, "978-0201616224", "Technology",  "A1-S3");
        bookCtrl.addBook("BK-004", "Thinking Fast and Slow",   "Daniel Kahneman",    "Farrar Straus",      2011, "978-0374533557", "Psychology",  "B2-S1");
        bookCtrl.addBook("BK-005", "Sapiens",                  "Yuval Noah Harari",  "Harper",             2011, "978-0062316097", "History",     "C1-S1");
        bookCtrl.addBook("BK-006", "Atomic Habits",            "James Clear",        "Avery",              2018, "978-0735211292", "Self-Help",   "D1-S1");
        bookCtrl.addBook("BK-007", "The Great Gatsby",         "F. Scott Fitzgerald","Scribner",           1925, "978-0743273565", "Fiction",     "E2-S3");
        bookCtrl.addBook("BK-008", "1984",                     "George Orwell",      "Secker & Warburg",   1949, "978-0451524935", "Fiction",     "E2-S4");
        bookCtrl.addBook("BK-009", "Introduction to Algorithms","Thomas H. Cormen",  "MIT Press",          2009, "978-0262033848", "Technology",  "A2-S1");
        bookCtrl.addBook("BK-010", "The Art of War",           "Sun Tzu",            "Oxford University",  500,  "978-0195014761", "Philosophy",  "F1-S1");

        // ── Users ─────────────────────────────────────────────────────────
        userCtrl.registerUser("USR-001", "Ananya Krishnan",  "ananya@mail.com",  "9500000001", "12 Anna Nagar, Chennai");
        userCtrl.registerUser("USR-002", "Vijay Ramasamy",   "vijay@mail.com",   "9500000002", "45 T Nagar, Chennai");
        userCtrl.registerUser("USR-003", "Kavitha Nair",     "kavitha@mail.com", "9500000003", "88 Velachery, Chennai");
        userCtrl.registerUser("USR-004", "Deepak Verma",     "deepak@mail.com",  "9500000004", "21 Adyar, Chennai");
        userCtrl.registerUser("USR-005", "Lakshmi Sundaram", "lakshmi@mail.com", "9500000005", "67 Mylapore, Chennai");
    }
}
