package org.uppaal.cli.exceptions;

/**
* parser exception, thrown when an unexpected token is encountered
*/

public class ParserException extends ConsoleException {
// the wrong token
private String token;

// the expected token
private String expected_token;

// the starting index of the wrong token
private int starting_index;

// line of this exception
private String line;

public ParserException () {
	super();
}

/**
* set the value of the wrong token of this exception
* @param token the wrong token for this exception
*/
public void setToken (String token) {
	this.token = token;
}

/**
* set the value of the expected token of this exception
* @param expected_token the expected token for this exception
*/
public void setExpectedToken (String expected_token) {
	this.expected_token = expected_token;
}

/**
* set the value of the starting index of the wrong token
* @param starting the starting index for this exception
*/
public void setStartingIndex (int starting_index) {
	this.starting_index = starting_index;
}

/**
* set the line of this exception
* @param the new line for this exception
*/
public void setLine (String line) {
	this.line = line;
}

@Override
public String getMessage () {
	StringBuffer message = new StringBuffer();
	if (this.token==null) {
		message.append("Parser error: end of line was reached while looking for ");
		message.append(this.expected_token);
	} else {
		message.append("Parser error: unexpected token ");
		message.append(this.token);
		message.append (" at "+this.starting_index);
		message.append ("while looking for "+this.expected_token+".\n");
		message.append(this.line+"\n");
		for (int i = 0; i<this.starting_index;i++) message.append(" ");
		message.append("^");
	}
	return message.toString();
}
}
