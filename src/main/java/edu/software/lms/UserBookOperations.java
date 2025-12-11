package edu.software.lms;
import java.util.logging.Logger;
import java.util.List;
import java.util.Scanner;

public class UserBookOperations implements Window{
    static Scanner scanner = new Scanner(System.in);
    Logger logger = Logger.getLogger(getClass().getName());
    private final UserService userService;
    private final BookRepository bookRepo;
    private final BorrowingService borrowingService;
    private final MediaBorrowingService mediaBorrowingService;
    // for tests
    static void setScanner(Scanner testScanner) {
        scanner = testScanner;
    }

    public UserBookOperations(UserService userService) {
        Observer emailNotifier = new EmailNotifier();
        this.userService = userService;
        this.bookRepo = userService.getBookRepository();
        LoanRepository loanRepo = userService.getLoanRepository();

        this.borrowingService = new BorrowingService(userService.getUserRepository(), bookRepo, loanRepo, new SystemTimeProvider(), new BookFineStrategy(), emailNotifier);


        this.mediaBorrowingService = new MediaBorrowingService(userService.getUserRepository(), bookRepo, loanRepo, new SystemTimeProvider());
    }



    private void printHeader() { logger.info("\n=== Admin Book Operations ==="); }

    private void printMenu() {
        logger.info("Choose an option:");
        logger.info("1) Search book");
        logger.info("2) Borrow book / CD");
        logger.info("3) Return book / CD");
        logger.info("4) View my loans");
        logger.info("5) Pay fine");
        logger.info("back) Log out");
        logger.info("0) Exit application");
        logger.info("Choice: ");
    }




    private void borrowFlow() {
        logger.info("Enter item ID to borrow: ");
        String s = scanner.nextLine().trim();
        try {
            int id = Integer.parseInt(s);
            User u = userService.getCurrentUser();
            if (u == null) {
                logger.info("No user logged in. Go to login first.");
                return;
            }

            Pair<Boolean, String> res = mediaBorrowingService.borrowMedia(u.getUsername(), id);
            logger.info(res.second);
        } catch (NumberFormatException _) {
            logger.info("Invalid ID.");
        }
    }

    private void returnFlow() {
        logger.info("Enter loan ID to return: ");
        String loanId = scanner.nextLine().trim();

        Pair<Boolean, String> res = mediaBorrowingService.returnMedia(loanId);
        logger.info(res.second);
    }

    private void viewMyLoansFlow() {
        User u = userService.getCurrentUser();
        if (u == null) {
            logger.info("No user logged in.");
            return;
        }
        List<Loan> loans = userService.getLoanRepository().getLoansByUserId(u.getId());
        if (loans.isEmpty()) {
            logger.info("You have no loans.");
            return;
        }
        for (Loan l : loans) {
            String type = (l instanceof MediaLoan mediaLoan) ? mediaLoan.getMediaType().toString() : "BOOK";
            String loanId = l.getId();
            String itemId = String.valueOf(l.getBookId());
            String borrowed = String.valueOf(l.getBorrowDate());
            String due = String.valueOf(l.getDueDate());
            String returned = l.isReturned() ? String.valueOf(l.getReturnedDate()) : "NO";
            String msg="LoanId: " + loanId + " | ItemId: " + itemId + " | Type: " + type + " | Borrowed: " + borrowed
                       + " | Due: " + due + " | Returned: " + returned;
            logger.info(msg);
        }
    }

    private void payFineFlow() {
        User u = userService.getCurrentUser();
        if (u == null) { logger.info("No user logged in."); return; }
        logger.info("Your fine balance: " + u.getFineBalance() + " NIS");
        logger.info("Enter amount to pay: ");
        String s = scanner.nextLine().trim();
        try {
            int amt = Integer.parseInt(s);

            Pair<Boolean, String> res = borrowingService.payFine(u.getUsername(), amt);
            logger.info(res.second);
        } catch (NumberFormatException _) {
            logger.info("Invalid amount.");
        }
    }

    @Override
    public Window buildNextWindow() {
        printHeader();

        printMenu();
        String choice = scanner.nextLine().trim();
        return getWindow(choice);
    }

    private Window getWindow(String choice) {
        switch (choice) {
            case "1" -> { BookService.searchBookFlow(bookRepo);
                return this;
            }
            case "2" -> { borrowFlow();
                return this;
            }
            case "3" -> { returnFlow();
                return this;
            }
            case "4" -> { viewMyLoansFlow();
                return this;
            }
            case "5" -> { payFineFlow();
                return this;
            }
            case "back" -> { logger.info("logging out...");
                return WindowFactory.create(NextWindow.LOGIN_AND_SIGNUP, userService);
            }
            case "0" -> {
                return WindowFactory.create(NextWindow.EXIT, userService);
            }
            default -> { logger.info("Invalid choice. Try again.");
                return this;
            }
        }
    }


}
