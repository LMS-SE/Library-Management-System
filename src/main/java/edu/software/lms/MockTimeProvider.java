package edu.software.lms;

import java.time.LocalDate;

public class MockTimeProvider implements TimeProvider {
    private LocalDate current;
    public MockTimeProvider(LocalDate start) { this.current = start; }
    @Override public LocalDate today() { return current; }
    public void plusDays(long days) { current = current.plusDays(days); }
}

