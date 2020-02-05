package org.uppaal.cli;

import java.util.HashMap;
import jline.console.ConsoleReader;
import java.io.PrintWriter;
import java.io.IOException;

/**
* console manager handling the interaction with the user input
* displaying command results and error messages
*/

public class ConsoleManager {

// console reader for this console manager

private ConsoleReader reader;

// hash map of accepted commands
private HashMap<String, Command.CommandCode> accepted_commands;

// reverse hash map from command codes to command names
private HashMap<Command.CommandCode, String> command_names;

// output writer for this console manager
private PrintWriter out;

// error writer for this console manager
private PrintWriter err;


/**
* public constructor of a console Manager
* initializing its different attributes to default value
*/
public ConsoleManager () throws IOException {


	this.reader = new ConsoleReader();
            	this.out = new PrintWriter(reader.getOutput());
	this.err = new PrintWriter(System.err);

// initialize the map of accepted commands

	this.accepted_commands = new HashMap<String, Command.CommandCode>();
	this.accepted_commands.put("export", Command.CommandCode.EXPORT);
	this.accepted_commands.put("import", Command.CommandCode.IMPORT);

// initialize the map of command names from the previous one

	this.command_names = new HashMap<Command.CommandCode, String>();
	for (String command_name: this.accepted_commands.keySet()) {
		this.command_names.put(this.accepted_commands.get(command_name), command_name);
	}
}

/**
* run this console manager until the end of the session
* accepts commands from the console, execute them, display their result or error message
*/

public void run () {

}
}