package com.cscie97.ledger;

import java.util.*; 

public class Ledger {
    private String name;
    private String description;
    private String seed;

    // Citation: Thank you "Anonymous Gear" on piazza for the inspiration for a temp block to write to.
    private Block currentBlock; // Block currently being written to, once transaction limit is reached write this block to the block map and start a new currentBlock

    private List<Account> accountList;
    private Block genesisBlock; // Initial blockchain block
    private Map <Integer, Block> blockMap = new HashMap <Integer, Block> ();    // Map of blocks with their associated ID values

    public Ledger (String name, String description, String seed) {
        this.name = name;
        this.description = description;
        this.seed = seed;

        this.accountList = new ArrayList<Account>();
        this.genesisBlock = new Block(1, null, "harvard");
        this.currentBlock = this.genesisBlock;
        this.blockMap.put(this.genesisBlock.getBlockNumber(), this.genesisBlock);
        try {
            this.createAccount("master");
        } catch (LedgerException e) {
            e.printStackTrace();
        }
    }

    // NOTE THIS MAY NOT BE NEEDED
    /**
     * Returns the block currently being updated with new transactions
     * @return this.currentBlock The temp block yet to be saved to the block map
     */
    public Block getCurrentBlock () {
        return this.currentBlock;
    }

    public Map <Integer, Block> getBlockMap () {
        return this.blockMap;
    }

    public List<Account> getAccounts () {
        return this.accountList;
    }

    public void addAccount (Account account) {
        this.accountList.add(account);
    }

    public Account getAccount (String accountId) {
        System.out.println(accountId);
        for (Account a:this.accountList) {

            // Return when we find a matching account id
            if (a.getAddress().equals(accountId)) {
                return a;
            }
        }
        return null;
    }


    /**
     * Creates new account with unique ID and balance of 0.
     * @param  accountId the unique account identifier
     * @return unique account ID
     * TODO: may need to add an @Exception... 
     */
    public String createAccount (String accountId) throws LedgerException {
        Account newAccount;
        // Check if account already exists with that accountID
        if (this.accountList.isEmpty() == true) {
            newAccount = new Account(accountId, Integer.MAX_VALUE);
        } else {
            for (Account a:this.accountList) {

                // If the provided account name is already used, return exception.
                if (a.getAddress().equals(accountId)) {
                    throw new LedgerException (
                        "Ledger", "Failed while creating new account, ID already used"
                    );
                }
            }
            // Create new account, add it to the ledger account list
            newAccount = new Account(accountId);
        }
        this.addAccount(newAccount);

        // Obtain balance of new account
        // Append new account and balance to current block's accountBalanceMap
        int balance = newAccount.getBalance();
        this.currentBlock.addAccountBalanceMap(newAccount, balance);
        return accountId;
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
     * Returns the mapping of all accounts and their associated balances for the given block
     * @return this.accountBalanceMap   The account balance map requested
     */
    public Map <Account, Integer> getAccountBalances () {
        return this.currentBlock.getAccountBalanceMap();
    }
    

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

