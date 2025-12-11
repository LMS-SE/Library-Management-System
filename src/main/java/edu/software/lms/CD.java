package edu.software.lms;

/**
 * Represents a CD media item in the library system.
 * A CD is treated as a specialized type of {@link Book} so it can be stored
 * in the same {@link BookRepository} without special handling.
 *
 * <p>This class does not introduce new fields but overrides {@link #toString()}
 * for CD-specific formatting.</p>
 */
public class CD extends Book {
    /**
     * Constructs a new CD with the given values.
     *
     * @param id     unique identifier for the CD
     * @param name   title of the CD
     * @param author creator or artist of the CD
     * @param isbn   CD's identifying code (stored using book-style ISBN field)
     */
    public CD(int id, String name, String author, String isbn) {
        super(id, name, author, isbn);
    }
    /**
     * Returns a string representation of the CD, including:
     * <ul>
     *     <li>ID</li>
     *     <li>Title</li>
     *     <li>Author</li>
     *     <li>ISBN</li>
     *     <li>Borrowed status</li>
     * </ul>
     *
     * @return formatted CD information
     */
    @Override
    public String toString() {
        return "CD{id=" + getId() + ", title='" + getName() + "', author='" + getAuthor() + "', isbn='" + getIsbn() + "', borrowed=" + isBorrowed() + "}";
    }
}
