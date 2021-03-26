package org.uppaal.cli.frontend;

/**
* command parser, parse a command line and return the corresponding handler
*/

import org.uppaal.cli.exceptions.UnknownCommandException;
import org.uppaal.cli.exceptions.ParserException;
import org.uppaal.cli.exceptions.ConsoleException;
import org.uppaal.cli.commands.DefaultHandler;
import org.uppaal.cli.commands.SetHandler;
import org.uppaal.cli.commands.CommandLauncher;
import org.uppaal.cli.commands.Handler;
import org.uppaal.cli.context.Context;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;

public class CommandParser extends AbstractParser {

// private reference parser of this command parser
private ReferenceParser reference_parser;

// map of command methods to invoke
private HashMap<String, Method> command_map;

// command launcher of this command parser
private CommandLauncher command_launcher;

// private type checker of this command parser
private TypeChecker type_checker;

public CommandParser(Context context) {
	super(context);
	this.command_line = new CommandLine();
	this.lexer = new CommandLexer();
	this.command_launcher = new CommandLauncher(context);
	this.command_map = new HashMap<String, Method>();
	this.templates = new HashSet<String>();
	this.type_checker = new TypeChecker();
	this.buffer = new StringBuffer();

	this.reference_parser = new ReferenceParser(this.context);
	this.reference_parser.setTemplates(this.templates);
	this.reference_parser.setCommandLine(this.command_line);
	this.reference_parser.setLexer(this.lexer);
	this.reference_parser.setBuffer(this.buffer);
	this.reference_parser.setTypeChecker(this.type_checker);

	try {
	this.command_map.put("start", this.getClass().getMethod("parseStart"));
	this.command_map.put("load", this.getClass().getMethod("parseLoad"));
	this.command_map.put("save", this.getClass().getMethod("parseSave"));
	this.command_map.put("unset", this.getClass().getMethod("parseUnset"));
	this.command_map.put("set", this.getClass().getMethod("parseSet"));
	this.command_map.put("reset", this.getClass().getMethod("parseReset"));
	this.command_map.put("show", this.getClass().getMethod("parseShow"));
	this.command_map.put("check", this.getClass().getMethod("parseCheck"));
	this.command_map.put("help", this.getClass().getMethod("parseHelp"));
	this.command_map.put("exit", this.getClass().getMethod("parseExit"));
	this.command_map.put("connect", this.getClass().getMethod("parseConnect"));
	this.command_map.put("disconnect", this.getClass().getMethod("parseDisconnect"));
	this.command_map.put("compile", this.getClass().getMethod("parseCompile"));
	} catch (Exception e) {
		System.out.println(e.getMessage());
		e.printStackTrace();
		System.exit(1);
	}
}

@Override
protected String getNextToken() {
	super.getNextToken();
	this.reference_parser.setToken(this.token);
	return this.token;
}

@Override
protected String checkNextToken(String ...value) {
	super.checkNextToken(value);
	this.reference_parser.setToken(this.token);
	return this.token;
}

@Override
protected String checkNextTokenType(TokenType... types) {
	super.checkNextTokenType(types);
	this.reference_parser.setToken(this.token);
	return this.token;
}

@Override
public String parseRef () {
	this.token = this.reference_parser.parseRef();
	this.type = this.reference_parser.getType();
	this.delimiter = this.reference_parser.getDelimiter();
	this.require_value = this.reference_parser.getRequireValue();
	return this.token;
}

@Override
public void setType (String type) {
	this.type = type;
	this.reference_parser.setType(type);
}

@Override
public void setElementType(String type) {
	this.reference_parser.setElementType(type);
}

@Override
public void setDefaultType(String type) {
	this.reference_parser.setDefaultType(type);
}

@Override
public void setCommandCompleter (CommandCompleter completer) {
	super.setCommandCompleter(completer);
	this.command_completer.setCommands(this.command_map.keySet());
	this.reference_parser.setCommandCompleter(this.command_completer);
}

/**
* parse a command and return the corresponding parsed line
* @param line the line to parse
* @param cursor the current cursor in the line
* @param ctx the parsing context
* @return the parsed line
*/
public ParsedLine parse(String line, int cursor, ParseContext ctx) {

// reset the command line and parse the command

	try {
		this.console_exception = null;
		this.command_line.reset(line);
		this.command_completer.clear();
		this.command_line.setCursor(cursor);
		this.parseCommand(line);
	} catch (ConsoleException e) {
		this.console_exception = e;
	}

return this.command_line;
}
/**
* parse a command line and return the corresponding handler will all the intended information
* @param line the line to parse
* @return the handler to execute to manage the command
*/
public Handler parseCommand (String line) {
	if (this.delimiter==null && line.equals("")) return this.handler = null;
	this.lexer.setLine(line);

// if we are in a delimited string parse the line and return the handler if the string was completed

	if (this.delimiter!=null) {
		String value = this.reference_parser.parsePropertyValue();

		if (value!=null) {
			((SetHandler)this.handler).addProperty(this.type, value);
			this.delimiter = null;

		if (this.getNextToken()!=null) this.throwParserException(this.token, null);
			return this.handler;
		} else return null;
	}

// otherwise parse the command entered by the user

	this.setType("document");
	String command = this.getNextToken();

	if (command==null)
		return null;

// if the command entered by the user is a well known keyword launch the requested command handler

	else if (this.command_map.keySet().contains(command)) {
		this.handler = this.command_launcher.getCommandHandler(command);
		this.reference_parser.setHandler(this.handler);
		this.handler.clear();
		if (this.command_completer!=null) {
			this.command_completer.setCommand(command);
			this.command_line.addWord(command);
		}
	}

// else if the line entered by the user starts with the name of a template setup a set command

	else {
		this.command_line.addWord(command);
		this.handler = this.command_launcher.getCommandHandler("set");
		this.handler.clear();

// setup the type according to the entered keyword

		switch (command) {
			case "system":
			case "declaration":
			this.setType(command);
			break;

			case "queries":
			this.setType("query");
			break;

// by default if the word entered by the user is a template parse it as such

			default:
			if (this.templates.contains(command)) this.setType("template");

// and otherwise throw a parser exception

			else {
				this.unknown_command_exception.setCommand(command);
				throw this.unknown_command_exception;
			}
			break;
		}

		if (this.command_completer!=null) this.command_completer.setCommand("set");
		command = "set";
	} 

// if the command is correct launch the corresponding handler and execute the appropriate method

	try {
		this.command_map.get(command).invoke(this);
	} catch (IllegalAccessException e) {
		System.out.println(e.getMessage());
		e.printStackTrace();
		System.exit(1);
	} catch (InvocationTargetException e) {
		if (e.getTargetException() instanceof ConsoleException)
			throw (ConsoleException)e.getTargetException();

		System.out.println(e.getMessage());
		e.printStackTrace();
		System.exit(1);
	}

// finally check that the line is well terminated before returning the handler

		if (this.getNextToken()!=null) this.throwParserException(this.token, null);
		return this.handler;
}

/**
* parse a start command requiring a new mode to be started
* start MODE (type = (symbolic | concrete)?
*/

public void parseStart () {

// check that the command start with the keyword start, then parse its mode and optionally its type

	this.checkToken("start");
	((DefaultHandler)this.handler).setCommand("start");
	String mode = this.checkNextToken("editor", "simulator", "verifier");
	this.checkTokenType(TokenType.STRING);
	this.handler.addArgument(mode);

	if (this.getNextToken()!=null && this.token.equals("--type")) {
		if (mode.equals("simulator")) {
			this.checkNextToken("=");
			String sub_mode = this.checkNextToken("symbolic", "concrete");
			this.handler.addArgument(sub_mode);
		} else this.throwParserException("type", "EOF");
	}
		}

/**
* parse a load command
* load ( document | templates | queries ) FILENAME
*/
public void parseLoad () {

// check the type of the command and its syntax

	if (this.getNextToken()==null)
		this.throwParserException (null, "\"document\", \"templates\", \"strategy\", \"data\" or NAME");

	switch (this.token) {
	case "document":
		case "queries":
		case "templates":
		case "data":
		this.handler.setObjectType(this.token);
		break;

		default:
		this.handler.setObjectType("strategy");
		this.handler.addArgument(this.token);
		break;
	}
	
	this.command_line.addWord(this.token);
	this.checkNextToken("from");
	this.command_line.addWord(this.token);
	String filename = this.lexer.parseFilename();
	if (filename==null) this.throwParserException(null, "FILENAME");
	this.command_line.addWord(filename);
	this.handler.addArgument(filename);
}

/**
* parse a save command
* save ( document | trace | queries ) FILENAME
*/
public void parseSave () {

// check that the command starts with  export, is followed by a valid type followed by a string

	if (this.getNextToken()==null)
		this.throwParserException (null, "document, templates, queries, data or NAME");

	switch (this.token) {
	case "document":
		case "queries":
		case "trace":
		case "data":
		this.handler.setObjectType(this.token);
		break;

		default:
		this.handler.setObjectType("strategy");
		this.handler.addArgument(this.token);
		break;
	}

	this.command_line.addWord(this.token);
	this.checkNextToken("to");
	this.command_line.addWord(this.token);
	String filename = this.lexer.parseFilename();
	if (filename==null) this.throwParserException(null, "FILENAME");
	this.command_line.addWord(filename);
	this.handler.addArgument(filename);
}

/**
* parse an unset command
* unset REF
*/
public void parseUnset() {
	this.getNextToken();
	this.parseRef();
}

/**
* parse a set command
* set REF = VALUE
*/
public void parseSet() {

// first of all parse the reference of the command

	if (this.type.equals("document")) this.getNextToken();
	this.parseRef();

// if the reference is of an element type or that an assignmentment is explicitly requested parse it

		if (this.type_checker.isElementType(this.type) || "=".equals(this.token)) {
			this.checkToken("=");
		this.reference_parser.parseValue();
		this.delimiter = this.reference_parser.getDelimiter();
		this.cancelRequireValue();
	}
}

/**
* parse a reset command
* reset REF as NAME
*/
public void parseReset() {
	this.getNextToken();
	this.parseRef();
	this.checkToken ("as");
	this.checkNextTokenType(TokenType.STRING);
	this.handler.addArgument(this.token);
}
/**
* parse a show command
* show REF
*/
public void parseShow() {
	this.getNextToken();
	this.parseRef();
}

/**
* parse a check command
* check QUERRYREF
*/
public void parseCheck() {
	this.checkNextToken("queries", "selection");
	this.parseRef();
}


/**
* parse a help command
* help COMMAND?
*/
public void parseHelp () {
	String token = this.getNextToken();
	if (token!=null) this.setType(token);
	else this.setType("default");
	this.handler.setObjectType(this.type);
}


/**
* parse an exit command
*/
public void parseExit() {
	this.checkToken("exit");
	((DefaultHandler)this.handler).setCommand("exit");
}

/**
* parse a connect command
*/
public void parseConnect() {
	((DefaultHandler)this.handler).setCommand("connect");
}

/**
* parse a disconnect command
*/
public void parseDisconnect() {
	this.checkToken("disconnect");
	((DefaultHandler)this.handler).setCommand("disconnect");
}

/**
* parse a compile command
*/
public void parseCompile() {
	this.checkToken("compile");
	((DefaultHandler)this.handler).setCommand("compile");
}

@Override
public void cancelRequireValue() {
	super.cancelRequireValue();
	this.reference_parser.cancelRequireValue();
}
	
}
