package org.uppaal.cli.exceptions;






import java.util.LinkedList;
import java.util.Iterator;

/**
* wrong mode exception, raised when the current mode does not support a certain command
*/

public class WrongObjectException extends ConsoleException {

// code of the wrong command
private String object_type;

/**
* public constructor of a wrong mode exception
*/
public WrongObjectException () {
	super();
}

/**
* @return the current mode of the uppaal command line interface
*/
public String getObjectType () {
	return this.object_type;
}

/**
* set the object code of this exception
* @param object_type the new object code for this exception
*/
public void setObjectType (String object_type) {
	this.object_type = object_type;
}
}
