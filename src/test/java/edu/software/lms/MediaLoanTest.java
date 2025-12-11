package edu.software.lms;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class MediaLoanTest {

    @Test
    void mediaLoanCarriesMediaType_andBasicFields() {
        LocalDate borrow = LocalDate.of(2025, 1, 1);
        LocalDate due = borrow.plusDays(7);
        MediaLoan ml = new MediaLoan("userA", 5, borrow, due, MediaType.CD);

        assertEquals("userA", ml.getUserId());
        assertEquals(5, ml.getBookId());
        assertEquals(borrow, ml.getBorrowDate());
        assertEquals(due, ml.getDueDate());
        assertEquals(MediaType.CD, ml.getMediaType());
        assertFalse(ml.isReturned());
        assertEquals(0, ml.overdueDays(borrow));
    }
}
