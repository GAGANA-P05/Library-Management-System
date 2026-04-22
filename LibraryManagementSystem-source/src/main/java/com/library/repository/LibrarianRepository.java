package com.library.repository;

import com.library.db.DBConnection;
import com.library.model.Librarian;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibrarianRepository implements Repository<Librarian, String> {

    @Override
public void save(Librarian l) {
    String sql = """
        INSERT INTO librarian (librarian_id, employee_id, name, email,
                               phone_number, working_hours, password)
        VALUES (?, ?, ?, ?, ?, ?, 'lib123')
        ON DUPLICATE KEY UPDATE
            name          = VALUES(name),
            email         = VALUES(email),
            phone_number  = VALUES(phone_number),
            working_hours = VALUES(working_hours)
    """;
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, l.getLibrarianId());
        ps.setString(2, l.getEmployeeId());
        ps.setString(3, l.getName());
        ps.setString(4, l.getEmail());
        ps.setString(5, l.getPhoneNumber());
        ps.setString(6, l.getWorkingHours());
        ps.executeUpdate();
    } catch (SQLException e) { e.printStackTrace(); }
}

    @Override
    public Optional<Librarian> findById(String id) {
        String sql = "SELECT * FROM librarian WHERE librarian_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Librarian> findAll() {
        List<Librarian> list = new ArrayList<>();
        String sql = "SELECT * FROM librarian";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM librarian WHERE librarian_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public Optional<Librarian> findByEmployeeId(String employeeId) {
        String sql = "SELECT * FROM librarian WHERE employee_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    private Librarian mapRow(ResultSet rs) throws SQLException {
        return new Librarian(
            rs.getString("librarian_id"),
            rs.getString("employee_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone_number"),
            rs.getString("working_hours")
        );
    }
}