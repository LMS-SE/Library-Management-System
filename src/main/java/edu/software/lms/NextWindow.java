package edu.software.lms;

/**
 * Enumeration defining all possible windows (UI screens)
 * that the system's window factory can create.
 */
public enum NextWindow {

    /** Login/sign-up menu. */
    LOGIN_AND_SIGNUP,

    /** Admin's book management window. */
    ADMIN_BOOK_OPERATIONS,

    /** Regular user window for borrowing/returning/searching. */
    USER_BOOK_OPERATIONS,

    /** Terminates the application. */
    EXIT
}
