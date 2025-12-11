package edu.software.lms;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class AdminBookOperationsWindow implements Window {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Logger logger = Logger.getLogger(AdminBookOperationsWindow.class.getName());

    private final UserService userService;
    private final BookRepository bookRepo;
    private final BorrowingService borrowingService;

    public AdminBookOperationsWindow(UserService userService) {
        Observer emailNotifier = new EmailNotifier();
        this.userService = userService;
        this.bookRepo = GettingRepoService.resolveRepoFromUserServiceOrFallback(userService);
        LoanRepository loanRepo = GettingRepoService.resolveLoanRepoFromUserServiceOrFallback(userService);
        this.borrowingService = new BorrowingService(userService.getUserRepository(), bookRepo, loanRepo, new SystemTimeProvider(), new BookFineStrategy(), emailNotifier);
    }

    private void printHeader() { logger.info("\n=== Admin Book Operations ==="); }

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

    private void addBookFlow() {
        System.out.print("Is this a CD? (y/N): "); // سؤال للمستخدم يفضل يظل
        String isCd = scanner.nextLine().trim();
        boolean cd = isCd.equalsIgnoreCase("y");

        System.out.print("Enter title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter author: ");
        String author = scanner.nextLine().trim();

        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine().trim();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            logger.warning("All fields are required. Aborting add.");
            return;
        }

        int nextId = computeNextId();
        Book book;
        if (cd) book = new CD(nextId, title, author, isbn);
        else book = new Book(nextId, title, author, isbn);

        boolean added = bookRepo.addBook(book);
        if (added) logger.info("Item added successfully.");
        else logger.warning("Failed to add (maybe duplicate id or ISBN).");
    }

    private int computeNextId() {
        int nextId = 1;
        if (bookRepo instanceof InMemoryBooks) {
            List<Book> list = ((InMemoryBooks) bookRepo).books; // package-private
            if (list != null && !list.isEmpty()) {
                int max = 0;
                for (Book b : list) if (b != null) max = Math.max(max, b.getId());
                nextId = max + 1;
            }
        }
        return nextId;
    }

    private void listAllBooks() {
        if (bookRepo instanceof InMemoryBooks) {
            List<Book> all = ((InMemoryBooks) bookRepo).books;
            if (all.isEmpty()) {
                logger.info("No items available.");
            } else {
                for (Book b : all) BookService.printBook(b);
            }
        } else {
            logger.warning("List-all not supported for this repository.");
        }
    }

    @Override
    public Window buildNextWindow() {
        printHeader();
        printMenu();
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> { addBookFlow(); return this; }
            case "2" -> { BookService.searchBookFlow(bookRepo); return this; }
            case "3" -> { listAllBooks(); return this; }
            case "4" -> { borrowingService.remindOverdue(); return this; }
            case "5" -> { unregisterUserFlow(); return this; }
            case "back" -> { logger.info("Logging out..."); return WindowFactory.create(NextWindow.LOGIN_AND_SIGNUP, userService); }
            case "0" -> { return WindowFactory.create(NextWindow.EXIT, userService); }
            default -> { logger.warning("Invalid choice. Try again."); return this; }
        }
    }

    private void unregisterUserFlow() {
        User admin = userService.getCurrentUser();
        if (admin == null) {
            logger.warning("No user logged in.");
            return;
        }
        System.out.print("Enter username to unregister: ");
        String target = scanner.nextLine().trim();
        Pair<Boolean, String> result = userService.unregisterUser(target);
        logger.info(result.second);
    }
}
