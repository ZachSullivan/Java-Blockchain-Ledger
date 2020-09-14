package com.cscie97.ledger;

/**
* The ledger exception class
* The custom exception records the reason, command for the point of failure
* Exception is thrown by the ledger class
*
* @author  Zachary Sullivan
* @since   2020-09-13 
*/
public class LedgerException extends Exception {

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
