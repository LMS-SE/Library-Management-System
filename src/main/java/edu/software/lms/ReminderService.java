package edu.software.lms;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReminderService implements Subject {

    private final LoanRepository loanRepo;
    private final TimeProvider timeProvider;
    private final UserRepository userRepo;
    private final List<Observer> observers = new ArrayList<>();

    public ReminderService(LoanRepository loanRepo, TimeProvider timeProvider,UserRepository userRepo) {
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

    public void sendOverdueNotifications() {
        LocalDate today = timeProvider.today();

        Map<User, Long> overdueCountByUser = new HashMap<>();

        for (Loan loan : loanRepo.getAllLoans()) {
            if (loan.isOverdue(today)) {
                overdueCountByUser.merge(userRepo.getUserById(loan.getUserId()), 1L, Long::sum);
            }
        }

        for (Map.Entry<User, Long> entry : overdueCountByUser.entrySet()) {
            User user = entry.getKey();
            Long count = entry.getValue();
            String msg = "You have " + count + " overdue book(s).";
            notifyObservers(user, msg);
        }
    }
}
