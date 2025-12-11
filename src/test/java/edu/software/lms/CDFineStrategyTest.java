package edu.software.lms;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CDFineStrategyTest {

    @Test
    void cdFineCalculation_zeroWhenNotOverdue() {
        CDFineStrategy s = new CDFineStrategy();
        assertEquals(0, s.calculateFine(0));
        assertEquals(0, s.calculateFine(-5));
    }

    @Test
    void cdFineCalculation_twentyPerDay() {
        CDFineStrategy s = new CDFineStrategy();
        assertEquals(20, s.calculateFine(1));
        assertEquals(100, s.calculateFine(5));
    }
}
