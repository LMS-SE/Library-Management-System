package edu.software.lms;

import java.time.LocalDate;
import java.util.UUID;

public class Loan {
    private final String id; // uuid
    private final String userId;
    private final int bookId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnedDate; // null إذا لم يرجع بعد
    private int fineApplied; // NIS
    private boolean finePaid;

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

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public int getBookId() { return bookId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnedDate() { return returnedDate; }
    public void setReturnedDate(LocalDate returnedDate) { this.returnedDate = returnedDate; }
    public int getFineApplied() { return fineApplied; }
    public void setFineApplied(int fineApplied) { this.fineApplied = fineApplied; }
    public boolean isFinePaid() { return finePaid; }
    public void setFinePaid(boolean finePaid) { this.finePaid = finePaid; }

    public boolean isReturned() { return returnedDate != null; }

    public boolean isOverdue(LocalDate currentDate) {
        if (isReturned()) {
            return returnedDate.isAfter(dueDate);
        }
        return currentDate.isAfter(dueDate);
    }

    public int overdueDays(LocalDate currentDate) {
        LocalDate checkDate = isReturned() ? returnedDate : currentDate;
        if (!checkDate.isAfter(dueDate)) return 0;
        return (int) (checkDate.toEpochDay() - dueDate.toEpochDay());
    }
}
