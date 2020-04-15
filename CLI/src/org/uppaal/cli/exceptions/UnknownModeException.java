package org.uppaal.cli.exceptions;


import org.uppaal.cli.enumerations.ExceptionCode;
import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ObjectCode;
import org.uppaal.cli.enumerations.ModeCode;

/**
* exception class for an unknown mode
*/

public class UnknownModeException extends ConsoleException {
// the name of the unknown mode

private ModeCode mode;

/**
* public constructor of an unknown mode exception
*/
public UnknownModeException () {
super(ExceptionCode.UNKNOWN_MODE);
}

/**
* @return the name of the unknown mode
*/

public ModeCode getMode() {
	return this.mode;
}

/**
* set the name of the unknown mode
* @param mode the name of the unknown mode
*/

public void setMode (ModeCode mode) {
	this.mode = mode;
}
}
