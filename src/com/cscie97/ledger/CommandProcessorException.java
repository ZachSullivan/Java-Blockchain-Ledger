package com.cscie97.ledger;

public class CommandProcessorException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CommandProcessorException(final String cmd, final String reason, final int line) {
        super((reason + " " + cmd + ":" + line));
    }
}
