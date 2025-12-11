package edu.software.lms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginAndSignUpWindowTest {

    private UserService userService;
    private LoginAndSignUpWindow window;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        window = new LoginAndSignUpWindow(userService);
    }

    /**
     * Helper to replace the static Scanner with one that reads from our test input.
     */
    private void setScannerInput(String allInputLines) throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(allInputLines.getBytes(StandardCharsets.UTF_8));
        Scanner testScanner = new Scanner(in);

        Field scannerField = LoginAndSignUpWindow.class.getDeclaredField("scanner");
        scannerField.setAccessible(true);
        scannerField.set(null, testScanner); // static field → null for instance
    }

    @Test
    void buildNextWindow_loginSuccessful_adminUser() throws Exception {
        setScannerInput("1\nadmin\npwd\n");

        User adminUser = new User("admin", "pwd", "a@a.com", true);
        when(userService.validateLoginCredentials("admin", "pwd"))
                .thenReturn(LoginResult.USER_FOUND_SUCCESSFULLY);
        when(userService.getCurrentUser()).thenReturn(adminUser);

        Window result = window.buildNextWindow();

        assertNotSame(window, result);
    }

    @Test
    void buildNextWindow_loginCancelled() throws Exception {
        setScannerInput("1\nuser\npwd\n");

        when(userService.validateLoginCredentials("user", "pwd"))
                .thenReturn(LoginResult.CANCEL_LOGIN);

        Window result = window.buildNextWindow();

        assertSame(window, result);
    }

    @Test
    void buildNextWindow_signUpSuccessful() throws Exception {
        setScannerInput("2\nnewuser\npwd\nemail@mail.com\n");

        when(userService.validateCreateAccountCredentials("newuser", "pwd", "email@mail.com"))
                .thenReturn(SignUpResult.USER_CREATED_SUCCESSFULLY);

        Window result = window.buildNextWindow();

        assertNotSame(window, result);
    }

    @Test
    void buildNextWindow_signUpCancelled() throws Exception {
        setScannerInput("2\nnewuser\npwd\nemail@mail.com\n");

        when(userService.validateCreateAccountCredentials("newuser", "pwd", "email@mail.com"))
                .thenReturn(SignUpResult.CANCEL_SIGNUP);

        Window result = window.buildNextWindow();

        assertSame(window, result);
    }

    @Test
    void buildNextWindow_exit() throws Exception {
        setScannerInput("0\n");

        Window result = window.buildNextWindow();

        assertNotSame(window, result);
    }

    @Test
    void buildNextWindow_invalidChoice() throws Exception {
        setScannerInput("invalid_choice\n");

        Window result = window.buildNextWindow();

        assertSame(window, result);
    }
}
