package com.cscie97.ledger;

import java.util.*; 

public class Ledger {
    private String name;
    private String description;
    private String seed;

    private List<Account> accountList = new ArrayList<Account>();  
    private Block genesisBlock; // Initial blockchain block
    private Map <Integer, Block> blockMap = new HashMap <Integer, Block> ();    // Map of blocks with their associated ID values

    public Ledger (String name, String desc, String seed) {
        this.name = name;
        this.description = desc;
        this.seed = seed;

        this.genesisBlock = new Block(0, null, "harvard");
    }
    /**
     * Creates new account with unique ID and balance of 0.
     * @param  accountId the unique account identifier
     * @return unique account ID
     * TODO: may need to add an @Exception... 
     */
    public String createAccount (String accountId) {
        return "null";
    }
    /**
     * Validate and process a transaction, adding to current block and updating balances if valid.
     * @param transaction   Transaction to be processed and validated
     * @return transactionId   Unique identifier for the transaction.
     */
    public String processTransaction (Transaction transaction) {
        return "transactionID";
    }

    /**
     * Retrieve account balance for a given account via account address (via most recent block).
     * @param address   Address of the given account
     * @return balance  The balance of the given account
     */
    public long getAccountBalance (String address) {
        // Todo: correct return value
        return 0;
    }

    /**
    public Map <Integer, Block> getAccountBalances () {
        //return Map <Integer, Block> 
    }
    */

    /**
     * Retreives a block given a specific block number
     * @param blockNumber   The identifier of the block to be retreived
     * @return block    The resulting block given the queried id
     */
    /*public Block getBlock (long blockNumber) {
        return new Block();
    }*/
    
    /**
     * Retreives a transaction given a specific transaction number
     * @param transactionId   The identifier of the transaction to be retreived
     * @return transaction    The resulting transaction given the queried id
     
    public Transaction getTransaction (String transactionId) {
        return new Transaction();
    }
    */

    /**
     * Valdiate current blockchain state, max of 10 transactions per block and balances total to max value
     */
    public void validate () {

    }
}

