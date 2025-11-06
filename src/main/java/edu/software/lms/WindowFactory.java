package edu.software.lms;

public class WindowFactory {
    public static Window create(NextWindow nextWindow,UserService userService) {
            switch (nextWindow) {
                case LOGIN_AND_SIGNUP -> {
                    return new LoginAndSignUpWindow(userService);
                }
                case ADMIN_BOOK_OPERATIONS -> {
                    return new AdminBookOperationsWindow(userService);
                }
                case EXIT -> {
                    return null;
                }
            }
            return null;
    }
}
