package com.library.service;

import com.library.model.Librarian;
import com.library.repository.LibrarianRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LibrarianService {

    private final LibrarianRepository librarianRepository;

    public LibrarianService(LibrarianRepository librarianRepository) {
        this.librarianRepository = librarianRepository;
    }

    public Librarian addLibrarian(String employeeId, String name, String email,
                                  String phone, String workingHours) {
        String librarianId = "LIB-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Librarian librarian = new Librarian(librarianId, employeeId, name, email, phone, workingHours);
        librarianRepository.save(librarian);
        return librarian;
    }

    public Optional<Librarian> findById(String librarianId) {
        return librarianRepository.findById(librarianId);
    }

    public List<Librarian> getAllLibrarians() {
        return librarianRepository.findAll();
    }

    public boolean updateLibrarian(String librarianId, String name, String phone, String hours) {
        Optional<Librarian> opt = librarianRepository.findById(librarianId);
        if (opt.isEmpty()) return false;
        Librarian l = opt.get();
        l.setName(name);
        l.setPhoneNumber(phone);
        l.setWorkingHours(hours);
        librarianRepository.save(l);
        return true;
    }

    public boolean removeLibrarian(String librarianId) {
        if (librarianRepository.findById(librarianId).isEmpty()) return false;
        librarianRepository.delete(librarianId);
        return true;
    }
}
