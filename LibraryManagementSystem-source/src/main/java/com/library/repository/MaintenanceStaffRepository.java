package com.library.repository;

import com.library.db.DBConnection;
import com.library.enums.StaffRole;
import com.library.model.MaintenanceStaff;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MaintenanceStaffRepository implements Repository<MaintenanceStaff, String> {

    @Override
public void save(MaintenanceStaff s) {
    String sql = """
        INSERT INTO maintenance_staff (staff_id, name, role, email, phone_number, password)
        VALUES (?, ?, ?, ?, ?, 'staff123')
        ON DUPLICATE KEY UPDATE
            name         = VALUES(name),
            role         = VALUES(role),
            email        = VALUES(email),
            phone_number = VALUES(phone_number)
    """;
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, s.getStaffId());
        ps.setString(2, s.getName());
        ps.setString(3, s.getRole().name());
        ps.setString(4, s.getEmail());
        ps.setString(5, s.getPhoneNumber());
        ps.executeUpdate();
    } catch (SQLException e) { e.printStackTrace(); }
}

    @Override
    public Optional<MaintenanceStaff> findById(String id) {
        String sql = "SELECT * FROM maintenance_staff WHERE staff_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<MaintenanceStaff> findAll() {
        List<MaintenanceStaff> list = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_staff";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM maintenance_staff WHERE staff_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<MaintenanceStaff> findByRole(StaffRole role) {
        List<MaintenanceStaff> list = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_staff WHERE role = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, role.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private MaintenanceStaff mapRow(ResultSet rs) throws SQLException {
        return new MaintenanceStaff(
            rs.getString("staff_id"),
            rs.getString("name"),
            StaffRole.valueOf(rs.getString("role")),
            rs.getString("email"),
            rs.getString("phone_number")
        );
    }
}