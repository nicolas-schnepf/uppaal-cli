package org.uppaal.cli.exceptions;

/**
* class handling wrong file extension for protected operations such as import or export
*/

import org.uppaal.cli.Command;

public class WrongExtensionException extends ConsoleException {
// the object code of this exception
private Command.ObjectCode object_code;

// the wrong extension of this exception
private String wrong_extension;

/**
* public constructor of a wrong extension extension
*/
public WrongExtensionException () {
	super(ExceptionCode.WRONG_EXTENSION);
}

/**
* @return the object code of this exception
*/
public Command.ObjectCode getObjectCode() {
	return this.object_code;
}

/**
* set the object code of this exception
* @param object_code the new object code for this exception
*/
public void setObjectCode(Command.ObjectCode object_code) {
	this.object_code = object_code;
}

/**
*@return the extension of this exception
*/
public String getWrongExtension () {
	return this.wrong_extension;
}

/**
* set the wrong extension of this exception
* @param wrong_extension the new wrong extension for this exception
*/
public void setWrongExtension(String wrong_extension) {
	this.wrong_extension = wrong_extension;
}
}