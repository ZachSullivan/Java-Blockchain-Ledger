package com.cscie97.ledger;

public class Transaction {
    private String transactionId; 
    private String note;
    private int amount;
    private int fee;
    
    private Account payer;
    private Account reciever;

    public Transaction (String identifier, int amount, int fee, String note, Account payer, Account reciever) {
        this.transactionId = identifier;
        this.amount = amount;
        this.fee = fee;
        this.note = note;

        this.payer = payer;
        this.reciever = reciever;
    }

    public Transaction (String identifier, int amount, int fee, Account payer, Account reciever) {
        this(identifier, amount, fee, "Default Transaction Note.", payer, reciever);
    }

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

    public Account getPayer () {
        return this.payer;
    }

    public Account getReciever () {
        return this.reciever;
    }
}
