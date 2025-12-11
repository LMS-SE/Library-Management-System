package edu.software.lms;

import java.util.Scanner;

public class BookService {
    Scanner  scanner=new Scanner(System.in);
    //!here
    public static void searchBookFlow(BookRepository bookRepo) {
        Scanner  scanner=new Scanner(System.in);
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
            default -> System.out.println("invalid choice");
        }
    }
    //!here
    public static void printBook(Book b) {
        if (b == null) {
            System.out.println("no result");
            return;
        }
        String type = (b instanceof CD) ? "CD" : "Book";
        System.out.println("ID: " + b.getId() + " | Type: " + type + " | Title: " + b.getName() + " | Author: " + b.getAuthor() + " | ISBN: " + b.getIsbn() + " | Borrowed: " + b.isBorrowed());
    }

}