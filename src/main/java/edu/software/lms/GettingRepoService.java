package edu.software.lms;

public class GettingRepoService {
    public static BookRepository resolveRepoFromUserServiceOrFallback(UserService userService) {
        if (userService == null) return new InMemoryBooks();
        try {
            var m = userService.getClass().getMethod("getBookRepository");
            Object repoObj = m.invoke(userService);
            if (repoObj instanceof BookRepository) return (BookRepository) repoObj;
        } catch (Exception ignored) { }
        return new InMemoryBooks();
    }

    public static LoanRepository resolveLoanRepoFromUserServiceOrFallback(UserService userService) {
        if (userService == null) return new InMemoryLoanRepository();
        try {
            var m = userService.getClass().getMethod("getLoanRepository");
            Object repoObj = m.invoke(userService);
            if (repoObj instanceof LoanRepository) return (LoanRepository) repoObj;
        } catch (Exception ignored) { }
        return new InMemoryLoanRepository();
    }
}
