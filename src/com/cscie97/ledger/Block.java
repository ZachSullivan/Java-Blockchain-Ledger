package com.cscie97.ledger;

import java.util.*;

public class Block {
    private int blockNumber;
    private String previousHash;
    private String hash;

    //private List<Transaction> transactionList = Arrays.asList(new Transaction[10]); // remove magic number
    private List<Transaction> transactionList = new ArrayList<Transaction>(); // remove magic number

    // A map containing all accounts and their corresponding balance
    //private Map <Account, Integer> accountBalanceMap = new HashMap <Account, Integer> ();
    private Map <Account, Integer> accountBalanceMap = new HashMap <Account, Integer> ();

    private Block previousBlock;

    public Block (int id, String prevHash, String hashVal) {
        this.blockNumber = id;
        this.previousHash = prevHash;
        this.hash = hashVal;
    }

    public int getBlockNumber () {
        return this.blockNumber;
    }

    public List<Transaction> getTransactionList () {
        return this.transactionList;
    }

    public void addTransactionList (Transaction transaction) {
        this.transactionList.add(transaction);
    }

    // NOTE I MAY NOT NEED THIS GETTER
    /**
     * Returns the mapping of all accounts and their associated balances for the given block
     * @return this.accountBalanceMap   The account balance map requested
     */
    public Map <Account, Integer> getAccountBalanceMap () {
        return this.accountBalanceMap;
    }

    /**
     * Adds a new account balance map to the private map variable
     * @param account The account to be added
     * @param balance The account's balanced to be added
     * @return return 0 on success, 1 on failure
     */
    public int addAccountBalanceMap (Account account, int balance) {
        this.accountBalanceMap.put(account, balance);
        return 0;
    }
}
