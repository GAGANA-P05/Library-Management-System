package com.library.service;

import com.library.db.DBConnection;

import java.sql.*;
import java.util.Optional;

public class LoginService {

    public static final String ROLE_ADMIN     = "ADMIN";
    public static final String ROLE_LIBRARIAN = "LIBRARIAN";
    public static final String ROLE_STAFF     = "STAFF";
    public static final String ROLE_USER      = "USER";

    /**
     * Returns an array: [role, id, name] if found, else empty.
     */
    public Optional<String[]> login(String email, String password) {

        // Check admin
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT admin_id, name FROM admin WHERE email=? AND password=?");
            ps.setString(1, email); ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(new String[]{
                ROLE_ADMIN, rs.getString("admin_id"), rs.getString("name")});
        } catch (SQLException e) { e.printStackTrace(); }

        // Check librarian
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT librarian_id, name FROM librarian WHERE email=? AND password=?");
            ps.setString(1, email); ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(new String[]{
                ROLE_LIBRARIAN, rs.getString("librarian_id"), rs.getString("name")});
        } catch (SQLException e) { e.printStackTrace(); }

        // Check maintenance staff
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT staff_id, name FROM maintenance_staff WHERE email=? AND password=?");
            ps.setString(1, email); ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(new String[]{
                ROLE_STAFF, rs.getString("staff_id"), rs.getString("name")});
        } catch (SQLException e) { e.printStackTrace(); }

        // Check user
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT user_id, name FROM user WHERE email=? AND password=?");
            ps.setString(1, email); ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(new String[]{
                ROLE_USER, rs.getString("user_id"), rs.getString("name")});
        } catch (SQLException e) { e.printStackTrace(); }

        return Optional.empty();
    }
}