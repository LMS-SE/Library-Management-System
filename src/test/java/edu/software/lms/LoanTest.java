package edu.software.lms;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
public class LoanTest {
    @Test
    public void testOverdue() {
        LocalDate borrow = LocalDate.of(2024,1,1);
        LocalDate due = LocalDate.of(2024,1,10);
        Loan loan = new Loan("U1", 1, borrow, due);


        Assertions.assertTrue(loan.isOverdue(LocalDate.of(2024,1,11)));
        Assertions.assertFalse(loan.isOverdue(LocalDate.of(2024,1,5)));
    }


    @Test
    public void testReturnedOverdue() {
        LocalDate borrow = LocalDate.of(2024,1,1);
        LocalDate due = LocalDate.of(2024,1,10);
        Loan loan = new Loan("U1", 1, borrow, due);


        loan.setReturnedDate(LocalDate.of(2024,1,12));
        Assertions.assertTrue(loan.isOverdue(LocalDate.now()));
    }


    @Test
    public void testOverdueDays() {
        LocalDate borrow = LocalDate.of(2024,1,1);
        LocalDate due = LocalDate.of(2024,1,10);
        Loan loan = new Loan("U1", 1, borrow, due);
        Assertions.assertEquals(2, loan.overdueDays(LocalDate.of(2024,1,12)));
    }
}
