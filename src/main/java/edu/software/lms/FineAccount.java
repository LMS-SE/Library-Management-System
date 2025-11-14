package edu.software.lms;

public class FineAccount {

    private double balance;

    public FineAccount() {
        this.balance = 0;
    }

    public void addFine(double amount) {
        this.balance += amount;
    }

    public boolean payFine(double amount) {
        if (amount <= 0 || amount > balance) return false;
        balance -= amount;
        return true;
    }

    public double getBalance() {
        return balance;
    }
}
