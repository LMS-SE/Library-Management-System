package edu.software.lms;

import java.time.LocalDate;

/**
 * Subclass of Loan that carries the media type (BOOK or CD).
 */
public class MediaLoan extends Loan {
    private final MediaType mediaType;

    public MediaLoan(String userId, int bookId, LocalDate borrowDate, LocalDate dueDate, MediaType mediaType) {
        super(userId, bookId, borrowDate, dueDate);
        this.mediaType = mediaType;
    }

    public MediaType getMediaType() { return mediaType; }
}
