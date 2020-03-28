package org.uppaal.cli;

import com.uppaal.engine.EngineException;
import org.uppaal.cli.exceptions.UnknownCommandException;
import org.uppaal.cli.exceptions.UnknownModeException;
import org.uppaal.cli.exceptions.ConsoleException;
import org.uppaal.cli.handlers.CommandHandler;
import org.uppaal.cli.handlers.Handler;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import jline.console.completer.FileNameCompleter;
import jline.console.completer.StringsCompleter;
import jline.console.completer.NullCompleter;
import jline.console.completer.ArgumentCompleter;


import java.util.HashMap;
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
private HashMap<String, Command.CommandCode> accepted_commands;

// reverse hash map from command codes to command names
private HashMap<Command.CommandCode, String> command_names;

// hash map of accepted modes for this command manager
private HashMap <String, Handler.HandlerCode> accepted_modes;

// hashmap of mode names for this console manager
private HashMap<Handler.HandlerCode, String> mode_names;

// hash map of accepted object codes for this console manager
private HashMap<String, Command.ObjectCode> accepted_objects;

// hash map of object names for this console manager
private HashMap<Command.ObjectCode, String> object_names;

// hash map of accepted command completers for this console manager
private HashMap <Handler.HandlerCode, Completer> command_completers;

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
	this.accepted_commands = new HashMap<String, Command.CommandCode>();
	this.command_names = new HashMap<Command.CommandCode, String>();
	this.accepted_modes = new HashMap<String, Handler.HandlerCode>();
	this.mode_names = new HashMap<Handler.HandlerCode, String>();
	this.accepted_objects = new HashMap<String, Command.ObjectCode>();
	this.object_names = new HashMap<Command.ObjectCode, String>();
	this.command_completers = new HashMap<Handler.HandlerCode, Completer>();

// initialize the map of accepted commands

	this.accepted_commands.put ("start", Command.CommandCode.START);
	this.accepted_commands.put("exit", Command.CommandCode.EXIT);
	this.accepted_commands.put("export", Command.CommandCode.EXPORT);
	this.accepted_commands.put("import", Command.CommandCode.IMPORT);
	this.accepted_commands.put("check", Command.CommandCode.CHECK);
	this.accepted_commands.put("clear", Command.CommandCode.CLEAR);
	this.accepted_commands.put("add", Command.CommandCode.ADD);
	this.accepted_commands.put("set", Command.CommandCode.SET);
	this.accepted_commands.put("unset", Command.CommandCode.UNSET);
	this.accepted_commands.put("show", Command.CommandCode.SHOW);
	this.accepted_commands.put("remove", Command.CommandCode.REMOVE);
	this.accepted_commands.put("rename", Command.CommandCode.RENAME);
	this.accepted_commands.put("select", Command.CommandCode.SELECT);
	this.accepted_commands.put("preview", Command.CommandCode.PREVIEW);
	this.accepted_commands.put("next", Command.CommandCode.NEXT);
	this.accepted_commands.put("finish", Command.CommandCode.FINISH);
	this.accepted_commands.put("unselect", Command.CommandCode.UNSELECT);
	this.accepted_commands.put("help", Command.CommandCode.HELP);

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

// initialize the map of accepted objects

	this.accepted_objects.put("document", Command.ObjectCode.DOCUMENT);
	this.accepted_objects.put("queries", Command.ObjectCode.QUERIES);
	this.accepted_objects.put("templates", Command.ObjectCode.TEMPLATES);
	this.accepted_objects.put("query", Command.ObjectCode.QUERY);
	this.accepted_objects.put("formula", Command.ObjectCode.FORMULA);
	this.accepted_objects.put("comment", Command.ObjectCode.COMMENT);
	this.accepted_objects.put("template", Command.ObjectCode.TEMPLATE);
	this.accepted_objects.put("trace", Command.ObjectCode.TRACE);
	this.accepted_objects.put("locations", Command.ObjectCode.LOCATIONS);
	this.accepted_objects.put("edges", Command.ObjectCode.EDGES);
	this.accepted_objects.put("location", Command.ObjectCode.LOCATION);
	this.accepted_objects.put("invariant", Command.ObjectCode.INVARIANT);
	this.accepted_objects.put("edge", Command.ObjectCode.EDGE);
	this.accepted_objects.put("source", Command.ObjectCode.SOURCE);
	this.accepted_objects.put("target", Command.ObjectCode.TARGET);
	this.accepted_objects.put("select", Command.ObjectCode.SELECT);
	this.accepted_objects.put("guard", Command.ObjectCode.GUARD);
	this.accepted_objects.put("sync", Command.ObjectCode.SYNC);
	this.accepted_objects.put("assign", Command.ObjectCode.ASSIGN);
	this.accepted_objects.put("variable", Command.ObjectCode.VARIABLE);
	this.accepted_objects.put("variables", Command.ObjectCode.VARIABLES);
	this.accepted_objects.put("declaration", Command.ObjectCode.DECLARATION);
	this.accepted_objects.put("system", Command.ObjectCode.SYSTEM);
	this.accepted_objects.put("option", Command.ObjectCode.OPTION);
	this.accepted_objects.put("options", Command.ObjectCode.OPTIONS);
	this.accepted_objects.put("clock", Command.ObjectCode.CLOCK);
	this.accepted_objects.put("transition", Command.ObjectCode.TRANSITION);
	this.accepted_objects.put("transitions", Command.ObjectCode.TRANSITIONS);
	this.accepted_objects.put("constraint", Command.ObjectCode.CONSTRAINT);
	this.accepted_objects.put("clocks", Command.ObjectCode.CLOCKS);

// initialize the map of object names from the previous one

	for (String object_name: this.accepted_objects.keySet()) {
		Command.ObjectCode object_code = this.accepted_objects.get(object_name);
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
* @return the command completer for the current mode
*/
private Completer getCommandCompleter () {

// if there is already a completer for the current mode in the corresponding hash map simply return it

	Handler.HandlerCode active_mode= this.command_handler.getMode();
	if (this.command_completers.containsKey(active_mode))
		return this.command_completers.get(active_mode);

// create a list of commands for the current active mode

	Command.CommandCode[] active_commands = this.command_handler.getActiveCommands();
	LinkedList<String> commands = new LinkedList<String>();

	for (Command.CommandCode command_code:active_commands) {
		String command_name = this.command_names.get(command_code);
		commands.add(command_name);
	}

// create a list of objects for the current active mode

	Command.ObjectCode[] accepted_objects = this.command_handler.getAcceptedObjects();
	LinkedList<String> objects = new LinkedList<String>();

	for (Command.ObjectCode object_code:accepted_objects) {
		String object_name = this.object_names.get(object_code);
		objects.add(object_name);
	}

// create the new completer, add it to the hash map and return it

	LinkedList<Completer> completers = new LinkedList<Completer>();
	completers.add(new StringsCompleter(commands));
	completers.add(new StringsCompleter(objects));
	completers.add(new FileNameCompleter());
	completers.add(new NullCompleter());

	ArgumentCompleter completer = new ArgumentCompleter(completers);
	this.command_completers.put(active_mode, completer);
	return completer;
}

/**
* set the completers for the current mode
*/
private void setCompleters() {
	this.reader.removeCompleter(this.command_completer);
	this.command_completer = this.getCommandCompleter();
	this.reader.addCompleter(this.command_completer);
}

/**
* parse a command line and return the correspon

/**
* run this console manager until the end of the session
* accepts commands from the console, execute them, display their result or error message
*/

public void run () throws EngineException, IOException {

// first initialize the console reader

	this.context.connectEngine();
	this.setPrompt();
	this.command_completer = this.getCommandCompleter();
	this.reader.addCompleter(this.command_completer);

	this.running = true;
	String line = null;

// read the next line while it is not null

	while (this.running) {
		line = reader.readLine();
		if (line==null) break;

// parse the provided command line, execute it and process the corresponding result

		try {
			Command command = this.parseCommandLine(line);
			CommandResult result = this.command_handler.handle(command);
			this.processResult(result);
		}

// if an exception is thrown manage it 

		catch (MalformedURLException e) {
			this.err.println(e.getMessage());
			this.err.flush();
		} catch (IOException e) {
				this.err.println(e.getMessage());
				this.err.flush();
		} catch (ConsoleException e) {
			this.processException(e);
		}
	}
	this.context.disconnectEngine();
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
		this.command.setCommandCode(this.accepted_commands.get(token));
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
		command.setObjectCode(Command.ObjectCode.MODE);
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
	this.setCompleters();
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
		String unknown_mode = ((UnknownModeException)exception).getMode();
		this.err.println("Unknown mode: "+unknown_mode);
		this.err.flush();
	break;

	default:
	break;
	}
}
}