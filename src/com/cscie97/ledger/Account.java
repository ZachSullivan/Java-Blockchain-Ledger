package com.cscie97.ledger;

public class Account {
    private String address; // Unique identifier for the account
    private int balance;    // Records total transfers + fees to and from account

    public Account (String accountId, int balance) {
        this.address = accountId;
        this.balance = balance;
    }

    public Account (String accountId) {
        this(accountId, 0);
    }

    /**
     * Returns this account object's unique identifier.
     * @return The unique idenfitier for this account
     */
    public String getAddress () {
        return this.address;
    }

    /**
     * Updates the balance of the account object.
     * @param amount    The value of the account object's new balance
     */
    public void setBalance (int amount) {
        this.balance = amount;
    }

    /**
     * Retrieves the balance of the account object.
     * @return The account's balance as an integer
     */
    public int getBalance () {
        return this.balance;
    }

    public String toString() { 
        String result = (this.getAddress() + "'s account"); 
        return result;
    } 
}
