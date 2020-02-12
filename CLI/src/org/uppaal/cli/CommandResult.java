package org.uppaal.cli;

/**
* class handling the result of a command
* and possibly asking to start another screen
*/

public class CommandResult {

/**
* enumeration of all possible return codes
*/

public static enum ResultCode {
OK, FILE_NOT_FOUND, OBJECT_NOT_FOUND, EDIT, MENU, EXIT, MODE_CHANGED
}

// code of the executed command

private Command.CommandCode command_code;

// return code of this command result

private ResultCode result_code;

// object code of the called command

private Command.ObjectCode object_code;

// arguments of this return code
private StringBuffer arguments;

/**
* public constructor of a command result
*/
public CommandResult() {
	this.result_code = ResultCode.OK;
	this.arguments = new StringBuffer();
}

/**
* @return the command code of this result
*/

public Command.CommandCode getCommandCode() {
	return this.command_code;
}

/**
* set the command code of this result
* @param the new command code of this result
*/

public void setCommandCode(Command.CommandCode command_code) {
	this.command_code = command_code;
}

/**
* @return the result code of this result
*/

public ResultCode getResultCode() {
	return this.result_code;
}


/**
* set the code of this command result
* @param result_code the new result code for this result
*/

public void setResultCode (ResultCode result_code) {
	this.result_code = result_code;
}

/**
* @return the object code of this command result
*/
public Command.ObjectCode getObjectCode() {
	return this.object_code;
}

/**
* set the object code for this command result
* @param object_code the new object code for this result
*/

public void setObjectCode(Command.ObjectCode object_code) {
	this.object_code = object_code;
}

/**
* get the arguments of this command result as a single string
* @return the text corresponding to the arguments of this command result
*/

public String getArgument() {
	return this.arguments.toString();
}

/**
* return the list of arguments of this command result as an array
* @return the array containing all arguments of this command result
*/
public String[] getArguments() {
	return this.arguments.toString().split(" | ");
}

/**
* add an argument to this command result
* @param argument the argument to add
*/

public void addArgument(String argument) {
// if no argument simply set the content of the buffer with the argument

	if (this.arguments.length()==0)
		this.arguments.append(argument);

// otherwise also append a pipe

	else {
		this.arguments.append(" | ");
		this.arguments.append(argument);
	}
}

/**
* clear this command result
*/
public void clear () {
	this.arguments.delete(0, this.arguments.length());
}
}