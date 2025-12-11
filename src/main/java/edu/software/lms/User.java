package edu.software.lms;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a user in the library system.
 *
 * <p>Users may be administrators or regular borrowers. Each user maintains
 * fine balance information and a list of active loan IDs.</p>
 */
public class User {

    private String username;
    private String password;
    private String id;
    private int fineBalance;
    private String email;
    private boolean isAdmin;

    /** List of loan IDs associated with this user. */
    private final List<String> loanIds;

    /**
     * Constructs a new user.
     *
     * @param username chosen username
     * @param password hashed password
     * @param email    email address
     * @param isAdmin  whether this user is an administrator
     */
    public User(String username, String password, String email, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.id = username; // user ID defaults to username
        this.fineBalance = 0;
        this.loanIds = new ArrayList<>();
        this.email = email;
        this.isAdmin = isAdmin;
    }

    // ------------------- Getters / Setters -------------------

    /** @return username of this user */
    public String getUsername() { return username; }

    /** Updates the username. */
    public void setUsername(String username) { this.username = username; }

    /** @return hashed password */
    public String getPassword() { return password; }

    /** Updates the password (already hashed). */
    public void setPassword(String password) { this.password = password; }

    /** @return user ID */
    public String getId() { return id; }

    /** Updates the user ID. */
    public void setId(String id) { this.id = id; }

    /** @return user’s email address */
    public String getEmail() { return email; }

    /** Updates the email address. */
    public void setEmail(String email) { this.email = email; }

    /** @return true if the user is an administrator */
    public boolean isAdmin() { return isAdmin; }

    /** Sets admin status. */
    public void setAdmin(boolean admin) { isAdmin = admin; }

    // ------------------- Fine Handling -------------------

    /**
     * @return current unpaid fine balance
     */
    public int getFineBalance() { return fineBalance; }

    /**
     * Adds a fine amount to the user's balance.
     *
     * @param amount amount to add (may not be negative)
     */
    public void addFine(int amount) { this.fineBalance += amount; }

    /**
     * Pays part or all of the user's fine balance.
     *
     * @param amount amount to deduct; ignored if not positive
     */
    public void payFine(int amount) {
        if (amount <= 0) return;
        this.fineBalance = Math.max(0, this.fineBalance - amount);
    }

    // ------------------- Loan Handling -------------------

    /**
     * @return list of IDs of loans associated with this user
     */
    public List<String> getLoanIds() { return loanIds; }

    /**
     * Adds a loan ID to this user.
     *
     * @param loanId ID of loan
     */
    public void addLoanId(String loanId) {
        if (loanId != null && !loanIds.contains(loanId)) {
            loanIds.add(loanId);
        }
    }

    /**
     * Removes a loan ID from the user.
     *
     * @param loanId ID to remove
     */
    public void removeLoanId(String loanId) {
        loanIds.remove(loanId);
    }

    // ------------------- Utility -------------------

    /** Checks if the provided password matches this user's stored password. */
    public boolean checkPassword(String password) { return this.password.equals(password); }

    @Override
    public String toString() {
        return "User{id='" + id + "', username='" + username + "', fineBalance=" + fineBalance + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getUsername(), user.getUsername()) &&
                Objects.equals(getPassword(), user.getPassword()) &&
                Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getUsername());
        result = 31 * result + Objects.hashCode(getPassword());
        result = 31 * result + Objects.hashCode(getId());
        return result;
    }
}
