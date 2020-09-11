package com.cscie97.ledger;

import java.util.*; 
import java.util.Map.Entry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class Ledger {
    private String name;
    private String description;
    private String seed;

    // Citation: Thank you "Anonymous Gear" on piazza for the inspiration for a temp block to write to.
    private Block currentBlock; // Block currently being written to, once transaction limit is reached write this block to the block map and start a new currentBlock

    private Block genesisBlock; // Initial blockchain block
    //private Map <Integer, Block> blockMap = new HashMap <Integer, Block> ();    // Map of blocks with their associated ID values
    private NavigableMap <Integer, Block> blockMap = new TreeMap <Integer, Block> ();    // Map of blocks with their associated ID values
    
    public Ledger (String name, String description, String seed) {
        this.name = name;
        this.description = description;
        this.seed = seed;

        this.genesisBlock = new Block(1);
        this.currentBlock = this.genesisBlock;
        // May want to wait unil transactions are complete before commiting to block map
        //this.blockMap.put(this.genesisBlock.getBlockNumber(), this.genesisBlock);
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

    public Account getAccount (String accountId) {
        System.out.println(accountId);
        for (Account a:this.currentBlock.getAccountBalanceMap().keySet()) {
        //for (Account a:this.getAccountBalances().keySet()) {

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
        Account newAccount = null;
        // Check if account already exists with that accountID
        try {
            if (this.currentBlock.getAccountBalanceMap() == null || this.currentBlock.getAccountBalanceMap().isEmpty() == true) {
            //if (this.getAccountBalances() == null || this.getAccountBalances().isEmpty() == true) {
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
        } catch(LedgerException e) {
            System.out.println(e);
            return "null";
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
     */
    public String processTransaction (Transaction transaction) throws LedgerException {

        // Make sure to verfiy that the current block transaction list doesnt exceed 10 transactions
        // if it does, then spawn a new block, may want to do a try catch or an ifelse for this

        // Verify the unique id
        List<Transaction> transactionList = this.currentBlock.getTransactionList();
        try {
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
        } catch(LedgerException e) {
            System.out.println(e);
        }

        System.out.println("PAYER ACCOUNT BALANCE: " +this.getAccount(transaction.getPayer()).getBalance());

        // Verify the payer and reciever accounts exist, and amount does not exceed payer's balance
        try {
            //if (this.getAccountBalance(transaction.getPayer()) < transaction.getAmount()) {
            if (this.getAccount(transaction.getPayer()).getBalance() < transaction.getAmount()) {
                throw new LedgerException (
                    "Ledger", "Failed validating transaction, amount exceeds payere's account balance"
                );
            }
            // Precalculate transaction from payer, taking into account fee
            /*int newBalance = (
                (this.getAccountBalance(transaction.getReciever()) - transaction.getAmount()) 
                + transaction.getFee()
            );*/
            int newBalance = (
                (this.getAccount(transaction.getReciever()).getBalance() - transaction.getAmount()) 
                + transaction.getFee()
            );
            
            if (newBalance > Integer.MAX_VALUE) {
                throw new LedgerException (
                    "Ledger", "Failed validating transaction, fee exceeds reciever's maximum account balance"
                );
            }
        } catch(LedgerException e) {
            System.out.println(e);
        }

        // Verify the amount is not less than 0 or greater than max int
        try {
            if ((transaction.getAmount() < 0) || (transaction.getAmount() > Integer.MAX_VALUE)) {
                throw new LedgerException (
                    "Ledger", "Failed validating transaction, amount exceeds boundaries"
                );
            }
        } catch(LedgerException e) {
            System.out.println(e);
        }

        // Verify the fee is not less than 0 or greater than 10
        try {  
            if ((transaction.getFee() < 0) || (transaction.getFee() > transaction.MAX_FEE)) {
                throw new LedgerException (
                    "Ledger", "Failed validating transaction, fee exceeds boundaries"
                );
            }
        } catch(LedgerException e) {
            System.out.println(e);
        }

        // Verify the note is not longer than 1024 chars
        String note = transaction.getNote();
        try {
            if (note.length() > transaction.MAX_NOTE_LEN) {
                throw new LedgerException (
                    "Ledger", "Failed validating transaction, note exceeds char limit"
                );
            }
        } catch(LedgerException e) {
            System.out.println(e);
        }

        Account payerAcc = this.getAccount(transaction.getPayer());
        Account recieverAcc = this.getAccount(transaction.getReciever());
        Account masterAcc = this.getAccount("master");

        // NEED TO CHECK THAT TRANSACTION LIST IS STILL UNDER 10
        // If not, then make a new block, and save the old one
        // Check if we have reached the transaction amount limit for this block
        if (this.currentBlock.getTransactionList().size() < 9) {
            // Transfer has been validated, add to transaction list
            this.currentBlock.addTransactionList(transaction);

            // Process the transaction between accounts
            // Note actually updating account balances may need to be done one we FINISH a block with 10 transactions
            /*payerAcc.setBalance(payerAcc.getBalance() - transaction.getAmount());
            recieverAcc.setBalance(recieverAcc.getBalance() + transaction.getAmount());
            masterAcc.setBalance(masterAcc.getBalance() + transaction.getFee());

            // Update the account balance map with new account balances
            this.currentBlock.addAccountBalanceMap(payerAcc, payerAcc.getBalance());
            this.currentBlock.addAccountBalanceMap(recieverAcc, recieverAcc.getBalance());
            this.currentBlock.addAccountBalanceMap(masterAcc, masterAcc.getBalance());*/

            /*int payerBalance = (payerAcc.getBalance() - transaction.getAmount());
            this.currentBlock.addAccountBalanceMap(payerAcc, newBalance);

            newBalance = (recieverAcc.getBalance() + transaction.getAmount());
            this.currentBlock.addAccountBalanceMap(recieverAcc, newBalance);

            newBalance = (masterAcc.getBalance() + transaction.getFee());
            this.currentBlock.addAccountBalanceMap(masterAcc, newBalance);*/

            int payerBalance = (payerAcc.getBalance() - transaction.getAmount());
            int recieverBalance = (recieverAcc.getBalance() + transaction.getAmount());
            int masterBalance = (masterAcc.getBalance() + transaction.getFee());

            Map <Account, Integer> blockMap = this.currentBlock.getAccountBalanceMap();
            for (Entry <Account, Integer> blockMapEntry: blockMap.entrySet()) {
                // Update all account balances for accounts in the current accountMap
                // blockMapEntry.getKey().setBalance(blockMapEntry.getValue());
                // Copy the accountBalanceEntry to the new block accountBalanceMap
                if (blockMapEntry.getKey() == payerAcc) {
                    this.currentBlock.addAccountBalanceMap(blockMapEntry.getKey(), payerBalance);
                } else if (blockMapEntry.getKey() == recieverAcc) {
                    this.currentBlock.addAccountBalanceMap(blockMapEntry.getKey(), recieverBalance);
                } else if (blockMapEntry.getKey() == masterAcc) {
                    this.currentBlock.addAccountBalanceMap(blockMapEntry.getKey(), masterBalance);
                }  
            }
            System.out.println("NEW ACCOUNTMAP: " + blockMap);

        } else {
            // Add the newly processed transaction to this new block's transactionList
            this.currentBlock.addTransactionList(transaction);

            // Update the account balance map with new account balances
            int newBalance = (payerAcc.getBalance() - transaction.getAmount());
            this.currentBlock.addAccountBalanceMap(payerAcc, newBalance);
            payerAcc.setBalance(newBalance);

            newBalance = (recieverAcc.getBalance() + transaction.getAmount());
            this.currentBlock.addAccountBalanceMap(recieverAcc, newBalance);
            recieverAcc.setBalance(newBalance);

            newBalance = (masterAcc.getBalance() + transaction.getFee());
            this.currentBlock.addAccountBalanceMap(masterAcc, newBalance);
            masterAcc.setBalance(newBalance);
            
            recieverAcc.setBalance(recieverAcc.getBalance() + transaction.getAmount());
            masterAcc.setBalance(masterAcc.getBalance() + transaction.getFee());
            // We've reached the transaction limit for ths block, so we must create a new block
            // First populate the current block's hash parameter
            // To do this, we also need a hash for each transaction in the transaction list
            ArrayList<String> transHashList = new ArrayList<String> ();
            for (Transaction t:this.currentBlock.getTransactionList()){
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
                    System.out.println(e);
                }
            }

            ArrayList<String> blockHash = merkleTree(transHashList);
            System.out.println("BLOCKHASH: " + blockHash.get(0));
            this.currentBlock.setHash(blockHash.get(0));

            // Add the current block to the block map
            this.blockMap.put(this.currentBlock.getBlockNumber(), this.currentBlock);
            System.out.println("blockMap: " + this.blockMap);

            // Instanciate a new block, assign the prevousHash param the hash of the prev block
            // Copy the accountBalanceMap from the last block to the new block
            Block oldBlock = currentBlock;
            Map <Account, Integer> oldBlockMap = oldBlock.getAccountBalanceMap();

            this.currentBlock = new Block((this.blockMap.size() + 1));
            this.currentBlock.setPrevHash(oldBlock.getHash());

            for (Entry <Account, Integer> blockMapEntry: oldBlockMap.entrySet()) {
                // Update all account balances for accounts in the current accountMap
                blockMapEntry.getKey().setBalance(blockMapEntry.getValue());
                // Copy the accountBalanceEntry to the new block accountBalanceMap
                this.currentBlock.addAccountBalanceMap(blockMapEntry.getKey(), blockMapEntry.getValue());
            }
        }


        // Should return a messageDigest as the unique ID
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
     * (if I can't get a pair of 2 hashes, then reuse the same hash twice)
     *  Ill then recursively call the merkletree function, passing in the new parent list (repeating the previous step)
     *  Base case will be when the hashList param has a length of 1
     *  see: https://medium.com/@vinayprabhu19/merkel-tree-in-java-b45093c8c6bd
     */
    private ArrayList<String> merkleTree (ArrayList<String> hashList) {
        // Base case
        if (hashList.size() == 1) {
            return hashList;
        } else {
            ArrayList<String> recursiveHashList = new ArrayList<String> ();
            
            // Ensure our hashList param is of even size, so we can grab element-pairs of 2
            if(hashList.size() % 2 == 1){
                // Hashlists with an odd number of hashes will simiply compute a new hash off the last hash twice
                hashList.add(hashList.get(hashList.size() - 1));
            }

            for (int i = 0; i < hashList.size(); i += 2) {
                String input = hashList.get(i).concat(hashList.get(i+1));

                try {
                    String hash = computeHash(input);
                    if (hash == null) {
                        throw new LedgerException (
                            "Ledger", "Unabled to compute hash."
                        );
                    } else {
                        recursiveHashList.add(hash);
                    }
                } catch (LedgerException e) {
                    System.out.println(e);
                }
            }

            return merkleTree(recursiveHashList);
        }
    }

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
     */
    public int getAccountBalance (String address)  {
        System.out.println(address);
        // Iterate through all accounts in the account balance map
        try {
            for (Account a:this.getAccountBalances().keySet()) {
                // Return when we find a matching account id
                if (a.getAddress().equals(address)) {
                    return a.getBalance();
                }
            }
            // We have processed all accounts in the map, account doesn't exist 
            throw new LedgerException (
                "Ledger", "Unabled to get account balance, account doesn't exist."
            );
        } catch(LedgerException | NullPointerException e) {
            System.out.println(e);
            return -1;
        }
    }

    /**
     * Returns the mapping of all accounts and their associated balances for the given block
     * @return this.accountBalanceMap   The account balance map requested, returns null if no map exists
     */
    public Map <Account, Integer> getAccountBalances () {
        Entry <Integer, Block>  recentBlock = this.blockMap.lastEntry();
        System.out.println("recentBlock: " + recentBlock);
        try {
            if (recentBlock == null) {
                throw new LedgerException (
                    "Ledger", "Unabled to get account balance map, blockMap is empty (no blocks committed yet)."
                );
            } else {
                return recentBlock.getValue().getAccountBalanceMap();
            }
        } catch(LedgerException e) {
            System.out.println(e);
            return null;
        }
        
        
        //return this.currentBlock.getAccountBalanceMap();
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
        // If I need to validate hashes, comparing block hash and the prevHash of the next block should do...
    }
}

