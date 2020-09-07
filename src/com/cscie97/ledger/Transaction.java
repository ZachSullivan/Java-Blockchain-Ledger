package com.cscie97.ledger;

public class Transaction {
    private String transactionId; 
    private String note;
    private long amount;
    private long fee;
    
    private Account payer;
    private Account reciever;

    public Transaction (String identifier, long amount, long fee, String note, Account payer, Account reciever) {
        this.transactionId = identifier;
        this.amount = amount;
        this.fee = fee;
        this.note = note;

        this.payer = payer;
        this.reciever = reciever;
    }

    public Transaction (String identifier, long amount, long fee, Account payer, Account reciever) {
        this(identifier, amount, fee, "Default Transaction Note.", payer, reciever);
    }

    public String getTransactionId () {
        return this.transactionId;
    }

    public long getAmount () {
        return this.amount;
    }

    public long getFee () {
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
