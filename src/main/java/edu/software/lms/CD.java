package edu.software.lms;

/**
 * CD extends Book so it can be stored in existing BookRepository without changes.
 */
public class CD extends Book {
    public CD(int id, String name, String author, String isbn) {
        super(id, name, author, isbn);
    }

    @Override
    public String toString() {
        return "CD{id=" + getId() + ", title='" + getName() + "', author='" + getAuthor() + "', isbn='" + getIsbn() + "', borrowed=" + isBorrowed() + "}";
    }
}
