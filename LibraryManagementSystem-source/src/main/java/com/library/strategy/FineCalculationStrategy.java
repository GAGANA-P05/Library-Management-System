package com.library.strategy;

public interface FineCalculationStrategy {
    double calculate(long daysLate);
    String getStrategyName();
}
