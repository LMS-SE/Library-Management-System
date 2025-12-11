package edu.software.lms;

import java.util.Scanner;

/**
 * Basic UI window interface representing one screen in the console-based system.
 *
 * <p>Each window implementation processes input and returns another window
 * instance (or null if the application should exit).</p>
 */
public interface Window {

    /** Shared scanner used by default for input. */
    Scanner scanner = new Scanner(System.in);

    /**
     * Executes the window logic and returns the next window to display.
     *
     * @return next window instance, or {@code null} to exit application
     */
    Window buildNextWindow();
}
