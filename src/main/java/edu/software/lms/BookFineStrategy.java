package edu.software.lms;

public class BookFineStrategy implements FineStrategy {
    private final int perDay;
    public BookFineStrategy() { this.perDay = 10; } // 10 NIS/day
    public BookFineStrategy(int perDay) { this.perDay = perDay; }
    @Override
    public int calculateFine(int overdueDays) {
        if (overdueDays <= 0) return 0;
        return overdueDays * perDay;
    }
}
