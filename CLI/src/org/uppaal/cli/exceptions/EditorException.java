package org.uppaal.cli.exceptions;

import org.uppaal.cli.enumerations.ExceptionCode;
import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ObjectCode;
import org.uppaal.cli.enumerations.ModeCode;

/**
* missing argument exception, thrown when an argument is missing to a command
*/

public class EditorException extends ConsoleException {
/*** public constructor of a missing argument exception
*/

public EditorException () {
	super(ExceptionCode.EDITOR);
}
}
