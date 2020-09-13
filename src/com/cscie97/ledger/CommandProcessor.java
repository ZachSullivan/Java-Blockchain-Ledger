package com.cscie97.ledger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CommandProcessor {

    // NOTE: This will only support 1 legder at a time, doc doesnt specify more than one simulatenously so keeping as is.
    Ledger ledger = null;

    // Search a list of commands for a suite of given arguments
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
     * @param command   The string command to be processed
     * @throws LedgerException
     * @throws CommandProcessorException
     */
    public void processCommand(String command) throws LedgerException, CommandProcessorException {
        // Etract substrings from the command string
        // .. start by splitting based on spaces
        // NOTE: I found the quotations in the sample script were not the same ascii "chars I was testing for
        command = command.replaceAll("[“”]", "\"");

        // Split our cmd string based on spaces (ignoring quotations)
        // Citation: this regex expression was obtained from Bohemian♦ on Stackoverflow
        // via: https://stackoverflow.com/questions/25477562/split-java-string-by-space-and-not-by-double-quotation-that-includes-space
        String cmds[] = command.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");

        String args[];
        Map<String, String> cmdMap;

        // Skip any blank commands or commands denoted as a comment (start with '#')
        if (!cmds[0].equals("#")) {
            // Process user command based on first element in command string
            switch(cmds[0]) {
                case "create-ledger":
                    System.out.println("Creating a new Ledger");

                    // Create-ledger command should specify the following arguments
                    args = new String[]{"create-ledger", "description", "seed"};
                    // Populate a new mapping of arguments to properties
                    cmdMap = parseArgs (cmds, args);

                    // Create a new ledger with the specified user args
                    ledger = new Ledger(cmdMap.get("create-ledger"), cmdMap.get("description"), cmdMap.get("seed"));
                    System.out.println("Created new ledger");
                    ledger.getBlockMap();
                    break;

                case "create-account":
                    System.out.println("Creating a new Account");

                    // Create-account command should specify the following arguments
                    args = new String[] {"create-account"};    
                    // Populate a new mapping of arguments to properties
                    cmdMap = parseArgs (cmds, args);

                    try {
                        ledger.createAccount(cmdMap.get("create-account"));
                    } catch (LedgerException e) {
                        throw new CommandProcessorException (
                            e.getAction(), e.getReason(), e.getStackTrace()[2].getLineNumber()
                        );
                    }

                    break;

                case "process-transaction":
                    System.out.println("Processesing a new transaction.");

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

                    int amount = 0;
                    int fee = 0;

                    // Some Transaction constructor arguments accept ints, must convert
                    try {
                        amount = Integer.parseInt(cmdMap.get("amount"));
                        fee = Integer.parseInt(cmdMap.get("fee"));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    // Generate a new transaction object based on user params
                    Transaction transaction = new Transaction (
                        cmdMap.get("process-transaction"),
                        amount,
                        fee,
                        cmdMap.get("note"),
                        cmdMap.get("payer"),
                        cmdMap.get("receiver")
                    );
                    
                    // Process the new transaction, validating and appending to block
                    try {
                        ledger.processTransaction(transaction);
                    } catch (LedgerException e) {
                        throw new CommandProcessorException (
                            e.getAction(), e.getReason(), e.getStackTrace()[0].getLineNumber()
                        );
                    }

                    break;

                case "get-account-balance":
                    System.out.println("Retrieving account balance");

                    // get-account-balance command should specify the following argument
                    args = new String[] {
                        "get-account-balance"
                    };
                    cmdMap = parseArgs (cmds, args);

                    // Obtain account balance, ensuring method did not fail
                    try {
                        int accountBalance = ledger.getAccountBalance(cmdMap.get("get-account-balance"));
                        if (accountBalance >= 0) {
                            System.out.println(
                                cmdMap.get("get-account-balance")
                                + "'s account balance: "
                                + accountBalance
                            );
                        }
                    } catch (LedgerException e) {
                        throw new CommandProcessorException (
                            e.getAction(), e.getReason(), e.getStackTrace()[0].getLineNumber()
                        );
                    }

                    break;
                
                case "get-account-balances":
                    System.out.println("Retrieving account balances");

                    try {
                        System.out.println(ledger.getAccountBalances());
                    } catch (LedgerException e) {
                        throw new CommandProcessorException (
                            e.getAction(), e.getReason(), e.getStackTrace()[0].getLineNumber()
                        );
                    }
                    
                    break;
                
                case "get-transaction":
                    // get-transaction command should specify the following argument
                    args = new String[] {"get-transaction"};
                    cmdMap = parseArgs (cmds, args);

                    System.out.println("Retrieving transaction " + cmdMap.get("get-transaction"));

                    // Retrieve the requested transaction object, validate return value
                    try {
                        Transaction t = ledger.getTransaction(cmdMap.get("get-transaction"));
                        System.out.println(t.toString());
                    } catch (LedgerException e) {
                        throw new CommandProcessorException (
                            e.getAction(), e.getReason(), e.getStackTrace()[0].getLineNumber()
                        );
                    }

                    break;

                case "get-block":
                    // get-block command should specify the following argument

                    args = new String[] {"get-block"};
                    cmdMap = parseArgs (cmds, args);
                    System.out.println("Retrieving block " + cmdMap.get("get-block"));

                    // getBlock arguments accept ints, must convert
                    int blockId = -1;
                    try {
                        blockId = Integer.parseInt(cmdMap.get("get-block"));
                        if (blockId < 0) {
                            System.out.println("Failed to parse blockId.");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    // Retrieve the requested block object, validate return value
                    try {
                        Block b = ledger.getBlock(blockId);
                        System.out.println(b.toString());
                    } catch (LedgerException e) {
                        throw new CommandProcessorException (
                            e.getAction(), e.getReason(), e.getStackTrace()[0].getLineNumber()
                        );
                    }

                    break;
                
                case "validate":
                    // Validate blockchain
                    System.out.println("Validating blockchain");
                    try {
                        ledger.validate();
                    } catch (LedgerException e) {
                        throw new CommandProcessorException (
                            e.getAction(), e.getReason(), e.getStackTrace()[0].getLineNumber()
                        );
                    }

                    break;
                
                default:
                    System.out.println();
                    break;
            }
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
                    try {
                        processCommand(fileScanner.nextLine()); 
                    } catch (CommandProcessorException e) {
                        System.out.println(e); 
                    }
                }  
    
                fileScanner.close();
            } catch (FileNotFoundException e) { 
                //e.printStackTrace(); 
                System.out.println(e);       
            }
			
        }
    }
}

