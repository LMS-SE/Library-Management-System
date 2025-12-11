package edu.software.lms;

import java.time.LocalDate;
import java.util.*;

/**
 * Service responsible for scanning loans for overdue items and
 * notifying all registered observers.
 *
 * <p>Implements the observer pattern via the {@link Subject} interface.</p>
 */
public class ReminderService implements Subject {

    private final LoanRepository loanRepo;
    private final TimeProvider timeProvider;
    private final UserRepository userRepo;
    private final List<Observer> observers = new ArrayList<>();

    /**
     * Constructs a new reminder service.
     *
     * @param loanRepo     repository containing loan records
     * @param timeProvider time provider for computing overdue loans
     * @param userRepo     repository of users for resolving usernames
     */
    public ReminderService(LoanRepository loanRepo,
                           TimeProvider timeProvider,
                           UserRepository userRepo) {
        this.loanRepo = loanRepo;
        this.timeProvider = timeProvider;
        this.userRepo = userRepo;
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(User user, String message) {
        for (Observer obs : observers) {
            obs.notify(user, message);
        }
    }

    /**
     * Scans all loans for overdue items and sends aggregated notifications
     * to each user.
     *
     * <p>Each user receives one notification indicating the number
     * of overdue items they have.</p>
     */
    public void sendOverdueNotifications() {

        LocalDate today = timeProvider.today();
        Map<User, Long> overdueCountByUser = new HashMap<>();

        for (Loan loan : loanRepo.getAllLoans()) {
            if (loan.isOverdue(today)) {
                User u = userRepo.getUserById(loan.getUserId());
                overdueCountByUser.merge(u, 1L, Long::sum);
            }
        }

        for (Map.Entry<User, Long> entry : overdueCountByUser.entrySet()) {
            User user = entry.getKey();
            long count = entry.getValue();
            String msg = "You have " + count + " overdue book(s).";
            notifyObservers(user, msg);
        }
    }
}
