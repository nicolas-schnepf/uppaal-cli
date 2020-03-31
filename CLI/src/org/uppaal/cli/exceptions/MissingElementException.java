package org.uppaal.cli.exceptions;

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
	super(ConsoleException.ExceptionCode.MISSING_ELEMENT);
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
