package com.library.strategy;

public class StandardFineStrategy implements FineCalculationStrategy {
    private static final double FINE_PER_DAY = 5.0;

    @Override
    public double calculate(long daysLate) {
        return daysLate * FINE_PER_DAY;
    }

    @Override
    public String getStrategyName() {
        return "Standard (₹5/day)";
    }
}
