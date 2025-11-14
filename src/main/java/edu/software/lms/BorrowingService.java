package edu.software.lms;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BorrowingService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final TimeProvider timeProvider;
    private final FineCalculator fineCalculator;
    private static final int BOOK_LOAN_DAYS = 28;

    public BorrowingService(UserRepository userRepository,
                            BookRepository bookRepository,
                            LoanRepository loanRepository,
                            TimeProvider timeProvider,
                            FineStrategy fineStrategy) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.timeProvider = timeProvider;
        this.fineCalculator = new FineCalculator(fineStrategy, timeProvider);
    }

    public Pair<Boolean, String> borrowBook(String username, int bookId) {
        if (username == null || username.isEmpty()) return new Pair<>(false, "Invalid user");
        User user = userRepository.getUserByUsername(username);
        if (user == null) return new Pair<>(false, "User not found");

        if (user.getFineBalance() > 0) {
            return new Pair<>(false, "Cannot borrow: outstanding fine balance = " + user.getFineBalance());
        }

        List<Loan> userLoans = loanRepository.getLoansByUserId(user.getId());
        LocalDate today = timeProvider.today();
        boolean hasOverdue = userLoans.stream().anyMatch(l -> l.isOverdue(today) && !l.isReturned());
        if (hasOverdue) {
            return new Pair<>(false, "Cannot borrow: you have overdue book(s).");
        }

        Book book = bookRepository.getBookById(bookId);
        if (book == null) return new Pair<>(false, "Book not found");
        if (book.isBorrowed()) return new Pair<>(false, "Book is already borrowed");

        LocalDate borrowDate = today;
        LocalDate dueDate = borrowDate.plus(BOOK_LOAN_DAYS, ChronoUnit.DAYS);
        Loan loan = new Loan(user.getId(), bookId, borrowDate, dueDate);
        boolean added = loanRepository.addLoan(loan);
        if (!added) return new Pair<>(false, "Failed to record loan");

        book.setBorrowed(true);
        user.addLoanId(loan.getId());

        return new Pair<>(true, "Book borrowed successfully. Due date: " + dueDate.toString());
    }

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

        loanRepository.updateLoan(loan);
        Book book = bookRepository.getBookById(loan.getBookId());
        if (book != null) book.setBorrowed(false);

        User user = userRepository.getUserById(loan.getUserId());
        if (user != null) user.removeLoanId(loan.getId());

        return new Pair<>(true, "Book returned. Applied fine: " + fine + " NIS");
    }

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

    public java.util.List<Loan> detectOverdueLoans() {
        LocalDate today = timeProvider.today();
        return loanRepository.getAllLoans().stream()
                .filter(l -> !l.isReturned() && l.isOverdue(today))
                .toList();
    }
}

