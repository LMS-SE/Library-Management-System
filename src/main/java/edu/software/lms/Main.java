package edu.software.lms;

import java.util.logging.Logger;

/**
 * Entry point of the Library Management System.
 *
 * <p>Initializes the first window and executes the window navigation loop
 * until the user selects the exit option.</p>
 */
public class Main {

    /** Logger used to print exit messages and diagnostics. */
    static Logger logger = Logger.getLogger(Main.class.getName());

    /**
     * Starts the program and runs the window navigation loop.
     *
     * @param args command-line arguments (ignored)
     */
    public static void main(String[] args) {

        Window window = WindowFactory.create(NextWindow.LOGIN_AND_SIGNUP, new UserService());

        // Application flow loop
        while (window != null) {
            window = window.buildNextWindow();   // transitions to next window based on user input, using factory pattern
        }

        logger.info("Thank you for using Our Library System!");
    }
}
