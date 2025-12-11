package edu.software.lms;

import java.util.Scanner;
import java.util.logging.Logger;

public final class LoginAndSignUpWindow implements Window {
    private static final Logger logger = Logger.getLogger(LoginAndSignUpWindow.class.getName());
    private static Scanner scanner = new Scanner(System.in);
    private final UserService userService;

    public LoginAndSignUpWindow(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Window buildNextWindow() {
        printMessage();
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> {
                return loginOperation();
            }
            case "2" -> {
                return signUpOperation();
            }
            case "0" -> {
                return WindowFactory.create(NextWindow.EXIT, userService);
            }
            default -> {
                logger.warning("Invalid choice. Try again.");
                return this;
            }
        }
    }

    private void printMessage() {
        logger.info("\n===== Library System Menu =====");
        logger.info("1. Log In");
        logger.info("2. Sign Up");
        logger.info("0. Exit");
        logger.info("Enter choice: ");
    }

    private Window signUpOperation() {
        SignUpResult signUpResult;
        while (true) {
            signUpResult = createAccountPrompt();
            if (exitSignUp(signUpResult)) break;
            else logger.info(signUpResult.message);
        }

        if (signUpResult == SignUpResult.CANCEL_SIGNUP) return this;
        return WindowFactory.create(NextWindow.USER_BOOK_OPERATIONS, userService);
    }

    private Window loginOperation() {
        LoginResult loginResult = null;
        while (true) {
            loginResult = logInPrompt();
            if (exitLogin(loginResult)) break;
            else logger.info(loginResult.message);
        }

        if (loginResult == LoginResult.CANCEL_LOGIN) return this;
        if (userService.getCurrentUser().isAdmin()) {
            return WindowFactory.create(NextWindow.ADMIN_BOOK_OPERATIONS, userService);
        }
        return WindowFactory.create(NextWindow.USER_BOOK_OPERATIONS, userService);
    }

    private boolean exitSignUp(SignUpResult signUpResult) {
        return signUpResult == SignUpResult.CANCEL_SIGNUP ||
                signUpResult == SignUpResult.USER_CREATED_SUCCESSFULLY;
    }

    private boolean exitLogin(LoginResult loginResult) {
        return loginResult == LoginResult.CANCEL_LOGIN ||
                loginResult == LoginResult.USER_FOUND_SUCCESSFULLY;
    }

    private LoginResult logInPrompt() {
        logger.info("==Log In==");
        logger.info("==To go back type 0 in either the password or the username==");
        logger.info("Username : ");
        String username = scanner.nextLine().trim();
        logger.info("Password : ");
        String pwd = scanner.nextLine().trim();
        return userService.validateLoginCredentials(username, pwd);
    }

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
