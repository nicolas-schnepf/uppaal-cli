package org.uppaal.cli.exceptions;

import org.uppaal.cli.Command;

/**
* parent class of every uppaal console exception
* provides the constructors and fields that are common to every uppaal console exceptions.
*/

public abstract class ConsoleException extends RuntimeException {
/** exception codes */

public static enum ExceptionCode {
UNKNOWN_MODE,
WRONG_MODE,
UNKNOWN_COMMAND,
WRONG_COMMAND,
WRONG_OBJECT,
MISSING_ARGUMENT,
EXTRA_ARGUMENT,
EDITOR,
MENU,
WRONG_ARGUMENT,
WRONG_EXTENSION,
WRONG_FORMAT,
MISSING_ELEMENT,
EXISTING_ELEMENT
}

// command code

protected Command.CommandCode command_code;

// object code

	protected Command.ObjectCode object_code;

// exception message

protected String message;

// exception code

protected ExceptionCode exception_code;

/**
* protected constructor of a console exception
* initialize an exception with its exception code
* @param code the code of the exception
*/
protected ConsoleException (ExceptionCode code) {
	this.exception_code = code;
}

/**
* @return the command code of this exception
*/

public Command.CommandCode getCommandCode() {
	return this.command_code;
}

/**
* set the command code of this exception
* @param command_code: the new command code of this exception
*/
public void setCommandCode (Command.CommandCode command_code) {
	this.command_code = command_code;
}

/**
* @return the object code of this exception
*/

public Command.ObjectCode getObjectCode() {
	return this.object_code;
}

/**
* set the object code of this exception
* @param object_code: the new object code of this exception
*/
public void setObjectCode (Command.ObjectCode object_code) {
	this.object_code = object_code;
}

/**
* @return the message of this exception
*/
public String getMessage() {
	return this.message;
}

/**
* set the message of this exception
* @param message the new message of this exception
*/
public void setMessage (String message) {
	this.message = message;
}

/**
* @return the exception code of this exception
*/
public ExceptionCode getExceptionCode() {
	return this.exception_code;
}
}