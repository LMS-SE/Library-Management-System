package edu.software.lms;

import java.time.LocalDate;

/**
 * Time provider implementation used for testing.
 *
 * <p>Allows tests to simulate time progression manually without depending on
 * the system clock.</p>
 */
public class MockTimeProvider implements TimeProvider {

    /** Internal date value that tests can manipulate. */
    private LocalDate current;

    /**
     * Creates a mock time provider initialized at the given starting date.
     *
     * @param start initial date to return from {@link #today()}
     */
    public MockTimeProvider(LocalDate start) {
        this.current = start;
    }

    /**
     * Returns the current simulated date.
     *
     * @return current mock date
     */
    @Override
    public LocalDate today() {
        return current;
    }

    /**
     * Advances the internal date by the specified number of days.
     *
     * @param days number of days to add
     */
    public void plusDays(long days) {
        current = current.plusDays(days);
    }
}
