package edu.software.lms;

public class UserService {
    private UserRepository userRepository = new InMemoryUserRepository();
    private BookRepository bookRepository = new InMemoryBooks();
    private LoanRepository loanRepository = new InMemoryLoanRepository();
    private User currentUser;

    public UserService() { }

    public UserRepository getUserRepository() { return userRepository; }
    public void setUserRepository(UserRepository userRepository) { this.userRepository = userRepository; }

    public BookRepository getBookRepository() { return bookRepository; }
    public void setBookRepository(BookRepository bookRepository) { this.bookRepository = bookRepository; }

    public LoanRepository getLoanRepository() { return loanRepository; }
    public void setLoanRepository(LoanRepository loanRepository) { this.loanRepository = loanRepository; }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User currentUser) { this.currentUser = currentUser; }

    public SignUpResult validateCreateAccountCredentials(String username, String pwd) {
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
        User newUser = new User(username, hashedPwd);
        boolean userCreationResult = userRepository.addUser(newUser);
        if (userCreationResult) {
            this.currentUser = newUser;
            return SignUpResult.USER_CREATED_SUCCESSFULLY;
        }
        return SignUpResult.USER_CREATION_FAILED;
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
}
