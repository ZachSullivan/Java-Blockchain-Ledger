package com.cscie97.ledger;

import java.util.*; 
import java.util.Map.Entry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

/**
* The Ledger class manages the blocks of the blockchain, as well as the accounts and transactions.
* The ledger can:
* - process and validate transactions
* - create and reteieve accounts, transactions, blocks and account balances/maps
* - validate the state of the blockchain
*
* @author  Zachary Sullivan
* @since   2020-09-13 
*/
public class Ledger {
    private String name;
    private String description;
    private String seed;

    // Citation: Thank you "Anonymous Gear" on class piazza for the inspiration for a temp block to write to.
    // Block currently being written to
    // .. once transaction limit is reached write this block to the block map and start a new currentBlock
    private Block currentBlock;

    // Initial blockchain block
    private Block genesisBlock;

    // Map of blocks with their associated ID values
    private NavigableMap <Integer, Block> blockMap = new TreeMap <Integer, Block> ();
    
    public Ledger (String name, String description, String seed) {
        this.name = name;
        this.description = description;
        this.seed = seed;

        this.genesisBlock = new Block(1);
        this.currentBlock = this.genesisBlock;

        try {
            this.createAccount("master");
        } catch (LedgerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the identifier for the ledger
     * @return the ledger's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Retrieves the ledger's descripton
     * @return the ledger's description
     */
    public String getDescription () {
        return this.description;
    }

    /**
     * Retrieves the ledger's mapping of blocks to block ids
     * @return the ledger's mapping of blocks
     */
    public Map <Integer, Block> getBlockMap () {
        return this.blockMap;
    }
    
    /**
     * Retrieves a specficied account via unique id
     * @param accountId String value of requested account
     * @return  The requested account object
     * @throws LedgerException
     */
    public Account getAccount (String accountId) throws LedgerException {
        if (accountId == null) {
            throw new LedgerException (
                "Ledger", "Failed while getting account, ID null"
            );
        }
        for (Account a:this.currentBlock.getAccountBalanceMap().keySet()) {
            // Return account that matches account id
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
     * @throws LedgerException
     */
    public String createAccount (String accountId) throws LedgerException {
        Account newAccount = null;
        // Check if account already exists with that accountID
        if (this.currentBlock.getAccountBalanceMap() == null || this.currentBlock.getAccountBalanceMap().isEmpty() == true) {
            newAccount = new Account(accountId, Integer.MAX_VALUE);
        } else {
            for (Account a:this.currentBlock.getAccountBalanceMap().keySet()) {

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
     * @throws LedgerException
     */
    public String processTransaction (Transaction transaction) throws LedgerException {

        Account payerAcc = this.getAccount(transaction.getPayer());
        Account recieverAcc = this.getAccount(transaction.getReciever());
        Account masterAcc = this.getAccount("master");

        if (masterAcc == null || payerAcc == null || recieverAcc == null) {
            throw new LedgerException (
                "Ledger", "Unable to get account, account doesn't exist."
            );
        }

        // Verify the unique id
        List<Transaction> transactionList = this.currentBlock.getTransactionList();

        if (transactionList != null) {
            for (Transaction t: transactionList) {
                
                if (t.getTransactionId().equals(transaction.getTransactionId())) {
                    throw new LedgerException (
                        "Ledger", "Failed validating transaction, ID already used"
                    );
                }
            }
        } else {
            throw new LedgerException (
                "Ledger", "Failed validating transaction, null transaction list"
            );
        }

        // Verify the payer and reciever accounts exist, and amount does not exceed payer's balance
        if (this.getAccount(transaction.getPayer()).getBalance() < transaction.getAmount()) {
            throw new LedgerException (
                "Ledger", "Failed validating transaction, amount exceeds payere's account balance"
            );
        }

        // Precalculate transaction from payer, taking into account fee
        int newBalance = (
            (recieverAcc.getBalance() - transaction.getAmount()) 
            //(this.getAccount(transaction.getReciever()).getBalance() - transaction.getAmount()) 
            + transaction.getFee()
        );
        
        if (newBalance > Integer.MAX_VALUE) {
            throw new LedgerException (
                "Ledger", "Failed validating transaction, fee exceeds reciever's maximum account balance"
            );
        }

        // Verify the amount is not less than 0 or greater than max int
        if ((transaction.getAmount() < 0) || (transaction.getAmount() > Integer.MAX_VALUE)) {
            throw new LedgerException (
                "Ledger", "Failed validating transaction, amount exceeds boundaries"
            );
        }

        // Verify the fee is not less than 0 or greater than 10 
        if (transaction.getFee() < transaction.MAX_FEE) {
            throw new LedgerException (
                "Ledger", "Failed validating transaction, minimum fee not provided"
            );
        }

        // Verify the note is not longer than 1024 chars
        String note = transaction.getNote();
        if (note.length() > transaction.MAX_NOTE_LEN) {
            throw new LedgerException (
                "Ledger", "Failed validating transaction, note exceeds char limit"
            );
        }

        // Transfer has been validated, add to transaction list
        this.currentBlock.addTransactionList(transaction);

        // Update account balances for master, payer and reciever
        int payerOldBalance = payerAcc.getBalance();
        int payerNewBalance = (payerOldBalance - transaction.getAmount()) - transaction.getFee();
        payerAcc.setBalance(payerNewBalance);

        int recieverOldBalance = recieverAcc.getBalance();
        int recieverNewBalance = recieverOldBalance + transaction.getAmount();
        recieverAcc.setBalance(recieverNewBalance);

        int masterOldBalance = masterAcc.getBalance();
        int masterNewBalance = masterOldBalance + transaction.getFee();
        masterAcc.setBalance(masterNewBalance);

        // Check if we have reached the transaction amount limit for this block
        if (this.currentBlock.getTransactionList().size() > 9) {

            // We've reached the transaction limit for ths block, so we must create a new block
            ArrayList<String> transHashList = new ArrayList<String> ();
            for (Transaction t:this.currentBlock.getTransactionList()){

                // Update the account balance mapping for every transaction in transaction list       
                payerAcc = this.getAccount(t.getPayer());
                recieverAcc = this.getAccount(t.getReciever());
                masterAcc = this.getAccount("master");

                this.currentBlock.addAccountBalanceMap(payerAcc, payerAcc.getBalance());
                this.currentBlock.addAccountBalanceMap(recieverAcc, recieverAcc.getBalance());
                this.currentBlock.addAccountBalanceMap(masterAcc, masterAcc.getBalance());


                // Populate the current block's hash parameter
                // To do this, we also need a hash for each transaction in the transaction list
                try {
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
                    objectStream.writeObject(t);
                    objectStream.flush();
                    byte [] byteArray = byteStream.toByteArray();

                    String hash = computeHash(byteArray.toString());
                    if (hash == null) {
                        throw new LedgerException (
                            "Ledger", "Unabled to compute hash."
                        );
                    } else {
                        transHashList.add(hash);
                    }
                } catch (LedgerException | IOException e) {
                    throw new LedgerException (
                        "Ledger", "Failed hashing transaction list."
                    );
                }
            }

            // Compute and assign a hash to the block
            ArrayList<String> blockHash = merkleTree(transHashList);
            this.currentBlock.setHash(blockHash.get(0));

            // Add the current block to the block map
            this.blockMap.put(this.currentBlock.getBlockNumber(), this.currentBlock);

            // Instanciate a new block, assign the prevousHash param the hash of the prev block
            // Deep copy the accountBalanceMap from the last block to the new block
            Block oldBlock = currentBlock;
            Map <Account, Integer> oldBlockMap = oldBlock.getAccountBalanceMap();

            this.currentBlock = new Block((this.blockMap.size() + 1));
            this.currentBlock.setPrevHash(oldBlock.getHash());

            for (Entry <Account, Integer> blockMapEntry: oldBlockMap.entrySet()) {
                // Update all account balances for accounts in the current accountMap
                blockMapEntry.getKey().setBalance(blockMapEntry.getValue());
                // Deep copy the accountBalanceEntry to the new block accountBalanceMap
                this.currentBlock.addAccountBalanceMap(blockMapEntry.getKey(), blockMapEntry.getValue());
            }
        }

        return transaction.getTransactionId();
    }

    /**
     *  In order to create a new block, we need to provide a unique hash value for the PREVIOUS block.
     *  this hash value will be computed from each transaction in the block's list of transactions.
     * 
     *  To do this, ill need to produce a hashlist of all transaction hashes in the given block.
     *  (this could be done by passing in an argument of transaction.hash as a list to a merkle tree function)
     *  (In the merkle tree function) I'll then need to iterate through the hashlist param
     *  ill take a pair (every 2) hashList values and produce a new hash that will be appended to a parentHashList
     *  Note Ill keep iterating over the hashlist until I reach the end. 
     *  (if I can't get a pair of 2 hashes, then reuse the same hash twice)
     *  Ill then recursively call the merkletree function, passing in the new parent list (repeating the previous step)
     *  Base case will be when the hashList param has a length of 1
     * 
     *  Citation merkleTree method code was originally based on the following article: 
     *  https://medium.com/@vinayprabhu19/merkel-tree-in-java-b45093c8c6bd
     * 
     *  @param hashList list of hash values to be computed via merkletree (initally a list of transaction hashes)
     *  @return The final hash value computed from a merkletree of hashes
     *  @throws LedgerException
     */
    private ArrayList<String> merkleTree (ArrayList<String> hashList) throws LedgerException {
        // Base case occurs when we only have one hash value left
        if (hashList.size() == 1) {
            return hashList;
        } else {
            ArrayList<String> recursiveHashList = new ArrayList<String> ();
            // Ensure our hashList param is of even size, so we can grab element-pairs of 2
            if(hashList.size() % 2 == 1){
                // Hashlists with an odd number of hashes will simiply compute a new hash off the last hash twice
                hashList.add(hashList.get(hashList.size() - 1));
            }

            // Iterate through hash list and producing a new hash based on pairs of 2 elements
            for (int i = 0; i < hashList.size(); i += 2) {
                String input = hashList.get(i).concat(hashList.get(i + 1));

                String hash = computeHash(input);
                if (hash == null) {
                    throw new LedgerException (
                        "Ledger", "Unabled to compute merkle tree leaf."
                    );
                } else {
                    recursiveHashList.add(hash);
                }
            }

            return merkleTree(recursiveHashList);
        }
    }

    /**
     * Generates a new hash value given an input string, via SHA-256 algorithm
     * @param input String value to be hashed
     * @return the output of the SHA-256 hashed input param
     */
    private String computeHash (String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(this.seed.getBytes(StandardCharsets.UTF_8));
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert the byte array to a string using the base 64 encoder
            String hashStr = Base64.getEncoder().encodeToString(hash);
            return hashStr;
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Retrieve account balance for a given account via account address (via most recent block).
     * @param address   Address of the given account
     * @return balance  The balance of the given account
     * @throws LedgerException
     */
    public int getAccountBalance (String address) throws LedgerException  {
        Map <Account, Integer> accountBalances = null;
        try {
            accountBalances = this.getAccountBalances();
        } catch (LedgerException e) {
            throw new LedgerException (
                "Ledger", "Unabled to get account balance, account doesn't exist."
            );
        }
        if (accountBalances != null) {
            // Iterate through all accounts in the account balance map
            for (Entry <Account, Integer> mapEntry: accountBalances.entrySet()) {
                if (mapEntry.getKey().getAddress().equals(address)) {
                    return mapEntry.getValue();
                }
            }
        }
        return 0;
    }

    /**
     * Returns the mapping of all accounts and their associated balances for the given block
     * @return this.accountBalanceMap   The account balance map requested, returns null if no map exists
     * @throws LedgerException
     */
    public Map <Account, Integer> getAccountBalances () throws LedgerException {
        Entry <Integer, Block>  recentBlock = this.blockMap.lastEntry();
        if (recentBlock == null) {
            throw new LedgerException (
                "Ledger", "Unabled to get account balance map, blockMap is empty (no blocks committed yet)."
            );
        } else {
            return recentBlock.getValue().getAccountBalanceMap();
        }
    }
    

    /**
     * Retreives a block given a specific block number
     * @param blockNumber   The identifier of the block to be retreived
     * @return block    The resulting block given the queried id
     * @throws LedgerException
     */
    public Block getBlock (int blockNumber) throws LedgerException {
        for (Entry <Integer, Block> blockEntry:this.blockMap.entrySet()) {
            if (blockEntry.getKey() == blockNumber) {
                return blockEntry.getValue();
            }
        }
        // Iterated through all blocks, block not found.
        throw new LedgerException (
            "Ledger", "Unabled to get block, block not found."
        );
    }
    
    /**
     * Retreives a transaction given a specific transaction number
     * @param transactionId   The identifier of the transaction to be retreived
     * @return transaction    The resulting transaction given the queried id
     * @throws LedgerException
     */
    public Transaction getTransaction (String transactionId) throws LedgerException {
        Entry <Integer, Block>  recentBlock = this.blockMap.lastEntry();
        if (recentBlock == null) {
            throw new LedgerException (
                "Ledger", "Unabled to get transaction, blockMap is empty (no blocks committed yet)."
            );
        } else {
            Transaction t = recentBlock.getValue().getTransaction(transactionId);
            if (t == null) {
                throw new LedgerException (
                    "Ledger", "Unabled to get transaction, Transaction not found in recent block."
                );
            }
            return t;
        }

    }

    /**
     * Valdiate current blockchain state, max of 10 transactions per block and balances total to max value
     * @throws LedgerException
     */
    public void validate () throws LedgerException {
        // Iterate through each block in the block chain
        String prevHash = null;
        for (Entry <Integer, Block> blockEntry:this.blockMap.entrySet()) {
            Block block = blockEntry.getValue();

            // Validate the previous block's hash matches the current block's record of the prevous block's hash
            if (block.getPrevHash() != prevHash) {
                throw new LedgerException (
                    "Ledger", "Unabled to validate block, hash values missmatch between blocks."
                );
            } else {
                System.out.println("Block: " + blockEntry.getKey() + " meets hash req.");
            }

            // Validate each completed block has exactly 10 transactions.
            if (block.getTransactionList().size() != 10) {
                throw new LedgerException (
                    "Ledger", "Unabled to validate block, block doesn't meet transaction count limit."
                );
            } else {
                System.out.println("Block: " + blockEntry.getKey() + " meets transaction limit.");
            }

            // Validate account balances total to the max value
            int blockBalance = 0;
            for (Entry <Account, Integer> accountEntry:block.getAccountBalanceMap().entrySet()) {
                blockBalance += accountEntry.getValue();
            }
            if (blockBalance != Integer.MAX_VALUE) {
                throw new LedgerException (
                    "Ledger", "Unabled to validate block, block accounts don't total to max value."
                );
            } else {
                System.out.println("Block: " + blockEntry.getKey() + " account balances match expected total.");
            }
            prevHash = block.getHash();
        }    
    }

    public String toString () {
        String result = "Ledger " + this.getName() + " " + this.getDescription();
        return result;
    }
}

