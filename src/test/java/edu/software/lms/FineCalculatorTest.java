package edu.software.lms;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
public class FineCalculatorTest {


    @Test
    public void testFineCalculation() {
        FineStrategy mock = days -> days * 2;
        TimeProvider mockTime = () -> LocalDate.of(2024,1,20);


        FineCalculator calc = new FineCalculator(mock, mockTime);
        Loan loan = new Loan("U1", 1, LocalDate.of(2024,1,1), LocalDate.of(2024,1,10));


        Assertions.assertEquals(20, calc.computeFineForLoan(loan));
    }
}
