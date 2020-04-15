package org.uppaal.cli.exceptions;

import org.uppaal.cli.enumerations.ExceptionCode;
import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ObjectCode;
import org.uppaal.cli.enumerations.ModeCode;

/**
* exception class for an unknown command
*/

public class UnknownCommandException extends ConsoleException {
// the name of the unknown command

private String command;

/**
* public constructor of an unknown command exception
*/
public UnknownCommandException () {
super(ExceptionCode.UNKNOWN_COMMAND);
}

/**
* @return the name of the unknown command
*/

public String getCommand() {
	return this.command;
}

/**
* set the name of the unknown command
* @param command the name of the unknown command
*/

public void setCommand (String command) {
	this.command = command;
}
}
