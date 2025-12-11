package edu.software.lms;

import java.util.Scanner;
import java.util.logging.Logger;

public class BookService {

    private static final Logger logger = Logger.getLogger(BookService.class.getName());
    private static final Scanner scanner = new Scanner(System.in);

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

    public static void printBook(Book b) {
        if (b == null) {
            logger.info("No result found.");
            return;
        }

        String type = (b instanceof CD) ? "CD" : "Book";

        logger.info(
                "ID: " + b.getId()
                        + " | Type: " + type
                        + " | Title: " + b.getName()
                        + " | Author: " + b.getAuthor()
                        + " | ISBN: " + b.getIsbn()
                        + " | Borrowed: " + b.isBorrowed()
        );
    }
}
