package model;

/**
 * This represents the user's "Wallet."
 * Encapsulation ensures the balance cannot be modified
 * without going through business logic checks.
 */
public class Account {
    private Double balance;

    public Account(Double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.balance = initialBalance;
    }

    public void addFunds(Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.balance += amount;
    }

    public void deductFunds(Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Deduction amount must be positive");
        }
        if (this.balance >= amount) {
            this.balance -= amount;
        }
    }

    public Double getBalance() {
        return balance;
    }
}