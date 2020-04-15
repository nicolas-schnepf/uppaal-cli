package org.uppaal.cli.exceptions;


import org.uppaal.cli.enumerations.ExceptionCode;
import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ObjectCode;


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
