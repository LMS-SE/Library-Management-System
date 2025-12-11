package edu.software.lms;

public class Book {
    private int id;
    private String name;
    private String author;
    private String isbn;
    private boolean borrowed;
    public Book(int id, String name, String author, String isbn) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.isbn = isbn;
        this.borrowed = false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;
        return getId() == book.getId() &&
                getName().equals(book.getName()) &&
                getAuthor().equals(book.getAuthor()) &&
                getIsbn().equals(book.getIsbn());
    }
    @Override
    public int hashCode() {
        return java.util.Objects.hash(getId(), getName(), getAuthor(), getIsbn());
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public boolean isBorrowed() { return borrowed; }
    public void setBorrowed(boolean borrowed) { this.borrowed = borrowed; }
}
