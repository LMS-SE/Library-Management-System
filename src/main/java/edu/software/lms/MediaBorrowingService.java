package edu.software.lms;

import java.time.LocalDate;
import java.util.List;

public class MediaBorrowingService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final TimeProvider timeProvider;

    private static final int BOOK_LOAN_DAYS = 28;
    private static final int CD_LOAN_DAYS = 7;

    public MediaBorrowingService(UserRepository userRepository,
                                 BookRepository bookRepository,
                                 LoanRepository loanRepository,
                                 TimeProvider timeProvider) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.timeProvider = timeProvider;
    }

    public Pair<Boolean, String> borrowMedia(String username, int mediaId) {
        if (username == null || username.isEmpty()) return new Pair<>(false, "Invalid user");
        User user = userRepository.getUserByUsername(username);
        if (user == null) return new Pair<>(false, "User not found");
        if (user.getFineBalance() > 0) return new Pair<>(false, "Cannot borrow: outstanding fine balance = " + user.getFineBalance());

        List<Loan> userLoans = loanRepository.getLoansByUserId(user.getId());
        LocalDate borrowDate = timeProvider.today();
        boolean hasOverdue = userLoans.stream().anyMatch(l -> l.isOverdue(borrowDate) && !l.isReturned());
        if (hasOverdue) return new Pair<>(false, "Cannot borrow: you have overdue item(s).");

        Book book = bookRepository.getBookById(mediaId);
        if (book == null) return new Pair<>(false, "Media not found");
        if (book.isBorrowed()) return new Pair<>(false, "Media is already borrowed");

        MediaType mt = (book instanceof CD) ? MediaType.CD : MediaType.BOOK;
        int days = (mt == MediaType.CD) ? CD_LOAN_DAYS : BOOK_LOAN_DAYS;
        LocalDate dueDate = borrowDate.plusDays(days);

        MediaLoan loan = new MediaLoan(user.getId(), mediaId, borrowDate, dueDate, mt);
        boolean added = loanRepository.addLoan(loan);
        if (!added) return new Pair<>(false, "Failed to record loan");

        book.setBorrowed(true);
        user.addLoanId(loan.getId());
        return new Pair<>(true, "Media borrowed successfully. Due date: " + dueDate);
    }

    public Pair<Boolean, String> returnMedia(String loanId) {
        Loan loan = loanRepository.getLoanById(loanId);
        if (loan == null) return new Pair<>(false, "Loan not found");
        if (loan.isReturned()) return new Pair<>(false, "Already returned");

        LocalDate today = timeProvider.today();
        loan.setReturnedDate(today);

        // determine media type (default BOOK)
        MediaType mt = MediaType.BOOK;
        if (loan instanceof MediaLoan mediaLoan) {
            mt = mediaLoan.getMediaType();
        } else {
            // try to detect by inspecting book repository (fallback)
            Book b = bookRepository.getBookById(loan.getBookId());
            if (b instanceof CD) mt = MediaType.CD;
        }

        int overdueDays = loan.overdueDays(today);
        int fine = 0;
        if (mt == MediaType.CD) {
            fine = new CDFineStrategy().calculateFine(overdueDays);
        } else {
            fine = new BookFineStrategy().calculateFine(overdueDays);
        }
        loan.setFineApplied(fine);
        if (fine > 0) {
            User user = userRepository.getUserById(loan.getUserId());
            if (user != null) user.addFine(fine);
            loan.setFinePaid(false);
        } else {
            loan.setFinePaid(true);
        }

        returnMediaHelper(loan, loanRepository, bookRepository, userRepository);

        return new Pair<>(true, "Media returned. Applied fine: " + fine + " NIS");
    }

    static void returnMediaHelper(Loan loan, LoanRepository loanRepository, BookRepository bookRepository, UserRepository userRepository) {
        loanRepository.updateLoan(loan);
        Book book = bookRepository.getBookById(loan.getBookId());
        if (book != null) book.setBorrowed(false);

        User user = userRepository.getUserById(loan.getUserId());
        if (user != null) user.removeLoanId(loan.getId());
    }

}
