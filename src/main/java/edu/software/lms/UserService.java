package edu.software.lms;

import java.util.List;

public class UserService {
    private UserRepository userRepository = new InMemoryUserRepository();
    private BookRepository bookRepository = new InMemoryBooks();
    private LoanRepository loanRepository = new InMemoryLoanRepository();
    private User currentUser;

    public UserService() {
        createAdmin("admin","admin@ADMIN123","admin@gmail.com");
    }

    public UserRepository getUserRepository() { return userRepository; }
    public void setUserRepository(UserRepository userRepository) { this.userRepository = userRepository; }

    public BookRepository getBookRepository() { return bookRepository; }
    public void setBookRepository(BookRepository bookRepository) { this.bookRepository = bookRepository; }

    public LoanRepository getLoanRepository() { return loanRepository; }
    public void setLoanRepository(LoanRepository loanRepository) { this.loanRepository = loanRepository; }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User currentUser) { this.currentUser = currentUser; }

    public SignUpResult validateCreateAccountCredentials(String username, String pwd,String email) {
        if (pwd.equals("0") || username.equals("0")) {
            return SignUpResult.CANCEL_SIGNUP;
        }
        if (pwd.length() < 8) {
            return SignUpResult.PASSWORD_TOO_SHORT;
        }
        if (!CustomUtilities.isStrongPassword(pwd)) {
            return SignUpResult.PASSWORD_WEAK;
        }
        String hashedPwd = CustomUtilities.hashPassword(pwd, username);
        User newUser = new User(username, hashedPwd, email,false);
        boolean userCreationResult = userRepository.addUser(newUser);
        if (userCreationResult) {
            this.currentUser = newUser;
            return SignUpResult.USER_CREATED_SUCCESSFULLY;
        }
        return SignUpResult.USER_CREATION_FAILED;
    }

    public void createAdmin(String username, String pwd, String email) {
        if (pwd.equals("0") || username.equals("0")) {
            return;
        }
        if (pwd.length() < 8) {
            return;
        }
        if (!CustomUtilities.isStrongPassword(pwd)) {
            return;
        }
        String hashedPwd = CustomUtilities.hashPassword(pwd, username);
        User newUser = new User(username, hashedPwd, email,true);
        boolean userCreationResult = userRepository.addUser(newUser);
        if (userCreationResult) {
            this.currentUser = newUser;
        }
    }


    public LoginResult validateLoginCredentials(String username, String pwd) {
        if (username.isEmpty()) return LoginResult.INVALID_USERNAME;
        if (pwd.isEmpty()) return LoginResult.INVALID_PASSWORD;
        if (pwd.equals("0") || username.equals("0")) {
            return LoginResult.CANCEL_LOGIN;
        }
        String hashedPwd = CustomUtilities.hashPassword(pwd, username);
        Pair<User, LoginResult> result = validateCredentials(username, hashedPwd);
        currentUser = result.first;
        return result.second;
    }

    public Pair<User, LoginResult> validateCredentials(String username, String password) {
        if (username == null || username.isEmpty()) return new Pair<>(null, LoginResult.INVALID_USERNAME);
        if (password == null || password.isEmpty()) return new Pair<>(null, LoginResult.INVALID_PASSWORD);
        User user = userRepository.getUserByUsername(username);
        if (user == null) return new Pair<>(null, LoginResult.NO_USER_FOUND);
        if (user.checkPassword(password)) return new Pair<>(user, LoginResult.USER_FOUND_SUCCESSFULLY);
        return new Pair<>(null, LoginResult.USER_FOUND_WRONG_PASSWORD);
    }

    private boolean isAdmin(User user) {
        // Simple rule: user with username "admin" is treated as admin
        return user != null && "admin".equalsIgnoreCase(user.getUsername());
    }


    public Pair<Boolean, String> unregisterUser(String targetUsername) {
        if (currentUser == null) {
            return new Pair<>(false, "No user logged in.");
        }

        if (!isAdmin(currentUser)) {
            return new Pair<>(false, "Only admins can unregister users.");
        }

        if (targetUsername == null || targetUsername.isEmpty()) {
            return new Pair<>(false, "Username to unregister is required.");
        }

        User target = userRepository.getUserByUsername(targetUsername);
        if (target == null) {
            return new Pair<>(false, "User not found.");
        }

        // Don’t allow deleting the admin account itself
        if (isAdmin(target)) {
            return new Pair<>(false, "Admin account cannot be unregistered.");
        }

        // 1) Unpaid fines?
        if (target.getFineBalance() > 0) {
            return new Pair<>(false,
                    "User has unpaid fines (" + target.getFineBalance() + " NIS) and cannot be unregistered.");
        }

        // 2) Active loans?
        List<Loan> loans = loanRepository.getLoansByUserId(target.getId());
        boolean hasActiveLoans = loans.stream().anyMatch(l -> !l.isReturned());
        if (hasActiveLoans) {
            return new Pair<>(false, "User has active loans and cannot be unregistered.");
        }

        // 3) Perform delete
        boolean deleted = userRepository.deleteUser(target.getId());
        if (!deleted) {
            return new Pair<>(false, "Failed to unregister user (repository error).");
        }

        return new Pair<>(true, "User '" + targetUsername + "' unregistered successfully.");
    }

}
