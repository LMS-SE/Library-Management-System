package edu.software.lms;

import java.util.List;
import java.util.Scanner;

public class UserBookOperationsWindow implements Window {
    private static final Scanner scanner = new Scanner(System.in);

    private final UserService userService;
    private final BookRepository bookRepo;
    private final BorrowingService borrowingService;

    public UserBookOperationsWindow(UserService userService) {
        this.userService = userService;
        this.bookRepo = resolveRepoFromUserServiceOrFallback(userService);
        LoanRepository loanRepo = resolveLoanRepoFromUserServiceOrFallback(userService);
        this.borrowingService = new BorrowingService(userService.getUserRepository(), bookRepo, loanRepo, new SystemTimeProvider(), new BookFineStrategy());
    }

    private BookRepository resolveRepoFromUserServiceOrFallback(UserService userService) {
        if (userService == null) return new InMemoryBooks();
        try {
            var m = userService.getClass().getMethod("getBookRepository");
            Object repoObj = m.invoke(userService);
            if (repoObj instanceof BookRepository) return (BookRepository) repoObj;
        } catch (Exception ignored) { }
        return new InMemoryBooks();
    }

    private LoanRepository resolveLoanRepoFromUserServiceOrFallback(UserService userService) {
        if (userService == null) return new InMemoryLoanRepository();
        try {
            var m = userService.getClass().getMethod("getLoanRepository");
            Object repoObj = m.invoke(userService);
            if (repoObj instanceof LoanRepository) return (LoanRepository) repoObj;
        } catch (Exception ignored) { }
        return new InMemoryLoanRepository();
    }

    private void printHeader() { System.out.println("\n=== User Book Operations ==="); }

    private void printMenu() {
        System.out.println("Choose an option:");

        System.out.println("1) Search book");
        System.out.println("2) List all books");
        System.out.println("3) Borrow book");
        System.out.println("4) Return book");
        System.out.println("5) View my loans");
        System.out.println("6) Pay fine");
        System.out.println("back) Log out");
        System.out.println("0) Exit application");
        System.out.print("Choice: ");
    }






    private void borrowFlow() {
        System.out.print("Enter book ID to borrow: ");
        String s = scanner.nextLine().trim();
        try {
            int id = Integer.parseInt(s);
            User u = userService.getCurrentUser();
            if (u == null) {
                System.out.println("No user logged in. Go to login first.");
                return;
            }
            Pair<Boolean, String> res = borrowingService.borrowBook(u.getUsername(), id);
            System.out.println(res.second);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private void returnFlow() {
        System.out.print("Enter loan ID to return: ");
        String loanId = scanner.nextLine().trim();
        Pair<Boolean, String> res = borrowingService.returnBook(loanId);
        System.out.println(res.second);
    }

    private void viewMyLoansFlow() {
        User u = userService.getCurrentUser();
        if (u == null) {
            System.out.println("No user logged in.");
            return;
        }
        List<Loan> loans = userService.getLoanRepository().getLoansByUserId(u.getId());
        if (loans.isEmpty()) {
            System.out.println("You have no loans.");
            return;
        }
        for (Loan l : loans) {
            System.out.println("LoanId: " + l.getId() + " | BookId: " + l.getBookId() + " | Borrowed: " + l.getBorrowDate()
                    + " | Due: " + l.getDueDate() + " | Returned: " + (l.isReturned() ? l.getReturnedDate() : "NO"));
        }
    }

    private void payFineFlow() {
        User u = userService.getCurrentUser();
        if (u == null) { System.out.println("No user logged in."); return; }
        System.out.println("Your fine balance: " + u.getFineBalance() + " NIS");
        System.out.print("Enter amount to pay: ");
        String s = scanner.nextLine().trim();
        try {
            int amt = Integer.parseInt(s);
            Pair<Boolean, String> res = borrowingService.payFine(u.getUsername(), amt);
            System.out.println(res.second);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
        }
    }

    @Override
    public Window buildNextWindow() {
        printHeader();
        printMenu();
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> { BookService.searchBookFlow(bookRepo); return this; }
            case "2" -> { BookService.listAllBooks(bookRepo); return this; }
            case "3" -> { borrowFlow(); return this; }
            case "4" -> { returnFlow(); return this; }
            case "5" -> { viewMyLoansFlow(); return this; }
            case "6" -> { payFineFlow(); return this; }
            case "back" -> { System.out.println("logging out..."); return WindowFactory.create(NextWindow.LOGIN_AND_SIGNUP, userService); }
            case "0" -> { return WindowFactory.create(NextWindow.EXIT, userService); }
            default -> { System.out.println("Invalid choice. Try again."); return this; }
        }
    }
}

