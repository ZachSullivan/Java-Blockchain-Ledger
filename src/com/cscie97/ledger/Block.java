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

    /**
     *  In order to create a new block, we need to provide a unique hash value for the PREVIOUS block.
     *  this hash value will be computed from each transaction in the block's list of transactions.
     * 
     *  To do this, ill need to produce a hashlist of all transaction hashes in the given block.
     *  (this could be done by passing in an argument of transaction.hash as a list to a merkle tree function)
     *  (In the merkle tree function) I'll then need to iterate through the hashlist param
     *  ill take a pair (every 2) hashList values and produce a new hash that will be appended to a parentHashList
     *  Note Ill keep iterating over the hashlist until I reach the end. 
     * (if I can't get a pair of 2 hashes, then reuse the same hash twice)
     *  Ill then recursively call the merkletree function, passing in the new parent list (repeating the previous step)
     *  Base case will be when the hashList param has a length of 1
     *  see: https://medium.com/@vinayprabhu19/merkel-tree-in-java-b45093c8c6bd
     */

    public Block (int id) {
        this.blockNumber = id;
    }

    public int getBlockNumber () {
        return this.blockNumber;
    }

    public List<Transaction> getTransactionList () {
        return this.transactionList;
    }

    public Transaction getTransaction (String transaction) {
        for (Transaction t:this.transactionList) {
            if (t.getTransactionId().equals(transaction)) {
                return t;
            }
        }
        return null;
    }

    public void addTransactionList (Transaction transaction) {
        this.transactionList.add(transaction);
    }

    public void setPrevHash (String hash) {
        this.previousHash = hash;
    }

    public String getPrevHash () {
        return this.previousHash;
    }

    public String getHash () {
        return this.hash;
    }

    public void setHash (String hash) {
        this.hash = hash;
    }
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
    public void addAccountBalanceMap (Account key, int value) {
        this.accountBalanceMap.put(key, value);
    }
}
