package org.uppaal.cli.exceptions;

import org.uppaal.cli.handlers.Handler;
import org.uppaal.cli.Command;
import java.util.LinkedList;
import java.util.Iterator;

/**
* wrong mode exception, raised when the current mode does not support a certain command
*/

public class WrongCommandException extends ConsoleException {

// code of the wrong command
private LinkedList<Handler.HandlerCode> handler_codes;

/**
* public constructor of a wrong mode exception
*/
public WrongCommandException () {
	super(ExceptionCode.WRONG_COMMAND);
	this.handler_codes = new LinkedList<Handler.HandlerCode>();
}

/**
* @return the current mode of the uppaal command line interface
*/
public Iterator<Handler.HandlerCode> getHandlerCodes() {
	return this.handler_codes.iterator();
}

/**
* add a handler code to this exception
* @param handler_code the new handler code for this exception
*/
public void addHandlerCode (Handler.HandlerCode handler_code) {
	this.handler_codes.add(handler_code);
}

/**
* clear the list of handler codes
*/
public void clearHandlerCodes () {
	this.handler_codes.clear();
}
}