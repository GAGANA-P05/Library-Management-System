package com.library.model;

import com.library.enums.FineStatus;
import java.time.LocalDate;

public class Fine {
    private String fineId;
    private String transactionId;
    private String userId;
    private long daysLate;
    private double fineAmount;
    private FineStatus paymentStatus;
    private LocalDate paymentDate;

    public Fine(String fineId, String transactionId, String userId,
                long daysLate, double fineAmount) {
        this.fineId = fineId;
        this.transactionId = transactionId;
        this.userId = userId;
        this.daysLate = daysLate;
        this.fineAmount = fineAmount;
        this.paymentStatus = FineStatus.UNPAID;
    }

    public String getFineId() { return fineId; }
    public String getTransactionId() { return transactionId; }
    public String getUserId() { return userId; }
    public long getDaysLate() { return daysLate; }
    public double getFineAmount() { return fineAmount; }
    public FineStatus getPaymentStatus() { return paymentStatus; }
    public LocalDate getPaymentDate() { return paymentDate; }

    public void markAsPaid() {
        this.paymentStatus = FineStatus.PAID;
        this.paymentDate = LocalDate.now();
    }

    public boolean isPaid() {
        return this.paymentStatus == FineStatus.PAID;
    }

    @Override
    public String toString() {
        return "Fine{id='" + fineId + "', amount=" + fineAmount +
               ", status=" + paymentStatus + "}";
    }
}
