package com.library.service;

import com.library.enums.BookStatus;
import com.library.enums.TransactionStatus;
import com.library.factory.FineFactory;
import com.library.factory.TransactionFactory;
import com.library.model.*;
import com.library.observer.LibraryObserver;
import com.library.observer.Observable;
import com.library.repository.*;
import com.library.strategy.FineCalculationStrategy;
import com.library.strategy.StandardFineStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Creator (GRASP): TransactionService creates Transaction and Fine objects.
 * Controller (GRASP): coordinates between Book, User, Transaction, Fine subsystems.
 */
public class TransactionService implements Observable {

    private static final int DEFAULT_LOAN_DAYS = 14;

    private final TransactionRepository transactionRepository;
    private final FineRepository fineRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final TransactionFactory transactionFactory;
    private final FineFactory fineFactory;
    private FineCalculationStrategy fineStrategy;
    private final List<LibraryObserver> observers = new ArrayList<>();

    public TransactionService(TransactionRepository transactionRepository,
                              FineRepository fineRepository,
                              BookRepository bookRepository,
                              UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.fineRepository = fineRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.transactionFactory = new TransactionFactory();
        this.fineFactory = new FineFactory();
        this.fineStrategy = new StandardFineStrategy();
    }

    // ── Observer wiring ───────────────────────────────────────────────────
    @Override public void addObserver(LibraryObserver o)    { observers.add(o); }
    @Override public void removeObserver(LibraryObserver o) { observers.remove(o); }
    @Override
    public void notifyObservers(String eventType, String message, String targetId) {
        observers.forEach(o -> o.update(eventType, message, targetId));
    }

    // ── Fine Strategy (Strategy pattern) ─────────────────────────────────
    public void setFineStrategy(FineCalculationStrategy strategy) {
        this.fineStrategy = strategy;
    }

    // ── Issue Book ────────────────────────────────────────────────────────
    public Transaction issueBook(String userId, String bookId, String librarianId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        if (!book.isAvailable())
            throw new IllegalStateException("Book is not available: " + book.getTitle());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (user.getPendingFineAmount() > 0)
            throw new IllegalStateException(
                    "User has unpaid fines: ₹" + user.getPendingFineAmount() + ". Please clear before borrowing.");

        String txId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(DEFAULT_LOAN_DAYS);

        Transaction tx = transactionFactory.create(txId, userId, bookId, librarianId, issueDate, dueDate);
        transactionRepository.save(tx);

        book.setStatus(BookStatus.ISSUED);
        bookRepository.save(book);

        user.addBorrowedBook(bookId);
        userRepository.save(user);

        notifyObservers("BOOK_ISSUED",
                "Book '" + book.getTitle() + "' issued successfully. Due date: " + dueDate + ".",
                userId);

        return tx;
    }

    // ── Return Book ───────────────────────────────────────────────────────
    public Optional<Fine> returnBook(String transactionId, String librarianId) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));

        if (tx.getStatus() == TransactionStatus.RETURNED || tx.getStatus() == TransactionStatus.CLOSED)
            throw new IllegalStateException("Book already returned for transaction: " + transactionId);

        LocalDate returnDate = LocalDate.now();
        tx.setReturnDate(returnDate);
        tx.setStatus(TransactionStatus.RETURNED);
        transactionRepository.save(tx);

        Book book = bookRepository.findById(tx.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + tx.getBookId()));
        book.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book);

        User user = userRepository.findById(tx.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + tx.getUserId()));
        user.removeBorrowedBook(tx.getBookId());
        userRepository.save(user);

        // Check overdue
        if (tx.isOverdue()) {
            long daysLate = tx.getDaysLate();
            double fineAmount = fineStrategy.calculate(daysLate);

            String fineId = "FINE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Fine fine = fineFactory.create(fineId, transactionId, tx.getUserId(), daysLate, fineAmount);
            fineRepository.save(fine);

            user.setPendingFineAmount(user.getPendingFineAmount() + fineAmount);
            userRepository.save(user);

            tx.setStatus(TransactionStatus.OVERDUE);
            transactionRepository.save(tx);

            notifyObservers("FINE_GENERATED",
                    "Book '" + book.getTitle() + "' returned " + daysLate +
                    " day(s) late. Fine generated: ₹" + fineAmount + ". Fine ID: " + fineId,
                    tx.getUserId());

            return Optional.of(fine);
        }

        notifyObservers("BOOK_RETURNED",
                "Book '" + book.getTitle() + "' returned successfully. No fine incurred.",
                tx.getUserId());

        tx.setStatus(TransactionStatus.CLOSED);
        transactionRepository.save(tx);
        return Optional.empty();
    }

    // ── Pay Fine ──────────────────────────────────────────────────────────
    public boolean payFine(String fineId) {
        Optional<Fine> opt = fineRepository.findById(fineId);
        if (opt.isEmpty()) return false;

        Fine fine = opt.get();
        if (fine.isPaid()) return false;

        fine.markAsPaid();
        fineRepository.save(fine);

        User user = userRepository.findById(fine.getUserId()).orElse(null);
        if (user != null) {
            double remaining = Math.max(0, user.getPendingFineAmount() - fine.getFineAmount());
            user.setPendingFineAmount(remaining);
            userRepository.save(user);
        }

        // Close the transaction
        transactionRepository.findById(fine.getTransactionId()).ifPresent(tx -> {
            tx.setStatus(TransactionStatus.CLOSED);
            transactionRepository.save(tx);
        });

        notifyObservers("FINE_PAID",
                "Fine ₹" + fine.getFineAmount() + " paid successfully. Fine ID: " + fineId + ". Transaction closed.",
                fine.getUserId());

        return true;
    }

    // ── Queries ───────────────────────────────────────────────────────────
    public List<Transaction> getAllTransactions()                 { return transactionRepository.findAll(); }
    public List<Transaction> getTransactionsByUser(String uid)   { return transactionRepository.findByUserId(uid); }
    public List<Fine>        getAllFines()                        { return fineRepository.findAll(); }
    public List<Fine>        getFinesByUser(String uid)          { return fineRepository.findByUserId(uid); }
    public List<Fine>        getUnpaidFinesByUser(String uid)    { return fineRepository.findUnpaidByUserId(uid); }
    public double            totalFinesCollected()               { return fineRepository.totalCollected(); }
    public Optional<Transaction> getTransaction(String txId)     { return transactionRepository.findById(txId); }
    public Optional<Fine>        getFine(String fineId)          { return fineRepository.findById(fineId); }
}
