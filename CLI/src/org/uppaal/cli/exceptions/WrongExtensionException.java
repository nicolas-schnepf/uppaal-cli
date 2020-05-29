package org.uppaal.cli.exceptions;

/**
* class handling wrong file extension for protected operations such as import or export
*/







public class WrongExtensionException extends ConsoleException {
// the object code of this exception
private String object_code;

// the wrong extension of this exception
private String wrong_extension;

/**
* public constructor of a wrong extension extension
*/
public WrongExtensionException () {
	super();
}

/**
* @return the object code of this exception
*/
public String getString() {
	return this.object_code;
}

/**
* set the object code of this exception
* @param object_code the new object code for this exception
*/
public void setString(String object_code) {
	this.object_code = object_code;
}

/**
*@return the extension of this exception
*/
public String getWrongExtension () {
	return this.wrong_extension;
}

/**
* set the wrong extension of this exception
* @param wrong_extension the new wrong extension for this exception
*/
public void setWrongExtension(String wrong_extension) {
	this.wrong_extension = wrong_extension;
}

@Override
public String getMessage () {
	return "Error: you cannot "+this.command+" "+this.object_type+" from a file with extension "+this.wrong_extension+".";
	}
}
