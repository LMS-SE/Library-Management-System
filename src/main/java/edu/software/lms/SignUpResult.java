package edu.software.lms;

public enum SignUpResult {
    INVALID_PASSWORD("The password you entered was invalid\n"),
    INVALID_USERNAME("The username you entered was invalid\n"),
    PASSWORD_TOO_SHORT("The password you entered was too short\n"),
    PASSWORD_WEAK("The password you entered was too weak\n" +
            "it should have numbers, symbols , lowercase and uppercase letters\n"),
    USER_CREATED_SUCCESSFULLY("User logged in successfully\n"),
    USER_CREATION_FAILED("failed to create user, duplicate username"),
    CANCEL_SIGNUP("");
    public final String message;
    private SignUpResult(String message) {
        this.message = message;
    }
}
