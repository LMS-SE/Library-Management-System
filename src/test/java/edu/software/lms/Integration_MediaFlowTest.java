package edu.software.lms;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class Integration_MediaFlowTest {

    @Test
    void fullFlow_registerAddCdBorrowReturnAndPayFine() {
        InMemoryUserRepository ur = new InMemoryUserRepository();
        InMemoryBooks br = new InMemoryBooks();
        InMemoryLoanRepository lr = new InMemoryLoanRepository();
        MockTimeProvider tp = new MockTimeProvider(LocalDate.of(2025, 3, 1));

        UserService us = new UserService();
        us.setUserRepository(ur);
        us.setBookRepository(br);
        us.setLoanRepository(lr);

        SignUpResult signUp = us.validateCreateAccountCredentials("tom","Aa1!passw");
        assertEquals(SignUpResult.USER_CREATED_SUCCESSFULLY, signUp);
        User u = us.getCurrentUser();
        assertNotNull(u);

        // add CD
        CD cd = new CD(50, "MegaHits", "Various", "CD-50");
        br.addBook(cd);

        // borrow via media service
        MediaBorrowingService mbs = new MediaBorrowingService(ur, br, lr, tp);
        Pair<Boolean,String> borrow = mbs.borrowMedia(u.getUsername(), 50);
        assertTrue(borrow.first);
        Loan loan = lr.getAllLoans().get(0);

        // advance time to make it overdue by 2 days
        tp.plusDays(9); // borrowed +9 => overdue 2
        Pair<Boolean,String> ret = mbs.returnMedia(loan.getId());
        assertTrue(ret.first);

        Loan updated = lr.getLoanById(loan.getId());
        assertEquals(2 * 20, updated.getFineApplied());

        User stored = ur.getUserByUsername(u.getUsername());
        assertEquals(40, stored.getFineBalance());

        // pay fine using existing BorrowingService.payFine (keeps compatibility)
        BorrowingService bs = new BorrowingService(ur, br, lr, tp, new BookFineStrategy());
        Pair<Boolean,String> pay = bs.payFine(u.getUsername(), 40);
        assertTrue(pay.first);
        User after = ur.getUserByUsername(u.getUsername());
        assertEquals(0, after.getFineBalance());
    }
}
