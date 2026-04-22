package com.library.factory;

import com.library.model.Fine;

public class FineFactory implements EntityFactory<Fine> {
    @Override
    public Fine create(Object... params) {
        // params: fineId, transactionId, userId, daysLate(long), fineAmount(double)
        if (params.length < 5) throw new IllegalArgumentException("FineFactory requires 5 parameters");
        return new Fine(
                (String) params[0],
                (String) params[1],
                (String) params[2],
                (long)   params[3],
                (double) params[4]
        );
    }
}
