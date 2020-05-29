package org.uppaal.cli.frontend;

/**
* command lexer of the uppaal command line interface, read tokens
*/

import org.uppaal.cli.exceptions.TokenException;
public class CommandLexer {
// the token exception of this lexer
private TokenException token_exception;

// the line to parse
private String line;

// the length of the string to parse
private int length;

// the starting index of the current token
private int starting_index;

// the current index in the line
private int index;

// the type of the current token
private TokenType token_type;

public CommandLexer () {
	this.token_exception = new TokenException ();
	this.line = null;
	this.length = -1;
	this.starting_index = 0;
	this.index = 0;
}

/**
* @return the line of this command lexer
*/
public String getLine() {
	return this.line;
}

/**
* set the line of this command lexer
* @param line the line to parse
*/
public void setLine (String line) {
	this.line = line;
	this.length = this.line.length();
	this.index = 0;
}

/**
* @return the starting index of the current token 
*/
public int getStartingIndex () {
	return this.starting_index;
}

/**
* @return the type of the current token
*/
public TokenType getTokenType() {
	return this.token_type;
}

/**
* @return the next token in the line, null if terminated
*/
public String getNextToken() {

// check that we are not at the end of the line

	if (this.index>=this.length) return null;
	this.skipWhiteSpaces();
	char c = this.line.charAt(this.index);

switch (c) {
		case '.':
		case '*':
		case '(':
		case ')':
		case '{':
		case '}':
		case '[':
		case ']':
		this.starting_index = this.index;
		this.index++;
		return this.line.substring(this.starting_index, this.index);

// for a transition arrow check that every character is well positioned

		case '-':
		this.starting_index = this.index;
		this.index++;
		if (this.index<this.length) {
			c = this.line.charAt(this.index);
			if (c!= '>') this.throwTokenException("illegal character "+c);
			else this.index ++;
		} else this.throwTokenException ("reached end of line while looking for >");
		return this.line.substring(this.starting_index, this.index);

// for a delimited string call the correct method

		case '\'' :
		case '"':
		return this.parseDelimitedString();

// finally in the default case check that we have either a number or a default string

		default:
		if (Character.isDigit(c)) return this.parseNumber();
		else if (Character.isLetter(c)) return this.parseString();
		else this.throwTokenException("illegal character "+c);
		return null;
	}
}

/**
* parse a filename
* @return the parsed filename
*/
public String parseFilename () {
	StringBuffer filename = new StringBuffer();
	this.skipWhiteSpaces();
		char c = this.line.charAt(this.index);

	while (c!=' ' && this.index<this.length) {
		switch (c) {
			case '.':
			case '/':
			case '-':
			case '_':
			this.index++;
			filename.append(this.line.substring(this.index-1, this.index));
			break;

			default:
			if (Character.isDigit(c)) filename.append(this.parseNumber());
			else if (Character.isLetter(c)) filename.append(this.parseString());
			else this.throwTokenException("illegal character "+c);
			break;
		}

		if (this.index<this.length)
			c = this.line.charAt(this.index);
	}

	this.token_type = TokenType.FILENAME;
	return filename.toString();
}

/**
* throw a token exception
* @param message the message to be displayed
* @exception a token exception with the provided message
*/
private void throwTokenException(String message) {
	this.token_exception.setMessage(message);
	throw this.token_exception;
}

/**
* skip white spaces
*/
private void skipWhiteSpaces () {
	while (this.index<this.length && this.line.charAt(this.index)==' ') this.index ++;
}

/**
* parse a number
* @return the string describing the number
*/
private String parseNumber () {
	this.starting_index = this.index;
	while (this.index<this.length && Character.isDigit(this.line.charAt(this.index))) this.index++;
	this.token_type = TokenType.NUMBER;
	return line.substring(this.starting_index, this.index);
}

/**
* parse a string delimited by a certain character
* @return the parsed string without its delimiters
*/
private String parseDelimitedString () {
	char delimiter = this.line.charAt(this.index);
	this.index++;
	this.starting_index = this.index;
	while (this.index<this.length && this.line.charAt(this.index)!=delimiter) this.index++;
	if (this.index == this.length) this.throwTokenException("unclosed string");
	String str = this.line.substring (this.starting_index, this.index);
	this.token_type = TokenType.DELIMITED_STRING;
	this.index ++;
	return str;
}

/**
* parse an string delimited by white spaces
* @return the parsed string
*/
private String parseString () {
	this.starting_index = this.index;
	this.token_type = TokenType.STRING;
	boolean finished = false;
	char c = this.line.charAt(this.index);

	while (this.index<this.length && !finished)  {
		switch (c) {
		case '_':
		this.index++;
		if (this.index<this.length) c = this.line.charAt(this.index);
		break;

		default:
if (Character.isLetterOrDigit(c))  {
			this.index++;
			if (this.index<this.length) c = this.line.charAt(this.index);
		} else finished = true;
		break;
		}
	}

	return this.line.substring(this.starting_index, this.index);
}
}