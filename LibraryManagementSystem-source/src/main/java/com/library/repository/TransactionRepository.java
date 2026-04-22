package com.library.repository;

import com.library.db.DBConnection;
import com.library.enums.TransactionStatus;
import com.library.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionRepository implements Repository<Transaction, String> {

    @Override
    public void save(Transaction t) {
        String sql = """
            INSERT INTO transaction (transaction_id, user_id, book_id, librarian_id,
                                     issue_date, due_date, return_date, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                return_date = VALUES(return_date),
                status = VALUES(status)
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getTransactionId());
            ps.setString(2, t.getUserId());
            ps.setString(3, t.getBookId());
            ps.setString(4, t.getLibrarianId());
            ps.setDate  (5, Date.valueOf(t.getIssueDate()));
            ps.setDate  (6, Date.valueOf(t.getDueDate()));
            ps.setDate  (7, t.getReturnDate() == null ? null : Date.valueOf(t.getReturnDate()));
            ps.setString(8, t.getStatus().name());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public Optional<Transaction> findById(String id) {
        String sql = "SELECT * FROM transaction WHERE transaction_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transaction";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void delete(String id) { /* transactions not deleted */ }

    public List<Transaction> findByUserId(String userId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transaction WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Transaction> findByBookId(String bookId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transaction WHERE book_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bookId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Optional<Transaction> findActiveByBookId(String bookId) {
        String sql = "SELECT * FROM transaction WHERE book_id = ? AND status IN ('ISSUED','OVERDUE')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bookId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction t = new Transaction(
            rs.getString("transaction_id"),
            rs.getString("user_id"),
            rs.getString("book_id"),
            rs.getString("librarian_id"),
            rs.getDate  ("issue_date").toLocalDate(),
            rs.getDate  ("due_date").toLocalDate()
        );
        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) t.setReturnDate(returnDate.toLocalDate());
        t.setStatus(TransactionStatus.valueOf(rs.getString("status")));
        return t;
    }
}