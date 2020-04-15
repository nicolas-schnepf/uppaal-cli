package org.uppaal.cli;

import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ObjectCode;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.commands.Command;
import org.uppaal.cli.context.Context;

import com.uppaal.engine.EngineException;
import org.uppaal.cli.exceptions.UnknownCommandException;
import org.uppaal.cli.exceptions.UnknownModeException;
import org.uppaal.cli.exceptions.ConsoleException;
import org.uppaal.cli.enumerations.ModeCode;
import org.uppaal.cli.commands.CommandHandler;
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

// private command to update at each new entree of the user
private Command command;

// private unknown command exception to throw when receiving an unknown command
private UnknownCommandException unknown_command_exception;

// hash map of accepted commands
private HashMap<String, OperationCode> accepted_commands;

// reverse hash map from command codes to command names
private HashMap<OperationCode, String> command_names;

// hash map of accepted modes for this command manager
private HashMap <String, ModeCode> accepted_modes;

// hashmap of mode names for this console manager
private HashMap<ModeCode, String> mode_names;

// hash map of accepted object codes for this console manager
private HashMap<String, ObjectCode> accepted_objects;

// hash map of object names for this console manager
private HashMap<ObjectCode, String> object_names;

// hash map of accepted command completers for this console manager
private HashMap <ModeCode, Completer> command_completers;

// active command completer for this console manager
private Completer command_completer;

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
	this.command = new Command();
	this.unknown_command_exception = new UnknownCommandException();
            	this.out = new PrintWriter(reader.getOutput());
	this.err = new PrintWriter(System.err);
	this.command_handler = new CommandHandler(context);
	this.accepted_commands = new HashMap<String, OperationCode>();
	this.command_names = new HashMap<OperationCode, String>();
	this.accepted_modes = new HashMap<String, ModeCode>();
	this.mode_names = new HashMap<ModeCode, String>();
	this.accepted_objects = new HashMap<String, ObjectCode>();
	this.object_names = new HashMap<ObjectCode, String>();
	this.command_completers = new HashMap<ModeCode, Completer>();

// initialize the map of accepted commands

	this.accepted_commands.put ("start", OperationCode.START);
	this.accepted_commands.put("exit", OperationCode.EXIT);
	this.accepted_commands.put("compile", OperationCode.COMPILE);
	this.accepted_commands.put("connect", OperationCode.CONNECT);
	this.accepted_commands.put("disconnect", OperationCode.DISCONNECT);
	this.accepted_commands.put("export", OperationCode.EXPORT);
	this.accepted_commands.put("import", OperationCode.IMPORT);
	this.accepted_commands.put("check", OperationCode.CHECK);
	this.accepted_commands.put("set", OperationCode.SET);
	this.accepted_commands.put("unset", OperationCode.UNSET);
	this.accepted_commands.put("undo", OperationCode.UNDO);
	this.accepted_commands.put("redo", OperationCode.REDO);
	this.accepted_commands.put("show", OperationCode.SHOW);
	this.accepted_commands.put("reset", OperationCode.RESET);
	this.accepted_commands.put("select", OperationCode.SELECT);
	this.accepted_commands.put("finish", OperationCode.FINISH);
	this.accepted_commands.put("unselect", OperationCode.UNSELECT);
	this.accepted_commands.put("help", OperationCode.HELP);

// initialize the map of command names from the previous one

	for (String command_name: this.accepted_commands.keySet()) {
		OperationCode code = this.accepted_commands.get(command_name);
		this.command_names.put(code, command_name);
	}

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

// initialize the map of accepted objects

	this.accepted_objects.put("document", ObjectCode.DOCUMENT);
	this.accepted_objects.put("queries", ObjectCode.QUERIES);
	this.accepted_objects.put("templates", ObjectCode.TEMPLATES);
	this.accepted_objects.put("query", ObjectCode.QUERY);
	this.accepted_objects.put("formula", ObjectCode.FORMULA);
	this.accepted_objects.put("comment", ObjectCode.COMMENT);
	this.accepted_objects.put("template", ObjectCode.TEMPLATE);
	this.accepted_objects.put("trace", ObjectCode.TRACE);
	this.accepted_objects.put("state", ObjectCode.STATE);
	this.accepted_objects.put("preview", ObjectCode.PREVIEW);
	this.accepted_objects.put("next", ObjectCode.NEXT);
	this.accepted_objects.put("locations", ObjectCode.LOCATIONS);
	this.accepted_objects.put("edges", ObjectCode.EDGES);
	this.accepted_objects.put("location", ObjectCode.LOCATION);
	this.accepted_objects.put("invariant", ObjectCode.INVARIANT);
	this.accepted_objects.put("init", ObjectCode.INIT);
	this.accepted_objects.put("committed", ObjectCode.COMMITTED);
	this.accepted_objects.put("edge", ObjectCode.EDGE);
	this.accepted_objects.put("source", ObjectCode.SOURCE);
	this.accepted_objects.put("target", ObjectCode.TARGET);
	this.accepted_objects.put("select", ObjectCode.SELECT);
	this.accepted_objects.put("guard", ObjectCode.GUARD);
	this.accepted_objects.put("sync", ObjectCode.SYNC);
	this.accepted_objects.put("assign", ObjectCode.ASSIGN);
	this.accepted_objects.put("variable", ObjectCode.VARIABLE);
	this.accepted_objects.put("variables", ObjectCode.VARIABLES);
	this.accepted_objects.put("declaration", ObjectCode.DECLARATION);
	this.accepted_objects.put("system", ObjectCode.SYSTEM);
	this.accepted_objects.put("option", ObjectCode.OPTION);
	this.accepted_objects.put("options", ObjectCode.OPTIONS);
	this.accepted_objects.put("clock", ObjectCode.CLOCK);
	this.accepted_objects.put("transition", ObjectCode.TRANSITION);
	this.accepted_objects.put("transitions", ObjectCode.TRANSITIONS);
	this.accepted_objects.put("constraint", ObjectCode.CONSTRAINT);
	this.accepted_objects.put("clocks", ObjectCode.CLOCKS);

// initialize the map of object names from the previous one

	for (String object_name: this.accepted_objects.keySet()) {
		ObjectCode object_code = this.accepted_objects.get(object_name);
		this.object_names.put(object_code, object_name);
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
			Command command = this.parseCommandLine(line);
			CommandResult result = this.command_handler.handle(command);
			this.processResult(result);
		}

// if an exception is thrown manage it 

		catch (ConsoleException e) {
			this.processException(e);
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
private Command parseCommandLine (String line) {
	Command command = this.command;
	command.clear();
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

	if (begin==pos) return command;
	token = line.substring(begin,pos);

	if (this.accepted_commands.containsKey(token))
		this.command.setOperationCode(this.accepted_commands.get(token));
	else {
		this.unknown_command_exception.setCommand(token);
		throw this.unknown_command_exception;
	}

        // skip white space:
        
	while (pos < line.length() && Character.isWhitespace(line.charAt(pos)))
		++pos;        
	begin = pos;

// skip white spaces
	while (pos < line.length() && !Character.isWhitespace(line.charAt(pos)))
		++pos;

// compute the object code of this command

	if (begin==pos) return command;
	token = line.substring(begin,pos);

	if (this.accepted_objects.containsKey(token))
		command.setObjectCode(this.accepted_objects.get(token));
	else if (this.accepted_modes.containsKey(token)) {
		command.setObjectCode(ObjectCode.MODE);
		command.setMode(this.accepted_modes.get(token));
	} else
		this.command.addArgument(token);

// finally parse the remaining arguments of the command line

	while(begin!=pos) {
// skip white space:

		while (pos < line.length() && Character.isWhitespace(line.charAt(pos))) ++pos;        
		begin = pos;

// skip white spaces and add the next argument if any

		while (pos < line.length() && !Character.isWhitespace(line.charAt(pos))) ++pos;
		if (begin!=pos) {
			token = line.substring(begin,pos);
			command.addArgument(token);
		}
	}

	return command;
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

/**
* process an exception and possibly display a help message
* @param exception the exception to process
*/

private void processException (ConsoleException exception) {

// process the exception according to its type

	switch (exception.getExceptionCode()) {

// for an unknown command exception print a corresponding error message and exit

	case UNKNOWN_COMMAND:
		String unknown_command = ((UnknownCommandException)exception).getCommand();
		this.err.println("Unknown command: "+unknown_command);
		this.err.flush();
	break;

// for an unknown mode exception print a corresponding error message and exit

	case UNKNOWN_MODE:
//		String unknown_mode = ((UnknownModeException)exception).getMode();
//		this.err.println("Unknown mode: "+unknown_mode);
//		this.err.flush();
	break;

	default:
	break;
	}
}
}
