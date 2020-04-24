package org.uppaal.cli.exceptions;





/**
* parent class of every uppaal console exception
* provides the constructors and fields that are common to every uppaal console exceptions.
*/

public abstract class ConsoleException extends RuntimeException {



// command code

protected String command;

// object code

	protected String object_type;

// exception message

protected String message;

// exception code


/**
* protected constructor of a console exception
* initialize an exception with its exception code
* @param code the code of the exception
*/
protected ConsoleException () {
}

/**
* @return the command code of this exception
*/

public String getCommand () {
	return this.command;
}

/**
* set the command code of this exception
* @param command: the new command code of this exception
*/
public void setCommand (String command) {
	this.command = command;
}

/**
* @return the object code of this exception
*/

public String getObjectType() {
	return this.object_type;
}

/**
* set the object code of this exception
* @param object_type: the new object code of this exception
*/
public void setObjectType (String object_type) {
	this.object_type = object_type;
}

/**
* @return the message of this exception
*/
public String getMessage() {
	return this.message;
}

/**
* set the message of this exception
* @param message the new message of this exception
*/
public void setMessage (String message) {
	this.message = message;
}
}
