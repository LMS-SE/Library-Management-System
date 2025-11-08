package edu.software.lms;

import java.util.List;
import java.util.Scanner;


public class AdminBookOperationsWindow implements Window {
    private static final Scanner scanner = new Scanner(System.in);

    private final UserService userService;
    private final BookRepository bookRepo;

    public AdminBookOperationsWindow(UserService userService) {
        this.userService = userService;
        this.bookRepo = resolveRepoFromUserServiceOrFallback(userService);
    }

    private BookRepository resolveRepoFromUserServiceOrFallback(UserService userService) {
        if (userService == null) return new InMemoryBooks();
        try {
            // try to call a getter if it exists: userService.getBookRepository()
            var m = userService.getClass().getMethod("getBookRepository");
            Object repoObj = m.invoke(userService);
            if (repoObj instanceof BookRepository) return (BookRepository) repoObj;
        } catch (NoSuchMethodException ignored) {
            // method doesn't exist -> fallback
        } catch (Exception e) {
            // reflection invocation problem -> fallback
        }
        return new InMemoryBooks();
    }

    private void printHeader() {
        System.out.println("\n=== Admin Book Operations ===");
    }

    private void printMenu() {
        System.out.println("Choose an option:");
        System.out.println("1) Add book");
        System.out.println("2) Search book");
        System.out.println("3) List all books");
        System.out.println("back) Log out");
        System.out.println("0) Exit application");
        System.out.print("Choice: ");
    }

    private void addBookFlow() {
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
        Book book = new Book(nextId, title, author, isbn);
        boolean added = bookRepo.addBook(book);
        if (added) System.out.println("✅ Book added successfully.");
        else System.out.println("❌ Failed to add book (maybe duplicate id or ISBN).");
    }

    private int computeNextId() {
        int nextId = 1;
        if (bookRepo instanceof InMemoryBooks) {
            List<Book> list = ((InMemoryBooks) bookRepo).books; // package-private access
            if (list != null && !list.isEmpty()) {
                int max = 0;
                for (Book b : list) if (b != null) max = Math.max(max, b.getId());
                nextId = max + 1;
            }
        } else {
            // best-effort: try to find an id by checking getBookById 1..N (not ideal)
            // keep nextId = 1 (repo.addBook should reject duplicates based on repo rules)
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
        System.out.println("ID: " + b.getId() + " | Title: " + b.getName() + " | Author: " + b.getAuthor() + " | ISBN: " + b.getIsbn());
    }

    private void listAllBooks() {
        if (bookRepo instanceof InMemoryBooks) {
            List<Book> all = ((InMemoryBooks) bookRepo).books; // package-private
            if (all.isEmpty()) {
                System.out.println("No books available.");
            } else {
                for (Book b : all) printBook(b);
            }
        } else {
            // best-effort: try to search by known fields? fallback message
            System.out.println("List-all not supported for this repository.");
        }
    }

    @Override
    public Window buildNextWindow() {
        printHeader();
        printMenu();
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> {
                addBookFlow();
                return this; // stay in the same window
            }
            case "2" -> {
                searchBookFlow();
                return this;
            }
            case "3" -> {
                listAllBooks();
                return this;
            }
            case "back" -> {
                System.out.println("logging out...");
                return WindowFactory.create(NextWindow.LOGIN_AND_SIGNUP, userService);
            }
            case "0" -> {
                return WindowFactory.create(NextWindow.EXIT, userService);
            }
            default -> {
                System.out.println("Invalid choice. Try again.");
                return this;
            }
        }
    }
}
