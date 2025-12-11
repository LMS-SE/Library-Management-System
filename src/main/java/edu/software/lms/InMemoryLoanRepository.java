package edu.software.lms;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory implementation of the {@link LoanRepository} interface.
 * Stores all loans inside an internal list.
 *
 * <p>This implementation is suitable for testing and non-persistent runtime usage.
 * All methods operate directly on the internal list.</p>
 */
public class InMemoryLoanRepository implements LoanRepository {

    /** Internal list storing all loans. */
    private final List<Loan> loans = new ArrayList<>();

    /**
     * Retrieves a loan by its unique ID.
     *
     * @param id the loan ID to search for
     * @return matching {@link Loan}, or {@code null} if no loan exists with that ID
     */
    @Override
    public Loan getLoanById(String id) {
        if (id == null) return null;

        return loans.stream()
                .filter(l -> id.equals(l.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all loans associated with a given user ID.
     *
     * @param userId ID of the user
     * @return list of loans; empty list if user has no loans or ID is null
     */
    @Override
    public List<Loan> getLoansByUserId(String userId) {
        if (userId == null) return new ArrayList<>();

        return loans.stream()
                .filter(l -> userId.equals(l.getUserId()))
                .toList();
    }

    /**
     * Returns a copy of all loans stored in the repository.
     *
     * @return list containing all loans
     */
    @Override
    public List<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }

    /**
     * Adds a new loan to the repository.
     * <p>Fails if:</p>
     * <ul>
     *     <li>The loan is null</li>
     *     <li>A loan already exists with the same ID</li>
     * </ul>
     *
     * @param loan the loan to insert
     * @return true if successfully added, false otherwise
     */
    @Override
    public boolean addLoan(Loan loan) {
        if (loan == null) return false;

        if (getLoanById(loan.getId()) != null)
            return false;

        loans.add(loan);
        return true;
    }

    /**
     * Updates an existing loan entry.
     * <p>This method finds the loan with the same ID and replaces it.</p>
     *
     * @param loan loan object with updated state; ignored if null
     */
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
