package edu.software.lms;

/**
 * Factory responsible for creating different window (UI) instances.
 *
 * <p>This is used to manage transitions between login, admin, and user views.</p>
 */
public class WindowFactory {

    /** Prevents instantiation. */
    private WindowFactory() {}

    /**
     * Creates a window instance based on the given enum value.
     *
     * @param nextWindow enum indicating which window to instantiate
     * @param userService shared user service for dependency injection
     * @return window instance or null if EXIT was selected
     */
    public static Window create(NextWindow nextWindow, UserService userService) {
        switch (nextWindow) {
            case LOGIN_AND_SIGNUP -> {
                return new LoginAndSignUpWindow(userService);
            }
            case ADMIN_BOOK_OPERATIONS -> {
                return new AdminBookOperationsWindow(userService);
            }
            case USER_BOOK_OPERATIONS -> {
                return new UserBookOperations(userService);
            }
            case EXIT -> {
                return null;
            }
        }
        return null;
    }
}