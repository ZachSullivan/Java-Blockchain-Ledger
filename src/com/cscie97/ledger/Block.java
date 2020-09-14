package com.cscie97.ledger;

import java.util.*;

/**
* The Block class, is an individual block within the Ledger Service.
* Each block is created and maintained by the ledger service, 
* and contains information regarding up to 10 transactions between accounts
* Each block has:
* - an block number providing a unique identity.
* - a previous hash providing the hash of the previous block in the block chain.
* - a hash of the this block in the block chain.
* - a list of transaction objects (max of 10 per block)
* - a mapping of accounts and their balances for this block
* - a previous block providing a reference to the block preceeding this block
*
* @author  Zachary Sullivan
* @since   2020-09-13 
*/
public class Block {
    private int blockNumber;        // Unique identifier for the block
    private String previousHash;    // Hash value of the previous block
    private String hash;            // Hash value for this block

    // List of all valid transactions for this block
    private List<Transaction> transactionList = new ArrayList<Transaction>(); 

    // A map containing all accounts as keys and their corresponding balance as values
    private Map <Account, Integer> accountBalanceMap = new HashMap <Account, Integer> ();

    private Block previousBlock;    // Block object preceeding this block

    public Block (int id) {
        this.blockNumber = id;
    }

    /**
     * Retrieve the unique identifier for this block
     * @return This block's unique identifier
     */
    public int getBlockNumber () {
        return this.blockNumber;
    }

    /**
     * Retrieve the list of transactions to occur within the block
     * @return The list of transactions within this block
     */
    public List<Transaction> getTransactionList () {
        return this.transactionList;
    }

    /**
     * Retrieve a specific Transaction object from the block's list of transactions (search by id)
     * @param transaction   The unique identifier of the requested Transaction object
     * @return  The requested Transaction object
     */
    public Transaction getTransaction (String transaction) {
        for (Transaction t:this.transactionList) {
            if (t.getTransactionId().equals(transaction)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Add a new Transaction object to the block's list of transactions
     * @param transaction   The new Transaction object to be added
     */
    public void addTransactionList (Transaction transaction) {
        this.transactionList.add(transaction);
    }

    /**
     * Store the previous Block object as a record in this block
     * @param block     Block object of the previous block
     */
    public void setPrevBlock (Block block) {
        this.previousBlock = block;
    }

    /**
     * Retrieve the block preceeding the this block
     * @return the Block object preceeding this block
     */
    public Block getPrevBlock () {
        return this.previousBlock;
    }

    /**
     * Store the previous block's hash value
     */
    public void setPrevHash (String hash) {
        this.previousHash = hash;
    }

    /**
     * Retrieve the previous block's hash value
     * @return The previous block's hash value
     */
    public String getPrevHash () {
        return this.previousHash;
    }
    
    /**
     * Retrieve this block's hash value
     * @return This block's hash value
     */
    public String getHash () {
        return this.hash;
    }

    /**
     * Store this block's hash value
     * @param hash  The hash value of this block
     */
    public void setHash (String hash) {
        this.hash = hash;
    }

    /**
     * Returns the mapping of all accounts and their associated balances for the given block
     * @return The requested account balance map
     */
    public Map <Account, Integer> getAccountBalanceMap () {
        return this.accountBalanceMap;
    }

    /**
     * Adds a new account balance map to the private map variable
     * @param account The account to be added
     * @param balance The account's balanced to be added
     */
    public void addAccountBalanceMap (Account key, int value) {
        this.accountBalanceMap.put(key, value);
    }

    public String toString() { 
        String result = (
            "Block " + this.getBlockNumber() + 
            " hash: " + this.getHash() + 
            " Transaction List: " + this.getTransactionList()
        ); 
        return result;
    } 
}
