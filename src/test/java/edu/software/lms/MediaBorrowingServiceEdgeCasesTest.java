package edu.software.lms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MediaBorrowingServiceEdgeCasesTest {

    @ParameterizedTest
    @MethodSource("invalidUserProvider")
    void borrowMedia_invalidUsers_fail(String username, String expectedMessage) {
        InMemoryUserRepository ur = new InMemoryUserRepository();
        InMemoryBooks br = new InMemoryBooks();
        InMemoryLoanRepository lr = new InMemoryLoanRepository();
        MockTimeProvider tp = new MockTimeProvider(LocalDate.of(2025, 1, 1));

        MediaBorrowingService mbs = new MediaBorrowingService(ur, br, lr, tp);

        Pair<Boolean, String> res = mbs.borrowMedia(username, 1);

        assertFalse(res.first);
        assertEquals(expectedMessage, res.second);
    }

    private static Stream<Arguments> invalidUserProvider() {
        return Stream.of(
                Arguments.of(null, "Invalid user"),
                Arguments.of("", "Invalid user"),
                Arguments.of("missing", "User not found")
        );
    }
    @Test
    void borrowMedia_failsWhenUserHasOutstandingFine() {
        InMemoryUserRepository ur = new InMemoryUserRepository();
        InMemoryBooks br = new InMemoryBooks();
        InMemoryLoanRepository lr = new InMemoryLoanRepository();
        MockTimeProvider tp = new MockTimeProvider(LocalDate.of(2025, 1, 1));

        User u = new User("john", "pwd", "john@mail.com", false);
        u.setId("u-john");
        u.addFine(50);
        ur.addUser(u);

        MediaBorrowingService mbs = new MediaBorrowingService(ur, br, lr, tp);

        Pair<Boolean, String> res = mbs.borrowMedia("john", 1);

        assertFalse(res.first);
        assertTrue(res.second.contains("outstanding fine"));
    }

    @Test
    void borrowMedia_failsWhenUserHasOverdueLoan() {
        InMemoryUserRepository ur = new InMemoryUserRepository();
        InMemoryBooks br = new InMemoryBooks();
        InMemoryLoanRepository lr = new InMemoryLoanRepository();
        MockTimeProvider tp = new MockTimeProvider(LocalDate.of(2025, 1, 10));

        User u = new User("alex", "pwd", "alex@mail.com", false);
        u.setId("u-alex");
        ur.addUser(u);

        Book b = new Book(1, "B", "A", "ISBN1");
        br.addBook(b);

        Loan overdueLoan = new Loan(
                u.getId(),
                1,
                LocalDate.of(2024, 12, 1),
                LocalDate.of(2024, 12, 20)
        );
        lr.addLoan(overdueLoan);

        MediaBorrowingService mbs = new MediaBorrowingService(ur, br, lr, tp);

        Pair<Boolean, String> res = mbs.borrowMedia("alex", 1);

        assertFalse(res.first);
        assertTrue(res.second.contains("overdue"));
    }

    @Test
    void borrowMedia_failsWhenMediaNotFound() {
        InMemoryUserRepository ur = new InMemoryUserRepository();
        InMemoryBooks br = new InMemoryBooks();
        InMemoryLoanRepository lr = new InMemoryLoanRepository();
        MockTimeProvider tp = new MockTimeProvider(LocalDate.of(2025, 1, 1));

        User u = new User("sam", "pwd", "sam@mail.com", false);
        u.setId("u-sam");
        ur.addUser(u);

        MediaBorrowingService mbs = new MediaBorrowingService(ur, br, lr, tp);

        Pair<Boolean, String> res = mbs.borrowMedia("sam", 999);

        assertFalse(res.first);
        assertEquals("Media not found", res.second);
    }

    @Test
    void borrowMedia_failsWhenMediaAlreadyBorrowed() {
        InMemoryUserRepository ur = new InMemoryUserRepository();
        InMemoryBooks br = new InMemoryBooks();
        InMemoryLoanRepository lr = new InMemoryLoanRepository();
        MockTimeProvider tp = new MockTimeProvider(LocalDate.of(2025, 1, 1));

        User u = new User("max", "pwd", "max@mail.com", false);
        u.setId("u-max");
        ur.addUser(u);

        Book book = new Book(5, "Book", "Author", "ISBN-5");
        book.setBorrowed(true);
        br.addBook(book);

        MediaBorrowingService mbs = new MediaBorrowingService(ur, br, lr, tp);

        Pair<Boolean, String> res = mbs.borrowMedia("max", 5);

        assertFalse(res.first);
        assertEquals("Media is already borrowed", res.second);
    }

    @Test
    void borrowMedia_failsWhenLoanCannotBeRecorded() {
        InMemoryUserRepository ur = new InMemoryUserRepository();
        InMemoryBooks br = new InMemoryBooks();
        MockTimeProvider tp = new MockTimeProvider(LocalDate.of(2025, 1, 1));

        User u = new User("kate", "pwd", "kate@mail.com", false);
        u.setId("u-kate");
        ur.addUser(u);

        Book book = new Book(1, "Book", "Author", "ISBN-1");
        br.addBook(book);

        MediaBorrowingService mbs = getMediaBorrowingService(ur, br, tp);

        Pair<Boolean, String> res = mbs.borrowMedia("kate", 1);

        assertFalse(res.first);
        assertEquals("Failed to record loan", res.second);
    }

    private static MediaBorrowingService getMediaBorrowingService(InMemoryUserRepository ur, InMemoryBooks br, MockTimeProvider tp) {
        LoanRepository failingLoanRepo = new LoanRepository() {
            @Override
            public boolean addLoan(Loan loan) {
                return false;
            }

            @Override
            public Loan getLoanById(String id) {
                return null;
            }

            @Override
            public java.util.List<Loan> getLoansByUserId(String userId) {
                return java.util.List.of();
            }

            @Override
            public java.util.List<Loan> getAllLoans() {
                return java.util.List.of();
            }

            @Override
            public boolean updateLoan(Loan loan) {
                return false;
            }
        };

        return new MediaBorrowingService(ur, br, failingLoanRepo, tp);
    }

    @Test
    void returnMedia_failsWhenLoanNotFound() {
        InMemoryUserRepository ur = new InMemoryUserRepository();
        InMemoryBooks br = new InMemoryBooks();
        InMemoryLoanRepository lr = new InMemoryLoanRepository();
        MockTimeProvider tp = new MockTimeProvider(LocalDate.of(2025, 1, 1));

        MediaBorrowingService mbs = new MediaBorrowingService(ur, br, lr, tp);

        Pair<Boolean, String> res = mbs.returnMedia("no-such-loan");

        assertFalse(res.first);
        assertEquals("Loan not found", res.second);
    }

    @Test
    void returnMedia_failsWhenLoanAlreadyReturned() {
        InMemoryUserRepository ur = new InMemoryUserRepository();
        InMemoryBooks br = new InMemoryBooks();
        InMemoryLoanRepository lr = new InMemoryLoanRepository();
        MockTimeProvider tp = new MockTimeProvider(LocalDate.of(2025, 1, 1));

        Loan loan = new Loan(
                "u-1",
                1,
                LocalDate.of(2024, 12, 1),
                LocalDate.of(2024, 12, 10)
        );
        loan.setReturnedDate(LocalDate.of(2024, 12, 11));
        lr.addLoan(loan);

        MediaBorrowingService mbs = new MediaBorrowingService(ur, br, lr, tp);

        Pair<Boolean, String> res = mbs.returnMedia(loan.getId());

        assertFalse(res.first);
        assertEquals("Already returned", res.second);
    }

    @Test
    void returnMedia_usesFallbackDetectionForCDLoan() {
        InMemoryUserRepository ur = new InMemoryUserRepository();
        InMemoryBooks br = new InMemoryBooks();
        InMemoryLoanRepository lr = new InMemoryLoanRepository();
        MockTimeProvider tp = new MockTimeProvider(LocalDate.of(2025, 1, 10));

        User user = new User("jay", "pwd", "jay@mail.com", false);
        user.setId("u-jay");
        ur.addUser(user);

        CD cd = new CD(5, "Album", "Singer", "CD-5");
        br.addBook(cd);

        Loan loan = new Loan(
                user.getId(),
                5,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 3)
        );
        lr.addLoan(loan);

        MediaBorrowingService mbs = new MediaBorrowingService(ur, br, lr, tp);

        Pair<Boolean, String> res = mbs.returnMedia(loan.getId());

        assertTrue(res.first);
        Loan updated = lr.getLoanById(loan.getId());
        assertTrue(updated.getFineApplied() > 0);
        User stored = ur.getUserById(user.getId());
        assertTrue(stored.getFineBalance() > 0);
    }

    @Test
    void returnMedia_bookReturnedOnTime_hasNoFine() {
        InMemoryUserRepository ur = new InMemoryUserRepository();
        InMemoryBooks br = new InMemoryBooks();
        InMemoryLoanRepository lr = new InMemoryLoanRepository();
        MockTimeProvider tp = new MockTimeProvider(LocalDate.of(2025, 1, 10));

        User user = new User("dan", "pwd", "dan@mail.com", false);
        user.setId("u-dan");
        ur.addUser(user);

        Book book = new Book(1, "Book", "Author", "ISBN-1");
        br.addBook(book);

        MediaLoan loan = new MediaLoan(
                user.getId(),
                1,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 10),
                MediaType.BOOK
        );
        lr.addLoan(loan);

        MediaBorrowingService mbs = new MediaBorrowingService(ur, br, lr, tp);

        Pair<Boolean, String> res = mbs.returnMedia(loan.getId());

        assertTrue(res.first);
        Loan updated = lr.getLoanById(loan.getId());
        assertEquals(0, updated.getFineApplied());
        assertTrue(updated.isFinePaid());
        User stored = ur.getUserById(user.getId());
        assertEquals(0, stored.getFineBalance());
    }

    @Test
    void returnMedia_handlesMissingUserGracefully() {
        InMemoryUserRepository ur = new InMemoryUserRepository();
        InMemoryBooks br = new InMemoryBooks();
        InMemoryLoanRepository lr = new InMemoryLoanRepository();
        MockTimeProvider tp = new MockTimeProvider(LocalDate.of(2025, 1, 10));

        CD cd = new CD(3, "CD", "Artist", "CD-3");
        br.addBook(cd);

        MediaLoan loan = new MediaLoan(
                "ghost-user",
                3,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 3),
                MediaType.CD
        );
        lr.addLoan(loan);

        MediaBorrowingService mbs = new MediaBorrowingService(ur, br, lr, tp);

        Pair<Boolean, String> res = mbs.returnMedia(loan.getId());

        assertTrue(res.first);
    }

    @Test
    void returnMedia_handlesMissingBookGracefully() {
        InMemoryUserRepository ur = new InMemoryUserRepository();
        InMemoryBooks br = new InMemoryBooks();
        InMemoryLoanRepository lr = new InMemoryLoanRepository();
        MockTimeProvider tp = new MockTimeProvider(LocalDate.of(2025, 1, 10));

        User user = new User("x", "pwd", "x@mail.com", false);
        user.setId("u-x");
        ur.addUser(user);

        MediaLoan loan = new MediaLoan(
                user.getId(),
                999,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 3),
                MediaType.BOOK
        );
        lr.addLoan(loan);

        MediaBorrowingService mbs = new MediaBorrowingService(ur, br, lr, tp);

        Pair<Boolean, String> res = mbs.returnMedia(loan.getId());

        assertTrue(res.first);
    }
}
