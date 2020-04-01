package org.uppaal.cli.handlers;

/***
* abstract class providing the method interfaces and protected fields for any operation handler
*/

import org.uppaal.cli.exceptions.ExtraArgumentException;
import org.uppaal.cli.exceptions.MissingArgumentException;
import org.uppaal.cli.exceptions.WrongExtensionException;
import org.uppaal.cli.exceptions.WrongArgumentException;
import org.uppaal.cli.exceptions.WrongObjectException;
import org.uppaal.cli.commands.Command.OperationCode;
import org.uppaal.cli.commands.Command.ObjectCode;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.commands.Command;
import org.uppaal.cli.commands.Context;
import java.util.HashSet;

public abstract class OperationHandler extends AbstractHandler {
protected OperationCode operation_code;
protected CommandResult command_result;
protected HashSet<ObjectCode> accepted_objects;
private WrongObjectException wrong_object_exception;
private WrongArgumentException wrong_argument_exception;
private WrongExtensionException wrong_extension_exception;
private MissingArgumentException missing_argument_exception;
private ExtraArgumentException extra_argument_exception;

/**
* protected constructor of an abstract handler
* initialize it from a context and an array of accepted commands
* @param context the context that will be used by this handler
* @param accepted_operations the array of command codes accepted by this handler
*/

protected OperationHandler (Context context, OperationCode operation_code) {
	super(context);
	this.operation_code = operation_code;
	this.command_result = new CommandResult();
	this.extra_argument_exception = new ExtraArgumentException();
	this.missing_argument_exception = new MissingArgumentException();
	this.wrong_argument_exception = new WrongArgumentException();
	this.wrong_extension_exception = new WrongExtensionException();
	this.wrong_object_exception = new WrongObjectException();
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
protected void throwMissingArgumentException (Command.OperationCode operation_code, Command.ObjectCode object_code, int expected, int received) {
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
protected void throwExtraArgumentException (Command.OperationCode operation_code, Command.ObjectCode object_code, int expected, int received) {
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
public void throwWrongObjectException (Command.OperationCode operation_code, Command.ObjectCode object_code) {
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

protected void throwWrongArgumentException (Command.OperationCode operation_code, String argument) {
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
protected void throwWrongExtensionException(Command.OperationCode operation_code, Command.ObjectCode object_code, String extension) {
	this.wrong_extension_exception.setOperationCode(operation_code);
	this.wrong_extension_exception.setObjectCode(object_code);
	this.wrong_extension_exception.setWrongExtension(extension);
	throw this.wrong_extension_exception;
}

@Override
public HashSet<OperationCode> getAcceptedOperations() {
	HashSet<OperationCode> accepted_operation = new HashSet<OperationCode>();
	accepted_operation.add(this.operation_code);
	return accepted_operation;
}

@Override
public boolean acceptCommand (Command command) {
	return (command.getOperationCode()==this.operation_code) && (this.accepted_objects.contains(command.getObjectCode()));
}

@Override
public HashSet<ObjectCode> getAcceptedObjects() {
	return this.accepted_objects;
}
}
