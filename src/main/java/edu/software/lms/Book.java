package edu.software.lms;
/**
 * Represents a basic book entity with ID, title, author, ISBN, and borrowed status.
 * This class is also used as the parent class for {@link CD}.
 *
 * <p>Books are uniquely identified by ID and ISBN. The class provides
 * getters, setters, and equality checks to support repository operations.</p>
 */
public class Book {
    private int id;
    private final String name;
    private String author;
    private String isbn;
    private boolean borrowed;

    /**
     * Constructs a new Book with the given attributes.
     *
     * @param id     unique identifier for the book
     * @param name   title of the book
     * @param author book's author
     * @param isbn   international standard book number
     */
    public Book(int id, String name, String author, String isbn) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.isbn = isbn;
        this.borrowed = false;
    }
    /**
     * Compares this book with another object for equality.
     * Two books are considered equal if their ID, title, author,
     * and ISBN fields match.
     *
     * @param o the object to compare with
     * @return true if both objects represent the same book, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;
        return getId() == book.getId() &&
                getName().equals(book.getName()) &&
                getAuthor().equals(book.getAuthor()) &&
                getIsbn().equals(book.getIsbn());
    }

    /**
     * Generates a hash code for the book based on ID, title, author, and ISBN.
     *
     * @return hash code for this book
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(getId(), getName(), getAuthor(), getIsbn());
    }


    /**
     * Retrieves the book's ID.
     *
     * @return book ID
     */
    public int getId() { return id; }
    /**
     * Updates the book's ID.
     *
     * @param id new identifier value
     */
    public void setId(int id) { this.id = id; }
    /**
     * Retrieves the book's title.
     *
     * @return title of the book
     */
    public String getName() { return name; }
    /**
     * Retrieves the book's author.
     *
     * @return book author
     */
    public String getAuthor() { return author; }
    /**
     * Sets a new author for the book.
     *
     * @param author new author name
     */
    public void setAuthor(String author) { this.author = author; }
    /**
     * Retrieves the ISBN of the book.
     *
     * @return book ISBN
     */
    public String getIsbn() { return isbn; }

    /**
     * Updates the ISBN value of the book.
     *
     * @param isbn new ISBN string
     */
    public void setIsbn(String isbn) { this.isbn = isbn; }
    /**
     * Checks whether the book is currently borrowed.
     *
     * @return true if borrowed, false otherwise
     */
    public boolean isBorrowed() { return borrowed; }
    /**
     * Updates the borrowed status of the book.
     *
     * @param borrowed true if the book is now borrowed, false otherwise
     */
    public void setBorrowed(boolean borrowed) { this.borrowed = borrowed; }
}
