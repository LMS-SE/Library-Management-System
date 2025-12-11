package edu.software.lms;

import java.util.ArrayList;
import java.util.List;

public class InMemoryBooks implements BookRepository {

    private final List<Book> books;

    public InMemoryBooks(List<Book> books) {
        this.books = books;
    }

    public InMemoryBooks() {
        books = new ArrayList<>();
    }

    @Override
    public Book getBookById(int id) {
        return books.stream().filter(book -> book.getId() == id).findFirst().orElse(null);
    }

    @Override
    public Book getBookByName(String name) {
        return books.stream().filter(book -> book.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public Book getBookByAuthor(String author) {
        return books.stream().filter(book -> book.getAuthor().equals(author)).findFirst().orElse(null);
    }

    @Override
    public Book getBookByISBN(String isbn) {
        return books.stream().filter(book -> book.getIsbn().equals(isbn)).findFirst().orElse(null);
    }

    @Override
    public boolean addBook(Book book) {
        if (book == null) return false;
        if (books.stream().anyMatch(b -> b.getId() == book.getId() || b.getIsbn().equals(book.getIsbn()))) return false;
        books.add(book);
        return true;
    }

    @Override
    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    @Override
    public int getNextId() {
        return books.stream().mapToInt(Book::getId).max().orElse(0) + 1;
    }
}
