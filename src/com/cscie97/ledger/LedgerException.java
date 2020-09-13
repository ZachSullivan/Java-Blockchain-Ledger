package com.cscie97.ledger;

public class LedgerException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String action;
    private String reason;

    public LedgerException (String action, String reason) {
        super((reason + " " + action));
        this.action = action;
        this.reason = reason;
    }

    public String getAction () {
        return this.action;
    }

    public String getReason () {
        return this.reason;
    }
}
