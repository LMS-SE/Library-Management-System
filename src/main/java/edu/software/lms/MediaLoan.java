package edu.software.lms;

import java.time.LocalDate;

/**
 * Specialized form of {@link Loan} that includes the media type (BOOK or CD).
 */
public class MediaLoan extends Loan {

    /** Type of media associated with this loan. */
    private final MediaType mediaType;

    /**
     * Constructs a media loan record.
     *
     * @param userId ID of user borrowing the item
     * @param bookId ID of the media item
     * @param borrowDate date the item was borrowed
     * @param dueDate date the item is due
     * @param mediaType type of media (BOOK or CD)
     */
    public MediaLoan(String userId,
                     int bookId,
                     LocalDate borrowDate,
                     LocalDate dueDate,
                     MediaType mediaType) {

        super(userId, bookId, borrowDate, dueDate);
        this.mediaType = mediaType;
    }

    /**
     * Returns the media type for this loan.
     *
     * @return BOOK or CD
     */
    public MediaType getMediaType() {
        return mediaType;
    }
}
