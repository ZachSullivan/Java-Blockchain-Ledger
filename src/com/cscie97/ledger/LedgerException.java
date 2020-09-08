package com.cscie97.ledger;

public class LedgerException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public LedgerException (String action, String reason) {
        super((reason + " " + action));
    }
}
