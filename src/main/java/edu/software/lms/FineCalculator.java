package edu.software.lms;

import java.time.LocalDate;

public class FineCalculator {
    private final FineStrategy strategy;
    private final TimeProvider timeProvider;

    public FineCalculator(FineStrategy strategy, TimeProvider timeProvider) {
        this.strategy = strategy;
        this.timeProvider = timeProvider;
    }

    public int computeFineForLoan(Loan loan) {
        LocalDate today = timeProvider.today();
        int overdueDays = loan.overdueDays(today);
        return strategy.calculateFine(overdueDays);
    }
}
