package com.library.controller;

import com.library.enums.StaffRole;
import com.library.model.MaintenanceStaff;
import com.library.service.MaintenanceService;

import java.util.List;
import java.util.Optional;

public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    public MaintenanceStaff addStaff(String name, StaffRole role, String email, String phone) {
        return maintenanceService.addStaff(name, role, email, phone);
    }

    public List<MaintenanceStaff> getAllStaff() {
        return maintenanceService.getAllStaff();
    }

    public List<MaintenanceStaff> getStaffByRole(StaffRole role) {
        return maintenanceService.getStaffByRole(role);
    }

    public Optional<MaintenanceStaff> getStaffById(String id) {
        return maintenanceService.findById(id);
    }

    public boolean removeStaff(String id) {
        return maintenanceService.removeStaff(id);
    }
}
