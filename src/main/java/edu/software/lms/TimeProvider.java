package edu.software.lms;

import java.time.LocalDate;

/**
 * Abstraction for retrieving the current date.
 *
 * <p>This allows tests to replace real time with a predictable mock
 * implementation.</p>
 */
public interface TimeProvider {

    /**
     * Returns today's date.
     *
     * @return current date according to the implementation
     */
    LocalDate today();
}
