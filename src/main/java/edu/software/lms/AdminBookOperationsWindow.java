package edu.software.lms;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
/**
 * Represents the admin window for performing book-related operations
 * such as adding new media items, listing all books, searching books,
 * sending overdue notifications, and unregistering users.
 *
 * <p>This class is used only when the logged-in user is an administrator.
 * It interacts with {@link UserService}, {@link BookRepository},
 * {@link BorrowingService}, and various other components.</p>
 */
public class AdminBookOperationsWindow implements Window {
    private static Scanner scanner = new Scanner(System.in);
    private static final Logger logger = Logger.getLogger(AdminBookOperationsWindow.class.getName());
    private final UserService userService;
    private final BookRepository bookRepo;
    private final BorrowingService borrowingService;

    /**
     * Sets the scanner instance (used in tests).
     *
     * @param testScanner scanner to inject for testing
     */
    static void setScanner(Scanner testScanner) {
        scanner = testScanner;
    }
    /**
     * Constructs a new admin operations window.
     *
     * @param userService the user service providing repositories and authentication context
     */
    public AdminBookOperationsWindow(UserService userService) {
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
    }
    /**
     * Prints the header banner for admin operations.
     */
    private void printHeader() {
        logger.info("\n=== Admin Book Operations ===");
    }
    /**
     * Prints the menu options available to the administrator.
     */
    private void printMenu() {
        logger.info("Choose an option:");
        logger.info("1) Add book / CD");
        logger.info("2) Search book");
        logger.info("3) List all books");
        logger.info("4) Send Overdue Notifications to all users");
        logger.info("5) Unregister user");
        logger.info("back) Log out");
        logger.info("0) Exit application");
        logger.info("Choice: ");
    }
    /**
     * Handles the flow of adding either a Book or CD into the repository.
     * Validates fields and inserts the item into the system.
     */
    private void addBookFlow() {
        logger.info("Is this a CD? (y/N): ");
        String isCd = scanner.nextLine().trim();
        boolean cd = isCd.equalsIgnoreCase("y");

        logger.info("Enter title: ");
        String title = scanner.nextLine().trim();

        logger.info("Enter author: ");
        String author = scanner.nextLine().trim();

        logger.info("Enter ISBN: ");
        String isbn = scanner.nextLine().trim();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            logger.warning("All fields are required. Aborting add.");
            return;
        }

        int nextId = bookRepo.getNextId();
        Book book = cd ? new CD(nextId, title, author, isbn) : new Book(nextId, title, author, isbn);
        boolean added = bookRepo.addBook(book);
        if (added) logger.info("Item added successfully.");
        else logger.warning("Failed to add (maybe duplicate id or ISBN).");
    }
    /**
     * Prints all books (and CDs) stored in the repository.
     * Displays an informative message if no entries exist.
     */
    private void listAllBooks() {
        List<Book> all = bookRepo.getAllBooks();
        if (all.isEmpty()) logger.info("No items available.");
        else all.forEach(BookService::printBook);
    }
    /**
     * Processes the admin's choice and returns the next window to display.
     *
     * @return the next window instance or {@code null} if exiting
     */
    @Override
    public Window buildNextWindow() {
        printHeader();
        printMenu();
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> {
                addBookFlow();
                return this;
            }
            case "2" -> {
                BookService.searchBookFlow(bookRepo);
                return this;
            }
            case "3" -> {
                listAllBooks();
                return this;
            }
            case "4" -> {
                borrowingService.remindOverdue();
                return this;
            }
            case "5" -> {
                unregisterUserFlow();
                return this;
            }
            case "back" -> {
                logger.info("Logging out...");
                return WindowFactory.create(NextWindow.LOGIN_AND_SIGNUP, userService);
            }
            case "0" -> {
                return WindowFactory.create(NextWindow.EXIT, userService);
            }
            default -> {
                logger.warning("Invalid choice. Try again.");
                return this;
            }
        }
    }
    /**
     * Handles the process of unregistering a user, only available to admins.
     * Validates permissions, checks user existence, active loans, and unpaid fines.
     */
    private void unregisterUserFlow() {
        User admin = userService.getCurrentUser();
        if (admin == null) {
            logger.warning("No user logged in.");
            return;
        }
        logger.info("Enter username to unregister: ");
        String target = scanner.nextLine().trim();
        Pair<Boolean, String> result = userService.unregisterUser(target);
        logger.info(result.second);
    }
}
