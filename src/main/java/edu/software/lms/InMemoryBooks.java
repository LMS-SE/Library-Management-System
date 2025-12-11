package edu.software.lms;

import java.util.ArrayList;
import java.util.List;

/**
 * An in-memory implementation of the {@link BookRepository} interface.
 * Stores {@link Book} and {@link CD} objects inside a List.
 *
 * <p>This implementation is primarily used for testing or simple runtime
 * scenarios where persistent storage is not required.</p>
 */
public class InMemoryBooks implements BookRepository {

    /** Internal list storing all books and CDs. */
    private final List<Book> books;

    /**
     * Creates a new repository using a pre-populated list of books.
     *
     * @param books initial collection of books to store
     */
    public InMemoryBooks(List<Book> books) {
        this.books = books;
    }

    /**
     * Creates an empty in-memory repository.
     */
    public InMemoryBooks() {
        books = new ArrayList<>();
    }

    /**
     * Retrieves a book by its ID.
     *
     * @param id the book's unique identifier
     * @return the book matching the ID, or {@code null} if not found
     */
    @Override
    public Book getBookById(int id) {
        return books.stream()
                .filter(book -> book.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves a book by its title.
     *
     * @param name the title to search for
     * @return match or {@code null} if not found
     */
    @Override
    public Book getBookByName(String name) {
        return books.stream()
                .filter(book -> book.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves a book by its author.
     *
     * @param author the author's name
     * @return match or {@code null} if none is found
     */
    @Override
    public Book getBookByAuthor(String author) {
        return books.stream()
                .filter(book -> book.getAuthor().equals(author))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves a book by its ISBN.
     *
     * @param isbn ISBN to search for
     * @return matching book or {@code null}
     */
    @Override
    public Book getBookByISBN(String isbn) {
        return books.stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst()
                .orElse(null);
    }

    /**
     * Attempts to add a new book to the repository.
     * <p>Fails if:</p>
     * <ul>
     *     <li>The book is null</li>
     *     <li>A book exists with the same ID</li>
     *     <li>A book exists with the same ISBN</li>
     * </ul>
     *
     * @param book the book to insert
     * @return true if added, false otherwise
     */
    @Override
    public boolean addBook(Book book) {
        if (book == null) return false;

        boolean exists = books.stream()
                .anyMatch(b -> b.getId() == book.getId()
                        || b.getIsbn().equals(book.getIsbn()));

        if (exists) return false;

        books.add(book);
        return true;
    }

    /**
     * Retrieves all books stored in the repository.
     *
     * @return a fresh list containing all stored books
     */
    @Override
    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    /**
     * Computes the next available ID based on the highest existing ID.
     *
     * @return max ID + 1, or 1 if the repository is empty
     */
    @Override
    public int getNextId() {
        return books.stream()
                .mapToInt(Book::getId)
                .max()
                .orElse(0) + 1;
    }
}
