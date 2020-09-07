package com.cscie97.ledger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CommandProcessor {

    /**
     * Processes, formats and outputs a single command
     * 
     * @param command The string command to be processed
     */
    public void processCommand(String command) {

        String cmds[] = command.split(" ");

        // Skip any blank lines or lines denoted as a comment (start with '#')
        if (cmds.length > 1){
            if (!cmds[0].equals("#")) {

                // Process user command based on first element in command string
                switch(cmds[0]) {
                    case "create-ledger":
                        System.out.println("TRIGGERED A CASE!!!!");
                        break;

                    case "create-account":
                        System.out.println("TRIGGERED A CASE!!!!");
                        break;

                    case "process-transaction":
                        System.out.println("TRIGGERED A CASE!!!!");
                        break;

                    case "get-account-balance":
                        System.out.println("TRIGGERED A CASE!!!!");
                        break;
                    
                    case "get-account-balances":
                        System.out.println("TRIGGERED A CASE!!!!");
                        break;
                    
                    case "get-transaction":
                        System.out.println("TRIGGERED A CASE!!!!");
                        break;
                    
                    case "validate":
                        System.out.println("TRIGGERED A CASE!!!!");
                        break;
                }

                for(String s: cmds){
                    System.out.println(s);
                    
                }
                System.out.println();
            }
        }
    }

    /**
     * Processes a file containing multiple commands, passes extracted command to processCommand method
     * 
     * @param commandFile The file of commands to be processed
     * @throws CommandProcessorException
     */
    public void processCommandFile(String commandFile) throws CommandProcessorException {

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

