package org.uppaal.cli;

import com.uppaal.engine.EngineException;
import org.uppaal.cli.handlers.CommandHandler;
import org.uppaal.cli.handlers.Handler;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.FileNameCompleter;
import jline.console.completer.StringsCompleter;

import java.util.HashMap;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
* console manager handling the interaction with the user input
* displaying command results and error messages
*/

public class ConsoleManager {

// uppaal context for this command manager

private Context context;

// console reader for this console manager

private ConsoleReader reader;

// hash map of accepted commands
private HashMap<String, Command.CommandCode> accepted_commands;

// reverse hash map from command codes to command names
private HashMap<Command.CommandCode, String> command_names;

// hash map of accepted modes for this command manager
private HashMap <String, Handler.HandlerCode> accepted_modes;

// hashmap of mode names for this console manager
private HashMap<Handler.HandlerCode, String> mode_names;

// hash map of accepted completers for this console manager
private HashMap <Handler.HandlerCode, Completer> completers;

// active completer for this console manager
private Completer active_completer;

// private command handler for this console manager
private CommandHandler command_handler;

// output writer for this console manager
private PrintWriter out;

// error writer for this console manager
private PrintWriter err;

// boolean telling if the console manager is running
private boolean running;

/**
* public constructor of a console Manager
* initializing its different attributes to default value
* @param context the uppaal context to handle
*/
public ConsoleManager (Context context) throws IOException, IOException {

	this.context = context;
	this.reader = new ConsoleReader();
            	this.out = new PrintWriter(reader.getOutput());
	this.err = new PrintWriter(System.err);
	this.command_handler = new CommandHandler(context);
	this.accepted_commands = new HashMap<String, Command.CommandCode>();
	this.command_names = new HashMap<Command.CommandCode, String>();
	this.accepted_modes = new HashMap<String, Handler.HandlerCode>();
	this.mode_names = new HashMap<Handler.HandlerCode, String>();
	this.completers = new HashMap<Handler.HandlerCode, Completer>();

// create the first completer for the baseline command lines available for every mode

	Completer completer = new StringsCompleter("start", "exit");
	this.reader.addCompleter(completer);

// initialize the map of accepted commands

	this.accepted_commands.put ("start", Command.CommandCode.START);
	this.accepted_commands.put("exit", Command.CommandCode.EXIT);
	this.accepted_commands.put("export", Command.CommandCode.EXPORT);
	this.accepted_commands.put("import", Command.CommandCode.IMPORT);
	this.accepted_commands.put("check", Command.CommandCode.CHECK);

// initialize the map of command names from the previous one

	for (String command_name: this.accepted_commands.keySet()) {
		Command.CommandCode code = this.accepted_commands.get(command_name);
		this.command_names.put(code, command_name);
	}

// initialize the map of accepted modes

	this.accepted_modes.put("editor", Handler.HandlerCode.EDITOR);
	this.accepted_modes.put("simulator", Handler.HandlerCode.SIMULATOR);
	this.accepted_modes.put("verifier", Handler.HandlerCode.VERIFIER);

// initialize the map of mode names from the previous one

	for (String mode_name: this.accepted_modes.keySet()) {
		Handler.HandlerCode mode_code = this.accepted_modes.get(mode_name);
		this.mode_names.put(mode_code, mode_name);
	}
}

/**
* set the prompt of this console manager
*/
private void setPrompt () {
	String mode = this.mode_names.get(this.command_handler.getMode());
	this.reader.setPrompt("uppaal "+mode+"$");
}

/**
* create a completer for the current mode
* @return the newly created completer
*/
public Completer getCompleter () {

// if there is already a completer for the current mode in the corresponding hash map simply return it

	Handler.HandlerCode active_mode= this.command_handler.getMode();
	if (this.completers.containsKey(active_mode))
		return this.completers.get(active_mode);

// create a list of commands for the current active mode

	Command.CommandCode[] active_commands = this.command_handler.getActiveCommands();
	LinkedList<String> commands = new LinkedList<String>();

	for (Command.CommandCode command_code:active_commands) {
		String command_name = this.command_names.get(command_code);
		commands.add(command_name);
	}

// create the new completer, add it to the hash map and return it

	Completer completer = new StringsCompleter(commands);
	this.completers.put(active_mode, completer);
	return completer;
}

/**
* set the completers for the current mode
*/
public void setCompleter() {
	this.reader.removeCompleter(this.active_completer);
	this.active_completer = this.getCompleter();
	this.reader.addCompleter(this.active_completer);
}

/**
* run this console manager until the end of the session
* accepts commands from the console, execute them, display their result or error message
*/

public void run () throws EngineException, IOException {

// first initialize the console reader

	this.context.connectEngine();
	this.setPrompt();
	this.active_completer = this.getCompleter();
	this.reader.addCompleter(this.active_completer);

	this.running = true;
	String line = null;

// read the next line while it is not null

	while (this.running) {
		line = reader.readLine();
		if (line==null) break;
	}
	this.context.disconnectEngine();
}
}