package com.cscie97.ledger.test;

import com.cscie97.ledger.CommandProcessor;
import com.cscie97.ledger.CommandProcessorException;

public class TestDriver {
    public static void main(String args[]) {
      
        CommandProcessor cliProcessor = new CommandProcessor();

        try {
            cliProcessor.processCommandFile(args[0]);
        } catch (CommandProcessorException e) {
            e.printStackTrace();
        }

    }
}
