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
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class CommandParser {

// command lexer of this parser
private CommandLexer lexer;

// type checker of this command parser
private TypeChecker type_checker;

// current token of this parser
private String token;

// type of the reference of the command parsed by this parser
private String type;

// map of command methods to invoke
private HashMap<String, Method> command_map;

// command launcher of this command parser
private CommandLauncher command_launcher;

// current command handler associated with this parser
private Handler handler;

// hash map of token names to associate to an error message
private HashMap<TokenType, String> token_map;

// boolean telling if a reference require a value
private boolean require_value;

// parser exception of this parser
private ParserException parser_exception;

// unknown command exception of this handler
private UnknownCommandException unknown_command_exception;

public CommandParser(Context context) {
	this.lexer = new CommandLexer();
	this.type_checker = new TypeChecker();
	this.command_launcher = new CommandLauncher(context);
	this.command_map = new HashMap<String, Method>();
	this.token_map = new HashMap<TokenType, String>();
	this.parser_exception = new ParserException();
	this.unknown_command_exception = new UnknownCommandException();

	this.token_map.put(TokenType.STRING, "string");
	this.token_map.put(TokenType.NUMBER, "number");
	this.token_map.put(TokenType.DELIMITED_STRING, "delimited string");

	try {
	this.command_map.put("start", this.getClass().getMethod("parseStart"));
	this.command_map.put("import", this.getClass().getMethod("parseImport"));
	this.command_map.put("export", this.getClass().getMethod("parseExport"));
	this.command_map.put("unset", this.getClass().getMethod("parseUnset"));
	this.command_map.put("set", this.getClass().getMethod("parseSet"));
	this.command_map.put("reset", this.getClass().getMethod("parseReset"));
	this.command_map.put("show", this.getClass().getMethod("parseShow"));
	this.command_map.put("select", this.getClass().getMethod("parseSelect"));
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

/**
* set the line to parse
* @param line the line to parse
*/
public void setLine (String line) {
	this.lexer.setLine(line);
}

/**
* get the next token from the line
* @return the next token of the line, null if terminated
*/
private String getNextToken() {
	this.token = this.lexer.getNextToken();
	return this.token;
}

/**
* parse a property value from the line
* @return the property value extracted from the line
*/
private String parsePropertyValue() {
	this.token = this.lexer.parsePropertyValue();
	return this.token;
}

/**
* parse a command line and return the corresponding handler will all the intended information
* @param line the line to parse
* @return the handler to execute to manage the command
* @exception an exception if an unknown command is provided or if the syntax is not correct
*/
public Handler parseCommand (String line) {
	this.lexer.setLine(line);
	this.type = "document";
	String command = this.getNextToken();
	if (command==null || !this.command_map.keySet().contains(command)) {
		this.unknown_command_exception.setCommand(command);
		throw this.unknown_command_exception;
	}

// if the command is correct launch the corresponding handler and execute the appropriate method

	try {
		this.handler = this.command_launcher.getCommandHandler(command);
		this.handler.clear();
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
* throw a parser exception
* @param token the token for the exception
* @param expected the expected token for the exception
* @exception a parser exception with all the intended information
*/
private void throwParserException (String token, String expected) {
	this.parser_exception.setToken(token);
	this.parser_exception.setStartingIndex(this.lexer.getStartingIndex());
	this.parser_exception.setExpectedToken (expected);
	this.parser_exception.setLine(this.lexer.getLine());
	throw this.parser_exception;
}

/**
* check the value of the current token
* @param value the expected value of the token
* @exception a parser error if the current token does not belongs to the provided value
*/
private void checkToken(String... values) {
	if (this.token!=null) {
		for (String value : values) {
			if (this.token.equals(value)) return;
	}
}

	StringBuffer expected = new StringBuffer();
	for (String value:values) {
		if (expected.length()>0) expected.append(" or ");
		expected.append(value);
	}

	this.throwParserException(this.token, expected.toString());
}

/**
* get the next token from the lexer and check its type
* @param value the expected value of the next token
* @exception a parser exception if the next token does not belongs to the intended value
*/
private String checkNextToken(String ...value) {
	this.getNextToken();
	this.checkToken(value);
	return this.token;
}

/**
* check the type of the current token
* @param types the accepted types for the current token
* @exception a parser exception containing a message describing the error
*/
private void checkTokenType(TokenType... types) {
	for (TokenType type: types) {
		if (type==this.lexer.getTokenType()) return;
	}

	String token_type = this.token_map.get(this.lexer.getTokenType());
	StringBuffer expected = new StringBuffer();

	for (TokenType type : types) {
		if (expected.length()!=0) expected.append (" or ");
		expected.append(this.token_map.get(type));
	}
this.throwParserException (token, expected.toString());
}

/**
* check the type of the next token
* @param types the possible types for the next token
* @return the next token if correct
* @exception a parser exception if the next token does not belongs to the intended type
*/
private String checkNextTokenType(TokenType... types) {
	this.getNextToken();
	this.checkTokenType(types);
	return this.token;
}

/**
* parse a start command requiring a new mode to be started
* start MODE (type = (symbolic | concrete)?
*/

public void parseStart () {

// check that the command start with the keyword start, then parse its mode and optionally its type

	this.checkToken("start");
	((DefaultHandler)this.handler).setCommand("start");
	String mode = this.getNextToken();
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
* parse an import command
* import ( document | templates | queries ) FILENAME
*/
public void parseImport () {

// check that the command starts with  import, is followed by a valid type followed by a string

	this.checkToken("import");
	String type = this.checkNextToken("document", "queries", "templates");
	String filename = this.lexer.parseFilename();
	this.handler.setObjectType(type);
	this.handler.addArgument(filename);
}

/**
* parse an export command
* export ( document | trace | queries ) FILENAME
*/
public void parseExport () {

// check that the command starts with  export, is followed by a valid type followed by a string

	this.checkToken("export");
	String type = this.checkNextToken("document", "queries", "TRACE");
	String filename = this.lexer.parseFilename();
	this.handler.setObjectType(type);
	this.handler.addArgument(filename);
}

/**
* parse an unset command
* unset REF
*/
public void parseUnset() {
	if (this.getNextToken().equals("*")) this.handler.addArgument(this.token);
	else this.parseRef();
}

/**
* parse a set command
* set REF = VALUE
*/
public void parseSet() {
	this.getNextToken();
	this.parseRef();
	if (this.token!=null && this.token.equals("=")) {
		this.parseValue();
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
* parse a select command
* select QUERYREF | STATE | TRANSITION
*/
public void parseSelect() {
	String token = this.getNextToken();
	if (token.equals("state") || token.equals("transition")) this.handler.addArgument(token);
	else this.parseQueryRef();
}

/**
* parse a check command
* check QUERRYREF
*/
public void parseCheck() {
	((DefaultHandler)this.handler).setCommand("check");
	this.getNextToken();
	this.parseQueryRef();
}


/**
* parse a help command
* help COMMAND?
*/
public void parseHelp () {
	String token = this.getNextToken();
	if (this.command_map.keySet().contains(token)) this.handler.addArgument(token);
	else {
		StringBuffer expected = new StringBuffer();
		for (String command:this.command_map.keySet()) {
			if (expected.length()!=0) expected.append(" or");
			expected.append(command);
		}
		this.throwParserException(token, expected.toString());
	}
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
* parse an exit command
*/
public void parseCompile() {
	this.checkToken("compile");
	((DefaultHandler)this.handler).setCommand("compile");
}

/**
* parse a reference
* ref : PROPERTY | ((ELEMREF | QUERYREF | OPTIONREF ) ( . PROPERTY)?)
*/
public void parseRef () {

// identify the type of the reference and apply the corresponding method

	if (this.type_checker.isTypeProperty(this.type, this.token)) this.parseProperty();
	else if (this.token.equals("queries")) this.parseQueryRef();
	else if (this.token.equals("options")) this.parseOptionRef();
	else this.parseElementRef();

// if the next token is a dot check that the provided property well belongs to the type of the object

	if (this.token!=null && this.token.equals(".")) {
		this.getNextToken();
		this.parseProperty();
}

	this.handler.setObjectType(this.type);
}

/**
* parse a property and check that its type is correct
* @exception a type exception if the parsed property does not belongs to the current type
*/
public void parseProperty() {
	this.type_checker.checkTypeProperty(this.type, this.token);
	this.type = this.token;
	if (this.handler instanceof SetHandler) this.require_value = true;
	this.getNextToken();
}

/**
* parse a reference to an uppaal element
* ELEMENTREF : NAME ([((* (, *)?) |(NAME (, NAME) ? ) ] )?
*/
public void parseElementRef () {

// the first token must be the name of a template

	this.checkTokenType(TokenType.STRING);
	this.type = "template";
	this.handler.addArgument(this.token);

// if some index is provided parse it accordingly

	this.getNextToken();
	if (this.token!=null && this.token.equals("[")) {
		if (!this.getNextToken().equals("*")) this.checkTokenType(TokenType.STRING);
		this.type = "location";
		this.handler.addArgument(this.token);

		if(this.getNextToken().equals("->")) {
			if (!this.getNextToken().equals("*")) this.checkTokenType(TokenType.STRING);
			this.handler.addArgument(this.token);
			this.type = "edge";
			this.getNextToken();
		}

		this.checkToken("]");
		this.getNextToken();
	}
}


/**
* parse a reference to an uppaal query
* QUERYREF : query [(* | NAME | NUMBER)? ]
*/

public void parseQueryRef () {

// check that the reference begins with the keyword query followed by a [

	this.checkToken("query");
	this.checkNextToken("[");

// get the argument of the query and check that it is well a string or a number

	String query = this.getNextToken();
	this.checkTokenType(TokenType.STRING, TokenType.NUMBER);
	this.checkNextToken("]");

// finally add the query as argument to the handler and set the reference type

	this.handler.addArgument(query);
	this.type = "query";
	if (this.handler instanceof SetHandler) this.require_value = true;
}



/**
* parse the reference to an uppaal option
* OPTIONREF : option[* | NAME ]
*/
public void parseOptionRef () {

// check that the reference begins with the keyword option followed by a [

	this.checkToken("option");
	this.checkNextToken("[");

// get the argument of the option and check that it is well a string

	String option = this.getNextToken();
	this.checkTokenType(TokenType.STRING);
	this.checkNextToken("]");

// finally add the option as argument to the handler and set the reference type

	this.handler.addArgument(option);
	this.type = "option";
	if (this.handler instanceof SetHandler) this.require_value = true;
}

/**
* check that a value is well formed
* VALUE : STRING | {(PROPERTY:STRING (, PROPERTY: STRING)* )?}
*/
public void parseValue () {

// if the current token is an opening brass check that the reference is well an element

	if (this.type_checker.isElementType(this.type)) {
		this.checkNextToken("{");
		boolean finished = false;

		while (!finished) {

// check that the next token is well a property of the referred type

			String property = this.checkNextTokenType(TokenType.STRING);
			this.type_checker.checkTypeProperty(this.type, property);

// check that a delimited string is assigned to this property

			this.checkNextToken(":");
			String value = this.parsePropertyValue();

// add the property and its value to the command handler

		((SetHandler) this.handler).addProperty(property, value);

// finally parse the argument delimiter and finish the loop if it is a closing brass

			String delimiter = this.checkNextToken(";", "}");
			finished = delimiter.equals("}");
		}
	}

// if the current token is a string check that the reference is well a property

	else {
		this.type_checker.checkProperty(this.type);
		String value = this.parsePropertyValue();
		this.handler.addArgument(value);
		this.require_value = false;
	}
}
}