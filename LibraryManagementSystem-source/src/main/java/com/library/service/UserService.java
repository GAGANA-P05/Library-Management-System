package com.library.service;

import com.library.factory.UserFactory;
import com.library.model.User;
import com.library.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserRepository userRepository;
    private final UserFactory userFactory;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userFactory = new UserFactory();
    }

    public User registerUser(String userId, String name, String email,
                             String phone, String address) {
        if (userRepository.findByEmail(email).isPresent())
            throw new IllegalArgumentException("Email already registered: " + email);

        User user = userFactory.create(userId, name, email, phone, address, LocalDate.now());
        userRepository.save(user);
        return user;
    }

    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean updateUser(String userId, String name, String phone, String address) {
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty()) return false;
        User u = opt.get();
        u.setName(name);
        u.setPhoneNumber(phone);
        u.setAddress(address);
        userRepository.save(u);
        return true;
    }

    public boolean removeUser(String userId) {
        if (userRepository.findById(userId).isEmpty()) return false;
        userRepository.delete(userId);
        return true;
    }

    public void addPendingFine(String userId, double amount) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setPendingFineAmount(u.getPendingFineAmount() + amount);
            userRepository.save(u);
        });
    }

    public void clearPendingFine(String userId, double amount) {
        userRepository.findById(userId).ifPresent(u -> {
            double remaining = Math.max(0, u.getPendingFineAmount() - amount);
            u.setPendingFineAmount(remaining);
            userRepository.save(u);
        });
    }
}
