package org.uppaal.cli.exceptions;

import org.uppaal.cli.Command;
import java.util.LinkedList;
import java.util.Iterator;

/**
* wrong mode exception, raised when the current mode does not support a certain command
*/

public class WrongObjectException extends ConsoleException {

// code of the wrong command
private Command.ObjectCode object_code;

/**
* public constructor of a wrong mode exception
*/
public WrongObjectException () {
	super(ExceptionCode.WRONG_OBJECT);
}

/**
* @return the current mode of the uppaal command line interface
*/
public Command.ObjectCode getObjectCode () {
	return this.object_code;
}

/**
* set the object code of this exception
* @param object_code the new object code for this exception
*/
public void setObjectCode (Command.ObjectCode object_code) {
	this.object_code = object_code;
}
}