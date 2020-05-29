package org.uppaal.cli.commands;

/***
* abstract class providing the method interfaces and protected fields for any operation handler
*/

import org.uppaal.cli.exceptions.ConsoleException;
import org.uppaal.cli.enumerations.ResultCode;
import org.uppaal.cli.exceptions.ExtraArgumentException;
import org.uppaal.cli.exceptions.MissingArgumentException;
import org.uppaal.cli.exceptions.WrongModeException;
import org.uppaal.cli.exceptions.WrongExtensionException;
import org.uppaal.cli.exceptions.WrongArgumentException;
import org.uppaal.cli.exceptions.WrongObjectException;
import org.uppaal.cli.context.ModeCode;
import org.uppaal.cli.commands.CommandResult;

import org.uppaal.cli.context.Context;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.HashMap;
import java.lang.reflect.Method;

public abstract class AbstractHandler implements Handler {
protected Context context;
protected String operation_code;
protected String object_type;
protected CommandResult command_result;
protected LinkedList<String> arguments;
protected HashMap<String, Method> operation_map;
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
	this.operation_map = new HashMap<String, Method>();

	this.arguments = new LinkedList<String>();
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

protected AbstractHandler (Context context, String operation_code) {
	this(context);
	this.operation_code = operation_code;
}

@Override
public CommandResult handle () {
	this.command_result.clear();
	this.command_result.setObjectType(this.getObjectType());
	this.command_result.setResultCode(ResultCode.OK);
	if (!this.operation_map.keySet().contains(this.getObjectType()))
				this.throwWrongObjectException(this.getObjectType());

	try {
		this.operation_map.get(this.getObjectType()).invoke(this);
	} catch (IllegalAccessException e) {
	System.out.println(e.getMessage());
	e.printStackTrace();
	System.exit(1);
	} catch (InvocationTargetException e) {
		if (e.getTargetException() instanceof ConsoleException)
			throw (ConsoleException)e.getTargetException();

	System.out.println(e.getMessage());
	e.printStackTrace();
	System.exit(1);
	}

	return this.command_result;
}

/***
* set the uppaal context of this command handler
* @param context the context to set
*/

public void setContext (Context context) {
	this.context = context;
}


/**
* set the object code of this command
* @param object_type the new object code for this command
*/
public void setObjectType (String object_type) {
	this.object_type = object_type;
}

/**
* @return the current object code of this command
*/
public String getObjectType() {
	return this.object_type;
}

@Override
public void addArgument(String argument) {
	this.arguments.add(argument);
}

@Override
public int getArgumentNumber() {
	return this.arguments.size();
}

@Override
public void clear() {
	this.arguments.clear();
}

@Override
public String getArgumentAt (int index) {
	return this.arguments.get(index);
}

@Override
public ModeCode getMode () {
	return this.context.getMode();
}

/***
* throw a missing argument exception
* @param operation_code the code of the command
* @param object_type the object code for which throwing the exception
* @param expected the expected number of arguments
* @param received the received number of arguments
* @exception a missing argument exception
*/
protected void throwMissingArgumentException (String operation_code, String object_type, int expected, int received) {
	this.missing_argument_exception.setCommand(operation_code);
	this.missing_argument_exception.setObjectType(object_type);
	this.missing_argument_exception.setExpectedArgumentNumber(expected);
	this.missing_argument_exception.setReceivedArgumentNumber(received);
	throw (this.missing_argument_exception);
}

/***
* throw a extra argument exception
* @param operation_code the code of the command
* @param object_type the code of the object
* @param expected the expected number of arguments
* @param received the received number of arguments
* @exception a extra argument exception
*/
protected void throwExtraArgumentException (String operation_code, String object_type, int expected, int received) {
	this.extra_argument_exception.setCommand(operation_code);
	this.extra_argument_exception.setObjectType(object_type);
	this.extra_argument_exception.setExpectedArgumentNumber(expected);
	this.extra_argument_exception.setReceivedArgumentNumber(received);
	throw (this.extra_argument_exception);
}

/**
* throw a wrong object exception
* @param operation_code the name of the command throwing a wrong object exception
* @param object_type the code of the wrong object
* @exception an exception describing the kind of wrong object which were received
*/
public void throwWrongObjectException (String object_type) {
	this.wrong_object_exception.setCommand(this.operation_code);
	this.wrong_object_exception.setObjectType(object_type);
	throw this.wrong_object_exception;
}

/***
* throw a wrong argument exception
* @param operation_code the code of the command that received the wrong argument
* @param argument the wrong argument received by the handler
* @exception a wrong argument exception
*/

protected void throwWrongArgumentException (String operation_code, String argument) {
	this .wrong_argument_exception.setCommand(operation_code);
	this.wrong_argument_exception.setMessage(argument);
	throw this.wrong_argument_exception;
}

/**
* throw a wrong extension exception
* @param operation_code the code of the command that requires a wrong extension exception
* @param object_type the code of the object for which the wrong extension exception is thrown
* @param extension the extension of the file
* @exception a wrong extension exception
*/
protected void throwWrongExtensionException(String operation_code, String object_type, String extension) {
	this.wrong_extension_exception.setCommand(operation_code);
	this.wrong_extension_exception.setObjectType(object_type);
	this.wrong_extension_exception.setWrongExtension(extension);
	throw this.wrong_extension_exception;
}

/**
* check that a command is allowed in the current mode
* @param command the command to check
* @param modes the allowed modes for the command
* @exception a wrong mode exception if the command is not allowed in the current mode
*/

public void checkMode (String command, String object_type, ModeCode ...modes) {
	for (ModeCode mode:modes) {
		if (this.context.getMode()==mode) return;
	}
	this.wrong_mode_exception.setModeCode(this.context.getMode());
	this.wrong_mode_exception.setCommand(command);
	this.wrong_mode_exception.setObjectType(object_type);
	throw this.wrong_mode_exception;
}
}
