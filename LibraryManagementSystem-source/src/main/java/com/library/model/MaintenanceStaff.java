package com.library.model;

import com.library.enums.StaffRole;

public class MaintenanceStaff {
    private String staffId;
    private String name;
    private StaffRole role;
    private String email;
    private String phoneNumber;

    public MaintenanceStaff(String staffId, String name, StaffRole role,
                            String email, String phoneNumber) {
        this.staffId = staffId;
        this.name = name;
        this.role = role;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getStaffId() { return staffId; }
    public String getName() { return name; }
    public StaffRole getRole() { return role; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }

    public void setRole(StaffRole role) { this.role = role; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phone) { this.phoneNumber = phone; }

    @Override
    public String toString() {
        return "MaintenanceStaff{id='" + staffId + "', name='" + name + "', role=" + role + "}";
    }
}
