package org.uppaal.cli.exceptions;

import org.uppaal.cli.enumerations.ExceptionCode;
import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ObjectCode;

/**
* parent class of every uppaal console exception
* provides the constructors and fields that are common to every uppaal console exceptions.
*/

public abstract class ConsoleException extends RuntimeException {



// command code

protected OperationCode operation_code;

// object code

	protected ObjectCode object_code;

// exception message

protected String message;

// exception code

protected ExceptionCode exception_code;

/**
* protected constructor of a console exception
* initialize an exception with its exception code
* @param code the code of the exception
*/
protected ConsoleException (ExceptionCode code) {
	this.exception_code = code;
}

/**
* @return the command code of this exception
*/

public OperationCode getOperationCode() {
	return this.operation_code;
}

/**
* set the command code of this exception
* @param operation_code: the new command code of this exception
*/
public void setOperationCode (OperationCode operation_code) {
	this.operation_code = operation_code;
}

/**
* @return the object code of this exception
*/

public ObjectCode getObjectCode() {
	return this.object_code;
}

/**
* set the object code of this exception
* @param object_code: the new object code of this exception
*/
public void setObjectCode (ObjectCode object_code) {
	this.object_code = object_code;
}

/**
* @return the message of this exception
*/
public String getMessage() {
	return this.message;
}

/**
* set the message of this exception
* @param message the new message of this exception
*/
public void setMessage (String message) {
	this.message = message;
}

/**
* @return the exception code of this exception
*/
public ExceptionCode getExceptionCode() {
	return this.exception_code;
}
}
