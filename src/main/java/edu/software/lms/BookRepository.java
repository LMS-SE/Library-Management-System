package edu.software.lms;

import java.util.List;

public interface BookRepository {
    Book getBookById(int id);
    Book getBookByName(String name);
    Book getBookByAuthor(String author);
    Book getBookByISBN(String isbn);
    boolean addBook(Book book);

    List<Book> getAllBooks();
    int getNextId();
}
