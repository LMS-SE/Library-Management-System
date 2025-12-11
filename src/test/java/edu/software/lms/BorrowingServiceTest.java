package edu.software.lms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BorrowingServiceTest {

    private UserRepository users;
    private BookRepository books;
    private LoanRepository loans;
    private BorrowingService service;

    @BeforeEach
    void init() {
        users = new InMemoryUserRepository();
        books = new InMemoryBooks();
        loans = new InMemoryLoanRepository();
        TimeProvider time = () -> LocalDate.of(2024, 1, 1); // ثابت للتست
        FineStrategy strategy = days -> days; // 1 NIS لكل يوم
        service = new BorrowingService(users, books, loans, time, strategy,null);
    }

    // ------------------ Borrow tests ----------------------

    @Test
    void testBorrowUserNotFound() {
        var r = service.borrowBook("XXX", 1);
        assertFalse(r.first);
    }

    @Test
    void testBorrowBookNotFound() {
        User u = new User("John", "123","john@gmail.com",false);
        u.setId("U1");
        users.addUser(u);

        var r = service.borrowBook("John", 999);
        assertFalse(r.first);
    }

    @Test
    void testBorrowBookAlreadyBorrowed() {
        User u = new User("John", "123","john@gmail.com",false);
        u.setId("U1");
        users.addUser(u);

        Book b = new Book(1, "A", "B", "ISBN1");
        b.setBorrowed(true);   // الكتاب مستعار
        books.addBook(b);

        var r = service.borrowBook("John", 1);
        assertFalse(r.first);
    }

    @Test
    void testSuccessfulBorrow() {
        User u = new User("John", "123","john@gmail.com",false);
        u.setId("U1");
        users.addUser(u);

        books.addBook(new Book(1, "A", "B", "ISBN1"));

        var r = service.borrowBook("John", 1);
        assertTrue(r.first);
    }

    // ------------------ Return tests ----------------------

    @Test
    void testReturnLoanNotFound() {
        var r = service.returnBook("NOPE");
        assertFalse(r.first);
    }

    @Test
    void testReturnAlreadyReturned() {
        User u = new User("John", "123","john@gmail.com",false);
        u.setId("U1");
        users.addUser(u);

        Book b = new Book(1, "A", "B", "ISBN1");
        books.addBook(b);

        // Borrow manually
        Loan loan = new Loan(u.getId(), 1,
                LocalDate.of(2024,1,1),
                LocalDate.of(2024,1,29));

        loans.addLoan(loan);

        // اجعل الكتاب كأنه رجع سابقًا
        loan.setReturnedDate(LocalDate.of(2024,1,10));
        loans.updateLoan(loan);

        var r = service.returnBook(loan.getId());
        assertFalse(r.first); // لأنه رجع سابقًا
    }

    // ------------------ Payment tests ----------------------

    @Test
    void testPayFineUserNotFound() {
        var r = service.payFine("UNKNOWN", 50);
        assertFalse(r.first);
    }

    @Test
    void testPayFineInvalidAmount() {
        User u = new User("John", "123","john@gmail.com",false);
        users.addUser(u);

        var r = service.payFine("John", -10);
        assertFalse(r.first);
    }

    @Test
    void testPayFineSuccess() {
        User u = new User("John", "123","john@gmail.com",false);
        u.setId("U1");
        users.addUser(u);

        u.addFine(50);

        var r = service.payFine("John", 20);
        assertTrue(r.first);
        assertEquals(30, u.getFineBalance());
    }
}
