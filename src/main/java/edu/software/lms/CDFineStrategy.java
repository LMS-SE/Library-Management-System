package edu.software.lms;

public class CDFineStrategy implements FineStrategy {
    private final int perDay;
    public CDFineStrategy() { this.perDay = 20; } // 20 NIS/day for CDs
    public CDFineStrategy(int perDay) { this.perDay = perDay; }
    @Override
    public int calculateFine(int overdueDays) {
        if (overdueDays <= 0) return 0;
        return overdueDays * perDay;
    }
}
