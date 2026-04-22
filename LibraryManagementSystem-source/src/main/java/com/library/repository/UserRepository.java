package com.library.repository;

import com.library.db.DBConnection;
import com.library.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements Repository<User, String> {

    @Override
public void save(User user) {
    String sql = """
        INSERT INTO user (user_id, name, email, phone_number,
                          address, membership_date, pending_fine_amount, password)
        VALUES (?, ?, ?, ?, ?, ?, ?, 'user123')
        ON DUPLICATE KEY UPDATE
            name                = VALUES(name),
            email               = VALUES(email),
            phone_number        = VALUES(phone_number),
            address             = VALUES(address),
            pending_fine_amount = VALUES(pending_fine_amount)
    """;
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, user.getUserId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getEmail());
        ps.setString(4, user.getPhoneNumber());
        ps.setString(5, user.getAddress());
        ps.setDate  (6, Date.valueOf(user.getMembershipDate()));
        ps.setDouble(7, user.getPendingFineAmount());
        ps.executeUpdate();
    } catch (SQLException e) { e.printStackTrace(); }
}

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM user WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM user WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User(
            rs.getString("user_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone_number"),
            rs.getString("address"),
            rs.getDate  ("membership_date").toLocalDate()
        );
        u.setPendingFineAmount(rs.getDouble("pending_fine_amount"));
        return u;
    }
}