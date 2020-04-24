package org.uppaal.cli;


import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.context.Context;

import com.uppaal.engine.EngineException;
import org.uppaal.cli.exceptions.UnknownModeException;
import org.uppaal.cli.exceptions.ConsoleException;
import org.uppaal.cli.enumerations.ModeCode;
import org.uppaal.cli.commands.CommandLauncher;
import org.uppaal.cli.commands.Handler;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import jline.console.completer.FileNameCompleter;
import jline.console.completer.StringsCompleter;
import jline.console.completer.NullCompleter;
import jline.console.completer.ArgumentCompleter;


import java.util.HashMap;
import java.util.HashSet;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.MalformedURLException;
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

// hash map of accepted modes for this command manager
private HashMap <String, ModeCode> accepted_modes;

// hashmap of mode names for this console manager
private HashMap<ModeCode, String> mode_names;

// hash map of accepted command completers for this console manager
private HashMap <ModeCode, Completer> command_completers;

// active command completer for this console manager
private Completer command_completer;

// private command handler for this console manager
private CommandLauncher command_launcher;

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
	this.command_launcher = new CommandLauncher(context);
	this.accepted_modes = new HashMap<String, ModeCode>();
	this.mode_names = new HashMap<ModeCode, String>();
	this.command_completers = new HashMap<ModeCode, Completer>();

// initialize the map of accepted modes

	this.accepted_modes.put("editor", ModeCode.EDITOR);
	this.accepted_modes.put("symbolic_simulator", ModeCode.SYMBOLIC_SIMULATOR);
	this.accepted_modes.put("concrete_simulator", ModeCode.CONCRETE_SIMULATOR);
	this.accepted_modes.put("verifier", ModeCode.VERIFIER);

// initialize the map of mode names from the previous one

	for (String mode_name: this.accepted_modes.keySet()) {
		ModeCode mode_code = this.accepted_modes.get(mode_name);
		this.mode_names.put(mode_code, mode_name);
	}
}

/**
* set the prompt of this console manager
*/
private void setPrompt () {
	String mode = this.mode_names.get(this.context.getMode());
	this.reader.setPrompt("uppaal "+mode+"$");
}

/**
* parse a command line and return the correspon

/**
* run this console manager until the end of the session
* accepts commands from the console, execute them, display their result or error message
*/

public void run () throws EngineException, IOException {

// first initialize the console reader

	this.context.getEngineExpert().connectEngine();
	this.setPrompt();

	this.running = true;
	String line = null;

// read the next line while it is not null

	while (this.running) {
		line = reader.readLine();
		this.out. print("\033[H\033[2J");
this. out. flush();
		reader.clearScreen();
		this.out.flush();
		if (line==null) break;

// parse the provided command line, execute it and process the corresponding result

		try {
			Handler handler = this.parseCommandLine(line);
			CommandResult result = handler.handle();
			this.processResult(result);
			handler.clear();
			result.clear();
		}

// if an exception is thrown manage it 

		catch (ConsoleException e) {
			this.err.println(e.getMessage());
		}
	}
	this.context.getEngineExpert().disconnectEngine();
}

/**
* private method to parse a command line
* @param line the line to parse
* @return the object representing the command with the codes used by the handlers
* @exception an exception can be thrown if an unknown command or object was provided as input
*/
private Handler parseCommandLine (String line) {
	String token = null;
	int pos = 0;
	int begin = pos;

// skip white space:
	while (pos < line.length() && Character.isWhitespace(line.charAt(pos)))
		++pos;
	begin = pos;

// find white next space:
	while (pos < line.length() && !Character.isWhitespace(line.charAt(pos)))
		++pos;

// parse the name of the command if possible

	if (begin==pos) return null;
	token = line.substring(begin,pos);
	Handler handler = this.command_launcher.getCommandHandler(token);

	return handler;
}

/**
* private method to process a command result and execute all intended operations
* @param result the command result to process
*/
private void processResult (CommandResult result) {

// process the command result according to its code

	switch (result.getResultCode()) {

// if result is ok do nothing

	case OK:
	break;

// if return code is mode changed update the prompt

	case MODE_CHANGED:
	this.setPrompt();
	break;

// if return code is exit set the running condition to false

	case EXIT:
		this.running = false;
	break;

	default:
	break;
	}
}
}
