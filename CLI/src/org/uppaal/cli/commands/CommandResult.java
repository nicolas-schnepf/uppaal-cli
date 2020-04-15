package org.uppaal.cli.commands;

/**
* class handling the result of a command
* and possibly asking to start another screen
*/

import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ObjectCode;
import org.uppaal.cli.enumerations.ModeCode;
import org.uppaal.cli.enumerations.ResultCode;
import java.util.LinkedList;
import java.util.Iterator;

public class CommandResult implements Iterable {

// code of the executed command

private OperationCode operation_code;

// return code of this command result

private ResultCode result_code;

// object code of the called command

private ObjectCode object_code;

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

public OperationCode getOperationCode() {
	return this.operation_code;
}

/**
* set the command code of this result
* @param the new command code of this result
*/

public void setOperationCode(OperationCode operation_code) {
	this.operation_code = operation_code;
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
public ObjectCode getObjectCode() {
	return this.object_code;
}

/**
* set the object code for this command result
* @param object_code the new object code for this result
*/

public void setObjectCode(ObjectCode object_code) {
	this.object_code = object_code;
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
}
