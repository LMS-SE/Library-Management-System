package edu.software.lms;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class MediaBorrowingServiceTest {

    @Test
    void borrowCd_andBook_haveCorrectDueDates() {
        InMemoryUserRepository ur = new InMemoryUserRepository();
        InMemoryBooks br = new InMemoryBooks();
        InMemoryLoanRepository lr = new InMemoryLoanRepository();
        MockTimeProvider tp = new MockTimeProvider(LocalDate.of(2025, 1, 1));

        User u = new User("alice","pwd","user@gmail.com",false);
        u.setId("u-alice");
        ur.addUser(u);

        CD cd = new CD(1,"Hits","Various","CD-1");
        Book book = new Book(2,"Novel","Author","ISBN-2");
        br.addBook(cd);
        br.addBook(book);

        MediaBorrowingService mbs = new MediaBorrowingService(ur, br, lr, tp);

        Pair<Boolean,String> r1 = mbs.borrowMedia("alice", 1);
        assertTrue(r1.first);
        Loan loanCd = lr.getAllLoans().stream().filter(l -> l.getBookId() == 1).findFirst().orElse(null);
        assertNotNull(loanCd);
        assertEquals(tp.today().plusDays(7), loanCd.getDueDate());

        Pair<Boolean,String> r2 = mbs.borrowMedia("alice", 2);
        assertTrue(r2.first);
        Loan loanBook = lr.getAllLoans().stream().filter(l -> l.getBookId() == 2).findFirst().orElse(null);
        assertNotNull(loanBook);
        assertEquals(tp.today().plusDays(28), loanBook.getDueDate());
    }

    @Test
    void returnLateCd_applies20PerDay_andUpdatesUserFine() {
        InMemoryUserRepository ur = new InMemoryUserRepository();
        InMemoryBooks br = new InMemoryBooks();
        InMemoryLoanRepository lr = new InMemoryLoanRepository();
        MockTimeProvider tp = new MockTimeProvider(LocalDate.of(2025, 1, 1));

        User u = new User("bob","pwd","user@gmail.com",false);
        u.setId("u-bob");
        ur.addUser(u);
        CD cd = new CD(10,"Album","Singer","CD-10");
        br.addBook(cd);

        MediaBorrowingService mbs = new MediaBorrowingService(ur, br, lr, tp);

        Pair<Boolean,String> borrowRes = mbs.borrowMedia("bob", 10);
        assertTrue(borrowRes.first);
        Loan loan = lr.getAllLoans().getFirst();
        assertEquals(tp.today().plusDays(7), loan.getDueDate());

        tp.plusDays(12);
        Pair<Boolean,String> ret = mbs.returnMedia(loan.getId());
        assertTrue(ret.first);

        Loan updated = lr.getLoanById(loan.getId());
        int overdue = updated.overdueDays(tp.today());
        assertEquals(5, overdue);
        assertEquals(5 * 20, updated.getFineApplied());

        User stored = ur.getUserByUsername("bob");
        assertEquals(5 * 20, stored.getFineBalance());
    }
}
