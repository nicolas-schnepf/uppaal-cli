package org.uppaal.cli.exceptions;

import org.uppaal.cli.handlers.Handler;
import org.uppaal.cli.commands.Command;

/**
* Wrong argument exception, thrown when an argument is Wrong to a command
*/

public class WrongArgumentException extends ConsoleException {
/*** public constructor of a Wrong argument exception
*/

public WrongArgumentException () {
	super(ExceptionCode.WRONG_ARGUMENT);
}
}
