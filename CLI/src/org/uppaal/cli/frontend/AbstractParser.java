package org.uppaal.cli.frontend;

/**
* abstract parser, provide all fields and methods that are common to all parsers
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

public abstract class AbstractParser implements Parser {

// context of this command parser
protected Context context;

// command line of this command parser
protected CommandLine command_line;

// command completer of this command parser
protected CommandCompleter command_completer;

// command lexer of this parser
protected CommandLexer lexer;

// current token of this parser
protected String token;

// current command handler associated with this parser
protected Handler handler;

// hash map of token names to associate to an error message
protected HashMap<TokenType, String> token_map;

// parser exception of this parser
protected ParserException parser_exception;

// unknown command exception of this handler
protected UnknownCommandException unknown_command_exception;

// current console exception of this command parser
protected ConsoleException console_exception;

// hash set of template names
protected HashSet<String> templates;


// protected string delimiter of this command parser
protected String delimiter;

// protected string buffer of this command parser
protected StringBuffer buffer;

// boolean telling if a reference require a value
protected boolean require_value;

// type of the reference of the command parsed by this parser
protected String type;

public AbstractParser (Context context) {
	this.context = context;
	this.token_map = new HashMap<TokenType, String>();
	this.parser_exception = new ParserException();
	this.unknown_command_exception = new UnknownCommandException();
	this.console_exception = null;

	this.token_map.put(TokenType.STRING, "string");
	this.token_map.put(TokenType.NUMBER, "number");
	this.token_map.put(TokenType.DELIMITED_STRING, "delimited string");
}

/**
* get the type of this abstract parser
* @return the type of this abstract parser
*/
public String getType() {
	return this.type;
}

/**
* set the type of this parser
* @param type the new type of this parser
*/
public abstract void setType (String type);

/**
* set the element type of this parser
* @param type the new element type of this parser
*/
public abstract void setElementType(String type);

/**
* set the default type of this parser
* @param type the new default type of this parser
*/
public abstract void setDefaultType(String type);

/**
* parse the next reference of the command line
* @return the last parsed token
*/
public abstract String parseRef();

/**
* set the command lexer of this parser
* @param lexer the new lexer for this parser
*/
public void setLexer (CommandLexer lexer) {
	this.lexer = lexer;
}

/**
* set the command completer of this command parser
* @param completer the new completer of this command parser
*/
public void setCommandCompleter (CommandCompleter completer) {
	this.command_completer = completer;
}

/**
* set the string buffer of this parser
* @param buffer the new buffer of this parser
*/
public void setBuffer(StringBuffer buffer) {
	this.buffer = buffer;
}

/**
* set the line to parse
* @param line the line to parse
*/
public void setLine (String line) {
	this.lexer.setLine(line);
}

/**
* set the set of templates of this abstract parser
* @param templates the new set of templates for this abstract parser
*/
public void setTemplates (HashSet<String> templates) {
	this.templates = templates;
}

/**
* add a new template name to this parser
* @param template the template name to add
*/
public void addTemplate(String template) {
	this.templates.add(template);
	if (this.command_completer!=null) this.command_completer.addTemplate(template);
}

/**
* add all templates from a provided collection
* @param templates the collection of templates to add
*/
public void addTemplates(Collection<String> templates) {
	this.templates.addAll(templates);
	if (this.command_completer!=null) this.command_completer.addTemplates(templates);
}

/**
* remove a template name from this parser
* @param template the template to remove
*/
public void removeTemplate(String template) {
	this.templates.remove(template);
	if (this.command_completer!=null) this.command_completer.removeTemplate(template);
}

/**
* clear the set of template names
*/
public void clearTemplates() {
	this.templates.clear();
	if (this.command_completer!=null) this.command_completer.clearTemplates();
}

/**
* get the next token from the line
* @return the next token of the line, null if terminated
*/
protected String getNextToken() {
	this.token = this.lexer.getNextToken();
	return this.token;
}

/**
* throw a parser exception
* @param token the token for the exception
* @param expected the expected token for the exception
*/
protected void throwParserException (String token, String expected) {
	this.parser_exception.setToken(token);
	this.parser_exception.setStartingIndex(this.lexer.getStartingIndex());
	this.parser_exception.setExpectedToken (expected);
	this.parser_exception.setLine(this.lexer.getLine());
	this.parser_exception.setStackTrace(Thread.currentThread().getStackTrace());
	throw this.parser_exception;
}

/**
* check the value of the current token
* @param values the expected value of the token
*/
protected void checkToken(String... values) {
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

	if (this.token!=null) this.command_line.addWord(this.token);
	else if (this.lexer.getSkipped()) this.command_line.addWord("");
	this.throwParserException(this.token, expected.toString());
}

/**
* get the next token from the lexer and check its type
* @param value the expected value of the next token
* @return the value of the next token
*/
protected String checkNextToken(String ...value) {
	this.getNextToken();
	this.checkToken(value);
	return this.token;
}

/**
* check the type of the current token
* @param types the accepted types for the current token
*/
protected void checkTokenType(TokenType... types) {
	if (this.token!=null) {
	for (TokenType type: types) {
		if (type==this.lexer.getTokenType()) return;
	}
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
*/
protected String checkNextTokenType(TokenType... types) {
	this.getNextToken();
	this.checkTokenType(types);
	return this.token;
}


/**
* check if this parser currently has a console exception
* @return true if and only if this parser currently contains a console exception
*/
public boolean hasConsoleException () {
	return this.console_exception!=null;
}

/**
* get the current console exception of this command parser
* @return the current console exception of this command parser
*/
public ConsoleException getConsoleException() {
	return this.console_exception;
}

/**
* return the delimiter of this command parser
* @return the delimiter of this command parser
*/
public String getDelimiter () {
	return this.delimiter;
}

/**
* get the command handler of this command parser
* @return the command handler of this parser
*/
public Handler getHandler () {
	return this.handler;
}

/**
* set the handler of this parser
* @param handler the new handler for this parser
*/
public void setHandler (Handler handler) {
	this.handler = handler;
}

/**
* @return true if and only if a value is required
*/
public boolean getRequireValue() {
	return this.require_value;
}

/**
* cancel the value requirement
*/
public void cancelRequireValue() {
	this.require_value = false;
}
}