package org.uppaal.cli.exceptions;

import org.uppaal.cli.handlers.Handler;
import org.uppaal.cli.Command;

/**
* wrong mode exception, raised when the current mode does not exist
*/

public class WrongModeException extends ConsoleException {

// code of the wrong handler
private Handler.HandlerCode handler_code;

/**
* public constructor of a wrong mode exception
*/
public WrongModeException () {
	super(ExceptionCode.WRONG_MODE);
}

/**
* @return the current mode of the uppaal command line interface
*/
public Handler.HandlerCode getHandlerCode() {
	return this.handler_code;
}

/**
* set the handler code of this exception
* @param handler_code the new handler code for this exception
*/
public void setHandlerCode (Handler.HandlerCode handler_code) {
	this.handler_code = handler_code;
}
}