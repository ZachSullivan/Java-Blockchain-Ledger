package com.cscie97.ledger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandProcessor {

    // NOTE: This will only support 1 legder at a time, doc doesnt specify more than on simulatenously so keeping as is
    Ledger ledger = null;

    // Searches a list of commands for a suite of given arguments
    private Map<String, String> parseArgs (String cmds[], String args[]) {
        // Create a mapping of command keywords and their vars
        Map<String, String> cmdMap = new HashMap <String, String> ();

        for(int i = 0; i < cmds.length - 1; i++) {
            for (String arg : args) {
                // Check if the current command matches the expected argument
                if (cmds[i].equalsIgnoreCase(arg)){
                    // Before we copy the neighboring command, ensure the neighboring value is not outofbounds
                    if ((i+1) > 0 && (i+1) <= cmds.length) {
                        
                        cmdMap.put(cmds[i], cmds[i+1]);
                    }
                }
            }
        }
        return cmdMap;
    }
    
    /**
     * Processes, formats and outputs a single command
     * 
     * @param command The string command to be processed
     * @throws LedgerException
     * @throws CommandProcessorException
     */
    public void processCommand(String command) throws LedgerException, CommandProcessorException {
        // We need to extract substrings from the command string
        // start by splitting based on spaces
        // Note I found the quotations in the sample script were not the same ascii "chars I was testing for
        command = command.replaceAll("[“”]", "\"");

        // Split our cmd string based on spaces (ignoring quotations)
        // Citation: this regex expression was obtained from Bohemian♦ on Stackoverflow
        // via: https://stackoverflow.com/questions/25477562/split-java-string-by-space-and-not-by-double-quotation-that-includes-space
        String cmds[] = command.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");

        String args[];
        Map<String, String> cmdMap;
        // Skip any blank commands or commands denoted as a comment (start with '#')
        //if (cmds.length > 1){
        if (!cmds[0].equals("#")) {
            System.out.println();
            // Process user command based on first element in command string
            switch(cmds[0]) {
                // NEED TO CHECK COMMAND LINES ARE EXPECTED LENGTH
                case "create-ledger":
                    System.out.println("Creating a new Ledger");
                    // Create-ledger command should specify the following arguments
                    args = new String[]{"create-ledger", "description", "seed"};
                    
                    // Populate a new mapping of arguments to properties
                    cmdMap = parseArgs (cmds, args);
                    //System.out.println("CMDMAP" + cmdMap);

                    // Create a new ledger with the specified user args
                    ledger = new Ledger(cmdMap.get("create-ledger"), cmdMap.get("description"), cmdMap.get("seed"));
                    System.out.println("Created new ledger");
                    //System.out.println("Current blockMap");
                    ledger.getBlockMap();
                    break;

                case "create-account":
                    System.out.println("Creating a new Account");
                    // Create-account command should specify the following arguments
                    args = new String[] {"create-account"};
                        
                    // Populate a new mapping of arguments to properties
                    cmdMap = parseArgs (cmds, args);

                    ledger.createAccount(cmdMap.get("create-account"));
                    System.out.println("Created new account");
                    
                    break;

                case "process-transaction":
                    // process-transaction command should specify the following arguments
                    args = new String[] {
                        "process-transaction",
                        "amount",
                        "fee",
                        "note",
                        "payer",
                        "receiver",
                    };

                    cmdMap = parseArgs (cmds, args);

                    System.out.println("Processesing a new transaction: " + cmdMap.get("process-transaction"));
                    System.out.println("transaction amount: " + cmdMap.get("amount"));
                    System.out.println("transaction fee: " + cmdMap.get("fee"));
                    System.out.println("TO: " + cmdMap.get("receiver"));
                    System.out.println("FROM: " + cmdMap.get("payer"));

                    int amount = 0;
                    int fee = 0;
                    // Some constructor arguments accept ints, must convert
                    try {
                        amount = Integer.parseInt(cmdMap.get("amount"));
                        fee = Integer.parseInt(cmdMap.get("fee"));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    Transaction transaction = new Transaction(
                        cmdMap.get("process-transaction"),
                        amount,
                        fee,
                        cmdMap.get("note"),
                        cmdMap.get("payer"),
                        cmdMap.get("receiver")
                    );

                    ledger.processTransaction(transaction);
                    System.out.println("Processed a new transaction");
                    break;

                case "get-account-balance":
                    System.out.println("Retrieving account balance");
                    args = new String[] {
                        "get-account-balance"
                    };
                    cmdMap = parseArgs (cmds, args);
                    int accountBalance = ledger.getAccountBalance(cmdMap.get("get-account-balance"));
                    if (accountBalance < 0) {
                        System.out.println("Couldn't retrieve account balance");
                    } else {
                        System.out.println(
                            cmdMap.get("get-account-balance")
                            + "'s account balance: "
                            + accountBalance
                        );
                        
                        System.out.println("Retrieved account balance");
                    }
                    break;
                
                case "get-account-balances":
                    System.out.println("Retrieving account balances");
                    Map <Account, Integer> accountBalances = ledger.getAccountBalances();
                    if (accountBalances == null) {
                        System.out.println("Couldn't retrieve account balance map");
                    } else {
                        System.out.println(accountBalances);
                    }
                    
                    break;
                
                case "get-transaction":
                    //System.out.println("TRIGGERED A CASE!!!!");
                    break;
                
                case "validate":
                    //System.out.println("TRIGGERED A CASE!!!!");
                    break;
            }

            /*for(String s: cmds){
                System.out.println(s);
                
            }
            System.out.println();*/
        }
    }
   // }

    /**
     * Processes a file containing multiple commands, passes extracted command to
     * processCommand method
     * 
     * @param commandFile The file of commands to be processed
     * @throws CommandProcessorException
     * @throws LedgerException
     */
    public void processCommandFile(String commandFile) throws CommandProcessorException, LedgerException {

        // Open the file, thow exception if file doesnt exist or is a dir
        File testScript = new File (commandFile);
        if (testScript.exists() == false || testScript.isDirectory() == true) {
            throw new CommandProcessorException (
                "processCommandFile", "Failed while attempting to open file", 25
            );
        } else {
            try {
                Scanner fileScanner = new Scanner(testScript);
                while (fileScanner.hasNextLine()) {
                    processCommand(fileScanner.nextLine());   
                }  
    
                fileScanner.close();
            } catch (FileNotFoundException e) { 
                e.printStackTrace();        
            }
			
        }
    }
}

