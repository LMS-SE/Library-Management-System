package edu.software.lms;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class MediaReportServiceTest {

    @Test
    void generateOverdueSummary_containsBothTypes_andCorrectTotal() {
        InMemoryUserRepository ur = new InMemoryUserRepository();
        InMemoryBooks br = new InMemoryBooks();
        InMemoryLoanRepository lr = new InMemoryLoanRepository();
        MockTimeProvider tp = new MockTimeProvider(LocalDate.of(2025, 6, 1));

        User u = new User("sam","pwd");
        u.setId("u-sam");
        ur.addUser(u);

        CD cd = new CD(1,"Song","Artist","CD-A");
        Book book = new Book(2,"Story","Writer","ISBN-B");
        br.addBook(cd);
        br.addBook(book);

        MediaLoan loanCd = new MediaLoan(u.getId(), 1, tp.today().minusDays(15), tp.today().minusDays(8), MediaType.CD);
        MediaLoan loanBook = new MediaLoan(u.getId(), 2, tp.today().minusDays(40), tp.today().minusDays(12), MediaType.BOOK);
        lr.addLoan(loanCd);
        lr.addLoan(loanBook);

        MediaReportService reportService = new MediaReportService(lr, br, tp);
        String report = reportService.generateOverdueSummary();

        assertTrue(report.contains("Overdue Report"));
        int cdOver = loanCd.overdueDays(tp.today());
        int bookOver = loanBook.overdueDays(tp.today());
        int expectedTotal = cdOver * 20 + bookOver * 10;
        assertTrue(report.contains(String.valueOf(expectedTotal)), "report should contain expected total fine");
    }
}
