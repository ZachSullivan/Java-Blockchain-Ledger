package com.cscie97.ledger;

import java.util.*;

public class Block {
    private long blockNumber;
    private String previousHash;
    private String hash;

    //private List<Transaction> transactionList = Arrays.asList(new Transaction[10]); // remove magic number
    // A map containing each account and it's balance (for each account referenced in this block)
    private Map <Integer, Account> accountBalanceMap = new HashMap <Integer, Account> ();

    private Block previousBlock;

    public Block (long id, String prevHash, String hashVal) {
        this.blockNumber = id;
        this.previousHash = prevHash;
        this.hash = hashVal;
    }
}
