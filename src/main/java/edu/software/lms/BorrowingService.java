package edu.software.lms;

import java.time.LocalDate;
import java.util.List;
/**
 * Service responsible for handling all borrowing and returning operations
 * for books. This includes loan creation, overdue checking, fine calculation,
 * fine payment, and integration with {@link ReminderService} for sending overdue notifications.
 *
 * <p>The service coordinates interactions between:
 * <ul>
 *     <li>{@link UserRepository}</li>
 *     <li>{@link BookRepository}</li>
 *     <li>{@link LoanRepository}</li>
 *     <li>{@link TimeProvider}</li>
 *     <li>{@link FineCalculator}</li>
 * </ul></p>
 *
 * <p>This service is focused on books, while {@link MediaBorrowingService}
 * provides media-type handling for CDs and books together.</p>
 */
public class BorrowingService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final TimeProvider timeProvider;
    private final FineCalculator fineCalculator;
    private static final int BOOK_LOAN_DAYS = 28;
    private final ReminderService  reminderService;

    /**
     * Constructs a new BorrowingService with the required dependencies.
     *
     * @param userRepository repository for user data
     * @param bookRepository repository for books
     * @param loanRepository repository for loan data
     * @param timeProvider provider for current dates
     * @param fineStrategy strategy used for fine calculation
     * @param emailNotifier observer notified when overdue reminders are sent
     */
    public BorrowingService(UserRepository userRepository,
                            BookRepository bookRepository,
                            LoanRepository loanRepository,
                            TimeProvider timeProvider,
                            FineStrategy fineStrategy,
                            Observer emailNotifier) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.timeProvider = timeProvider;
        this.fineCalculator = new FineCalculator(fineStrategy, timeProvider);
        reminderService=new ReminderService(loanRepository,timeProvider, userRepository);
        reminderService.addObserver(emailNotifier);

    }
    /**
     * Sends overdue reminder notifications to all users with overdue loans.
     * This delegates directly to {@link ReminderService#sendOverdueNotifications()}.
     */
    void remindOverdue(){
        reminderService.sendOverdueNotifications();
    }
    /**
     * Attempts to borrow a book for a given user and book ID.
     *
     * <p>Borrowing fails under any of these conditions:
     * <ul>
     *     <li>User does not exist</li>
     *     <li>User has outstanding fines</li>
     *     <li>User has overdue loans</li>
     *     <li>Book does not exist</li>
     *     <li>Book is already borrowed</li>
     * </ul>
     * </p>
     *
     * <p>If successful, a new {@link Loan} is created and persisted.</p>
     *
     * @param username username of borrower
     * @param bookId ID of book to borrow
     * @return a {@link Pair} containing success flag and message
     */
    public Pair<Boolean, String> borrowBook(String username, int bookId) {
        if (username == null || username.isEmpty()) return new Pair<>(false, "Invalid user");
        User user = userRepository.getUserByUsername(username);
        if (user == null) return new Pair<>(false, "User not found");

        if (user.getFineBalance() > 0) {
            return new Pair<>(false, "Cannot borrow: outstanding fine balance = " + user.getFineBalance());
        }

        List<Loan> userLoans = loanRepository.getLoansByUserId(user.getId());
        LocalDate borrowDate = timeProvider.today();
        boolean hasOverdue = userLoans.stream().anyMatch(l -> l.isOverdue(borrowDate) && !l.isReturned());
        if (hasOverdue) {
            return new Pair<>(false, "Cannot borrow: you have overdue book(s).");
        }

        Book book = bookRepository.getBookById(bookId);
        if (book == null) return new Pair<>(false, "Book not found");
        if (book.isBorrowed()) return new Pair<>(false, "Book is already borrowed");

        LocalDate dueDate = borrowDate.plusDays(BOOK_LOAN_DAYS);
        Loan loan = new Loan(user.getId(), bookId, borrowDate, dueDate);
        boolean added = loanRepository.addLoan(loan);
        if (!added) return new Pair<>(false, "Failed to record loan");

        book.setBorrowed(true);
        user.addLoanId(loan.getId());

        return new Pair<>(true, "Book borrowed successfully. Due date: " + dueDate.toString());
    }
    /**
     * Attempts to return a book associated with a given loan ID.
     *
     * <p>Returning triggers the following steps:
     * <ul>
     *     <li>Loan is marked as returned</li>
     *     <li>Fine is calculated if overdue</li>
     *     <li>Fine is applied to the user's balance</li>
     *     <li>The underlying book is marked as not borrowed</li>
     *     <li>The loan is updated in the repository</li>
     * </ul>
     * </p>
     *
     * @param loanId ID of loan to return
     * @return a {@link Pair} containing success flag and message
     */
    public Pair<Boolean, String> returnBook(String loanId) {
        Loan loan = loanRepository.getLoanById(loanId);
        if (loan == null) return new Pair<>(false, "Loan not found");
        if (loan.isReturned()) return new Pair<>(false, "Book already returned");

        LocalDate today = timeProvider.today();
        loan.setReturnedDate(today);

        int fine = fineCalculator.computeFineForLoan(loan);
        loan.setFineApplied(fine);
        if (fine > 0) {
            User user = userRepository.getUserById(loan.getUserId());
            if (user != null) {
                user.addFine(fine);
            }
            loan.setFinePaid(false);
        } else {
            loan.setFinePaid(true);
        }

        MediaBorrowingService.returnMediaHelper(loan, loanRepository, bookRepository, userRepository);

        return new Pair<>(true, "Book returned. Applied fine: " + fine + " NIS");
    }
    /**
     * Allows a user to pay part or all of their fine balance.
     *
     * @param username user whose fines are being paid
     * @param amount amount to pay (must be positive)
     * @return a {@link Pair} describing success and message feedback
     */
    public Pair<Boolean, String> payFine(String username, int amount) {
        if (username == null) return new Pair<>(false, "Invalid user");
        if (amount <= 0) return new Pair<>(false, "Amount must be positive");

        User user = userRepository.getUserByUsername(username);
        if (user == null) return new Pair<>(false, "User not found");

        int before = user.getFineBalance();
        user.payFine(amount);
        int after = user.getFineBalance();

        String msg = "Paid " + amount + " NIS. Balance before: " + before + ", now: " + after;
        return new Pair<>(true, msg);
    }

}

