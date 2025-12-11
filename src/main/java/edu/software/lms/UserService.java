package edu.software.lms;

import java.util.List;

/**
 * Central service managing users, authentication, repositories, and admin tasks.
 *
 * <p>Acts as the façade that the UI uses to interact with user-related logic.</p>
 */
public class UserService {

    private UserRepository userRepository = new InMemoryUserRepository();
    private BookRepository bookRepository = new InMemoryBooks();
    private LoanRepository loanRepository = new InMemoryLoanRepository();

    /** Holds the currently logged-in user (session user). */
    private User currentUser;

    /**
     * Creates a UserService and automatically initializes the admin account.
     */
    public UserService() {
        createAdmin("admin","admin@ADMIN123","admin@gmail.com");
    }

    // ------------ Repository getters/setters ------------

    public UserRepository getUserRepository() { return userRepository; }
    public void setUserRepository(UserRepository userRepository) { this.userRepository = userRepository; }

    public BookRepository getBookRepository() { return bookRepository; }
    public void setBookRepository(BookRepository bookRepository) { this.bookRepository = bookRepository; }

    public LoanRepository getLoanRepository() { return loanRepository; }
    public void setLoanRepository(LoanRepository loanRepository) { this.loanRepository = loanRepository; }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User currentUser) { this.currentUser = currentUser; }

    /**
     * Validates input for account creation and attempts to register the user.
     *
     * <p>Performs:</p>
     * <ul>
     *   <li>Input cancellation check</li>
     *   <li>Password length check</li>
     *   <li>Password strength check</li>
     *   <li>Password hashing</li>
     *   <li>Repository insertion</li>
     * </ul>
     *
     * @param username username entered
     * @param pwd raw password entered
     * @param email user’s email
     * @return result of sign-up attempt
     */
    public SignUpResult validateCreateAccountCredentials(String username, String pwd, String email) {
        if (pwd.equals("0") || username.equals("0"))
            return SignUpResult.CANCEL_SIGNUP;

        if (pwd.length() < 8)
            return SignUpResult.PASSWORD_TOO_SHORT;

        if (!CustomUtilities.isStrongPassword(pwd))
            return SignUpResult.PASSWORD_WEAK;

        String hashedPwd = CustomUtilities.hashPassword(pwd, username);
        User newUser = new User(username, hashedPwd, email,false);

        boolean created = userRepository.addUser(newUser);

        if (created) {
            this.currentUser = newUser;
            return SignUpResult.USER_CREATED_SUCCESSFULLY;
        }

        return SignUpResult.USER_CREATION_FAILED;
    }

    /**
     * Creates the admin account with full privileges.
     *
     * @param username admin username
     * @param pwd admin raw password
     * @param email admin email
     */
    public void createAdmin(String username, String pwd, String email) {
        if (pwd.equals("0") || username.equals("0"))
            return;
        if (pwd.length() < 8)
            return;
        if (!CustomUtilities.isStrongPassword(pwd))
            return;

        String hashedPwd = CustomUtilities.hashPassword(pwd, username);
        User admin = new User(username, hashedPwd, email,true);

        boolean created = userRepository.addUser(admin);
        if (created)
            this.currentUser = admin;
    }

    /**
     * Validates login input and attempts authentication.
     *
     * @param username username user typed
     * @param pwd raw password
     * @return login result enum
     */
    public LoginResult validateLoginCredentials(String username, String pwd) {

        if (username.isEmpty())
            return LoginResult.INVALID_USERNAME;

        if (pwd.isEmpty())
            return LoginResult.INVALID_PASSWORD;

        if (pwd.equals("0") || username.equals("0"))
            return LoginResult.CANCEL_LOGIN;

        String hashedPwd = CustomUtilities.hashPassword(pwd, username);
        Pair<User, LoginResult> result = validateCredentials(username, hashedPwd);

        currentUser = result.first;
        return result.second;
    }

    /**
     * Validates username + hashed password against stored repository values.
     *
     * @param username username
     * @param password hashed password
     * @return pair(user, result)
     */
    public Pair<User, LoginResult> validateCredentials(String username, String password) {

        if (username == null || username.isEmpty())
            return new Pair<>(null, LoginResult.INVALID_USERNAME);

        if (password == null || password.isEmpty())
            return new Pair<>(null, LoginResult.INVALID_PASSWORD);

        User user = userRepository.getUserByUsername(username);

        if (user == null)
            return new Pair<>(null, LoginResult.NO_USER_FOUND);

        if (user.checkPassword(password))
            return new Pair<>(user, LoginResult.USER_FOUND_SUCCESSFULLY);

        return new Pair<>(null, LoginResult.USER_FOUND_WRONG_PASSWORD);
    }

    /**
     * Determines whether the given user is an admin.
     *
     * @param user user object
     * @return true if user is admin
     */
    private boolean isAdmin(User user) {
        return user != null && "admin".equalsIgnoreCase(user.getUsername());
    }

    /**
     * Attempts to unregister a user.
     *
     * <p>Fails if:</p>
     * <ul>
     *   <li>No user logged in</li>
     *   <li>Current user is not admin</li>
     *   <li>Target user does not exist</li>
     *   <li>Target is admin</li>
     *   <li>Target has unpaid fines</li>
     *   <li>Target has active loans</li>
     * </ul>
     *
     * @param targetUsername username to unregister
     * @return (success flag, message)
     */
    public Pair<Boolean, String> unregisterUser(String targetUsername) {
        if (currentUser == null)
            return new Pair<>(false, "No user logged in.");

        if (!isAdmin(currentUser))
            return new Pair<>(false, "Only admins can unregister users.");

        if (targetUsername == null || targetUsername.isEmpty())
            return new Pair<>(false, "Username to unregister is required.");

        User target = userRepository.getUserByUsername(targetUsername);
        if (target == null)
            return new Pair<>(false, "User not found.");

        if (isAdmin(target))
            return new Pair<>(false, "Admin account cannot be unregistered.");

        if (target.getFineBalance() > 0)
            return new Pair<>(false,
                    "User has unpaid fines (" + target.getFineBalance() + " NIS) and cannot be unregistered.");

        List<Loan> loans = loanRepository.getLoansByUserId(target.getId());
        boolean hasActiveLoans = loans.stream().anyMatch(l -> !l.isReturned());

        if (hasActiveLoans)
            return new Pair<>(false, "User has active loans and cannot be unregistered.");

        boolean deleted = userRepository.deleteUser(target.getId());

        if (!deleted)
            return new Pair<>(false, "Failed to unregister user (repository error).");

        return new Pair<>(true, "User '" + targetUsername + "' unregistered successfully.");
    }
}
