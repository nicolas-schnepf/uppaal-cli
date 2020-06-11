package org.uppaal.cli.frontend;


import org.uppaal.cli.commands.SetHandler;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.context.Context;

import com.uppaal.engine.EngineException;
import org.uppaal.cli.exceptions.UnknownModeException;
import org.uppaal.cli.exceptions.ConsoleException;
import org.uppaal.cli.enumerations.ModeCode;
import org.uppaal.cli.commands.CommandLauncher;
import org.uppaal.cli.commands.Handler;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.LineReader;
import org.jline.builtins.Nano;

import java.util.HashMap;
import java.util.HashSet;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.Scanner;

/**
* console manager handling the interaction with the user input
* displaying command results and error messages
*/

public class ConsoleManager {

// uppaal context for this command manager

private Context context;

// console reader for this console manager
private LineReader reader;

// nano text editor for this console manager
private Nano nano;

// temporary text buffer for this console manager
private File buffer;



// console prompt
private String prompt;

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
	this.reader = LineReaderBuilder.builder().build();
            	this.out = this.reader.getTerminal().writer();
	this.buffer = File.createTempFile("Uppaal", "");
	this.nano = new Nano(this.reader.getTerminal(), this.buffer);
	this.err = new PrintWriter(System.err);
	this.command_parser = new CommandParser(context);
}

/**
* run this console manager until the end of the session
* accepts commands from the console, execute them, display their result or error message
*/

public void run () throws EngineException, IOException {

// first initialize the console reader

	this.context.getEngineExpert().connectEngine();
	this.prompt = "uppaal editor$";

	this.running = true;
	String line = null;

// read the next line while it is not null

	while (this.running) {
		try {
			line = reader.readLine(this.prompt);
			if (line.equals("")) continue;

// parse the provided command line, execute it and process the corresponding result

			this.handler = this.command_parser.parseCommand(line);
			if (this.command_parser.getRequireValue()) this.editProperty();
			CommandResult result = handler.handle();
			this.processResult(result);
			handler.clear();
			result.clear();
		}

// if an exception is thrown manage it 

catch (EndOfFileException e) {
		this.running = false;
	}

		catch (ConsoleException e) {
			this.err.println(e.getMessage());
			e.printStackTrace();
			this.err.flush();
		}
	}
	this.context.getEngineExpert().disconnectEngine();
}

/**
* edit a property and set it according to the value entered by the user
*/
private void editProperty () throws IOException, FileNotFoundException {

// get the current value of the property to edit and write it to the text buffer of this console manager

	String value = ((SetHandler) this.handler).getPropertyValue();
	BufferedWriter writer= new BufferedWriter(new FileWriter(this.buffer));
writer.write(value);
writer.close();

// run the nano editor with the content of the text buffer

this.nano.open(this.buffer.getAbsolutePath());
this.nano.run();

// retrieve the new value from the text buffer

Scanner scanner = new Scanner(this.buffer);
scanner.useDelimiter("\\Z");
	value = scanner.next();
	scanner.close();

// add the new value as parameter to the command handler

	this.handler.addArgument(value);
	this.command_parser.cancelRequireValue();
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
		this.prompt = "uppaal "+mode+"$";
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
