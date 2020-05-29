package org.uppaal.cli.exceptions;




import org.uppaal.cli.context.ModeCode;

/**
* existing element exception, thrown when an uppaal is existing in the current document
*/

public class ExistingElementException extends ConsoleException {
// the name of the existing element
private String name;

/**
* public constructor of a existing element exception
*/
public ExistingElementException () {
	super();
}

/**
* @return the name of the existing element
*/
public String getName () {
	return this.name;
}

/**
* set the name of this existing element exception
* @param name the new name for this existing element exception
*/
public void setName (String name) {
	this.name = name;
}
}
