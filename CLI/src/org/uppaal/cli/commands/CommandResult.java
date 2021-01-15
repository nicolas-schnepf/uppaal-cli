package org.uppaal.cli.commands;

/**
* class handling the result of a command
* and possibly asking to start another screen
*/

import org.uppaal.cli.context.ModeCode;
import org.uppaal.cli.enumerations.ResultCode;
import java.util.LinkedList;
import java.util.Iterator;

public class CommandResult implements Iterable<String> {

// code of the executed command

private String command;

// return code of this command result

private ResultCode result_code;

// object code of the called command

private String object_type;

// arguments of this return code
private LinkedList<String> arguments;

/**
* public constructor of a command result
*/
public CommandResult() {
	this.result_code = ResultCode.OK;
	this.arguments = new LinkedList<String>();
}

/**
* @return the command code of this result
*/

public String getCommand() {
	return this.command;
}

/**
* set the command code of this result
* @param command the new command code of this result
*/

public void setCommand(String command) {
	this.command = command;
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
public String getObjectType() {
	return this.object_type;
}

/**
* set the object code for this command result
* @param object_type the new object code for this result
*/

public void setObjectType(String object_type) {
	this.object_type = object_type;
}

@Override
public Iterator<String> iterator () {
	return this.arguments.iterator();
}

/**
* add an argument to this command result
* @param argument the argument to add
*/

public void addArgument(String argument) {
	this.arguments.add(argument);
}

/**
* clear this command result
*/
public void clear () {
	this.arguments.clear();
}

/**
* @return the number of arguments of this command result
*/
public int getArgumentNumber() {
	return this.arguments.size();
}

/**
* return the argument of this command result at a specified index
* @param index the index of this argument
* @return the corresponding argument
*/
public String getArgumentAt (int index) {
	return this.arguments.get(index);
}

/**
* remove the last argument from the command result
* @return the last argument of the command result after removing it
*/
public String removeLastArgument() {
	return this.arguments.removeLast();
}
}
