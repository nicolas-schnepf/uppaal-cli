package org.uppaal.cli.exceptions;

import org.uppaal.cli.enumerations.ExceptionCode;
import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ObjectCode;
import org.uppaal.cli.enumerations.ModeCode;

/**
* missing element exception, thrown when an uppaal is missing in the current document
*/

public class MissingElementException extends ConsoleException {
// the name of the missing element
private String name;

/**
* public constructor of a missing element exception
*/
public MissingElementException () {
	super(ExceptionCode.MISSING_ELEMENT);
}

/**
* @return the name of the missing element
*/
public String getName () {
	return this.name;
}

/**
* set the name of this missing element exception
* @param name the new name for this missing element exception
*/
public void setName (String name) {
	this.name = name;
}
}
