package org.uppaal.cli.exceptions;

/**
* type exception, thrown when a reference to a property does not match the type of an element
* or when an assignment does not respect the typing constraints
*/

public class TypeException extends ConsoleException {
// the type of this exception
private String type;

// the property of this exception
private String property;

public TypeException () {
	super();
}

/**
* set the type of this exception
* @param type the new type for this exception
*/
public void setType(String type) {
	this.type = type;
}

/**
* set the property of this type exception
* @param property the property of this exception
*/
public void setProperty (String property) {
	this.property = property;
}

/**
* @return the message associated with this exception
*/
public String getMessage() {
	if (this.property==null) return "Type error: unsupported assignment for type "+this.type;
	else return "Type error: "+this.type+" has no member "+this.property;
}
}
