package com.library.controller;

import com.library.model.Book;
import com.library.service.BookService;
import com.library.strategy.*;

import java.util.List;
import java.util.Optional;

/**
 * MVC Controller – receives user input from View, delegates to Service, returns result.
 * Depends on abstraction (BookService), not concretions.
 */
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    public Book addBook(String bookId, String title, String author, String publisher,
                        int year, String isbn, String category, String shelf) {
        return bookService.addBook(bookId, title, author, publisher, year, isbn, category, shelf);
    }

    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    public Optional<Book> getBookById(String bookId) {
        return bookService.findById(bookId);
    }

    public List<Book> searchByTitle(String query) {
        bookService.setSearchStrategy(new SearchByTitleStrategy());
        return bookService.searchBooks(query);
    }

    public List<Book> searchByAuthor(String query) {
        bookService.setSearchStrategy(new SearchByAuthorStrategy());
        return bookService.searchBooks(query);
    }

    public List<Book> searchByCategory(String query) {
        bookService.setSearchStrategy(new SearchByCategoryStrategy());
        return bookService.searchBooks(query);
    }

    public List<Book> searchByIsbn(String query) {
        bookService.setSearchStrategy(new SearchByISBNStrategy());
        return bookService.searchBooks(query);
    }

    public boolean updateBook(String bookId, String title, String author,
                              String publisher, int year, String category, String shelf) {
        return bookService.updateBook(bookId, title, author, publisher, year, category, shelf);
    }

    public boolean removeBook(String bookId) {
        return bookService.removeBook(bookId);
    }

    public boolean markAsDamaged(String bookId) {
        return bookService.markAsDamaged(bookId);
    }

    public boolean isAvailable(String bookId) {
        return bookService.isAvailable(bookId);
    }
}
