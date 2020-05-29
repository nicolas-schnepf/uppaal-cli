package org.uppaal.cli.exceptions;

/**
* token exception, thrown when a token is not valid
*/

public class TokenException extends ConsoleException {
public TokenException () {
	super();
}

@Override
public String getMessage () {
	return"Token error: "+super.getMessage();
}
}
