package com.library.service;

import com.library.enums.BookStatus;
import com.library.factory.BookFactory;
import com.library.model.Book;
import com.library.observer.LibraryObserver;
import com.library.observer.Observable;
import com.library.repository.BookRepository;
import com.library.strategy.BookSearchStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Single Responsibility: manages book lifecycle.
 * Open/Closed: search strategy injected, no modification needed for new search types.
 * Observer: fires BOOK_DAMAGED events.
 */
public class BookService implements Observable {

    private final BookRepository bookRepository;
    private final BookFactory bookFactory;
    private BookSearchStrategy searchStrategy;
    private final List<LibraryObserver> observers = new ArrayList<>();

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.bookFactory = new BookFactory();
    }

    // ── Observer wiring ────────────────────────────────────────────────────
    @Override public void addObserver(LibraryObserver o)    { observers.add(o); }
    @Override public void removeObserver(LibraryObserver o) { observers.remove(o); }

    @Override
    public void notifyObservers(String eventType, String message, String targetId) {
        observers.forEach(o -> o.update(eventType, message, targetId));
    }

    // ── Search strategy (Strategy pattern) ────────────────────────────────
    public void setSearchStrategy(BookSearchStrategy strategy) {
        this.searchStrategy = strategy;
    }

    public List<Book> searchBooks(String query) {
        if (searchStrategy == null) throw new IllegalStateException("Search strategy not set");
        return searchStrategy.search(bookRepository.findAll(), query);
    }

    // ── CRUD ──────────────────────────────────────────────────────────────
    public Book addBook(String bookId, String title, String author, String publisher,
                        int publicationYear, String isbn, String category, String shelfLocation) {
        Book book = bookFactory.create(bookId, title, author, publisher,
                publicationYear, isbn, category, shelfLocation);
        bookRepository.save(book);
        return book;
    }

    public Optional<Book> findById(String bookId) {
        return bookRepository.findById(bookId);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public boolean updateBook(String bookId, String title, String author,
                              String publisher, int year, String category, String shelf) {
        Optional<Book> opt = bookRepository.findById(bookId);
        if (opt.isEmpty()) return false;
        Book book = opt.get();
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setPublicationYear(year);
        book.setCategory(category);
        book.setShelfLocation(shelf);
        bookRepository.save(book);
        return true;
    }

    public boolean removeBook(String bookId) {
        Optional<Book> opt = bookRepository.findById(bookId);
        if (opt.isEmpty()) return false;
        bookRepository.delete(bookId);
        return true;
    }

    public boolean markAsDamaged(String bookId) {
        Optional<Book> opt = bookRepository.findById(bookId);
        if (opt.isEmpty()) return false;
        Book book = opt.get();
        book.setStatus(BookStatus.DAMAGED);
        bookRepository.save(book);
        notifyObservers("BOOK_DAMAGED",
                "Book '" + book.getTitle() + "' (ID: " + bookId + ") is damaged and needs repair/replacement.",
                bookId);
        return true;
    }

    public void setStatus(String bookId, BookStatus status) {
        bookRepository.findById(bookId).ifPresent(b -> {
            b.setStatus(status);
            bookRepository.save(b);
        });
    }

    public boolean isAvailable(String bookId) {
        return bookRepository.findById(bookId)
                .map(Book::isAvailable)
                .orElse(false);
    }
}
