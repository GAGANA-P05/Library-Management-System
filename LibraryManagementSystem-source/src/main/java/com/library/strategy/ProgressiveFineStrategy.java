package com.library.strategy;

public class ProgressiveFineStrategy implements FineCalculationStrategy {

    @Override
    public double calculate(long daysLate) {
        if (daysLate <= 7)  return daysLate * 5.0;
        if (daysLate <= 14) return 7 * 5.0 + (daysLate - 7) * 10.0;
        return 7 * 5.0 + 7 * 10.0 + (daysLate - 14) * 20.0;
    }

    @Override
    public String getStrategyName() {
        return "Progressive (₹5→₹10→₹20/day)";
    }
}
