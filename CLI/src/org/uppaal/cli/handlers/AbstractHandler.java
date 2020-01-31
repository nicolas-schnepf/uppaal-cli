package org.uppaal.cli.handlers;

/***
* abstract class providing the method interfaces and protected fields for any command handler
*/

import org.uppaal.cli.exceptions.MissingArgumentException;
import org.uppaal.cli.exceptions.WrongArgumentException;
import org.uppaal.cli.CommandResult;
import org.uppaal.cli.Command;
import org.uppaal.cli.Context;

public abstract class AbstractHandler implements Handler {
protected Context context;
private WrongArgumentException wrong_argument_exception;
private MissingArgumentException missing_argument_exception;

public AbstractHandler (Context context) {
	this.context = null;
	this.missing_argument_exception = new MissingArgumentException();
	this.wrong_argument_exception = new WrongArgumentException();
}

/***
* set the uppaal context of this command handler
* @param context the context to set
*/

public void setContext (Context context) {
	this.context = context;
}

/***
* throw a missing argument exception
* @param command_code the code of the command
* @exception a missing argument exception
*/
protected void throwMissingArgumentException (Command.CommandCode command_code) {
	this.missing_argument_exception.setCommandCode(command_code);
	throw (this.missing_argument_exception);
}

/***
* throw a wrong argument exception
* @param command_code the code of the command that received the wrong argument
* @param argument the wrong argument received by the handler
* @exception a wrong argument exception
*/

protected void throwWrongArgumentException (Command.CommandCode command_code, String argument) {
	this .wrong_argument_exception.setCommandCode(command_code);
	this.wrong_argument_exception.setMessage(argument);
	throw this.wrong_argument_exception;
}
}