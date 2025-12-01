package edu.software.lms;

import java.util.List;
import java.util.Scanner;

public class AdminBookOperationsWindow implements Window {
    private static final Scanner scanner = new Scanner(System.in);

    private final UserService userService;
    private final BookRepository bookRepo;
    private final BorrowingService borrowingService;
    private final MediaBorrowingService mediaBorrowingService;
    private Observer emailNotifier;
    public AdminBookOperationsWindow(UserService userService) {
        emailNotifier=new EmailNotifier();
        this.userService = userService;
        this.bookRepo = resolveRepoFromUserServiceOrFallback(userService);
        LoanRepository loanRepo = resolveLoanRepoFromUserServiceOrFallback(userService);

        this.borrowingService = new BorrowingService(userService.getUserRepository(), bookRepo, loanRepo, new SystemTimeProvider(), new BookFineStrategy(),emailNotifier);


        this.mediaBorrowingService = new MediaBorrowingService(userService.getUserRepository(), bookRepo, loanRepo, new SystemTimeProvider());
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

    private void printHeader() { System.out.println("\n=== Admin Book Operations ==="); }

    private void printMenu() {
        System.out.println("Choose an option:");
        System.out.println("1) Add book / CD");
        System.out.println("2) Search book");
        System.out.println("3) List all books");
        System.out.println("4) Borrow book / CD");
        System.out.println("5) Return book / CD");
        System.out.println("6) View my loans");
        System.out.println("7) Pay fine");
        System.out.println("8) Send Overdue Notifications to all users");
        System.out.println("back) Log out");
        System.out.println("0) Exit application");
        System.out.print("Choice: ");
    }

    private void addBookFlow() {
        System.out.print("Is this a CD? (y/N): ");
        String isCd = scanner.nextLine().trim();
        boolean cd = isCd.equalsIgnoreCase("y");

        System.out.print("Enter title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Enter author: ");
        String author = scanner.nextLine().trim();
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine().trim();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            System.out.println("All fields are required. Aborting add.");
            return;
        }

        int nextId = computeNextId();
        Book book;
        if (cd) book = new CD(nextId, title, author, isbn);
        else book = new Book(nextId, title, author, isbn);

        boolean added = bookRepo.addBook(book);
        if (added) System.out.println("Item added successfully.");
        else System.out.println("Failed to add (maybe duplicate id or ISBN).");
    }

    private int computeNextId() {
        int nextId = 1;
        if (bookRepo instanceof InMemoryBooks) {
            List<Book> list = ((InMemoryBooks) bookRepo).books; // package-private in your code
            if (list != null && !list.isEmpty()) {
                int max = 0;
                for (Book b : list) if (b != null) max = Math.max(max, b.getId());
                nextId = max + 1;
            }
        }
        return nextId;
    }

    private void searchBookFlow() {
        System.out.println("Search by: 1-Title  2-Author  3-ISBN");
        System.out.print("Choice: ");
        String opt = scanner.nextLine().trim();
        switch (opt) {
            case "1" -> {
                System.out.print("Enter title: ");
                Book b = bookRepo.getBookByName(scanner.nextLine().trim());
                printBook(b);
            }
            case "2" -> {
                System.out.print("Enter author: ");
                Book b = bookRepo.getBookByAuthor(scanner.nextLine().trim());
                printBook(b);
            }
            case "3" -> {
                System.out.print("Enter ISBN: ");
                Book b = bookRepo.getBookByISBN(scanner.nextLine().trim());
                printBook(b);
            }
            default -> System.out.println("اختيار غير صالح للبحث.");
        }
    }

    private void printBook(Book b) {
        if (b == null) {
            System.out.println("لا توجد نتيجة.");
            return;
        }
        String type = (b instanceof CD) ? "CD" : "Book";
        System.out.println("ID: " + b.getId() + " | Type: " + type + " | Title: " + b.getName() + " | Author: " + b.getAuthor() + " | ISBN: " + b.getIsbn() + " | Borrowed: " + b.isBorrowed());
    }

    private void listAllBooks() {
        if (bookRepo instanceof InMemoryBooks) {
            List<Book> all = ((InMemoryBooks) bookRepo).books;
            if (all.isEmpty()) {
                System.out.println("No items available.");
            } else {
                for (Book b : all) printBook(b);
            }
        } else {
            System.out.println("List-all not supported for this repository.");
        }
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
        printOverdueAndFine();
        printMenu();
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> { addBookFlow(); return this; }
            case "2" -> { searchBookFlow(); return this; }
            case "3" -> { listAllBooks(); return this; }
            case "4" -> { borrowFlow(); return this; }
            case "5" -> { returnFlow(); return this; }
            case "6" -> { viewMyLoansFlow(); return this; }
            case "7" -> { payFineFlow(); return this; }
            case "8" -> { borrowingService.remindOverdue(); return this; }

            case "back" -> { System.out.println("logging out..."); return WindowFactory.create(NextWindow.LOGIN_AND_SIGNUP, userService); }
            case "0" -> { return WindowFactory.create(NextWindow.EXIT, userService); }
            default -> { System.out.println("Invalid choice. Try again."); return this; }
        }
    }

    private void printOverdueAndFine() {

    }
}
