package com.cscie97.ledger.test;

import com.cscie97.ledger.CommandProcessor;
import com.cscie97.ledger.CommandProcessorException;
import com.cscie97.ledger.LedgerException;

public class TestDriver {
    public static void main(String args[]) {
      
        CommandProcessor cliProcessor = new CommandProcessor();

        try {
            cliProcessor.processCommandFile(args[0]);
        } catch (CommandProcessorException | LedgerException e) {
            //e.printStackTrace();
            System.out.println(e.toString());
        }

    }
}
