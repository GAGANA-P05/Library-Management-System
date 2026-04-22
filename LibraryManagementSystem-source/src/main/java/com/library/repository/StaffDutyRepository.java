package com.library.repository;

import com.library.db.DBConnection;
import com.library.model.StaffDuty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StaffDutyRepository {

    public void save(StaffDuty duty) {
        String sql = """
            INSERT INTO staff_duty
                (duty_id, staff_id, assigned_by, task_type, description, assigned_date, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE status = VALUES(status)
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString   (1, duty.getDutyId());
            ps.setString   (2, duty.getStaffId());
            ps.setString   (3, duty.getAssignedBy());
            ps.setString   (4, duty.getTaskType());
            ps.setString   (5, duty.getDescription());
            ps.setTimestamp(6, Timestamp.valueOf(duty.getAssignedDate()));
            ps.setString   (7, duty.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<StaffDuty> findByStaffId(String staffId) {
        List<StaffDuty> list = new ArrayList<>();
        String sql = "SELECT * FROM staff_duty WHERE staff_id = ? ORDER BY assigned_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, staffId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<StaffDuty> findAll() {
        List<StaffDuty> list = new ArrayList<>();
        String sql = "SELECT * FROM staff_duty ORDER BY assigned_date DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Optional<StaffDuty> findById(String dutyId) {
        String sql = "SELECT * FROM staff_duty WHERE duty_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dutyId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean updateStatus(String dutyId, String newStatus) {
        String sql = "UPDATE staff_duty SET status = ? WHERE duty_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, dutyId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void delete(String dutyId) {
        String sql = "DELETE FROM staff_duty WHERE duty_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dutyId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private StaffDuty mapRow(ResultSet rs) throws SQLException {
        return new StaffDuty(
            rs.getString   ("duty_id"),
            rs.getString   ("staff_id"),
            rs.getString   ("assigned_by"),
            rs.getString   ("task_type"),
            rs.getString   ("description"),
            rs.getTimestamp("assigned_date").toLocalDateTime(),
            rs.getString   ("status")
        );
    }
}