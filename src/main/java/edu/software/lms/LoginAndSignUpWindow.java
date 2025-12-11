package edu.software.lms;

import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Window responsible for handling login and sign-up interactions.
 *
 * <p>Displays the initial menu and routes user input to authentication flows.</p>
 */
public final class LoginAndSignUpWindow implements Window {
    private static final Logger logger = Logger.getLogger(LoginAndSignUpWindow.class.getName());
    private static Scanner scanner = new Scanner(System.in);

    private final UserService userService;

    /**
     * Creates a new login/signup window using the given user service.
     *
     * @param userService shared user service
     */
    public LoginAndSignUpWindow(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays menu, reads user input, and transitions to the next window.
     *
     * @return next window or {@code null} on application exit
     */
    @Override
    public Window buildNextWindow() {
        printMessage();
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> { return loginOperation(); }
            case "2" -> { return signUpOperation(); }
            case "0" -> { return WindowFactory.create(NextWindow.EXIT, userService); }
            default -> {
                logger.warning("Invalid choice. Try again.");
                return this;
            }
        }
    }

    /** Prints the login/signup menu. */
    private void printMessage() {
        logger.info("\n===== Library System Menu =====");
        logger.info("1. Log In");
        logger.info("2. Sign Up");
        logger.info("0. Exit");
        logger.info("Enter choice: ");
    }

    /**
     * Runs the sign-up loop until creation succeeds or user cancels.
     *
     * @return next window after sign-up completes or cancels
     */
    private Window signUpOperation() {
        SignUpResult result;

        while (true) {
            result = createAccountPrompt();
            if (exitSignUp(result)) break;
            logger.info(result.message);
        }

        if (result == SignUpResult.CANCEL_SIGNUP) return this;

        return WindowFactory.create(NextWindow.USER_BOOK_OPERATIONS, userService);
    }

    /**
     * Runs login loop until credentials are accepted or canceled.
     *
     * @return next window after login
     */
    private Window loginOperation() {
        LoginResult result;

        while (true) {
            result = logInPrompt();
            if (exitLogin(result)) break;
            logger.info(result.message);
        }

        if (result == LoginResult.CANCEL_LOGIN) return this;

        if (userService.getCurrentUser().isAdmin()) {
            return WindowFactory.create(NextWindow.ADMIN_BOOK_OPERATIONS, userService);
        }

        return WindowFactory.create(NextWindow.USER_BOOK_OPERATIONS, userService);
    }

    /** Determines if sign-up should exit based on result. */
    private boolean exitSignUp(SignUpResult result) {
        return result == SignUpResult.CANCEL_SIGNUP ||
                result == SignUpResult.USER_CREATED_SUCCESSFULLY;
    }

    /** Determines if login should exit based on result. */
    private boolean exitLogin(LoginResult result) {
        return result == LoginResult.CANCEL_LOGIN ||
                result == LoginResult.USER_FOUND_SUCCESSFULLY;
    }

    /**
     * Prompts user for login credentials.
     *
     * @return login result status
     */
    private LoginResult logInPrompt() {
        logger.info("==Log In==");
        logger.info("==To go back type 0 in either the password or the username==");
        logger.info("Username : ");
        String username = scanner.nextLine().trim();
        logger.info("Password : ");
        String pwd = scanner.nextLine().trim();

        return userService.validateLoginCredentials(username, pwd);
    }

    /**
     * Prompts user for sign-up data.
     *
     * @return result of sign-up validation
     */
    private SignUpResult createAccountPrompt() {
        logger.info("==Create Account==");
        logger.info("==To go back type 0 in either the password or the username==");
        logger.info("Username : ");
        String username = scanner.nextLine().trim();
        logger.info("Password : ");
        String pwd = scanner.nextLine().trim();
        logger.info("Email : ");
        String email = scanner.nextLine().trim();

        return userService.validateCreateAccountCredentials(username, pwd, email);
    }
}
