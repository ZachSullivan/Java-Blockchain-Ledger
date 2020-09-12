package com.cscie97.ledger;

import java.io.Serializable;

public class Transaction implements Serializable {
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


    //private Account payer;
    //private Account reciever;

    /*public Transaction (String identifier, int amount, int fee, String note, Account payer, Account reciever) {
        this.transactionId = identifier;
        this.amount = amount;
        this.fee = fee;
        this.note = note;

        this.payer = payer;
        this.reciever = reciever;
    }

    public Transaction (String identifier, int amount, int fee, Account payer, Account reciever) {
        this(identifier, amount, fee, "Default Transaction Note.", payer, reciever);
    }*/

    public String getTransactionId () {
        return this.transactionId;
    }

    public int getAmount () {
        return this.amount;
    }

    public int getFee () {
        return this.fee;
    }

    public String getNote () {
        return this.note;
    }

    public String getPayer () {
        return this.payer;
    }

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
