package org.uppaal.cli.exceptions;

import org.uppaal.cli.handlers.Handler;
import org.uppaal.cli.Command;

/**
* missing argument exception, thrown when an argument is missing to a command
*/

public class MissingArgumentException extends ConsoleException {
/*** public constructor of a missing argument exception
*/

public MissingArgumentException () {
	super(ExceptionCode.MISSING_ARGUMENT);
}
}