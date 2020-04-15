package org.uppaal.cli.context;

import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ObjectCode;
import org.uppaal.cli.exceptions.TraceFormatException;

/**
* abstract class for simulator experts
* simply providing a method to throw a trace format exception when necessary
*/

public abstract class SimulatorExpert extends AbstractExpert {
// the trace format exception of this simulator expert
protected TraceFormatException trace_format_exception;

public SimulatorExpert (Context context) {
	super(context);
	this.trace_format_exception = new TraceFormatException();
}

/**
* throw a trace format exception
* @param operation_code the code of the operation for which the trace format exception is thrown
* @param object_code the code of the object for which the exception is thrown
* @exception a trace format exception with all the necessary information
*/
public void throwTraceFormatException (OperationCode operation_code, ObjectCode object_code) {
	this.trace_format_exception.setOperationCode(operation_code);
	this.trace_format_exception.setObjectCode(object_code);
	throw this.trace_format_exception;
}
}
