package edu.software.lms;

public enum LoginResult {
    NO_USER_FOUND("no user with such username found"),
    USER_FOUND_WRONG_PASSWORD("wrong password"),
    USER_FOUND_SUCCESSFULLY("User logged in successfully\n"),
    INVALID_PASSWORD("The password you entered was invalid\n"),
    INVALID_USERNAME("The username you entered was invalid\n"),
        CANCEL_LOGIN("");
    public final String message;
    LoginResult(String message) {
        this.message = message;
    }

}
