package org.uppaal.cli.exceptions;





import org.uppaal.cli.context.ModeCode;


/**
* wrong mode exception, raised when the current mode does not exist
*/

public class WrongModeException extends ConsoleException {

// code of the wrong handler
private ModeCode mode_code;

/**
* public constructor of a wrong mode exception
*/
public WrongModeException () {
	super();
}

/**
* @return the current mode of the uppaal command line interface
*/
public ModeCode getModeCode() {
	return this.mode_code;
}

/**
* set the handler code of this exception
* @param mode_code the new handler code for this exception
*/
public void setModeCode (ModeCode mode_code) {
	this.mode_code = mode_code;
}

@Override
public String getMessage() {
	String mode = null;
	switch (this.mode_code) {
		case EDITOR:
		mode = "editor";
		break;

		case SIMULATOR:
		mode = "simulator";
		break;

		case VERIFIER:
		mode = "verifier";
		break;
	}

	return String.format("Error: this command is not allowed in %s mode.", mode);
}
}
