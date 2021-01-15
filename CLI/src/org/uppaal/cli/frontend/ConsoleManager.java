package org.uppaal.cli.frontend;


import org.uppaal.cli.commands.SetHandler;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.context.Context;

import com.uppaal.engine.EngineException;
import org.uppaal.cli.exceptions.UnknownModeException;
import org.uppaal.cli.exceptions.ConsoleException;
import org.uppaal.cli.exceptions.SelectorException;
import org.uppaal.cli.context.ModeCode;
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
import org.jline.reader.Completer;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.completer.NullCompleter;

import java.util.HashMap;
import java.util.HashSet;
import java.io.OutputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStream;
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

// private command completer of this console manager
private CommandCompleter command_completer;

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

// buffered reader of this console manager
private BufferedReader buffered_reader;

// private filename of this console manager
private String filename;

// line index of this console manager
private int line_index;

public ConsoleManager (Context context, String filename) throws IOException {
	this.context = context;
	this.out = new PrintWriter(System.out);
	this.err = new PrintWriter(System.err);
		this.command_parser = new CommandParser(context);
	this.openFile(filename);
}

/**
* public constructor of a console Manager
* initializing its different attributes to default value
* @param context the uppaal context to handle
* @throws IOException an exception if it was not possible to open the provided file
*/
public ConsoleManager (Context context) throws IOException {

	try {
		this.command_parser = new CommandParser(context);
		this.command_completer = new CommandCompleter(context);
		this.command_parser.setCommandCompleter(this.command_completer);
		Terminal terminal = TerminalBuilder.builder().build();
		this.reader = LineReaderBuilder.builder().terminal(terminal)
		.parser(this.command_parser)
		.completer(this.command_completer).build();
		this.reader.setKeyMap(LineReader.MAIN);
		this.out = new PrintWriter(this.reader.getTerminal().writer());
		this.err = new PrintWriter(System.err);

		this.context = context;
		this.buffer = File.createTempFile("Uppaal", "");
		this.nano = new Nano(this.reader.getTerminal(), this.buffer);
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
* open a new file
* @param filename the name of the file to open
* @throws IOException an io exception if there were some problem with the file
* @throws FileNotFoundException an exception if the file was not found
*/
public void openFile(String filename) throws IOException, FileNotFoundException {
	this.buffered_reader = new BufferedReader(new FileReader(filename));
		this.filename = filename;
		this.line_index = 0;
}

/**
* print the welcome message of the uppaal command line interface
*/
public void printWelcomeMessage() {
	this.out.println("Type \"help\" for more information.");
	this.out.println("");
	this.out.flush();
}

/**
* print the index of the line where the error happened
*/
public void printLineIndex() {

// print the index of the current line in the current file

	this.err.print ("File ");
	this.err.print (this.filename);
	this.err.print(", line ");
	this.err.print(this.line_index);
	this.err.println(":");
	this.err.flush();

// close the current buffered reader

	this.running = false;
	try {this.buffered_reader.close(); }
	catch (IOException e) {}
	this.buffered_reader = null;
}

/**
* print an error message given as argument
* @param message the message to print
*/
public void printError (String message) {
	if (this.buffered_reader!=null) this.printLineIndex();
	this.err.println(message);
	this.err.flush();
}

/**
* run this console manager until the end of the session
* accepts commands from the console, execute them, display their result or error message
* @param main a boolean stating if we are at the top of the module stack
* @throws EngineException an exception if some error was encountered with the uppaal engine
* @throws IOException an exception if it was not possible to open the provided file
*/

public void run (boolean main) throws EngineException, IOException {

// first initialize the console reader if necessary

	if (main) {
		this.context.getEngineExpert().connectEngine();
		if (this.command_completer!=null)
			this.command_completer.setOptions(this.context.getOptionExpert().getOptions());
		this.prompt = "uppaal editor$";
	}

	this.running = true;
	String line = null;

// read the next line while it is not null

	while (this.running) {
		try {

// if a line reader is defined read the next line and setup the corresponding command handler

			if (this.reader!=null) {
				if (this.command_parser.getDelimiter()==null) line = reader.readLine(this.prompt);
				else line = reader.readLine("");
				if (this.command_parser.hasConsoleException()) 
					throw this.command_parser.getConsoleException();
				else {
					this.handler = this.command_parser.getHandler();
					if (this.command_parser.getRequireValue()) this.editProperty();
				}
			}

// otherwise get the next line from the buffered reader and parse it

			else {
				line = this.buffered_reader.readLine();
				if (line==null) break;
				else this.line_index ++;
				this.handler = this.command_parser.parseCommand(line);
				if (this.command_parser.getRequireValue()) {
					this.printError ("Property edition only allowed in interactive mode.");
					this.handler = null;
					}
			}

// execute the parsed command and process the corresponding result

			if (this.handler==null || this.command_parser.getDelimiter()!=null) continue;
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
		this.printError(e.getMessage());
			e.printStackTrace();
		}
	}

// disconnect the engine if we are at the top of the module stack, otherwise set running to true

	if (main) this.context.getEngineExpert().disconnectEngine();
	else if (this.buffered_reader!=null) {
		this.buffered_reader.close();
		this.running = true;
	}
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
	if (scanner.hasNext()) this.handler.addArgument(scanner.next());
	else this.handler.addArgument(value);
	scanner.close();
	this.command_parser.cancelRequireValue();
}

/**
* private method to process a command result and execute all intended operations
* @param result the command result to process
* @throws EngineException an engine exception if there was some problem with the engine
* @param IOError an error if a file was not found
*/
private void processResult (CommandResult result) throws EngineException, IOException {

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

// if a file path is provided execute the corresponding module

		if (result.getArgumentNumber()==1) {
			BufferedReader buffered_reader = this.buffered_reader;
			String filename = this.filename;
			int line_index = this.line_index;


			this.openFile(result.getArgumentAt(0));
			this.run(false);

			if (this.buffered_reader!=null && buffered_reader!=null) {
				this.buffered_reader = buffered_reader;
				this.filename = filename;
				this.line_index = line_index;
			}
		} else if (this.buffered_reader==null) {
			for (String template: this.selection_manager.selectTemplates())
				this.command_parser.addTemplate(template);
			} else
				this.printError("Template selection only allowed in interactive mode.");
		break;

		case SELECT_TRANSITION:
			if (this.buffered_reader==null) {
			this.selection_manager.selectTransition();
			} else
				this.printError("Transition selection only allowed in interactive mode.");
		break;

		case SELECT_STATE:
			if (this.buffered_reader==null) {
			this.selection_manager.selectState();
			} else
				this.printError("State selection only allowed in interactive mode.");
		break;

		case SELECT_QUERIES:
			if (this.buffered_reader==null) {
			this.selection_manager.selectQueries();
			} else
				this.printError("Query selection only allowed in interactive mode.");
		break;

		case SELECT_DATA:
			if (this.buffered_reader==null) {
			this.selection_manager.selectData();
					} else
				this.printError("Data selection only allowed in interactive mode.");
		break;

// if the precision of the data selector needs to be updated perform it

		case SET_PRECISION:
		double precision = Double.parseDouble(result.removeLastArgument());
		this.selection_manager.setPrecision(precision);
		break;

// if mode changed update the prompt and the command parser

		case MODE_CHANGED:
		String mode = result.removeLastArgument();
		if (!this.prompt.equals("")) this.prompt = "uppaal "+mode+"$";
		if (result.getArgumentNumber()>0) {
			for (String argument: result) {
				this.err.println(argument);
				this.err.flush();
			}
		}

		switch (mode) {
			case "editor":
			case "verifier":
			this.command_parser.setElementType("template");
			this.command_parser.setDefaultType("template");
			break;

			case "simulator":
			this.command_parser.setElementType("process");
			this.command_parser.setDefaultType("variable");
			break;
		}
		break;

// if exit command was entered simply leave the uppaal command line interface

		case EXIT:
		this.running = false;
		if (this.buffered_reader!=null) {
			this.buffered_reader.close();
			this.buffered_reader = null;
		}
		break;

// for an io error display the name of the file

		 case IO_ERROR:
		String filename = result.getArgumentAt(0);
		this.err.println("IO error: file "+filename+" could not be loaded.");
		this.err.flush();
		 break;

// for an engine advize the user to disconnect and reconnect the engine

		 case ENGINE_ERROR:
		 this.err.println(result.getArgumentAt(0));
		 this.err.flush();
		this.err.println("Engine error: try to fix the problem or to run disconnect followed by connect.");
		this.err.flush();
		 break;

// for an engine advize the user to disconnect and reconnect the engine

		 case EVALUATION_ERROR:
		this.err.println("Compilation error: impossible to achieve the operation that you asked.");
		this.err.flush();
		 break;

// for a compilation error display all the errors which were encountered 

		 case COMPILATION_ERROR:
		if (this.buffered_reader!=null) this.printLineIndex();
		this.err.println ("There were "+result.getArgumentNumber()+" compilation errors:\n");
		for (String error:result) this.err.println(error+"\n");
		this.err.flush();
		break;
	}
}
}
