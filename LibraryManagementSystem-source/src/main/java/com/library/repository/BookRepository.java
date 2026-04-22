package com.library.repository;

import com.library.db.DBConnection;
import com.library.enums.BookStatus;
import com.library.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository implements Repository<Book, String> {

    @Override
    public void save(Book book) {
        String sql = """
            INSERT INTO book (book_id, title, author, publisher, publication_year,
                              isbn, category, shelf_location, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                title = VALUES(title),
                author = VALUES(author),
                publisher = VALUES(publisher),
                publication_year = VALUES(publication_year),
                isbn = VALUES(isbn),
                category = VALUES(category),
                shelf_location = VALUES(shelf_location),
                status = VALUES(status)
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, book.getBookId());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getPublisher());
            ps.setInt   (5, book.getPublicationYear());
            ps.setString(6, book.getIsbn());
            ps.setString(7, book.getCategory());
            ps.setString(8, book.getShelfLocation());
            ps.setString(9, book.getStatus().name());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public Optional<Book> findById(String id) {
        String sql = "SELECT * FROM book WHERE book_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM book";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM book WHERE book_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public Optional<Book> findByIsbn(String isbn) {
        String sql = "SELECT * FROM book WHERE isbn = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    public List<Book> findByCategory(String category) {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE category = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        Book b = new Book(
            rs.getString("book_id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("publisher"),
            rs.getInt   ("publication_year"),
            rs.getString("isbn"),
            rs.getString("category"),
            rs.getString("shelf_location")
        );
        b.setStatus(BookStatus.valueOf(rs.getString("status")));
        return b;
    }
}