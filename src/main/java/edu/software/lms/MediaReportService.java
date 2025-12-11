package edu.software.lms;

import java.time.LocalDate;
import java.util.List;

/**
 * Service responsible for generating reports related to media loans,
 * especially overdue items.
 *
 * <p>This class does not modify any state — it only reads from repositories and
 * constructs formatted output strings.</p>
 */
public class MediaReportService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final TimeProvider timeProvider;

    /**
     * Constructs a new reporting service.
     *
     * @param loanRepository repository containing loan records
     * @param bookRepository repository containing media information
     * @param timeProvider date provider used for calculating overdue items
     */
    public MediaReportService(LoanRepository loanRepository,
                              BookRepository bookRepository,
                              TimeProvider timeProvider) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.timeProvider = timeProvider;
    }

    /**
     * Generates a textual summary of all overdue loans.
     *
     * <p>For each overdue item, the report lists:</p>
     * <ul>
     *     <li>Loan ID</li>
     *     <li>Media name</li>
     *     <li>Media type (BOOK or CD)</li>
     *     <li>Number of overdue days</li>
     *     <li>Fine amount</li>
     * </ul>
     *
     * <p>Also includes the total fine amount at the end.</p>
     *
     * @return formatted multi-line report
     */
    public String generateOverdueSummary() {
        LocalDate today = timeProvider.today();

        List<Loan> overdue = loanRepository.getAllLoans().stream()
                .filter(l -> !l.isReturned() && l.isOverdue(today))
                .toList();

        int totalFine = 0;
        StringBuilder sb = new StringBuilder();

        sb.append("Overdue Report (").append(overdue.size()).append(" items)\n");

        for (Loan l : overdue) {

            // Determine media type safely
            MediaType type = MediaType.BOOK;

            if (l instanceof MediaLoan ml) type = ml.getMediaType();
            else {
                Book b = bookRepository.getBookById(l.getBookId());
                if (b instanceof CD) type = MediaType.CD;
            }

            int overdueDays = l.overdueDays(today);

            int fine = (type == MediaType.CD)
                    ? new CDFineStrategy().calculateFine(overdueDays)
                    : new BookFineStrategy().calculateFine(overdueDays);

            totalFine += fine;

            Book b = bookRepository.getBookById(l.getBookId());

            sb.append("LoanId: ").append(l.getId())
                    .append(" | Media: ").append(b == null ? "<unknown>" : b.getName())
                    .append(" | Type: ").append(type)
                    .append(" | OverdueDays: ").append(overdueDays)
                    .append(" | Fine: ").append(fine).append(" NIS\n");
        }

        sb.append("Total fine across all media: ").append(totalFine).append(" NIS\n");
        return sb.toString();
    }
}
