package edu.software.lms;

import java.util.List;

/**
 * Repository interface for managing {@link Loan} data storage.
 *
 * <p>Different implementations (in-memory, database-backed, etc.)
 * may store and retrieve loans differently.</p>
 */
public interface LoanRepository {

    /**
     * Retrieves a loan by its ID.
     *
     * @param id loan ID
     * @return matching loan or null if not found
     */
    Loan getLoanById(String id);

    /**
     * Retrieves all loans belonging to a specific user.
     *
     * @param userId user ID
     * @return list of loans; empty list if none exist
     */
    List<Loan> getLoansByUserId(String userId);

    /**
     * @return all loans in the system
     */
    List<Loan> getAllLoans();

    /**
     * Adds a loan to the repository.
     *
     * @param loan the loan to add
     * @return true if added successfully, false otherwise
     */
    boolean addLoan(Loan loan);

    /**
     * Updates the state of an existing loan.
     *
     * @param loan updated loan object
     */
    void updateLoan(Loan loan);
}
