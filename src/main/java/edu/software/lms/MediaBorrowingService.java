package edu.software.lms;

import java.time.LocalDate;
import java.util.List;

/**
 * Service responsible for borrowing and returning media items (Books or CDs).
 *
 * <p>Implements business rules such as overdue checks, fine calculations,
 * loan creation, and updating media availability.</p>
 */
public class MediaBorrowingService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final TimeProvider timeProvider;

    /** Loan duration for books (in days). */
    private static final int BOOK_LOAN_DAYS = 28;

    /** Loan duration for CDs (in days). */
    private static final int CD_LOAN_DAYS = 7;

    /**
     * Constructs a new media borrowing service.
     *
     * @param userRepository repository of users
     * @param bookRepository repository of books/media items
     * @param loanRepository repository managing loan records
     * @param timeProvider provider of the current date
     */
    public MediaBorrowingService(UserRepository userRepository,
                                 BookRepository bookRepository,
                                 LoanRepository loanRepository,
                                 TimeProvider timeProvider) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.timeProvider = timeProvider;
    }

    /**
     * Attempts to borrow a media item for a user.
     *
     * <p>Borrowing fails if:</p>
     * <ul>
     *     <li>User does not exist</li>
     *     <li>User has outstanding fines</li>
     *     <li>User has any overdue loans</li>
     *     <li>Media ID does not exist</li>
     *     <li>Media is already borrowed</li>
     * </ul>
     *
     * @param username username of the borrower
     * @param mediaId ID of the media item (book or CD)
     * @return pair of (success flag, user message)
     */
    public Pair<Boolean, String> borrowMedia(String username, int mediaId) {

        if (username == null || username.isEmpty()) {
            return new Pair<>(false, "Invalid user");
        }

        User user = userRepository.getUserByUsername(username);
        if (user == null) return new Pair<>(false, "User not found");

        if (user.getFineBalance() > 0)
            return new Pair<>(false, "Cannot borrow: outstanding fine balance = " + user.getFineBalance());

        List<Loan> userLoans = loanRepository.getLoansByUserId(user.getId());
        LocalDate borrowDate = timeProvider.today();

        // User cannot borrow if they have active overdue loans
        boolean hasOverdue = userLoans.stream()
                .anyMatch(l -> l.isOverdue(borrowDate) && !l.isReturned());

        if (hasOverdue)
            return new Pair<>(false, "Cannot borrow: you have overdue item(s).");

        Book book = bookRepository.getBookById(mediaId);
        if (book == null) return new Pair<>(false, "Media not found");
        if (book.isBorrowed()) return new Pair<>(false, "Media is already borrowed");

        // Determine media type and loan duration
        MediaType type = (book instanceof CD) ? MediaType.CD : MediaType.BOOK;
        int days = (type == MediaType.CD) ? CD_LOAN_DAYS : BOOK_LOAN_DAYS;

        LocalDate dueDate = borrowDate.plusDays(days);

        // Create loan record
        MediaLoan loan = new MediaLoan(user.getId(), mediaId, borrowDate, dueDate, type);
        boolean added = loanRepository.addLoan(loan);
        if (!added) return new Pair<>(false, "Failed to record loan");

        // Mark media as borrowed
        book.setBorrowed(true);
        user.addLoanId(loan.getId());

        return new Pair<>(true, "Media borrowed successfully. Due date: " + dueDate);
    }

    /**
     * Returns a media item, calculates overdue fine, and updates repositories.
     *
     * @param loanId ID of the loan record
     * @return pair of (success flag, result message)
     */
    public Pair<Boolean, String> returnMedia(String loanId) {

        Loan loan = loanRepository.getLoanById(loanId);
        if (loan == null) return new Pair<>(false, "Loan not found");
        if (loan.isReturned()) return new Pair<>(false, "Already returned");

        LocalDate today = timeProvider.today();
        loan.setReturnedDate(today);

        // Determine media type (Book or CD)
        MediaType type = MediaType.BOOK;

        if (loan instanceof MediaLoan mediaLoan) {
            type = mediaLoan.getMediaType();
        } else {
            // fallback detection from book repository
            Book b = bookRepository.getBookById(loan.getBookId());
            if (b instanceof CD) type = MediaType.CD;
        }

        // Calculate fine
        int overdueDays = loan.overdueDays(today);
        int fine = (type == MediaType.CD)
                ? new CDFineStrategy().calculateFine(overdueDays)
                : new BookFineStrategy().calculateFine(overdueDays);

        loan.setFineApplied(fine);

        // If fine exists, apply it to the user
        if (fine > 0) {
            User user = userRepository.getUserById(loan.getUserId());
            if (user != null) user.addFine(fine);
            loan.setFinePaid(false);
        } else {
            loan.setFinePaid(true);
        }

        // Update data structures and media availability
        returnMediaHelper(loan, loanRepository, bookRepository, userRepository);

        return new Pair<>(true, "Media returned. Applied fine: " + fine + " NIS");
    }

    /**
     * Static helper used for updating repositories after return.
     *
     * @param loan loan to update
     * @param loanRepository loan storage
     * @param bookRepository book storage
     * @param userRepository user storage
     */
    static void returnMediaHelper(Loan loan,
                                  LoanRepository loanRepository,
                                  BookRepository bookRepository,
                                  UserRepository userRepository) {

        loanRepository.updateLoan(loan);

        Book book = bookRepository.getBookById(loan.getBookId());
        if (book != null) book.setBorrowed(false);

        User user = userRepository.getUserById(loan.getUserId());
        if (user != null) user.removeLoanId(loan.getId());
    }
}
