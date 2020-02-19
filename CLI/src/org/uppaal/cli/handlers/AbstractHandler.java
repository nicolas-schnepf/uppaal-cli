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
protected CommandResult command_result;
protected Context context;
protected Command.CommandCode[] accepted_commands;
protected Command.ObjectCode[] accepted_objects;
private WrongArgumentException wrong_argument_exception;
private MissingArgumentException missing_argument_exception;

/**
* protected constructor of an abstract handler
* initialize it from a context and an array of accepted commands
* @param context the context that will be used by this handler
* @param accepted_commands the array of command codes accepted by this handler
*/

protected AbstractHandler (Context context) {
	this.context = context;
	this.accepted_commands = accepted_commands;
	this.command_result = new CommandResult();
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

@Override
public Command.CommandCode[] getAcceptedCommands() {
	return this.accepted_commands;
}

@Override
public Command.ObjectCode[] getAcceptedObjects() {
	return this.accepted_objects;
}

@Override
public boolean acceptCommand (Command command) {
	Command.CommandCode command_code= command.getCommandCode();
boolean 	found = false;

	for (Command.CommandCode code:this.accepted_commands) {
		if (code == command_code) {
			found = true;
			break;
		}
	}

	return found;
}
}