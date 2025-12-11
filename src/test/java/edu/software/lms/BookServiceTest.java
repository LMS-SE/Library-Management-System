package edu.software.lms;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class BookServiceTest {

    private void setScannerInput(String input) {
        ByteArrayInputStream in =
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        Scanner testScanner = new Scanner(in);
        BookService.setScanner(testScanner);
    }

    @Test
    void searchBookFlow_option1_callsGetBookByName() {
        BookRepository bookRepo = mock(BookRepository.class);
        Book dummy = new Book(1, "Some Title", "Author", "ISBN");
        when(bookRepo.getBookByName("Some Title")).thenReturn(dummy);

        // First line: choice "1", second line: title
        setScannerInput("1\nSome Title\n");

        BookService.searchBookFlow(bookRepo);

        verify(bookRepo).getBookByName("Some Title");
        verifyNoMoreInteractions(bookRepo);
    }

    @Test
    void searchBookFlow_option2_callsGetBookByAuthor() {
        BookRepository bookRepo = mock(BookRepository.class);
        Book dummy = new Book(1, "Title", "Some Author", "ISBN");
        when(bookRepo.getBookByAuthor("Some Author")).thenReturn(dummy);

        setScannerInput("2\nSome Author\n");

        BookService.searchBookFlow(bookRepo);

        verify(bookRepo).getBookByAuthor("Some Author");
        verifyNoMoreInteractions(bookRepo);
    }

    @Test
    void searchBookFlow_option3_callsGetBookByISBN() {
        BookRepository bookRepo = mock(BookRepository.class);
        Book dummy = new Book(1, "Title", "Author", "12345");
        when(bookRepo.getBookByISBN("12345")).thenReturn(dummy);

        setScannerInput("3\n12345\n");

        BookService.searchBookFlow(bookRepo);

        verify(bookRepo).getBookByISBN("12345");
        verifyNoMoreInteractions(bookRepo);
    }

    @Test
    void searchBookFlow_invalidChoice_logsWarningAndDoesNotCallRepo() {
        BookRepository bookRepo = mock(BookRepository.class);

        setScannerInput("99\n");

        BookService.searchBookFlow(bookRepo);

        verifyNoInteractions(bookRepo);
        // logging is not asserted here, but the default branch is executed
    }

    @Test
    void printBook_null_printsNoResultFound() {
        assertDoesNotThrow(() -> BookService.printBook(null));
    }

    @Test
    void printBook_withNormalBook_logsInfo() {
        Book book = new Book(1, "Title", "Author", "ISBN");
        assertDoesNotThrow(() -> BookService.printBook(book));
    }

    @Test
    void printBook_withCD_logsTypeCd() {
        Book cd = new CD(2, "Album", "Artist", "CD-123");
        assertDoesNotThrow(() -> BookService.printBook(cd));
    }

}
