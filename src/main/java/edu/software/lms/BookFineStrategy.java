package edu.software.lms;
/**
 * A {@link FineStrategy} implementation used for calculating fines
 * specifically for books. Books incur a fixed fine rate per overdue day.
 *
 * <p>The default fine is 10 NIS per day, but a custom
 * rate may be provided using the parameterized constructor.</p>
 */
public class BookFineStrategy implements FineStrategy {
    private final int perDay;

    /**
     * Creates a BookFineStrategy with the default fine of 10 NIS per overdue day.
     */
    public BookFineStrategy() { this.perDay = 10; } // 10 NIS/day
    /**
     * Creates a BookFineStrategy with a custom overdue fine rate.
     *
     * @param perDay the fine amount per overdue day
     */
    public BookFineStrategy(int perDay) { this.perDay = perDay; }

    /**
     * Calculates the overdue fine for a book.
     *
     * @param overdueDays number of days the item is overdue
     * @return the total fine (0 if no overdue days or negative input)
     */
    @Override
    public int calculateFine(int overdueDays) {
        if (overdueDays <= 0) return 0;
        return overdueDays * perDay;
    }
}
