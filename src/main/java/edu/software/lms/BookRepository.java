package edu.software.lms;

import java.util.List;
/**
 * Repository interface for managing books and CDs within the system.
 * Provides lookup, insertion, and retrieval operations.
 *
 * <p>Implementations may store books in memory, a database, or any
 * persistent storage mechanism.</p>
 */
public interface BookRepository {
    /**
     * Retrieves a book by its unique identifier.
     *
     * @param id the ID of the book to find
     * @return the matching {@link Book}, or {@code null} if not found
     */
    Book getBookById(int id);
    /**
     * Searches for a book by its title.
     *
     * @param name the title of the book
     * @return the matching {@link Book}, or {@code null} if none exists
     */
    Book getBookByName(String name);

    /**
     * Searches for a book by its author.
     *
     * @param author author's name
     * @return a matching {@link Book}, or {@code null} if none exists
     */
    Book getBookByAuthor(String author);

    /**
     * Searches for a book by its ISBN.
     *
     * @param isbn ISBN code
     * @return matching {@link Book}, or {@code null} if none exists
     */
    Book getBookByISBN(String isbn);

    /**
     * Attempts to add a new book into the repository.
     *
     * @param book the book to add
     * @return true if successful, false if duplicate or invalid
     */
    boolean addBook(Book book);
    /**
     * Retrieves all stored books.
     *
     * @return an immutable list of all books in the repository
     */
    List<Book> getAllBooks();

    /**
     * Computes the next available numeric ID for creating a new book.
     * Typically {@code max(existing IDs) + 1}.
     *
     * @return next available ID
     */
    int getNextId();
}
