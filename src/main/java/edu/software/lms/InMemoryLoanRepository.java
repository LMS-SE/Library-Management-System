package edu.software.lms;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        return loans.stream().filter(l -> userId.equals(l.getUserId())).collect(Collectors.toList());
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
    public boolean updateLoan(Loan loan) {
        if (loan == null) return false;
        for (int i = 0; i < loans.size(); i++) {
            if (loans.get(i).getId().equals(loan.getId())) {
                loans.set(i, loan);
                return true;
            }
        }
        return false;
    }
}
