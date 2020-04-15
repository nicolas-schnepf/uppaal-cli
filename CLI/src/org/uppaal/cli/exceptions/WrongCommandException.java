package org.uppaal.cli.exceptions;


import org.uppaal.cli.enumerations.ExceptionCode;
import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ObjectCode;
import org.uppaal.cli.enumerations.ModeCode;

import java.util.LinkedList;
import java.util.Iterator;

/**
* wrong mode exception, raised when the current mode does not support a certain command
*/

public class WrongCommandException extends ConsoleException {

// code of the wrong command
private LinkedList<ModeCode> handler_codes;

/**
* public constructor of a wrong mode exception
*/
public WrongCommandException () {
	super(ExceptionCode.WRONG_COMMAND);
	this.handler_codes = new LinkedList<ModeCode>();
}

/**
* @return the current mode of the uppaal command line interface
*/
public Iterator<ModeCode> getModeCodes() {
	return this.handler_codes.iterator();
}

/**
* add a handler code to this exception
* @param handler_code the new handler code for this exception
*/
public void addModeCode (ModeCode handler_code) {
	this.handler_codes.add(handler_code);
}

/**
* clear the list of handler codes
*/
public void clearModeCodes () {
	this.handler_codes.clear();
}
}
