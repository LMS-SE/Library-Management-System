package edu.software.lms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserBookOperationsTest {

    private UserService userService;
    private BookRepository bookRepo;
    private LoanRepository loanRepo;
    private UserRepository userRepo;
    private BorrowingService borrowingService;
    private MediaBorrowingService mediaBorrowingService;

    private UserBookOperations window;

    @BeforeEach
    void setUp() throws Exception {
        userService = mock(UserService.class);
        bookRepo = mock(BookRepository.class);
        loanRepo = mock(LoanRepository.class);
        userRepo = mock(UserRepository.class);

        when(userService.getBookRepository()).thenReturn(bookRepo);
        when(userService.getLoanRepository()).thenReturn(loanRepo);
        when(userService.getUserRepository()).thenReturn(userRepo);

        window = new UserBookOperations(userService);

        // replace real services created in constructor with mocks
        borrowingService = mock(BorrowingService.class);
        mediaBorrowingService = mock(MediaBorrowingService.class);

        var bsField = UserBookOperations.class.getDeclaredField("borrowingService");
        bsField.setAccessible(true);
        bsField.set(window, borrowingService);

        var mbsField = UserBookOperations.class.getDeclaredField("mediaBorrowingService");
        mbsField.setAccessible(true);
        mbsField.set(window, mediaBorrowingService);
    }

    private void setScannerInput(String allInputLines) {
        ByteArrayInputStream in =
                new ByteArrayInputStream(allInputLines.getBytes(StandardCharsets.UTF_8));
        Scanner testScanner = new Scanner(in);
        UserBookOperations.setScanner(testScanner);
    }

    @Test
    void buildNextWindow_choice1_callsSearchBookFlow_andReturnsSameWindow() {
        // User menu choice
        setScannerInput("1\n");

        // searchBookFlow itself will ask for its own input using BookService.scanner.
        // We reuse the pattern from before:
        BookService.setScanner(new Scanner(
                new ByteArrayInputStream("1\nSome Title\n".getBytes(StandardCharsets.UTF_8))
        ));
        when(bookRepo.getBookByName("Some Title"))
                .thenReturn(new Book(1, "Some Title", "Author", "ISBN"));

        Window result = window.buildNextWindow();

        assertSame(window, result);
        verify(bookRepo).getBookByName("Some Title");
    }

    @Test
    void buildNextWindow_choice2_borrowFlow_callsMediaBorrowingServiceBorrowMedia() {
        // choice "2", then item id "42"
        setScannerInput("2\n42\n");

        User u = new User("alice", "pwd", "mail", false);
        u.setId("u-alice");
        when(userService.getCurrentUser()).thenReturn(u);
        when(mediaBorrowingService.borrowMedia("alice", 42))
                .thenReturn(new Pair<>(true, "ok"));

        Window result = window.buildNextWindow();

        assertSame(window, result);
        verify(mediaBorrowingService).borrowMedia("alice", 42);
    }

    @Test
    void buildNextWindow_choice2_invalidId_doesNotCallBorrowMedia() {
        // choice "2", then invalid number
        setScannerInput("2\nnot-a-number\n");

        User u = new User("alice", "pwd", "mail", false);
        when(userService.getCurrentUser()).thenReturn(u);

        Window result = window.buildNextWindow();

        assertSame(window, result);
        verifyNoInteractions(mediaBorrowingService);
    }

    @Test
    void buildNextWindow_choice3_returnFlow_callsReturnMedia() {
        setScannerInput("3\nloan-123\n");

        when(mediaBorrowingService.returnMedia("loan-123"))
                .thenReturn(new Pair<>(true, "returned"));

        Window result = window.buildNextWindow();

        assertSame(window, result);
        verify(mediaBorrowingService).returnMedia("loan-123");
    }

    @Test
    void buildNextWindow_choice4_viewMyLoans_noUser_loggedIn() {
        setScannerInput("4\n");

        when(userService.getCurrentUser()).thenReturn(null);

        Window result = window.buildNextWindow();

        assertSame(window, result);

    }


    @Test
    void buildNextWindow_choice4_viewMyLoans_withLoans_fetchesLoans() {
        setScannerInput("4\n");

        User u = new User("bob", "pwd", "mail", false);
        u.setId("u-bob");
        when(userService.getCurrentUser()).thenReturn(u);
        when(userService.getLoanRepository()).thenReturn(loanRepo);


        Loan l1 = new Loan(
                u.getId(),
                1,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 29)
        );

        MediaLoan l2 = mock(MediaLoan.class);
        when(l2.getId()).thenReturn("L2");
        when(l2.getBookId()).thenReturn(2);
        when(l2.getBorrowDate()).thenReturn(LocalDate.of(2025, 1, 10));
        when(l2.getDueDate()).thenReturn(LocalDate.of(2025, 2, 7));
        when(l2.isReturned()).thenReturn(true);
        when(l2.getReturnedDate()).thenReturn(LocalDate.of(2025, 2, 1));
        when(l2.getMediaType()).thenReturn(MediaType.CD);

        when(loanRepo.getLoansByUserId("u-bob"))
                .thenReturn(List.of(l1, l2));

        Window result = window.buildNextWindow();

        assertSame(window, result);
        verify(loanRepo).getLoansByUserId("u-bob");
    }
    @Test
    void buildNextWindow_choice5_payFine_callsBorrowingService() {
        setScannerInput("5\n100\n");

        User u = new User("charlie", "pwd", "mail", false);
        u.setId("u-charlie");
        when(userService.getCurrentUser()).thenReturn(u);
        when(borrowingService.payFine("charlie", 100))
                .thenReturn(new Pair<>(true, "paid"));

        Window result = window.buildNextWindow();

        assertSame(window, result);
        verify(borrowingService).payFine("charlie", 100);
    }

    @Test
    void buildNextWindow_choice5_invalidAmount_doesNotCallBorrowingService() {
        setScannerInput("5\nnot-a-number\n");

        User u = new User("charlie", "pwd", "mail", false);
        when(userService.getCurrentUser()).thenReturn(u);

        Window result = window.buildNextWindow();

        assertSame(window, result);
        verifyNoInteractions(borrowingService);
    }

    @Test
    void buildNextWindow_back_returnsDifferentWindow() {
        setScannerInput("back\n");

        Window result = window.buildNextWindow();

        assertNotSame(window, result);
    }

    @Test
    void buildNextWindow_zero_returnsExitWindow() {
        setScannerInput("0\n");

        Window result = window.buildNextWindow();

        assertNotSame(window, result);
    }

    @Test
    void buildNextWindow_invalidChoice_returnsSameWindow() {
        setScannerInput("wtf\n");

        Window result = window.buildNextWindow();

        assertSame(window, result);
    }
}
