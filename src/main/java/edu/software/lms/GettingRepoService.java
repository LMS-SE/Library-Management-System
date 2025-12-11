package edu.software.lms;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GettingRepoService {

    private GettingRepoService() {}

    public static BookRepository resolveRepoFromUserServiceOrFallback(UserService userService) {
        if (userService == null) return new InMemoryBooks();
        try {
            Method m = userService.getClass().getMethod("getBookRepository");
            Object repoObj = m.invoke(userService);
            if (repoObj instanceof BookRepository) return (BookRepository) repoObj;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}
        return new InMemoryBooks();
    }

    public static LoanRepository resolveLoanRepoFromUserServiceOrFallback(UserService userService) {
        if (userService == null) return new InMemoryLoanRepository();
        try {
            Method m = userService.getClass().getMethod("getLoanRepository");
            Object repoObj = m.invoke(userService);
            if (repoObj instanceof LoanRepository) return (LoanRepository) repoObj;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}
        return new InMemoryLoanRepository();
    }
}
