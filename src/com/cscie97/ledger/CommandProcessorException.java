package com.cscie97.ledger;

public class CommandProcessorException extends Exception {
    private String command;
    private String reason;
    private int lineNumber;
    
    public CommandProcessorException (String cmd, String reason, int line) {
        super((reason + " " + cmd + ":" + line));
    }

    // may want to change getters to a single toString method that prints the exception

    public String getCommand () {
        return this.command;
    }

    public String getReason () {
        return this.reason;
    }

    public int getLine () {
        return this.lineNumber;
    }
}
