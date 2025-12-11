package edu.software.lms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookFineStrategyTest {

    @Test
    void calculateFine_zeroWhenNotOverdue() {
        BookFineStrategy s = new BookFineStrategy();

        assertEquals(0, s.calculateFine(0),  "Zero days overdue → 0 fine");
        assertEquals(0, s.calculateFine(-3), "Negative days (no overdue) → 0 fine");
    }

    @Test
    void calculateFine_usesPerDayRate() {
        BookFineStrategy sDefault = new BookFineStrategy();
        BookFineStrategy sCustom  = new BookFineStrategy(5);

        assertEquals(30, sDefault.calculateFine(3));
        assertEquals(15, sCustom.calculateFine(3));
    }
}
