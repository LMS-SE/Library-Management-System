package edu.software.lms;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Window allowing a regular user to search, borrow, return items,
 * view loans, and pay fines.
 *
 * <p>Provides the main interactive menu for non-admin users.</p>
 */
public class UserBookOperations implements Window {

    /** Scanner used for input; replaced in tests. */
    static Scanner scanner = new Scanner(System.in);

    /** Logger for printing UI messages. */
    Logger logger = Logger.getLogger(getClass().getName());

    private final UserService userService;
    private final BookRepository bookRepo;
    private final BorrowingService borrowingService;
    private final MediaBorrowingService mediaBorrowingService;

    /**
     * Replaces the scanner instance for testing.
     *
     * @param testScanner scanner stub for automated input
     */
    static void setScanner(Scanner testScanner) {
        scanner = testScanner;
    }

    /**
     * Creates the user operations window.
     *
     * @param userService shared service holding user/session data
     */
    public UserBookOperations(UserService userService) {
        Observer emailNotifier = new EmailNotifier();
        this.userService = userService;
        this.bookRepo = userService.getBookRepository();
        LoanRepository loanRepo = userService.getLoanRepository();

        this.borrowingService = new BorrowingService(
                userService.getUserRepository(),
                bookRepo,
                loanRepo,
                new SystemTimeProvider(),
                new BookFineStrategy(),
                emailNotifier
        );

        this.mediaBorrowingService = new MediaBorrowingService(
                userService.getUserRepository(),
                bookRepo,
                loanRepo,
                new SystemTimeProvider()
        );
    }

    /** Prints the header line. */
    private void printHeader() {
        logger.info("\n=== Admin Book Operations ===");
    }

    /** Prints the list of available actions. */
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

    /**
     * Borrowing flow: prompts user for item ID and performs borrow logic.
     */
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

    /**
     * Return flow: asks user for loan ID and processes return logic.
     */
    private void returnFlow() {
        logger.info("Enter loan ID to return: ");
        String loanId = scanner.nextLine().trim();
        Pair<Boolean, String> res = mediaBorrowingService.returnMedia(loanId);
        logger.info(res.second);
    }

    /**
     * Displays all loan records belonging to the currently logged-in user.
     */
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
            String type = (l instanceof MediaLoan m)
                    ? m.getMediaType().toString()
                    : "BOOK";

            String msg =
                    "LoanId: " + l.getId() +
                            " | ItemId: " + l.getBookId() +
                            " | Type: " + type +
                            " | Borrowed: " + l.getBorrowDate() +
                            " | Due: " + l.getDueDate() +
                            " | Returned: " + (l.isReturned() ? l.getReturnedDate() : "NO");

            logger.info(msg);
        }
    }

    /**
     * Handles the flow where a user pays part or all of their fine.
     */
    private void payFineFlow() {
        User u = userService.getCurrentUser();
        if (u == null) {
            logger.info("No user logged in.");
            return;
        }

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

    /**
     * Builds the next window based on the user's menu choice.
     *
     * @return next window instance or this window if repeating
     */
    @Override
    public Window buildNextWindow() {
        printHeader();
        printMenu();
        String choice = scanner.nextLine().trim();
        return getWindow(choice);
    }

    /**
     * Decides which window to return based on the chosen string.
     *
     * @param choice menu input
     * @return next window
     */
    private Window getWindow(String choice) {
        switch (choice) {
            case "1" -> {
                BookService.searchBookFlow(bookRepo);
                return this;
            }
            case "2" -> {
                borrowFlow();
                return this;
            }
            case "3" -> {
                returnFlow();
                return this;
            }
            case "4" -> {
                viewMyLoansFlow();
                return this;
            }
            case "5" -> {
                payFineFlow();
                return this;
            }
            case "back" -> {
                logger.info("logging out...");
                return WindowFactory.create(NextWindow.LOGIN_AND_SIGNUP, userService);
            }
            case "0" -> {
                return WindowFactory.create(NextWindow.EXIT, userService);
            }
            default -> {
                logger.info("Invalid choice. Try again.");
                return this;
            }
        }
    }
}
