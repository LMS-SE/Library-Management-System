package edu.software.lms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {


    @Test
    void testEquals() {
        Book b1 = new Book(1, "Clean Code", "Robert Martin", "123");
        Book b2 = new Book(1, "Clean Code", "Robert Martin", "123");

        assertEquals(b1, b2);

        Book b3 = new Book(2, "Clean Code", "Robert Martin", "123");
        assertNotEquals(b1, b3);

        Book b4 = new Book(1, "Clean Code", "Robert Martin", "XYZ");
        assertNotEquals(b1, b4);

        assertNotEquals(null, b1);

        assertNotEquals(null, b1);

    }

    @Test
    void setId() {
        Book book = new Book(1, "Book", "Author", "111");
        book.setId(5);
        assertEquals(5, book.getId());
    }

    @Test
    void setAuthor() {
        Book book = new Book(1, "Book", "Old Author", "111");
        book.setAuthor("New Author");
        assertEquals("New Author", book.getAuthor());
    }

    @Test
    void setIsbn() {
        Book book = new Book(1, "Book", "Author", "111");
        book.setIsbn("999");
        assertEquals("999", book.getIsbn());
    }
}