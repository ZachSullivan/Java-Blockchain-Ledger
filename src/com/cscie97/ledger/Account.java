package com.cscie97.ledger;

public class Account {
    private String address; // Unique identifier for the account
    private long balance;   // Records total transfers + fees to and from account

    public Account (String accountId) {
        this.address = accountId;
        this.balance = 0;
    }

    public String getAddress () {
        return this.address;
    }

    public void setBalance (long amount) {
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

    public long getBalance () {
        return this.balance;
    }
}
