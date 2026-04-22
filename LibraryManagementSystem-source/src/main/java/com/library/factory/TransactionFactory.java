package com.library.factory;

import com.library.model.Transaction;
import java.time.LocalDate;

public class TransactionFactory implements EntityFactory<Transaction> {
    @Override
    public Transaction create(Object... params) {
        // params: transactionId, userId, bookId, librarianId, issueDate, dueDate
        if (params.length < 6) throw new IllegalArgumentException("TransactionFactory requires 6 parameters");
        return new Transaction(
                (String)    params[0],
                (String)    params[1],
                (String)    params[2],
                (String)    params[3],
                (LocalDate) params[4],
                (LocalDate) params[5]
        );
    }
}
