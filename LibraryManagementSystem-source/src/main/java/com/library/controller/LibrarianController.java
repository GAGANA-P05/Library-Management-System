package com.library.controller;

import com.library.model.Librarian;
import com.library.service.LibrarianService;

import java.util.List;
import java.util.Optional;

public class LibrarianController {

    private final LibrarianService librarianService;

    public LibrarianController(LibrarianService librarianService) {
        this.librarianService = librarianService;
    }

    public Librarian addLibrarian(String employeeId, String name, String email,
                                  String phone, String workingHours) {
        return librarianService.addLibrarian(employeeId, name, email, phone, workingHours);
    }

    public List<Librarian> getAllLibrarians() {
        return librarianService.getAllLibrarians();
    }

    public Optional<Librarian> getLibrarianById(String id) {
        return librarianService.findById(id);
    }

    public boolean updateLibrarian(String id, String name, String phone, String hours) {
        return librarianService.updateLibrarian(id, name, phone, hours);
    }

    public boolean removeLibrarian(String id) {
        return librarianService.removeLibrarian(id);
    }
}
