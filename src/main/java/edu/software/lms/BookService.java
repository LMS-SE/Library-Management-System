package edu.software.lms;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility service class providing functionality for searching and printing
 * book or CD information. This class is not instantiable and exposes
 * only static helper methods used across the system.
 *
 * <p>BookService handles interactive user input for searching by title,
 * author, or ISBN and prints formatted details for books.</p>
 */
public final class BookService {

    /** Logger instance for outputting menu and search messages. */
    private static final Logger logger = Logger.getLogger(BookService.class.getName());

    /** Scanner used for reading user input (replaceable for tests). */
    private static Scanner scanner = new Scanner(System.in);

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private BookService() { }

    /**
     * Used in tests to replace the input scanner.
     *
     * @param testScanner scanner instance used for injecting test input
     */
    static void setScanner(Scanner testScanner) {
        scanner = testScanner;
    }

    /**
     * Interactive search flow allowing the user to search for a book by:
     * <ul>
     *     <li>Title</li>
     *     <li>Author</li>
     *     <li>ISBN</li>
     * </ul>
     *
     * <p>The method prints search options, reads user input,
     * performs a lookup using the provided {@link BookRepository},
     * and prints search results.</p>
     *
     * @param bookRepo repository used for searching books and CDs
     */
    public static void searchBookFlow(BookRepository bookRepo) {

        logger.info("Search by: 1-Title  2-Author  3-ISBN");
        logger.info("Choice:");

        String opt = scanner.nextLine().trim();

        switch (opt) {
            case "1" -> {
                logger.info("Enter title:");
                Book b = bookRepo.getBookByName(scanner.nextLine().trim());
                printBook(b);
            }
            case "2" -> {
                logger.info("Enter author:");
                Book b = bookRepo.getBookByAuthor(scanner.nextLine().trim());
                printBook(b);
            }
            case "3" -> {
                logger.info("Enter ISBN:");
                Book b = bookRepo.getBookByISBN(scanner.nextLine().trim());
                printBook(b);
            }
            default -> logger.warning("Invalid choice");
        }
    }

    /**
     * Prints the details of a given {@link Book} or {@link CD} in a formatted style.
     * If the book is null, a "No result found" message is displayed.
     *
     * <p>The output includes:</p>
     * <ul>
     *     <li>ID</li>
     *     <li>Type (Book or CD)</li>
     *     <li>Title</li>
     *     <li>Author</li>
     *     <li>ISBN</li>
     *     <li>Borrowed status</li>
     * </ul>
     *
     * @param b the book to print, or null if no book was found
     */
    public static void printBook(Book b) {

        if (b == null) {
            logger.info("No result found.");
            return;
        }

        String type = (b instanceof CD) ? "CD" : "Book";

        logger.log(
                Level.INFO,
                "ID: {0} | Type: {1} | Title: {2} | Author: {3} | ISBN: {4} | Borrowed: {5}",
                new Object[]{
                        b.getId(),
                        type,
                        b.getName(),
                        b.getAuthor(),
                        b.getIsbn(),
                        b.isBorrowed()
                }
        );
    }
}
