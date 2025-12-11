package edu.software.lms;

import java.time.LocalDate;

public class BorrowingService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final TimeProvider timeProvider;
    private final FineCalculator fineCalculator;
    private final ReminderService reminderService;
    private static final int BOOK_LOAN_DAYS = 28;

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

        reminderService = new ReminderService(loanRepository, timeProvider, userRepository);
        reminderService.addObserver(emailNotifier);
    }

    void remindOverdue() {
        reminderService.sendOverdueNotifications();
    }

    public Pair<Boolean, String> borrowBook(String username, int bookId) {
        Pair<Boolean, String> result = validateBorrowRequest(username, bookId);
        if (Boolean.FALSE.equals(result.first)) return result;

        User user = userRepository.getUserByUsername(username);
        LocalDate today = timeProvider.today();
        LocalDate dueDate = today.plusDays(BOOK_LOAN_DAYS);

        Loan loan = new Loan(user.getId(), bookId, today, dueDate);
        loanRepository.addLoan(loan);

        Book book = bookRepository.getBookById(bookId);
        book.setBorrowed(true);

        user.addLoanId(loan.getId());
        return new Pair<>(true, "Book borrowed successfully. Due date: " + dueDate);
    }

    public Pair<Boolean, String> returnBook(String loanId) {
        Loan loan = loanRepository.getLoanById(loanId);
        if (loan == null) return new Pair<>(false, "Loan not found");
        if (loan.isReturned()) return new Pair<>(false, "Book already returned");

        LocalDate today = timeProvider.today();
        loan.setReturnedDate(today);

        int fine = fineCalculator.computeFineForLoan(loan);
        applyFine(loan, fine);
        updateStatusAfterReturn(loan);

        return new Pair<>(true, "Book returned. Applied fine: " + fine + " NIS");
    }

    public Pair<Boolean, String> payFine(String username, int amount) {
        if (username == null) return new Pair<>(false, "Invalid user");
        if (amount <= 0) return new Pair<>(false, "Amount must be positive");

        User user = userRepository.getUserByUsername(username);
        if (user == null) return new Pair<>(false, "User not found");

        int before = user.getFineBalance();
        user.payFine(amount);

        return new Pair<>(true,
                "Paid " + amount + " NIS. Balance before: " + before + ", now: " + user.getFineBalance());
    }

    private Pair<Boolean, String> validateBorrowRequest(String username, int bookId) {
        if (username == null || username.isEmpty()) return new Pair<>(false, "Invalid user");

        User user = userRepository.getUserByUsername(username);
        if (user == null) return new Pair<>(false, "User not found");

        if (user.getFineBalance() > 0)
            return new Pair<>(false, "Cannot borrow: outstanding fine = " + user.getFineBalance());

        LocalDate today = timeProvider.today();
        boolean overdue = loanRepository.getLoansByUserId(user.getId())
                .stream()
                .anyMatch(l -> !l.isReturned() && l.isOverdue(today));

        if (overdue) return new Pair<>(false, "Cannot borrow: you have overdue book(s).");

        Book book = bookRepository.getBookById(bookId);
        if (book == null) return new Pair<>(false, "Book not found");
        if (book.isBorrowed()) return new Pair<>(false, "Book is already borrowed");

        return new Pair<>(true, "");
    }

    private void applyFine(Loan loan, int fine) {
        loan.setFineApplied(fine);
        loan.setFinePaid(fine == 0);

        if (fine > 0) {
            User user = userRepository.getUserById(loan.getUserId());
            if (user != null) user.addFine(fine);
        }
    }

    private void updateStatusAfterReturn(Loan loan) {
        MediaBorrowingService.returnMediaHelper(loan, loanRepository, bookRepository, userRepository);
    }
}
