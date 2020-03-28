/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uppaal.cli;

import org.uppaal.cli.handlers.Handler;
import java.util.LinkedList;
import java.util.Iterator;
import java.lang.Iterable;

/**
 * Command class parses an input line into a command and its arguments separated by white space.
 * @author Marius Mikucionis <marius@cs.aau.dk>
 */
public class Command implements Iterable<String>
{
public static enum CommandCode {
ADD, 
IMPORT, 
EXPORT, 
START, 
EXIT, 
CHECK, 
SHOW, 
REMOVE, 
CLEAR,
SELECT,
RENAME,
SET,
UNSET,
UNDO,
REDO,
PREVIEW,
NEXT,
FINISH,
UNSELECT,
HELP,
UNKNOWN
}

/*** enumeration of object codes */

public static enum ObjectCode {
DOCUMENT, 
QUERIES, 
TEMPLATES, 
DECLARATION,
LOCATIONS,
EDGES,
QUERY, 
FORMULA,
COMMENT,
TEMPLATE, 
NAME,
PARAMETER,
LOCATION,
INVARIANT,
INIT,
COMMITTED,
EDGE,
SOURCE,
TARGET,
SELECT,
GUARD,
SYNC,
ASSIGN,
TRACE,
SYSTEM,
OPTION,
OPTIONS,
VARIABLES,
VARIABLE,
CLOCK,
TRANSITION,
TRANSITIONS,
CONSTRAINT,
CLOCKS,
NONE, 
UNKNOWN, 
MODE
}

    private String command = null;
    private CommandCode command_code;
    private ObjectCode object_code;
    private Handler.HandlerCode mode;
    private LinkedList<String> arguments;
    public Command() {
	this.arguments = new LinkedList<String>();
    }


/**
* set the command code of this command
* @param command_code the new command code for this command
*/
public void setCommandCode(CommandCode command_code) {
	this.command_code = command_code;
}

/**
* @return the command code of this command
*/
public CommandCode getCommandCode () {
	return this.command_code;
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
public void setMode (Handler.HandlerCode mode) {
	this.mode = mode;
}

/**
* @return the mode of this command
*/
public Handler.HandlerCode getMode() {
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
	this.command_code = CommandCode.UNKNOWN;
	this.object_code = ObjectCode.UNKNOWN;
	this.mode = Handler.HandlerCode.UNKNOWN;
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
