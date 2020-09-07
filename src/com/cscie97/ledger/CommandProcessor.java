package com.cscie97.ledger;

import java.io.File;

public class CommandProcessor {
    
    /**
     * Processes, formats and outputs a single command
     * @param command   The string command to be processed
     */
    public void processCommand (String command) {

    }

    /**
     * Processes a file containing multiple commands
     * @param commandFile   The file of commands to be processed
     */
    public void processCommandFile (String commandFile) {
        File file = new File(commandFile); // Save the input args into a new file with according file name

        // This should be a trycatch
        // Validate that the file both exists and is not a directory name
        if ((file.exists() == true) && (file.isDirectory() == false)) {
            // Todo parse each line
                // for each line
                    // call processCommand method
                // exit when eof
        } else {
            CommandProcessorException cliException = new CommandProcessorException (
                "processCommandFile", 
                "Failed to validate file exists or may be a directory name",
                21
            );
        }
    }
}
