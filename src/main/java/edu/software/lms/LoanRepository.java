package edu.software.lms;

public interface LoanRepository {
    Loan getLoanById(String id);
    java.util.List<Loan> getLoansByUserId(String userId);
    java.util.List<Loan> getAllLoans();
    boolean addLoan(Loan loan);
    void updateLoan(Loan loan);
}
