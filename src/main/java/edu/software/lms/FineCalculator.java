package edu.software.lms;

import java.time.LocalDate;

/**
 * Calculates fines for overdue loans using a provided {@link FineStrategy}.
 *
 * <p>This class acts as a wrapper around the strategy pattern. The specific
 * fine rules (e.g., book fine vs. CD fine) are determined by the strategy
 * implementation passed to the constructor.</p>
 *
 * <p>{@link TimeProvider} is used to fetch the current date for determining
 * overdue duration.</p>
 */
public class FineCalculator {

    private final FineStrategy strategy;
    private final TimeProvider timeProvider;

    /**
     * Constructs a FineCalculator with a specific fine strategy and a time provider.
     *
     * @param strategy     the strategy used to compute overdue fines
     * @param timeProvider provider for current date, enabling tests and mocking
     */
    public FineCalculator(FineStrategy strategy, TimeProvider timeProvider) {
        this.strategy = strategy;
        this.timeProvider = timeProvider;
    }

    /**
     * Computes the fine for a given loan using the strategy supplied at construction time.
     *
     * <p>Steps:</p>
     * <ol>
     *     <li>Determine today's date via the time provider</li>
     *     <li>Calculate overdue days using {@link Loan#overdueDays(LocalDate)}</li>
     *     <li>Delegate fine calculation to {@link FineStrategy#calculateFine(int)}</li>
     * </ol>
     *
     * @param loan the loan whose fine is being calculated
     * @return the total fine in NIS
     */
    public int computeFineForLoan(Loan loan) {
        LocalDate today = timeProvider.today();
        int overdueDays = loan.overdueDays(today);
        return strategy.calculateFine(overdueDays);
    }
}
