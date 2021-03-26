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

// reference type of this type exception
private String ref_type;

public TypeException () {
	super();
}

/**
* clear all fields of this type exception
*/
public void clear() {
	this.type = this.property = this.ref_type = null;
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
* set the ref type of this type exception
* @param ref_type the new type for this type exception
*/
public void setRefType (String ref_type) {
	this.ref_type = ref_type;
}

/**
* @return the message associated with this exception
*/
public String getMessage() {
	if (this.ref_type!=null) return "Type error: "+this.type+" cannot be assigned to "+this.ref_type;
	else if (this.property==null) return "Type error: unsupported assignment for type "+this.type;
	else return "Type error: "+this.type+" has no member "+this.property;
}
}
