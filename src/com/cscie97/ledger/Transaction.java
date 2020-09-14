package com.cscie97.ledger;

import java.io.Serializable;

/**
* The Transaction class manages transactions made between accounts.
* The Transaction class provides:
* - a reference to the max fee and max note length values
* - a transaction id, note/description, amount and fee, payer and receiver
*
* @author  Zachary Sullivan
* @since   2020-09-13 
*/
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Used for removing magic numbers during transaction verfication
    public final int MAX_FEE = 10;
    public final int MAX_NOTE_LEN = 1024;
    
    private String transactionId; 
    private String note;
    private int amount;
    private int fee;
    
    private String payer;
    private String reciever;

    public Transaction (String identifier, int amount, int fee, String note, String payer, String reciever) {
        this.transactionId = identifier;
        this.amount = amount;
        this.fee = fee;
        this.note = note;

        this.payer = payer;
        this.reciever = reciever;
    }

    /**
     * Retrieves the object’s transactionId value
     * @return the identifier for the transaction object
     */
    public String getTransactionId () {
        return this.transactionId;
    }

    /**
     * Retrieves the object’s amount value
     * @return the amount for the transaction object
     */
    public int getAmount () {
        return this.amount;
    }

    /**
     * Retrieves the object’s fee value
     * @return the fee for the transaction object
     */
    public int getFee () {
        return this.fee;
    }

    /**
     * Retrieves the object’s note string
     * @return the description for the transaction object
     */
    public String getNote () {
        return this.note;
    }

    /**
     * Retrieves the object’s payer Account address
     * @return the payer Account address for the transaction object
     */
    public String getPayer () {
        return this.payer;
    }

    /**
     * Retrieves the object’s reciever Account address
     * @return the reciever Account address for the transaction object
     */
    public String getReciever () {
        return this.reciever;
    }

    public String toString() { 
        String result = (
            "Transaction " + this.getTransactionId() + 
            ": amount: " + this.getAmount() + 
            " from \"" + this.getPayer() + 
            "\" to \"" + this.getReciever() +
            "\" processing fee: " +  this.getFee()
        ); 
        return result;
    } 
}
