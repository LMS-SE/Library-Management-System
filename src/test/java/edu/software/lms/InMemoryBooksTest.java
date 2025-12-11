package edu.software.lms;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryBooksTest {
    InMemoryBooks inMemoryBooks;
    Book testBook;

    @BeforeEach
    void setUp() {
        inMemoryBooks = new InMemoryBooks();
        testBook = new Book(1,"OOP","Arthur Morgan","123");
        inMemoryBooks.addBook(testBook);
    }

    @AfterEach
    void tearDown() {
        inMemoryBooks = null;
        testBook = null;
    }

    @Test
    void getBookById() {
        Book foundBook = inMemoryBooks.getBookById(1);
        assertNotNull(foundBook);
    }

    @Test
    void getBookByName() {
        Book foundBook = inMemoryBooks.getBookByName("OOP");
        assertNotNull(foundBook);
    }

    @Test
    void getBookByAuthor() {
        Book foundBook = inMemoryBooks.getBookByAuthor("Arthur Morgan");
        assertNotNull(foundBook);
    }

    @Test
    void getBookByISBN() {
        Book foundBook = inMemoryBooks.getBookByISBN("123");
        assertNotNull(foundBook);
    }

    @Test
    void addNullBook() {
        assertFalse(inMemoryBooks.addBook(null));
    }

    @Test
    void addBookWithSameId() {
        Book bookToAdd = new Book(1,"Data Structure","John Marston","314");
        assertFalse(inMemoryBooks.addBook(bookToAdd));
    }

    @Test
    void addBookWithSameISBN() {
        Book bookToAdd = new Book(2,"Data Structure","John Marston","123");
        assertFalse(inMemoryBooks.addBook(bookToAdd));
    }

    @Test
    void addNewBook() {
        Book bookToAdd = new Book(2,"Data Structure","John Marston","314");
        assertTrue(inMemoryBooks.addBook(bookToAdd));
    }

    @Test
    void failToGetBookByName() {
        Book foundBook = inMemoryBooks.getBookByName("Data Structure");
        assertNull(foundBook);
    }

    @Test
    void failToGetBookByAuthor() {
        Book foundBook = inMemoryBooks.getBookByAuthor("John Marston");
        assertNull(foundBook);
    }

    @Test
    void failToGetBookByISBN() {
        Book foundBook = inMemoryBooks.getBookByISBN("314");
        assertNull(foundBook);
    }

    @Test
    void failToGetBookById() {
        Book foundBook = inMemoryBooks.getBookById(2);
        assertNull(foundBook);
    }

    @Test
    void getNextIdWhenEmpty() {
        InMemoryBooks emptyRepo = new InMemoryBooks();
        assertEquals(1, emptyRepo.getNextId());
    }

    @Test
    void getNextIdWhenBooksExist() {
        assertEquals(2, inMemoryBooks.getNextId());
    }

    @Test
    void getNextIdWithMultipleBooks() {
        inMemoryBooks.addBook(new Book(5, "DS", "John", "555"));
        inMemoryBooks.addBook(new Book(3, "Algo", "Bill", "999"));
        assertEquals(6, inMemoryBooks.getNextId());
    }
}
