package com.library.model;

public class Admin {
    private String adminId;
    private String username;
    private String password;
    private String name;
    private String email;

    public Admin(String adminId, String username, String password, String name, String email) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public String getAdminId() { return adminId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }

    public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    @Override
    public String toString() {
        return "Admin{id='" + adminId + "', username='" + username + "'}";
    }
}
