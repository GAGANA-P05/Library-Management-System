package com.library.service;

import com.library.enums.StaffRole;
import com.library.model.MaintenanceStaff;
import com.library.repository.MaintenanceStaffRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MaintenanceService {

    private final MaintenanceStaffRepository staffRepository;

    public MaintenanceService(MaintenanceStaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public MaintenanceStaff addStaff(String name, StaffRole role, String email, String phone) {
        String staffId = "STF-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        MaintenanceStaff staff = new MaintenanceStaff(staffId, name, role, email, phone);
        staffRepository.save(staff);
        return staff;
    }

    public List<MaintenanceStaff> getAllStaff()              { return staffRepository.findAll(); }
    public List<MaintenanceStaff> getStaffByRole(StaffRole r){ return staffRepository.findByRole(r); }
    public Optional<MaintenanceStaff> findById(String id)    { return staffRepository.findById(id); }

    public boolean removeStaff(String staffId) {
        if (staffRepository.findById(staffId).isEmpty()) return false;
        staffRepository.delete(staffId);
        return true;
    }
}
