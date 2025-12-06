package edu.software.lms;

import java.util.List;
import java.util.Scanner;

public class UserBookOperations implements Window{
    private static final Scanner scanner = new Scanner(System.in);

    private final UserService userService;
    private final BookRepository bookRepo;
    private final BorrowingService borrowingService;
    private final MediaBorrowingService mediaBorrowingService;

    public UserBookOperations(UserService userService) {
        Observer emailNotifier = new EmailNotifier();
        this.userService = userService;
        this.bookRepo = GettingRepoService.resolveRepoFromUserServiceOrFallback(userService);
        LoanRepository loanRepo = GettingRepoService.resolveLoanRepoFromUserServiceOrFallback(userService);

        this.borrowingService = new BorrowingService(userService.getUserRepository(), bookRepo, loanRepo, new SystemTimeProvider(), new BookFineStrategy(), emailNotifier);


        this.mediaBorrowingService = new MediaBorrowingService(userService.getUserRepository(), bookRepo, loanRepo, new SystemTimeProvider());
    }



    private void printHeader() { System.out.println("\n=== Admin Book Operations ==="); }

    private void printMenu() {
        System.out.println("Choose an option:");
        System.out.println("1) Search book");
        System.out.println("2) Borrow book / CD");
        System.out.println("3) Return book / CD");
        System.out.println("4) View my loans");
        System.out.println("5) Pay fine");
        System.out.println("back) Log out");
        System.out.println("0) Exit application");
        System.out.print("Choice: ");
    }




    private void borrowFlow() {
        System.out.print("Enter item ID to borrow: ");
        String s = scanner.nextLine().trim();
        try {
            int id = Integer.parseInt(s);
            User u = userService.getCurrentUser();
            if (u == null) {
                System.out.println("No user logged in. Go to login first.");
                return;
            }

            Pair<Boolean, String> res = mediaBorrowingService.borrowMedia(u.getUsername(), id);
            System.out.println(res.second);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private void returnFlow() {
        System.out.print("Enter loan ID to return: ");
        String loanId = scanner.nextLine().trim();

        Pair<Boolean, String> res = mediaBorrowingService.returnMedia(loanId);
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
            String type = (l instanceof MediaLoan) ? ((MediaLoan) l).getMediaType().toString() : "BOOK";
            System.out.println("LoanId: " + l.getId() + " | ItemId: " + l.getBookId() + " | Type: " + type + " | Borrowed: " + l.getBorrowDate()
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
            case "2" -> { borrowFlow(); return this; }
            case "3" -> { returnFlow(); return this; }
            case "4" -> { viewMyLoansFlow(); return this; }
            case "5" -> { payFineFlow(); return this; }
            case "back" -> { System.out.println("logging out..."); return WindowFactory.create(NextWindow.LOGIN_AND_SIGNUP, userService); }
            case "0" -> { return WindowFactory.create(NextWindow.EXIT, userService); }
            default -> { System.out.println("Invalid choice. Try again."); return this; }
        }
    }



}
