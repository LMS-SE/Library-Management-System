package edu.software.lms;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    private String username;
    private String password;
    private String id;
    private int fineBalance; // NIS
    private List<String> loanIds;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.id = username;
        this.fineBalance = 0;
        this.loanIds = new ArrayList<>();
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public boolean checkPassword(String password) { return this.password.equals(password); }

    public int getFineBalance() { return fineBalance; }
    public void addFine(int amount) { this.fineBalance += amount; }
    public void payFine(int amount) {
        if (amount <= 0) return;
        this.fineBalance = Math.max(0, this.fineBalance - amount);
    }

    public List<String> getLoanIds() { return loanIds; }
    public void addLoanId(String loanId) { if (loanId != null && !loanIds.contains(loanId)) loanIds.add(loanId); }
    public void removeLoanId(String loanId) { loanIds.remove(loanId); }

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
