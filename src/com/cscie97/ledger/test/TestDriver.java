package com.cscie97.ledger.test;

import com.cscie97.ledger.CommandProcessor;

public class TestDriver {
    public static void main (String args[]) {
        /*for (String str: args) {
            System.out.println("ARGS WERE:" + str);
        }*/
        System.out.println("ARG 0 was:" + args[0]);
      
        CommandProcessor cliProcessor = new CommandProcessor();
        cliProcessor.processCommandFile(args[0]);


    }
}
