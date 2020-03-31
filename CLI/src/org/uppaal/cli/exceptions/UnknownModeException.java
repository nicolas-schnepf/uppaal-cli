package org.uppaal.cli.exceptions;

/**
* exception class for an unknown mode
*/

public class UnknownModeException extends ConsoleException {
// the name of the unknown mode

private String mode;

/**
* public constructor of an unknown mode exception
*/
public UnknownModeException () {
super(ConsoleException.ExceptionCode.UNKNOWN_MODE);
}

/**
* @return the name of the unknown mode
*/

public String getMode() {
	return this.mode;
}

/**
* set the name of the unknown mode
* @param mode the name of the unknown mode
*/

public void setMode (String mode) {
	this.mode = mode;
}
}
