package org.uppaal.cli.exceptions;

/**
* class handling wrong format exceptions for editing and importing commands
*/


import org.uppaal.cli.enumerations.ExceptionCode;
import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ObjectCode;


public class WrongFormatException extends ConsoleException {
// the filename associated with this exception
private String filename;
// the number of the line of this exception
private int line_number;

/**
* public constructor of a wrong format exception
*/
public WrongFormatException () {
	super(ExceptionCode.WRONG_FORMAT);
}

/**
* @return the filename of this exception
*/
public String getFilename() {
	return this.filename;
}

/**
* set the filename associated with this exception
* @param filename the new filename for this exception
*/
public void setFilename (String filename) {
	this.filename = filename;
}

/**
* @return the number of the line of this exception
*/
public int getLineNumber() {
	return this.line_number;
}

/**
* set the line number for this exception
* @param line_number the new line number for this exception
*/
public void setLineNumber(int line_number) {
	this.line_number = line_number;
}
}
