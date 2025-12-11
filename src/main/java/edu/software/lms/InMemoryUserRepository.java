package edu.software.lms;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * In-memory implementation of the {@link UserRepository} interface.
 * Stores all users in a simple list without persistence.
 *
 * <p>This class is primarily suited for testing and lightweight runtime usage.</p>
 */
public class InMemoryUserRepository implements UserRepository {

    /** Internal list containing all registered users. */
    private final List<User> users;

    /**
     * Constructs an empty in-memory user repository.
     */
    public InMemoryUserRepository() {
        this.users = new ArrayList<>();
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id user ID
     * @return the matching user, or {@code null} if not found
     */
    @Override
    public User getUserById(String id) {
        if (id == null) return null;

        return users.stream()
                .filter(u -> id.equals(u.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username username to search
     * @return the matching user, or {@code null} if none exist
     */
    @Override
    public User getUserByUsername(String username) {
        if (username == null) return null;

        return users.stream()
                .filter(u -> username.equals(u.getUsername()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Adds a new user to the repository.
     *
     * <p>Addition fails if:</p>
     * <ul>
     *     <li>User is {@code null}</li>
     *     <li>Username or ID is duplicate</li>
     * </ul>
     *
     * @param user the user to add
     * @return true if user added successfully, false otherwise
     */
    @Override
    public boolean addUser(User user) {
        if (user == null || user.getUsername() == null) return false;

        boolean exists = users.stream().anyMatch(u ->
                (u.getId() != null && u.getId().equals(user.getId())) ||
                        (u.getUsername() != null && u.getUsername().equals(user.getUsername()))
        );

        if (exists) return false;

        users.add(user);
        return true;
    }

    /**
     * Removes a user with the specified ID.
     *
     * @param id ID of the user to delete
     * @return true if deletion succeeded, false if user not found
     */
    @Override
    public boolean deleteUser(String id) {
        Optional<User> found = users.stream()
                .filter(u -> id != null && id.equals(u.getId()))
                .findFirst();

        if (found.isPresent()) {
            users.remove(found.get());
            return true;
        }
        return false;
    }
}
