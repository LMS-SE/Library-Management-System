package edu.software.lms;

/**
 * Repository abstraction for storing and retrieving users.
 *
 * <p>Allows different storage methods (in-memory, database, etc.) to be plugged in.</p>
 */
public interface UserRepository {

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id ID of user
     * @return matching user or {@code null} if not found
     */
    User getUserById(String id);

    /**
     * Retrieves a user by username.
     *
     * @param username username to look for
     * @return matching user or {@code null}
     */
    User getUserByUsername(String username);

    /**
     * Adds a new user to the repository.
     *
     * @param user user object to add
     * @return true if added, false if duplicate or invalid
     */
    boolean addUser(User user);

    /**
     * Deletes a user by ID.
     *
     * @param id user ID
     * @return true if deletion was successful
     */
    boolean deleteUser(String id);
}
