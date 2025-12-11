package edu.software.lms;

/**
 * Enumeration representing the media type of a library item.
 *
 * <p>Used in {@link MediaLoan} and {@link MediaBorrowingService}
 * to determine loan durations and fine strategies.</p>
 */
public enum MediaType {

    /** Standard book item. */
    BOOK,

    /** Compact disc (CD). */
    CD
}
