package org.uppaal.cli.handlers;

import org.uppaal.cli.CommandResult;
import org.uppaal.cli.Command;
import org.uppaal.cli.Context;

/**
* concrete class implementing a verifier handler
* supporting all commands for the verifier mode
*/

public class VerifierHandler extends AbstractHandler {
// array of accepted command codes

private static Command.CommandCode[] verifier_commands = {
Command.CommandCode.CHECK
};

/**
* public constructor of a verifier handler
* initializing it from a reference to the uppaal context
* @param context the reference to the uppaal context for this handler
*/

public VerifierHandler (Context context) {
	super(context);
	this.accepted_commands = verifier_commands;
}

@Override
public CommandResult handle (Command command) {
	return this.command_result;
}

@Override
public Handler.HandlerCode getMode() {
	return Handler.HandlerCode.VERIFIER;
}
}