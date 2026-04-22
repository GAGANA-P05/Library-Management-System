package com.library.repository;

import com.library.db.DBConnection;
import com.library.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NotificationRepository implements Repository<Notification, String> {

    @Override
    public void save(Notification n) {
        String sql = """
            INSERT IGNORE INTO notification (notification_id, user_id, message, date_sent)
            VALUES (?, ?, ?, ?)
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString   (1, n.getNotificationId());
            ps.setString   (2, n.getUserId());
            ps.setString   (3, n.getMessage());
            ps.setTimestamp(4, Timestamp.valueOf(n.getDateSent()));
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public Optional<Notification> findById(String id) {
        String sql = "SELECT * FROM notification WHERE notification_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Notification> findAll() {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notification ORDER BY date_sent DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void delete(String id) { /* notifications not deleted */ }

    public List<Notification> findByUserId(String userId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE user_id = ? ORDER BY date_sent DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Notification mapRow(ResultSet rs) throws SQLException {
        Notification n = new Notification(
            rs.getString  ("notification_id"),
            rs.getString  ("user_id"),
            rs.getString  ("message")
        );
        return n;
    }
}