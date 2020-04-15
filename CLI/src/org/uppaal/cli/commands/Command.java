package org.uppaal.cli.commands;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ObjectCode;
import org.uppaal.cli.enumerations.ModeCode;
import java.util.LinkedList;
import java.util.Iterator;
import java.lang.Iterable;

/**
 * Command class parses an input line into a command and its arguments separated by white space.
 * @author Marius Mikucionis <marius@cs.aau.dk>
 */
public class Command implements Iterable<String> {

    private OperationCode operation_code;
    private ObjectCode object_code;
    private ModeCode mode;
    private LinkedList<String> arguments;
    public Command() {
	this.arguments = new LinkedList<String>();
    }


/**
* set the command code of this command
* @param operation_code the new command code for this command
*/
public void setOperationCode(OperationCode operation_code) {
	this.operation_code = operation_code;
}

/**
* @return the command code of this command
*/
public OperationCode getOperationCode () {
	return this.operation_code;
}

/**
* set the object code of this command
* @param object_code the new object code for this command
*/
public void setObjectCode (ObjectCode object_code) {
	this.object_code = object_code;
}

/**
* @return the current object code of this command
*/
public ObjectCode getObjectCode() {
	return this.object_code;
}

/**
* set the mode of this command
* @param mode the new mode for this command
*/
public void setMode (ModeCode mode) {
	this.mode = mode;
}

/**
* @return the mode of this command
*/
public ModeCode getMode() {
	return this.mode;
}

/**
* add an argument to this command
* @param argument the argument to add
*/
public void addArgument(String argument) {
	this.arguments.add(argument);
}

/**
* @return the number of arguments of this command
*/
public int getArgumentNumber() {
	return this.arguments.size();
}

/**
* @return an iterator on the list of arguments of this command
*/
public Iterator<String> iterator() {
	return this.arguments.iterator();
}

/**
* clear the list of arguments of this command
*/
public void clear() {
	this.operation_code = OperationCode.UNKNOWN;
	this.object_code = ObjectCode.UNKNOWN;
	this.mode = ModeCode.UNKNOWN;
	this.arguments.clear();
}

/**
* return an argument at a specified position
* @param index the position of the argument
* @return the intended argument
*/
public String getArgumentAt (int index) {
	return this.arguments.get(index);
}

/**
* @return the arguments of this command as a single string
*/
public String getSingleArgument () {
	StringBuffer buffer = new StringBuffer();
	for (String argument:this.arguments) buffer.append(argument+" ");
	return buffer.toString();
}
}
