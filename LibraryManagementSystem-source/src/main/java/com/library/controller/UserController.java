package com.library.controller;

import com.library.model.User;
import com.library.service.UserService;

import java.util.List;
import java.util.Optional;

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public User registerUser(String userId, String name, String email, String phone, String address) {
        return userService.registerUser(userId, name, email, phone, address);
    }

    public Optional<User> getUserById(String userId) {
        return userService.findById(userId);
    }

    public Optional<User> getUserByEmail(String email) {
        return userService.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    public boolean updateUser(String userId, String name, String phone, String address) {
        return userService.updateUser(userId, name, phone, address);
    }

    public boolean removeUser(String userId) {
        return userService.removeUser(userId);
    }
}
