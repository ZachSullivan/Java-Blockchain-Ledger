package com.cscie97.ledger;

/**
* The command processor exception class
* The custom exception records the reason, command, and exact line for the point of failure
* Exception is thrown by the command processor class
*
* @author  Zachary Sullivan
* @since   2020-09-13 
*/
public class CommandProcessorException extends Exception {

    private static final long serialVersionUID = 1L;

    public CommandProcessorException(final String cmd, final String reason, final int line) {
        super((reason + " " + cmd + ":" + line));
    }
}
