package com.cscie97.ledger;

public class Account {
    private String address; // Unique identifier for the account
    private int balance;   // Records total transfers + fees to and from account

    public Account (String accountId, int balance) {
        this.address = accountId;
        this.balance = balance;
    }

    public Account (String accountId) {
        this(accountId, 0);
    }

    public String getAddress () {
        return this.address;
    }

    public void setBalance (int amount) {
        this.balance = amount;
    }

    /*public void incrementBalance (long amount) {
        if ((this.balance + amount) <= Integer.MAX_VALUE) {
            this.balance += amount;
        }
    }

    public void decrementBalance (long amount) {
        if ((this.balance - amount) >= 0) {
            this.balance -= amount;
        }
    }*/

    public int getBalance () {
        return this.balance;
    }
}
