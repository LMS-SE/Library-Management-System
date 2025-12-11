package edu.software.lms;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryBooksTest {
    InMemoryBooks inMemoryBooks;
    Book testBooK;

    @BeforeEach
    void setUp() {
        inMemoryBooks = new InMemoryBooks();
        testBooK = new Book(1,"OOP","Arthur Morgan","123");
        inMemoryBooks.addBook(testBooK);
    }

    @AfterEach
    void tearDown() {
        inMemoryBooks = null;
        testBooK = null;
    }

    @Test
    void getBookById() {
        Book book = inMemoryBooks.getBookById(1);
        assertNotNull(book);

    }

    @Test
    void getBookByName() {
        Book book = inMemoryBooks.getBookByName("OOP");
        assertNotNull(book);

    }

    @Test
    void getBookByAuthor() {
        Book book = inMemoryBooks.getBookByAuthor("Arthur Morgan");
        assertNotNull(book);
    }

    @Test
    void getBookByISBN() {
        Book book = inMemoryBooks.getBookByISBN("123");
        assertNotNull(book);
    }

    @Test
    void addNullBook() {
        Book book = null;
        boolean adding_result = inMemoryBooks.addBook(book);
        assertFalse(adding_result);

    }
    @Test
    void addIdSimilarBook() {
        Book book2 = new Book(1,"Data Structure","John Marston","314");
        boolean adding_result = inMemoryBooks.addBook(book2);
        assertFalse(adding_result);
    }
    @Test
    void addISBNSimilarBook() {
        Book book2 = new Book(2,"Data Structure","John Marston","123");
        boolean adding_result = inMemoryBooks.addBook(book2);
        assertFalse(adding_result);
    }
    @Test
    void addNewBook() {
        Book book2 = new Book(2,"Data Structure","John Marston","314");
        boolean adding_result = inMemoryBooks.addBook(book2);
        assertTrue(adding_result);
    }
    @Test
    void failToGetBookByName() {
        Book book = inMemoryBooks.getBookByName("Data Structure");
        assertNull(book);

    }

    @Test
    void failToGetBookByAuthor() {
        Book book = inMemoryBooks.getBookByAuthor("John Marston");
        assertNull(book);
    }

    @Test
    void failToGetBookByISBN() {
        Book book = inMemoryBooks.getBookByISBN("314");
        assertNull(book);
    }
    @Test
    void failToGetBookById() {
        Book book = inMemoryBooks.getBookById(2);
        assertNull(book);
    }
    @Test
    void getNextIdWhenEmpty() {
        InMemoryBooks emptyRepo = new InMemoryBooks();
        int nextId = emptyRepo.getNextId();
        assertEquals(1, nextId);
    }

    @Test
    void getNextIdWhenBooksExist() {
        // Already added book with id = 1 in @BeforeEach
        int nextId = inMemoryBooks.getNextId();
        assertEquals(2, nextId);
    }

    @Test
    void getNextIdWithMultipleBooks() {
        inMemoryBooks.addBook(new Book(5, "DS", "John", "555"));
        inMemoryBooks.addBook(new Book(3, "Algo", "Bill", "999"));

        int nextId = inMemoryBooks.getNextId();
        assertEquals(6, nextId);
    }

}