package org.uppaal.cli.exceptions;

import org.uppaal.cli.enumerations.ExceptionCode;
import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ObjectCode;
import org.uppaal.cli.enumerations.ModeCode;

/**
* trace format exception, thrown when the format of the trace does not support a certain operation
*/

public class TraceFormatException extends ConsoleException {
public TraceFormatException () {
	super(ExceptionCode.TRACE_FORMAT);
}
}
