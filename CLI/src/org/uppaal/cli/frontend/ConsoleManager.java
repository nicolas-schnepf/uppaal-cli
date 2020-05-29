package org.uppaal.cli.frontend;


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

// private command parser of this console manager
private CommandParser command_parser;

// current command handler of this console manager
private Handler handler;

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
	this.command_parser = new CommandParser(context);
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
	this.reader.setPrompt("uppaal editor$");

	this.running = true;
	String line = null;

// read the next line while it is not null

	while (this.running) {
		line = reader.readLine();

		if (line==null) break;
		else if (line.equals("")) continue;

// parse the provided command line, execute it and process the corresponding result

		try {
			this.handler = this.command_parser.parseCommand(line);
			CommandResult result = handler.handle();
			this.processResult(result);
			handler.clear();
			result.clear();
		}

// if an exception is thrown manage it 

		catch (ConsoleException e) {
			this.err.println(e.getMessage());
			e.printStackTrace();
			this.err.flush();
		}
	}
	this.context.getEngineExpert().disconnectEngine();
}

/**
* private method to process a command result and execute all intended operations
* @param result the command result to process
*/
private void processResult (CommandResult result) {

// process the command result according to its code

	switch (result.getResultCode()) {

// if result is ok print the result of the command

		case OK:
		for (String arg:result) {
			this.out.println(arg);
			this.out.flush();
		}
		break;

		case MODE_CHANGED:
		String mode = result.getArgumentAt(0);
		this.reader.setPrompt("uppaal "+mode+"$");
		break;

// if exit command was entered simply leave the uppaal command line interface

		case EXIT:
		this.running = false;
		break;

// for an io error display the name of the file

		 case IO_ERROR:
		String filename = result.getArgumentAt(0);
		this.err.println("IO error: file "+filename+" could not be loaded.");
		 break;

// for an engine advize the user to disconnect and reconnect the engine

		 case ENGINE_ERROR:
		this.err.println("Engine error: try to run disconnect followed by connect.");
		this.err.flush();
		 break;

// for a compilation error display all the errors which were encountered 

		 case COMPILATION_ERROR:
		this.err.println ("There were "+result.getArgumentNumber()+" compilation errors:\n");
		for (String error:result) this.out.println(error+"\n");
		break;
	}
}
}
