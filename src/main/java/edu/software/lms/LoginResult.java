package edu.software.lms;

public enum LoginResult {
    NO_USER_FOUND("no user with such username found"),
    USER_FOUND_WRONG_PASSWORD("wrong password"),
    USER_FOUND_SUCCESSFULLY("User logged in successfully\n"),
    INVALID_PASSWORD("The password you entered was invalid\n"),
    INVALID_USERNAME("The username you entered was invalid\n"),
    PASSWORD_TOO_SHORT("The password you entered was too short\n"),
    PASSWORD_WEAK("The password you entered was too weak\n" +
                            "it should have numbers, symbols , lowercase and uppercase letters\n"),
    CANCEL_LOGIN("");
    public final String message;
    private LoginResult(String message) {
        this.message = message;
    }

}
