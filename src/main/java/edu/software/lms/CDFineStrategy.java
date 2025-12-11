package edu.software.lms;

/**
 * A {@link FineStrategy} implementation for CDs.
 * CDs have a higher overdue fine rate than books by default.
 *
 * <p>The default rate is 20 NIS per day, but a custom fine rate
 * may be provided via the parameterized constructor.</p>
 */
public class CDFineStrategy implements FineStrategy {

    private final int perDay;

    /**
     * Creates a CDFineStrategy with the default fine of 20 NIS per overdue day.
     */
    public CDFineStrategy() {
        this.perDay = 20; // default CD fine per day
    }

    /**
     * Creates a CDFineStrategy with a custom overdue fine rate.
     *
     * @param perDay fine amount to apply for every overdue day
     */
    public CDFineStrategy(int perDay) {
        this.perDay = perDay;
    }

    /**
     * Computes the fine for a given overdue duration.
     *
     * @param overdueDays number of days the CD is overdue
     * @return total fine; returns 0 if overdueDays is zero or negative
     */
    @Override
    public int calculateFine(int overdueDays) {
        if (overdueDays <= 0) return 0;
        return overdueDays * perDay;
    }
}
