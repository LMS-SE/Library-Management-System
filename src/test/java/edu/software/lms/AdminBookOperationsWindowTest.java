package edu.software.lms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminBookOperationsWindowTest {

    private UserService userService;
    private BookRepository bookRepo;
    private BorrowingService borrowingService;

    private AdminBookOperationsWindow window;

    @BeforeEach
    void setUp() throws Exception {
        // mocks for dependencies exposed by UserService
        userService = mock(UserService.class);
        bookRepo = mock(BookRepository.class);
        LoanRepository loanRepo = mock(LoanRepository.class);
        UserRepository userRepo = mock(UserRepository.class);

        when(userService.getBookRepository()).thenReturn(bookRepo);
        when(userService.getLoanRepository()).thenReturn(loanRepo);
        when(userService.getUserRepository()).thenReturn(userRepo);

        // create window under test (this will create a real BorrowingService)
        window = new AdminBookOperationsWindow(userService);

        // replace real BorrowingService with a mock, so we can verify calls
        borrowingService = mock(BorrowingService.class);
        Field bsField = AdminBookOperationsWindow.class.getDeclaredField("borrowingService");
        bsField.setAccessible(true);
        bsField.set(window, borrowingService);
    }

    /**
     * Helper to replace the static Scanner with one that reads from our test input.
     */
    private void setScannerInput(String allInputLines) throws Exception {
        ByteArrayInputStream in =
                new ByteArrayInputStream(allInputLines.getBytes(StandardCharsets.UTF_8));
        Scanner testScanner = new Scanner(in);

        Field scannerField = AdminBookOperationsWindow.class.getDeclaredField("scanner");
        scannerField.setAccessible(true);
        scannerField.set(null, testScanner); // static field → null for instance
    }

    @Test
    void buildNextWindow_addBookFlow_addsBookAndReturnsSameWindow() throws Exception {
        // first line: menu choice "1"
        // next lines: isCd, title, author, isbn
        setScannerInput("1\ny\nTitle\nAuthor\nISBN\n");

        when(bookRepo.getNextId()).thenReturn(42);
        when(bookRepo.addBook(any(Book.class))).thenReturn(true);

        Window result = window.buildNextWindow();

        assertSame(window, result);

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepo).addBook(bookCaptor.capture());
        Book added = bookCaptor.getValue();

        assertEquals(42, added.getId());
        assertEquals("Title", added.getName());
        assertEquals("Author", added.getAuthor());
        assertEquals("ISBN", added.getIsbn());
        assertInstanceOf(CD.class, added); // because we answered "y"
    }


    @Test
    void buildNextWindow_listAllBooks_callsRepositoryAndStaysOnSameWindow() throws Exception {
        setScannerInput("3\n");

        Book b1 = new Book(1, "A", "X", "111");
        Book b2 = new Book(2, "B", "Y", "222");
        when(bookRepo.getAllBooks()).thenReturn(List.of(b1, b2));

        Window result = window.buildNextWindow();

        assertSame(window, result);
        verify(bookRepo).getAllBooks();
        // BookService.printBook is invoked for each, giving coverage to listAllBooks()
    }

    @Test
    void buildNextWindow_sendOverdueNotifications_callsBorrowingService() throws Exception {
        setScannerInput("4\n");

        Window result = window.buildNextWindow();

        assertSame(window, result);
        verify(borrowingService).remindOverdue();
    }

    @Test
    void buildNextWindow_unregisterUserFlow_callsUserServiceAndStaysOnSameWindow() throws Exception {
        // choice "5" then username to unregister
        setScannerInput("5\nbob\n");

        User admin = new User("admin", "pwd", "admin@mail", true);
        when(userService.getCurrentUser()).thenReturn(admin);
        when(userService.unregisterUser("bob"))
                .thenReturn(new Pair<>(true, "User unregistered"));

        Window result = window.buildNextWindow();

        assertSame(window, result);
        verify(userService).unregisterUser("bob");
    }

    @Test
    void buildNextWindow_back_returnsLoginWindow() throws Exception {
        setScannerInput("back\n");

        Window result = window.buildNextWindow();

        // we don’t know the concrete type, but it should not be this window
        assertNotSame(window, result);
    }

    @Test
    void buildNextWindow_zero_returnsExitWindow() throws Exception {
        setScannerInput("0\n");

        Window result = window.buildNextWindow();

        assertNotSame(window, result);
    }

    @Test
    void buildNextWindow_invalidChoice_returnsSameWindow() throws Exception {
        setScannerInput("invalid_choice\n");

        Window result = window.buildNextWindow();

        assertSame(window, result);
        // logger.warning is called ("Invalid choice. Try again.") – we don’t assert logs here,
        // but executing the branch gives coverage.
    }
}
