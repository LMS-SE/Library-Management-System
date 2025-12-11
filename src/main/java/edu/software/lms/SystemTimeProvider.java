package edu.software.lms;

import java.time.LocalDate;

/**
 * Time provider that returns the system's current date.
 *
 * <p>Used in production, while {@link MockTimeProvider} is used for testing.</p>
 */
public class SystemTimeProvider implements TimeProvider {

    /**
     * Returns today's date from the system clock.
     *
     * @return current local date
     */
    @Override
    public LocalDate today() {
        return LocalDate.now();
    }
}
