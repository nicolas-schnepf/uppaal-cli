package org.uppaal.cli.frontend;


import org.uppaal.cli.commands.SetHandler;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.context.Context;

import com.uppaal.engine.EngineException;
import org.uppaal.cli.exceptions.UnknownModeException;
import org.uppaal.cli.exceptions.ConsoleException;
import org.uppaal.cli.exceptions.SelectorException;
import org.uppaal.cli.enumerations.ModeCode;
import org.uppaal.cli.commands.CommandLauncher;
import org.uppaal.cli.commands.Handler;

import org.jline.utils.InfoCmp.Capability;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.LineReader;
import org.jline.reader.Reference;
import org.jline.terminal.TerminalBuilder;
import org.jline.terminal.Terminal;
import org.jline.builtins.Nano;
import org.jline.keymap.KeyMap;

import java.util.HashMap;
import java.util.HashSet;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.net.MalformedURLException;
import java.lang.reflect.Method;
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

//selection manager of this console manager
private SelectionManager selection_manager;

/**
* public constructor of a console Manager
* initializing its different attributes to default value
* @param context the uppaal context to handle
*/
public ConsoleManager (Context context) throws IOException, IOException {

	try {
		Terminal terminal = TerminalBuilder.builder().build();
		this.reader = LineReaderBuilder.builder().terminal(terminal).build();
		this.reader.setKeyMap(LineReader.MAIN);
		this.out = this.reader.getTerminal().writer();
		this.err = new PrintWriter(System.err);

		this.context = context;
		this.buffer = File.createTempFile("Uppaal", "");
		this.nano = new Nano(this.reader.getTerminal(), this.buffer);

		this.command_parser = new CommandParser(context);
		this.selection_manager = new SelectionManager(terminal, context);

// add the undo and redo widgets to this console manager

		Method undo = context.getClass().getMethod("undo");
		CommandWidgets undo_widget = new CommandWidgets(this.reader, context, undo, "undo");
		undo_widget.addWidget("undo-widget", undo_widget::onKey);
		undo_widget.getKeyMap().bind(new Reference("undo-widget"), KeyMap.ctrl('U'));

		Method redo = context.getClass().getMethod("redo");
		CommandWidgets redo_widget = new CommandWidgets(this.reader, context, redo, "redo");
		redo_widget.addWidget("redo-widget", redo_widget::onKey);
		redo_widget.getKeyMap().bind(new Reference("redo-widget"), KeyMap.ctrl('R'));
	} catch (Exception e) {
		System.err.println(e.getMessage());
		e.printStackTrace();
		System.exit(1);
	}
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

		case ADD_TEMPLATE:
		this.command_parser.addTemplate(result.getArgumentAt(0));
		break;

		case ADD_TEMPLATES:
		for (String template:result) this.command_parser.addTemplate(template);
		break;

		case REMOVE_TEMPLATE:
		this.command_parser.removeTemplate(result.getArgumentAt(0));
		break;

		case RENAME_TEMPLATE:
		this.command_parser.removeTemplate(result.getArgumentAt(0));
		this.command_parser.addTemplate(result.getArgumentAt(1));
		break;

		case CLEAR_TEMPLATES:
		this.command_parser.clearTemplates();
		break;

// if template selection is required select them and update the command parser

		case SELECT_TEMPLATES:
		for (String template: this.selection_manager.selectTemplates())
			this.command_parser.addTemplate(template);
		break;

		case SELECT_TRANSITION:
		this.selection_manager.selectTransition();
		break;

		case SELECT_STATE:
		this.selection_manager.selectState();
		break;

// if mode changed update the prompt and the command parser

		case MODE_CHANGED:
		String mode = result.getArgumentAt(0);
		this.prompt = "uppaal "+mode+"$";
		switch (mode) {
			case "editor":
			this.command_parser.setElementType("template");
			this.command_parser.setDefaultType("template");
			break;

			case "simulator":
			this.command_parser.setElementType("process");
			this.command_parser.setDefaultType("variable");
			break;
			case "verifier":
			this.command_parser.setElementType("template");
			this.command_parser.setDefaultType("option");
			break;
		}
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

// for an engine advize the user to disconnect and reconnect the engine

		 case EVALUATION_ERROR:
		this.err.println("Compilation error: impossible to achieve the operation that you asked.");
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
