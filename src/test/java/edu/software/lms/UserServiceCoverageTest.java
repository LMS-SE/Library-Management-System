package edu.software.lms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceCoverageTest {

    @Test
    void createAdmin_returnsEarly_whenUsernameIsZero() {
        UserRepository repo = mock(UserRepository.class);
        UserService us = new UserService();
        us.setUserRepository(repo);

        us.createAdmin("0", "ADMIN@123A", "a@a.com");

        verifyNoInteractions(repo);
    }

    @Test
    void createAdmin_returnsEarly_whenPwdIsZero() {
        UserRepository repo = mock(UserRepository.class);
        UserService us = new UserService();
        us.setUserRepository(repo);

        us.createAdmin("admin2", "0", "a@a.com");

        verifyNoInteractions(repo);
    }

    @Test
    void createAdmin_returnsEarly_whenPwdTooShort() {
        UserRepository repo = mock(UserRepository.class);
        UserService us = new UserService();
        us.setUserRepository(repo);

        us.createAdmin("admin2", "Ab@1", "a@a.com");

        verifyNoInteractions(repo);
    }

    @Test
    void createAdmin_returnsEarly_whenPwdNotStrong() {
        UserRepository repo = mock(UserRepository.class);
        UserService us = new UserService();
        us.setUserRepository(repo);

        // 8+ chars but not strong (no symbol etc.)
        us.createAdmin("admin2", "Password1", "a@a.com");

        verifyNoInteractions(repo);
    }

    @Test
    void createAdmin_setsCurrentUser_whenRepoAddsUserSuccessfully() {
        UserRepository repo = mock(UserRepository.class);
        when(repo.addUser(any(User.class))).thenReturn(true);

        UserService us = new UserService();
        us.setUserRepository(repo);

        us.createAdmin("admin2", "ADMIN@123A", "a@a.com");

        assertNotNull(us.getCurrentUser());

        assertTrue(us.getCurrentUser().isAdmin());

    }

    @Test
    void createAdmin_doesNotSetCurrentUser_whenRepoRejectsUser() {
        UserRepository repo = mock(UserRepository.class);
        when(repo.addUser(any(User.class))).thenReturn(false);

        UserService us = new UserService();
        us.setUserRepository(repo);
        us.setCurrentUser(null);

        us.createAdmin("admin2", "ADMIN@123A", "a@a.com");

        assertNull(us.getCurrentUser());

    }

    @Test
    void validateLoginCredentials_invalidUsername_whenEmpty() {
        UserService us = new UserService();
        assertEquals(LoginResult.INVALID_USERNAME, us.validateLoginCredentials("", "x"));
    }

    @Test
    void validateLoginCredentials_invalidPassword_whenEmpty() {
        UserService us = new UserService();
        assertEquals(LoginResult.INVALID_PASSWORD, us.validateLoginCredentials("u", ""));
    }

    @Test
    void validateLoginCredentials_cancel_whenZeroTyped() {
        UserService us = new UserService();
        assertEquals(LoginResult.CANCEL_LOGIN, us.validateLoginCredentials("0", "x"));
        assertEquals(LoginResult.CANCEL_LOGIN, us.validateLoginCredentials("u", "0"));
    }

    @Test
    void validateLoginCredentials_success_setsCurrentUser() {
        UserRepository repo = new InMemoryUserRepository();
        UserService us = new UserService();
        us.setUserRepository(repo);

        // create a user with the SAME hashing rule
        String username = "bob";
        String rawPwd = "Bob@12345";
        String hashed = CustomUtilities.hashPassword(rawPwd, username);

        User u = new User(username, hashed, "bob@x.com", false);
        repo.addUser(u);

        LoginResult res = us.validateLoginCredentials(username, rawPwd);

        assertEquals(LoginResult.USER_FOUND_SUCCESSFULLY, res);
        assertNotNull(us.getCurrentUser());
        assertEquals(username, us.getCurrentUser().getUsername());
    }

    @Test
    void validateLoginCredentials_wrongPassword_doesNotSetCurrentUser() {
        UserRepository repo = new InMemoryUserRepository();
        UserService us = new UserService();
        us.setUserRepository(repo);

        String username = "bob";
        String hashed = CustomUtilities.hashPassword("Bob@12345", username);
        repo.addUser(new User(username, hashed, "bob@x.com", false));

        LoginResult res = us.validateLoginCredentials(username, "Wrong@12345");

        assertEquals(LoginResult.USER_FOUND_WRONG_PASSWORD, res);
        assertNull(us.getCurrentUser());
    }
}
