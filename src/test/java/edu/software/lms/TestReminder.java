package edu.software.lms;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

class TestReminder {
    @Test
    void testReminderNotification() {
        // Arrange
        LoanRepository repo = mock(LoanRepository.class);

        // Fix a stable "today"
        LocalDate today = LocalDate.now();
        TimeProvider tp = () -> today;  // or new MockTimeProvider(today)

        UserRepository userRepository = new InMemoryUserRepository();
        User u = new User("user", "pass", "user@uni.com",false);
        userRepository.addUser(u);

        ReminderService reminder = new ReminderService(repo, tp, userRepository);
        Observer email = mock(Observer.class);
        reminder.addObserver(email);

        // This loan is OVERDUE:
        // borrow 30 days ago, due 2 days ago
        Loan loan = new Loan(
                u.getId(),
                1,
                today.minusDays(30),
                today.minusDays(2)
        );

        when(repo.getAllLoans()).thenReturn(List.of(loan));

        // Act
        reminder.sendOverdueNotifications();

        // Assert
        verify(email).notify(eq(u), eq("You have 1 overdue book(s)."));
    }
    @Test
    void noOverdueLoans_noNotificationSent() {
        // Arrange
        LoanRepository repo = mock(LoanRepository.class);

        LocalDate today = LocalDate.now();
        TimeProvider tp = () -> today;

        UserRepository userRepository = new InMemoryUserRepository();
        User u = new User("user1", "pass", "user1@uni.com",false);
        userRepository.addUser(u);

        ReminderService reminder = new ReminderService(repo, tp, userRepository);
        Observer email = mock(Observer.class);
        reminder.addObserver(email);

        // Not overdue: due in future
        Loan loan = new Loan(
                u.getId(),
                1,
                today.minusDays(5),
                today.plusDays(3)
        );

        when(repo.getAllLoans()).thenReturn(List.of(loan));

        // Act
        reminder.sendOverdueNotifications();

        // Assert
        verifyNoInteractions(email);
    }
    @Test
    void multipleOverdueLoansForSameUser_singleNotificationWithCorrectCount() {
        // Arrange
        LoanRepository repo = mock(LoanRepository.class);

        LocalDate today = LocalDate.now();
        TimeProvider tp = () -> today;

        UserRepository userRepository = new InMemoryUserRepository();
        User u = new User("user2", "pass", "user2@uni.com",false);
        userRepository.addUser(u);

        ReminderService reminder = new ReminderService(repo, tp, userRepository);
        Observer email = mock(Observer.class);
        reminder.addObserver(email);

        Loan loan1 = new Loan(
                u.getId(),
                1,
                today.minusDays(30),
                today.minusDays(5)   // overdue
        );
        Loan loan2 = new Loan(
                u.getId(),
                2,
                today.minusDays(10),
                today.minusDays(1)   // also overdue
        );

        when(repo.getAllLoans()).thenReturn(List.of(loan1, loan2));

        // Act
        reminder.sendOverdueNotifications();

        // Assert
        verify(email).notify(eq(u), eq("You have 2 overdue book(s)."));
        verifyNoMoreInteractions(email);
    }

    @Test
    void overdueLoansForMultipleUsers_eachUserGetsSeparateNotification() {
        // Arrange
        LoanRepository repo = mock(LoanRepository.class);

        LocalDate today = LocalDate.now();
        TimeProvider tp = () -> today;

        UserRepository userRepository = new InMemoryUserRepository();
        User u1 = new User("userA", "pass", "userA@uni.com",false);
        User u2 = new User("userB", "pass", "userB@uni.com",false);
        userRepository.addUser(u1);
        userRepository.addUser(u2);

        ReminderService reminder = new ReminderService(repo, tp, userRepository);
        Observer email = mock(Observer.class);
        reminder.addObserver(email);

        Loan loan1 = new Loan(
                u1.getId(),
                1,
                today.minusDays(20),
                today.minusDays(2)   // overdue for userA
        );
        Loan loan2 = new Loan(
                u2.getId(),
                2,
                today.minusDays(15),
                today.minusDays(3)   // overdue for userB
        );

        when(repo.getAllLoans()).thenReturn(List.of(loan1, loan2));

        // Act
        reminder.sendOverdueNotifications();

        // Assert
        verify(email).notify(eq(u1), eq("You have 1 overdue book(s)."));
        verify(email).notify(eq(u2), eq("You have 1 overdue book(s)."));
        verifyNoMoreInteractions(email);
    }

    @Test
    void removedObserver_doesNotReceiveNotifications() {
        // Arrange
        LoanRepository repo = mock(LoanRepository.class);

        LocalDate today = LocalDate.now();
        TimeProvider tp = () -> today;

        UserRepository userRepository = new InMemoryUserRepository();
        User u = new User("userC", "pass", "userC@uni.com",false);
        userRepository.addUser(u);

        ReminderService reminder = new ReminderService(repo, tp, userRepository);
        Observer email = mock(Observer.class);
        reminder.addObserver(email);
        reminder.removeObserver(email);

        Loan loan = new Loan(
                u.getId(),
                1,
                today.minusDays(25),
                today.minusDays(1)   // overdue
        );

        when(repo.getAllLoans()).thenReturn(List.of(loan));

        // Act
        reminder.sendOverdueNotifications();

        // Assert
        verifyNoInteractions(email);
    }
}
