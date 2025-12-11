package edu.software.lms;

import java.util.ArrayList;
import java.util.List;

public class InMemoryLoanRepository implements LoanRepository {
    private final List<Loan> loans = new ArrayList<>();

    @Override
    public Loan getLoanById(String id) {
        if (id == null) return null;
        return loans.stream().filter(l -> id.equals(l.getId())).findFirst().orElse(null);
    }

    @Override
    public List<Loan> getLoansByUserId(String userId) {
        if (userId == null) return new ArrayList<>();
        return loans.stream()
                .filter(l -> userId.equals(l.getUserId()))
                .toList();
    }

    @Override
    public List<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }

    @Override
    public boolean addLoan(Loan loan) {
        if (loan == null) return false;
        if (getLoanById(loan.getId()) != null) return false;
        loans.add(loan);
        return true;
    }

    @Override
    public void updateLoan(Loan loan) {
        if (loan == null) return;
        for (int i = 0; i < loans.size(); i++) {
            if (loans.get(i).getId().equals(loan.getId())) {
                loans.set(i, loan);
                return;
            }
        }
    }
}
