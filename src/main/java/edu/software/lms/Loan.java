package edu.software.lms;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a loan record for a borrowed book or media item.
 *
 * <p>Includes borrow date, due date, return date, fine information,
 * and the IDs of both the user and item.</p>
 */
public class Loan {

    private final String id;
    private final String userId;
    private final int bookId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;

    private LocalDate returnedDate;
    private int fineApplied;
    private boolean finePaid;

    /**
     * Creates a new loan record with generated UUID.
     *
     * @param userId      ID of borrowing user
     * @param bookId      ID of borrowed book or media
     * @param borrowDate  date loan was initiated
     * @param dueDate     date the item is due for return
     */
    public Loan(String userId, int bookId, LocalDate borrowDate, LocalDate dueDate) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnedDate = null;
        this.fineApplied = 0;
        this.finePaid = false;
    }

    /** @return unique loan ID */
    public String getId() { return id; }

    /** @return ID of the user who borrowed the item */
    public String getUserId() { return userId; }

    /** @return ID of the borrowed book/media */
    public int getBookId() { return bookId; }

    /** @return date the loan started */
    public LocalDate getBorrowDate() { return borrowDate; }

    /** @return due date for return */
    public LocalDate getDueDate() { return dueDate; }

    /** @return return date or null if item not yet returned */
    public LocalDate getReturnedDate() { return returnedDate; }

    /** Sets the return date. */
    public void setReturnedDate(LocalDate returnedDate) { this.returnedDate = returnedDate; }

    /** @return fine applied to this loan */
    public int getFineApplied() { return fineApplied; }

    /** Sets the fine amount applied. */
    public void setFineApplied(int fineApplied) { this.fineApplied = fineApplied; }

    /** @return whether the applied fine has been paid */
    public boolean isFinePaid() { return finePaid; }

    /** Marks the fine as paid or unpaid. */
    public void setFinePaid(boolean finePaid) { this.finePaid = finePaid; }

    /**
     * @return true if the loan has been returned
     */
    public boolean isReturned() {
        return returnedDate != null;
    }

    /**
     * Determines whether the loan is overdue as of a given date.
     *
     * @param currentDate date to compare with
     * @return true if overdue, false otherwise
     */
    public boolean isOverdue(LocalDate currentDate) {
        if (isReturned()) {
            return returnedDate.isAfter(dueDate);
        }
        return currentDate.isAfter(dueDate);
    }

    /**
     * Calculates overdue days relative to the given date.
     *
     * @param currentDate date to compare against due date
     * @return number of days overdue, or 0 if not overdue
     */
    public int overdueDays(LocalDate currentDate) {
        LocalDate checkDate = isReturned() ? returnedDate : currentDate;
        if (!checkDate.isAfter(dueDate)) return 0;

        return (int) (checkDate.toEpochDay() - dueDate.toEpochDay());
    }
}
