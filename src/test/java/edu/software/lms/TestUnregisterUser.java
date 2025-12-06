package edu.software.lms;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestUnregisterUser {

    private UserService setupService() {
        UserService svc = new UserService();
        svc.setUserRepository(new InMemoryUserRepository());
        svc.setLoanRepository(new InMemoryLoanRepository());
        return svc;
    }

    @Test
    void adminCanUnregisterUserWithoutLoansOrFines() {
        UserService svc = setupService();
        UserRepository repo = svc.getUserRepository();

        User admin = new User("admin", "pwd","user@gmail.com",false);
        User user  = new User("alice", "pwd","user@gmail.com",false);
        repo.addUser(admin);
        repo.addUser(user);
        svc.setCurrentUser(admin);

        Pair<Boolean, String> result = svc.unregisterUser("alice");

        assertTrue(result.first);
        assertNull(repo.getUserByUsername("alice"));
    }

    @Test
    void nonAdminCannotUnregisterUser() {
        UserService svc = setupService();
        UserRepository repo = svc.getUserRepository();

        User normal = new User("bob", "pwd","user@gmail.com",false);
        User user   = new User("alice", "pwd","user@gmail.com",false);
        repo.addUser(normal);
        repo.addUser(user);
        svc.setCurrentUser(normal);

        Pair<Boolean, String> result = svc.unregisterUser("alice");

        assertFalse(result.first);
        assertNotNull(repo.getUserByUsername("alice"));
    }

    @Test
    void userWithActiveLoansCannotBeUnregistered() {
        UserService svc = setupService();
        UserRepository userRepo = svc.getUserRepository();
        LoanRepository loanRepo = svc.getLoanRepository();

        User admin = new User("admin", "pwd","user@gmail.com",false);
        User user  = new User("alice", "pwd","user@gmail.com",false);
        userRepo.addUser(admin);
        userRepo.addUser(user);
        svc.setCurrentUser(admin);

        // active loan (not returned)
        Loan loan = new Loan(user.getId(), 1,
                java.time.LocalDate.now().minusDays(2),
                java.time.LocalDate.now().plusDays(5));
        loanRepo.addLoan(loan);

        Pair<Boolean, String> result = svc.unregisterUser("alice");

        assertFalse(result.first);
        assertNotNull(userRepo.getUserByUsername("alice"));
    }

    @Test
    void userWithUnpaidFinesCannotBeUnregistered() {
        UserService svc = setupService();
        UserRepository repo = svc.getUserRepository();

        User admin = new User("admin", "pwd","user@gmail.com",false);
        User user  = new User("alice", "pwd","user@gmail.com",false);
        repo.addUser(admin);
        repo.addUser(user);
        svc.setCurrentUser(admin);

        user.addFine(50);

        Pair<Boolean, String> result = svc.unregisterUser("alice");

        assertFalse(result.first);
        assertNotNull(repo.getUserByUsername("alice"));
    }
}
