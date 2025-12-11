package edu.software.lms;

import java.time.LocalDate;
import java.util.List;

public class MediaReportService {
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final TimeProvider timeProvider;

    public MediaReportService(LoanRepository loanRepository, BookRepository bookRepository, TimeProvider timeProvider) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.timeProvider = timeProvider;
    }

    public String generateOverdueSummary() {
        LocalDate today = timeProvider.today();
        List<Loan> overdue = loanRepository.getAllLoans().stream()
                .filter(l -> !l.isReturned() && l.isOverdue(today))
                .toList();

        int totalFine = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("Overdue Report (").append(overdue.size()).append(" items)\n");
        for (Loan l : overdue) {
            MediaType mt = MediaType.BOOK;
            if (l instanceof MediaLoan mediaLoan) mt = mediaLoan.getMediaType();
            else {
                Book b = bookRepository.getBookById(l.getBookId());
                if (b instanceof CD) mt = MediaType.CD;
            }
            int overdueDays = l.overdueDays(today);
            int fine = (mt == MediaType.CD) ? new CDFineStrategy().calculateFine(overdueDays) : new BookFineStrategy().calculateFine(overdueDays);
            totalFine += fine;
            Book b = bookRepository.getBookById(l.getBookId());
            sb.append("LoanId: ").append(l.getId())
                    .append(" | Media: ").append(b == null ? "<unknown>" : b.getName())
                    .append(" | Type: ").append(mt)
                    .append(" | OverdueDays: ").append(overdueDays)
                    .append(" | Fine: ").append(fine).append(" NIS\n");
        }
        sb.append("Total fine across all media: ").append(totalFine).append(" NIS\n");
        return sb.toString();
    }
}
