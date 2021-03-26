package org.uppaal.cli.frontend;

/**
* reference parser, parse an object reference 
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

public class ReferenceParser extends AbstractParser {

// type checker of this command parser
private TypeChecker type_checker;


// current element type of this command parser
private String element_type;

// current default type of this command parser
private String default_type;

public ReferenceParser (Context context) {
	super(context);
	this.element_type = "template";
	this.default_type = "template";
}

@Override
public ParsedLine parse(String line, int cursor, ParseContext ctx) {
	return null;
}

/**
* set the token of this reference parser
* @param token the new token for this reference parser
*/
public void setToken(String token) {
	this.token = token;
}

/**
* set the type of this command parser
* @param type the new type of this command parser
*/
public void setType(String type) {
	this.type = type;
	if (this.command_completer!=null) this.command_completer.setType(type);
}

/**
* set the command line of this reference parser
* @param command_line the new line for this reference parser
*/
public void setCommandLine (CommandLine command_line) {
	this.command_line = command_line;
}

/**
* set the type checker of this reference parser
* @param type_checker the new type checker of this reference parser
*/
public void setTypeChecker(TypeChecker type_checker) {
	this.type_checker = type_checker;
}

/**
* set the element type of this command parser
* @param type the new element type of this command parser
*/
public void setElementType(String type) {
	this.element_type = type;
}

/**
* set the default type of this command parser
* @param type the new default type of this command parser
*/
public void setDefaultType(String type) {
	this.default_type = type;
}

/**
* parse a property value from the line
* @return the property value extracted from the line
*/
public String parsePropertyValue() {
	if (this.delimiter==null) {
		this.checkToken("'", "\"");
		this.delimiter = this.token;
	}

	String value = this.lexer.parseDelimitedString(this.delimiter.charAt(0));
	this.token = this.getNextToken();

// concatenate the parsed value to the buffer

	if (this.buffer.length()>0) this.buffer.append("\n");
	this.buffer.append(value);

// if the string is closed just return it

	if (this.token!=null && this.token.equals(this.delimiter)) {
		this.delimiter= null;
		value = this.buffer.toString();
		this.buffer.delete(0, this.buffer.length());
		return value;
	} else
		return null;
}

@Override
public void setCommandCompleter (CommandCompleter completer) {
	super.setCommandCompleter(completer);
	for (String type: this.type_checker) 
		this.command_completer.setProperties(type, this.type_checker.getProperties(type));
}

/**
* parse a reference
* ref : PROPERTY | ((ELEMREF | QUERYREF | OPTIONREF ) ( . PROPERTY)?)
* @return the last parsed token
*/
public String parseRef () {

// if the current token is null throw a parser exception

	if (this.token==null) 
		this.throwParserException(null, "REF");
// identify the type of the reference and apply the corresponding method

	switch (this.token) {
		case "queries":
		this.parseIndexedRef("query");
		break;

		case "selection":
		this.parseIndexedRef("selection");
		break;

		case "options":
		this.parseIndexedRef("option");
		break;

		case "parameters":
		this.parseIndexedRef("setting");
		break;

		case "variables":
		this.parseIndexedRef("variable");
		break;

		case "processes":
			this.parseIndexedRef("process");
		break;

		case "trace":
		case "data":
		case "state":
		this.setType(this.token);
		break;

		case "templates":
		this.setType("template");
		this.handler.addArgument(this.token);
		break;

		case "constraints":
		this.setType("constraint");
		this.handler.addArgument(this.token);
		break;

		default:
		if (this.type_checker.isTypeProperty(this.type, this.token)) this.parseProperty();
		else this.parseElementRef();
		break;
		}


// if the next token is a dot check that the provided property well belongs to the type of the object

	if (this.token!=null && this.token.equals(".")) {
		this.getNextToken();
		if (this.token==null) this.throwParserException(null, "REF");
		if (this.type.equals("process")) this.parseIndexedRef("variable");
		else this.parseProperty();
}

	this.handler.setObjectType(this.type);
	return this.token;
}

/**
* parse a property and check that its type is correct
*/
public void parseProperty() {
	if (!this.type_checker.isTypeProperty(this.type, this.token)) {
		if (this.token!=null) this.command_line.addWord(this.token);
		else this.command_line.addWord("");
	this.type_checker.checkTypeProperty(this.type, this.token);
	this.setType(this.token);
	}
	this.setType(this.token);
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
	if (this.type.equals("document")) this.setType(this.element_type);
	else this.setType(this.default_type);
	this.handler.addArgument(this.token);
	if (this.command_completer!=null) this.command_line.addWord(this.token);

// if some index is provided parse it accordingly

	this.getNextToken();
	if (this.token!=null && this.token.equals("(")) {
		if (this.type.equals("template")) {
			this.setType("location");
		if (this.getNextToken()!=null) {
			if (!this.token.equals("*"))this.checkTokenType(TokenType.STRING);
			this.command_line.addWord(this.token);
		} else this.checkToken("Location");
		this.handler.addArgument(this.token);
		} else if (this.type.equals("process")) {
			this.getNextToken();
			this.checkTokenType(TokenType.NUMBER);
			int argument_number = this.handler.getArgumentNumber();
			String process = this.handler.getArgumentAt(argument_number-1);
			this.handler.setArgument(argument_number-1, process+"("+this.token+")");
		}

		if (this.getNextToken()==null) this.throwParserException(null, "-> or )");
		else if(this.token.equals("->")) {
			this.type_checker.checkType(this.type, "location");
			this.setType("edge");
			if (this.getNextToken()==null)this.checkToken("EDGE");
			else if (this.token.equals("*")) this.checkTokenType(TokenType.STRING);
			this.handler.addArgument(this.token);
			this.command_line.addWord(this.token);
			this.getNextToken();
		}

		this.checkToken(")");
		this.getNextToken();
	}
}


/**
* parse a reference to an uppaal query
* QUERYREF : query [(* | NAME | NUMBER)? ]
* @param type the type of the reference to parse
*/

public void parseIndexedRef (String type) {

// get the argument of the query and check that it is well a string or a number

	String name = this.token;
	this.setType(type);
	String bracket = this.getNextToken();

	if (bracket!=null) {
	this.checkToken("(");
	String index = this.getNextToken();
	if (!this.type.equals("option")) this.checkTokenType(TokenType.NUMBER);
	this.command_line.addWord(index);
	this.checkNextToken(")");
	this.getNextToken();

// finally add the query as argument to the handler and set the reference type

		if (this.handler.getArgumentNumber()>0) {
			String process = this.handler.getArgumentAt(0);
			this.handler.setArgument(0, process+"."+name+"("+index+")");
		} else
		this.handler.addArgument(index);
	if (this.handler instanceof SetHandler) this.require_value = true;
	} else {
		if (this.handler.getArgumentNumber()>0) {
			String process = this.handler.getArgumentAt(0);
			this.handler.setArgument(0, process+"."+name);
		} else
			this.handler.addArgument(name);
	}
}

/**
* check that a value is well formed
* VALUE : STRING | {(PROPERTY:STRING (, PROPERTY: STRING)* )?}
*/
public void parseValue () {

// if the current token is a template name parse the corresponding reference

	String ref_type = this.type;
	this.getNextToken();

	if (this.token==null) this.throwParserException(null, "VALUE");
	else if (this.templates.contains(this.token)) {
		this.parseRef();
		this.type_checker.checkTypeEquals(ref_type, type);
	}

// if the current token is an opening brass check that the reference is well an element

	else if (this.type_checker.isElementType(this.type)) {
		this.checkToken("{");
		boolean finished = false;

		while (!finished) {

// check that the next token is well a property of the referred type

			if (this.getNextToken().equals("}")) finished = true;
			else {
				this.checkTokenType(TokenType.STRING);
				String property = this.token;
				this.type_checker.checkTypeProperty(this.type, property);

// check that a delimited string is assigned to this property

				this.checkNextToken(":");
				this.getNextToken();
				String value = this.parsePropertyValue();

// add the property and its value to the command handler

			((SetHandler) this.handler).addProperty(property, value);

// finally parse the argument delimiter and finish the loop if it is a closing brass

				String delimiter = this.checkNextToken(";", "}");
				finished = delimiter.equals("}");
			}
		}
	}

// if the current token is a string check that the reference is well a property

	else {
		this.type_checker.checkProperty(this.type);
		if (this.type.equals("init") || this.type.equals("committed"))
			this.handler.addArgument(this.token);
		else {
			String value = this.parsePropertyValue();
			if (value!=null) ((SetHandler)this.handler).addProperty(this.type, value);
		}

		this.require_value = false;
	}
}
}