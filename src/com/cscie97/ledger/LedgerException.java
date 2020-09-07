package com.cscie97.ledger;

public class LedgerException {
    private String action;  // Action performed causing exception
    private String reason;  // Reason for the exception

    public LedgerException (String action, String reason) {
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
