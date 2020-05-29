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
super();
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

@Override
public String getMessage() {
	return "Unknown mode: "+this.mode;
}
}
