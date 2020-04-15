package org.uppaal.cli.commands;

/***
* abstract class providing the method interfaces and protected fields for any operation handler
*/

import org.uppaal.cli.exceptions.ExtraArgumentException;
import org.uppaal.cli.exceptions.MissingArgumentException;
import org.uppaal.cli.exceptions.WrongModeException;
import org.uppaal.cli.exceptions.WrongExtensionException;
import org.uppaal.cli.exceptions.WrongArgumentException;
import org.uppaal.cli.exceptions.WrongObjectException;
import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ObjectCode;
import org.uppaal.cli.enumerations.ModeCode;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.commands.Command;
import org.uppaal.cli.context.Context;
import java.util.HashSet;

public abstract class AbstractHandler implements Handler {
protected Context context;
protected OperationCode operation_code;
protected CommandResult command_result;
private WrongModeException wrong_mode_exception;
private WrongObjectException wrong_object_exception;
private WrongArgumentException wrong_argument_exception;
private WrongExtensionException wrong_extension_exception;
private MissingArgumentException missing_argument_exception;
private ExtraArgumentException extra_argument_exception;

/**
* protected constructor of an abstract handler
* @param context the context of this handler
*/
protected AbstractHandler (Context context) {
	this.context = context;
	this.command_result = new CommandResult();
	this.extra_argument_exception = new ExtraArgumentException();
	this.missing_argument_exception = new MissingArgumentException();
	this.wrong_mode_exception = new WrongModeException();
	this.wrong_argument_exception = new WrongArgumentException();
	this.wrong_extension_exception = new WrongExtensionException();
	this.wrong_object_exception = new WrongObjectException();
}

/**
* protected constructor of an abstract handler
* initialize it from a context and an array of accepted commands
* @param context the context that will be used by this handler
* @param accepted_operations the array of command codes accepted by this handler
*/

protected AbstractHandler (Context context, OperationCode operation_code) {
	this(context);
	this.operation_code = operation_code;
}


/***
* set the uppaal context of this command handler
* @param context the context to set
*/

public void setContext (Context context) {
	this.context = context;
}

@Override
public ModeCode getMode () {
	return this.context.getMode();
}

/**
* @return the operation code of this handler
*/
public OperationCode getOperationCode () {
	return this.operation_code;
}

/***
* throw a missing argument exception
* @param operation_code the code of the command
* @param object_code the object code for which throwing the exception
* @param expected the expected number of arguments
* @param received the received number of arguments
* @exception a missing argument exception
*/
protected void throwMissingArgumentException (OperationCode operation_code, ObjectCode object_code, int expected, int received) {
	this.missing_argument_exception.setOperationCode(operation_code);
	this.missing_argument_exception.setObjectCode(object_code);
	this.missing_argument_exception.setExpectedArgumentNumber(expected);
	this.missing_argument_exception.setReceivedArgumentNumber(received);
	throw (this.missing_argument_exception);
}

/***
* throw a extra argument exception
* @param operation_code the code of the command
* @param object_code the code of the object
* @param expected the expected number of arguments
* @param received the received number of arguments
* @exception a extra argument exception
*/
protected void throwExtraArgumentException (OperationCode operation_code, ObjectCode object_code, int expected, int received) {
	this.extra_argument_exception.setOperationCode(operation_code);
	this.extra_argument_exception.setObjectCode(object_code);
	this.extra_argument_exception.setExpectedArgumentNumber(expected);
	this.extra_argument_exception.setReceivedArgumentNumber(received);
	throw (this.extra_argument_exception);
}

/**
* throw a wrong object exception
* @param operation_code the name of the command throwing a wrong object exception
* @param object_code the code of the wrong object
* @exception an exception describing the kind of wrong object which were received
*/
public void throwWrongObjectException (OperationCode operation_code, ObjectCode object_code) {
	this.wrong_object_exception.setOperationCode(operation_code);
	this.wrong_object_exception.setObjectCode(object_code);
	throw this.wrong_object_exception;
}

/***
* throw a wrong argument exception
* @param operation_code the code of the command that received the wrong argument
* @param argument the wrong argument received by the handler
* @exception a wrong argument exception
*/

protected void throwWrongArgumentException (OperationCode operation_code, String argument) {
	this .wrong_argument_exception.setOperationCode(operation_code);
	this.wrong_argument_exception.setMessage(argument);
	throw this.wrong_argument_exception;
}

/**
* throw a wrong extension exception
* @param operation_code the code of the command that requires a wrong extension exception
* @param object_code the code of the object for which the wrong extension exception is thrown
* @param extension the extension of the file
* @exception a wrong extension exception
*/
protected void throwWrongExtensionException(OperationCode operation_code, ObjectCode object_code, String extension) {
	this.wrong_extension_exception.setOperationCode(operation_code);
	this.wrong_extension_exception.setObjectCode(object_code);
	this.wrong_extension_exception.setWrongExtension(extension);
	throw this.wrong_extension_exception;
}

/**
* check that a command is allowed in the current mode
* @param command the command to check
* @param modes the allowed modes for the command
* @exception a wrong mode exception if the command is not allowed in the current mode
*/

public void checkMode (Command command, ModeCode ...modes) {
	for (ModeCode mode:modes) {
		if (this.context.getMode()==mode) return;
	}
	this.wrong_mode_exception.setModeCode(this.context.getMode());
	this.wrong_mode_exception.setOperationCode(command.getOperationCode());
	this.wrong_mode_exception.setObjectCode(command.getObjectCode());
	throw this.wrong_mode_exception;
}

/**
* check the number of parameters for a given command
* @param command the command to check
* @param min the minimum argument number
* @param max the maximum argument number
* @exception either a missing or an extra argument exception if one of the provided bounds is violated
*/
public void checkArgumentNumber(Command command, int min, int max) {

// retrieve the necessary information from the command

	OperationCode operation_code = command.getOperationCode();
	ObjectCode object_code = command.getObjectCode();
	int argument_number = command.getArgumentNumber();

// check that the bounds are well respected

	if (argument_number<min)
		this.throwMissingArgumentException(operation_code, object_code, min, argument_number);
	else if (argument_number>max)
		this.throwExtraArgumentException(operation_code, object_code, max, argument_number);
}

/**
* check the number of parameters for a given command
* @param command the command to check
* @param max the maximum argument number
* @exception an extra argument exception if the provided max bounds is violated
*/
public void checkArgumentNumber(Command command, int max) {

// retrieve the necessary information from the command

	OperationCode operation_code = command.getOperationCode();
	ObjectCode object_code = command.getObjectCode();
	int argument_number = command.getArgumentNumber();

// check that the max bound is well respected

	if (argument_number>max)
		this.throwExtraArgumentException(operation_code, object_code, max, argument_number);
}

@Override
public HashSet<OperationCode> getAcceptedOperations() {
	HashSet<OperationCode> accepted_operation = new HashSet<OperationCode>();
	accepted_operation.add(this.operation_code);
	return accepted_operation;
}
}
