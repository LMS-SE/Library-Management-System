package edu.software.lms;

/**
 * Enumeration describing all possible outcomes of a login attempt.
 *
 * <p>Each constant contains an associated user-facing message that can be shown
 * in the UI layer.</p>
 */
public enum LoginResult {

    /** Username not found in repository. */
    NO_USER_FOUND("no user with such username found"),

    /** Username exists but password does not match. */
    USER_FOUND_WRONG_PASSWORD("wrong password"),

    /** Login succeeded. */
    USER_FOUND_SUCCESSFULLY("User logged in successfully\n"),

    /** Password was empty or invalid format. */
    INVALID_PASSWORD("The password you entered was invalid\n"),

    /** Username was empty or invalid format. */
    INVALID_USERNAME("The username you entered was invalid\n"),

    /** User canceled the login process by typing '0'. */
    CANCEL_LOGIN("");

    /** UI message associated with this login result. */
    public final String message;

    LoginResult(String message) {
        this.message = message;
    }
}
