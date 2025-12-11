package edu.software.lms;

/**
 * Strategy interface for determining how fines are calculated
 * based on the number of overdue days.
 *
 * <p>Different media types (books, CDs, etc.) may use different
 * fine strategies by implementing this interface.</p>
 */
public interface FineStrategy {

    /**
     * Computes the fine amount for a given number of overdue days.
     *
     * @param overdueDays number of days the item is overdue; may be zero or negative
     * @return total fine amount, typically zero if {@code overdueDays <= 0}
     */
    int calculateFine(int overdueDays);
}
