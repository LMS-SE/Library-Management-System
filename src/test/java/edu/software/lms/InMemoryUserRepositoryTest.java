package edu.software.lms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {

    private InMemoryUserRepository repo;

    @BeforeEach
    void setUp() {
        repo = new InMemoryUserRepository();
    }

    @Test
    void addAndGetUser() {
        User u = new User("alice", "password123");
        u.setId("alice-id");
        boolean added = repo.addUser(u);
        assertTrue(added);

        User fetched = repo.getUserByUsername("alice");
        assertNotNull(fetched);
        assertEquals("alice", fetched.getUsername());
        assertEquals("alice-id", fetched.getId());

        User byId = repo.getUserById("alice-id");
        assertNotNull(byId);
        assertEquals("alice", byId.getUsername());
    }

    @Test
    void preventDuplicateUser() {
        User u1 = new User("bob", "p1");
        u1.setId("bob-id");
        assertTrue(repo.addUser(u1));

        User u2 = new User("bob", "p2"); // same username
        u2.setId("bob-id-2");
        assertFalse(repo.addUser(u2), "Should not add user with same username");
    }

    @Test
    void deleteUser() {
        User u = new User("carol", "pw");
        u.setId("c-id");
        repo.addUser(u);

        assertTrue(repo.deleteUser("c-id"));
        assertNull(repo.getUserById("c-id"));
        assertFalse(repo.deleteUser("c-id")); // already deleted
    }

    @Test
    void validateCredentials() {
        User u = new User("dave", "mysecret");
        u.setId("d-id");
        repo.addUser(u);
        UserService userService=new UserService();
        userService.setUserRepository(repo);
        assertEquals(new Pair<>(null,LoginResult.INVALID_USERNAME),userService.validateCredentials(null,"mysecret"));
        assertEquals(new Pair<>(null,LoginResult.INVALID_PASSWORD),userService.validateCredentials("dave",null));
        assertEquals(new Pair<>(null,LoginResult.NO_USER_FOUND),userService.validateCredentials("none","123"));
        assertEquals(new Pair<>(u, LoginResult.USER_FOUND_SUCCESSFULLY),userService.validateCredentials("dave","mysecret"));
        assertEquals(new Pair<>(null,LoginResult.USER_FOUND_WRONG_PASSWORD),userService.validateCredentials("dave","123"));
        ;
    }
}
