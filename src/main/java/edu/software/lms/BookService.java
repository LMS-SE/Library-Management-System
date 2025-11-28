package edu.software.lms;

import java.util.List;
import java.util.Scanner;

public class BookService {
    public static void searchBookFlow(BookRepository bookRepo) {
        Scanner scanner = new Scanner(System.in);
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
            default -> System.out.println("invalid.");
        }
    }
    private static void printBook(Book b) {
        if (b == null) {
            System.out.println("No result.");
            return;
        }
        System.out.println("ID: " + b.getId() + " | Title: " + b.getName() + " | Author: " + b.getAuthor() + " | ISBN: " + b.getIsbn() + " | Borrowed: " + b.isBorrowed());
    }

    public static void listAllBooks(BookRepository bookRepo) {
        if (bookRepo instanceof InMemoryBooks) {
            List<Book> all = ((InMemoryBooks) bookRepo).books;
            if (all.isEmpty()) {
                System.out.println("No books available.");
            } else {
                for (Book b : all) printBook(b);
            }
        } else {
            System.out.println("List-all not supported for this repository.");
        }
    }
}
