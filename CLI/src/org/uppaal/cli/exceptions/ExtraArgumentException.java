package org.uppaal.cli.exceptions;




import org.uppaal.cli.enumerations.ModeCode;

/**
* extra argument exception, thrown when an extra argument is provided to a command
*/

public class ExtraArgumentException extends ConsoleException {

// expected number of arguments
private int expected_argument_number;

// received number of arguments
private int received_argument_number;

/*** public constructor of a missing argument exception
*/

public ExtraArgumentException () {
	super();
}

/**
* @return the expected number of arguments
*/
public int getExpectedArgumentNumber() {
	return this.expected_argument_number;
}

/**
* set the expected number of arguments
* @param expected_argument_number the expected number of arguments
*/
public void setExpectedArgumentNumber(int expected_argument_number) {
	this.expected_argument_number = expected_argument_number;
}

/**
* @return the received number of arguments
*/
public int getReceivedArgumentNumber() {
	return this.received_argument_number;
}

/**
* set the received number of arguments
* @param received_argument_number the received number of arguments
*/
public void setReceivedArgumentNumber(int received_argument_number) {
	this.received_argument_number = received_argument_number;
}
}
