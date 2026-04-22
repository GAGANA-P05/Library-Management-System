package com.library.repository;

import com.library.db.DBConnection;
import com.library.enums.FineStatus;
import com.library.model.Fine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FineRepository implements Repository<Fine, String> {

    @Override
    public void save(Fine fine) {
        String sql = """
            INSERT INTO fine (fine_id, transaction_id, user_id,
                              days_late, fine_amount, payment_status, payment_date)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                payment_status = VALUES(payment_status),
                payment_date   = VALUES(payment_date)
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fine.getFineId());
            ps.setString(2, fine.getTransactionId());
            ps.setString(3, fine.getUserId());
            ps.setLong  (4, fine.getDaysLate());
            ps.setDouble(5, fine.getFineAmount());
            ps.setString(6, fine.getPaymentStatus().name());
            ps.setDate  (7, fine.getPaymentDate() == null ? null : Date.valueOf(fine.getPaymentDate()));
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public Optional<Fine> findById(String id) {
        String sql = "SELECT * FROM fine WHERE fine_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Fine> findAll() {
        List<Fine> list = new ArrayList<>();
        String sql = "SELECT * FROM fine";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void delete(String id) { /* fines not deleted */ }

    public List<Fine> findByUserId(String userId) {
        List<Fine> list = new ArrayList<>();
        String sql = "SELECT * FROM fine WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Fine> findUnpaidByUserId(String userId) {
        List<Fine> list = new ArrayList<>();
        String sql = "SELECT * FROM fine WHERE user_id = ? AND payment_status = 'UNPAID'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Optional<Fine> findByTransactionId(String transactionId) {
        String sql = "SELECT * FROM fine WHERE transaction_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, transactionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    public double totalCollected() {
        String sql = "SELECT SUM(fine_amount) FROM fine WHERE payment_status = 'PAID'";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    private Fine mapRow(ResultSet rs) throws SQLException {
        Fine f = new Fine(
            rs.getString("fine_id"),
            rs.getString("transaction_id"),
            rs.getString("user_id"),
            rs.getLong  ("days_late"),
            rs.getDouble("fine_amount")
        );
        if (FineStatus.valueOf(rs.getString("payment_status")) == FineStatus.PAID) {
            f.markAsPaid();
        }
        return f;
    }
}